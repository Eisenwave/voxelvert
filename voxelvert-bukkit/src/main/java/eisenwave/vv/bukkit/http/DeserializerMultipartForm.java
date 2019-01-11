package eisenwave.vv.bukkit.http;

import com.google.common.net.MediaType;
import eisenwave.spatium.util.PrimArrays;
import eisenwave.torrens.error.FileSyntaxException;
import eisenwave.torrens.io.Deserializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class DeserializerMultipartForm implements Deserializer<MultipartFormEntry[]> {
    
    private final static byte[] SEPARATOR_PREFIX = new byte[] {'\r', '\n', '-', '-'};
    
    private final byte[] crlfBoundary;
    
    public DeserializerMultipartForm(@NotNull byte[] boundary) {
        this.crlfBoundary = PrimArrays.concat(SEPARATOR_PREFIX, boundary);
    }
    
    public DeserializerMultipartForm(@NotNull String boundary) {
        this(boundary.getBytes(Charset.forName("US-ASCII")));
    }
    
    @NotNull
    @Override
    public MultipartFormEntry[] fromStream(InputStream stream) throws IOException {
        List<MultipartFormEntry> result = new ArrayList<>();
        validateBoundary(stream);
        validateCRLF(stream);
        
        do {
            ReadEntry entry = readEntry(stream);
            MultipartFormEntry formEntry = new MultipartFormEntry(entry.bytes);
            formEntry.setName(entry.name);
            formEntry.setType(entry.type);
            formEntry.setFilename(entry.filename);
            
            result.add(formEntry);
            if (entry.last) break;
        } while (true);
        
        return result.toArray(new MultipartFormEntry[result.size()]);
    }
    
    @NotNull
    private ReadEntry readEntry(InputStream stream) throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        
        String filename = null, name = null;
        MediaType type = null;
        
        for (String line = readLine(stream); !line.isEmpty(); line = readLine(stream)) {
            List<String> parsed = DeserializerHttpHeaders.deserializeMultiSemicolon(line);
            switch (parsed.get(0).toLowerCase()) {
                case "content-disposition": {
                    for (String value : parsed.subList(1, parsed.size())) {
                        if (value.endsWith("\"")) {
                            if (value.startsWith("filename=\""))
                                filename = value.substring(10, value.length() - 1);
                            else if (value.startsWith("name=\""))
                                name = value.substring(6, value.length() - 1);
                        }
                    }
                    break;
                }
                case "content-type": {
                    type = MediaType.parse(parsed.get(1));
                    break;
                }
            }
        }
        
        if (type == null) {
            throw new FileSyntaxException("entry content type unspecified");
        }
        
        byte[] buffer = null;
        int bufferIndex = 0;
        int boundaryIndex = 0;
        
        int postIndex = -1, post0 = -1, post1;
        
        while (true) {
            byte b;
            
            //System.out.print("\n");
            //System.out.print(ANSI.FG_YELLOW + (buffer == null? "null" : "[" + toString(buffer) + "]"));
            
            if (postIndex == 0) {
                post0 = stream.read();
                postIndex = 1;
                continue;
            }
            else if (postIndex == 1) {
                post1 = stream.read();
                boolean lastFlag;
                {
                    if (post0 == '\r') {
                        if (post1 == '\n') lastFlag = false;
                        else throw new FileSyntaxException("boundary followed by CR, 0x" + Integer.toHexString(post1));
                    }
                    else if (post0 == '-') {
                        if (post1 == '-') lastFlag = true;
                        else throw new FileSyntaxException("boundary followed by '-', 0x" + Integer.toHexString(post1));
                    }
                    else throw new FileSyntaxException("boundary followed by 0x" + Integer.toHexString(post0) + ", 0x" +
                            Integer.toHexString(post1));
                }
                
                return new ReadEntry(byteStream.toByteArray(), type, filename, name, lastFlag);
            }
            else {
                if (buffer == null) {
                    b = readByteSafely(stream);
                    //System.out.print(ANSI.FG_RED + "[" + toString(b) + "]");
                }
                else {
                    b = buffer[bufferIndex++];
                    //System.out.print(ANSI.FG_BLUE + "[" + toString(b) + ":" + (bufferIndex - 1) + "]");
                }
            }
            
            //System.out.print(ANSI.RESET + "[" + toString(crlfBoundary[boundaryIndex]) + ":" + boundaryIndex + "]");
            
            if (b == crlfBoundary[boundaryIndex]) {
                if (buffer == null) {
                    buffer = new byte[crlfBoundary.length];
                    buffer[0] = b;
                    for (int i = 1; i < buffer.length; i++)
                        buffer[i] = readByteSafely(stream);
                    bufferIndex = 1;
                }
                else if (boundaryIndex == 0) {
                    byte[] newBuffer = new byte[crlfBoundary.length];
                    newBuffer[0] = b;
                    int copy = buffer.length - bufferIndex + 1;
                    System.arraycopy(buffer, bufferIndex - 1, newBuffer, 0, copy);
                    for (int i = copy; i < newBuffer.length; i++) {
                        newBuffer[i] = readByteSafely(stream);
                        //System.out.print(ANSI.FG_RED + "(" + toString(buffer[i]) + ":" + i + ")");
                    }
                    bufferIndex = 1;
                    buffer = newBuffer;
                    //System.out.print("(" + copy + " buffer swap for [" + toString(newBuffer) + "])");
                }
                
                boundaryIndex++;
                if (boundaryIndex >= crlfBoundary.length)
                    postIndex = 0;
            }
            else {
                /* if (buffer != null) {
                    byteStream.write(buffer, 0, boundaryIndex);
                    buffer = null;
                }
                */
                if (boundaryIndex > 0) {
                    if (buffer != null) {
                        bufferIndex -= boundaryIndex;
                        byteStream.write(buffer[0]);
                        //System.out.print(ANSI.BG_PURPLE + toString(buffer[0]));
                    }
                    else {
                        byteStream.write(b);
                        //System.out.print(ANSI.BG_PURPLE + toString(b));
                    }
                    boundaryIndex = 0;
                }
                else {
                    if (buffer != null && bufferIndex >= buffer.length) {
                        buffer = null;
                    }
                    byteStream.write(b);
                    //System.out.print(ANSI.BG_PURPLE + toString(b));
                }
            }
        }
    }
    
    private static byte readByteSafely(InputStream stream) throws IOException {
        int b = stream.read();
        if (b < 0)
            throw new IOException("unexpect end of stream");
        return (byte) b;
    }
    
    /*
     * Reads a given amount of bytes from the stream and writes them into the buffer.
     *
     * @param buffer the buffer
     * @param stream the stream
     * @param required the required amount of bytes
     * @throws IOException if an I/O error occurrs
     *
    private void fillBuffer(DynamicByteBuffer buffer, InputStream stream, int required) throws IOException {
        for (int i = 0; i < required; i++) {
            buffer.put((byte) stream.read());
        }
        buffer.setPosition(buffer.getPosition() - required);
    }
    */
    
    private void validateBoundary(InputStream stream) throws IOException {
        byte[] buffer = new byte[crlfBoundary.length - 2];
        if (stream.read(buffer) != buffer.length)
            throw new FileSyntaxException("end of stream");
        
        for (int i = 0; i < buffer.length; i++) {
            if (buffer[i] != crlfBoundary[i + 2]) {
                String error = "boundary expected at beginning of stream (found \"" + new String(buffer) + "\")";
                throw new FileSyntaxException(error);
            }
        }
    }
    
    private void validateCRLF(InputStream stream) throws IOException {
        byte[] buffer = new byte[2];
        if (stream.read(buffer) != 2)
            throw new FileSyntaxException("end of stream when expecting CRLF");
        if (buffer[0] != '\r' || buffer[1] != '\n') {
            String hex0 = Integer.toHexString(buffer[0]);
            String hex1 = Integer.toHexString(buffer[1]);
            throw new FileSyntaxException("expected CRLF, got 0x" + hex0 + ", 0x" + hex1);
        }
    }
    
    /**
     * Reads a CRLF-terminated line of the header of a form part.
     *
     * @param stream the stream
     * @return the line
     * @throws IOException if an I/O error occurs
     */
    private String readLine(InputStream stream) throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        
        /*
        byte[] crlfBuffer = new byte[2];
        if (stream.read(crlfBuffer) != 2)
            throw new FileSyntaxException("end of stream when reading header");
        if (crlfBuffer[0] == '\r' && crlfBuffer[1] == '\n')
            return null;
        byteStream.write(crlfBuffer);
        */
        
        while (true) {
            int point = stream.read();
            if (point < 0)
                throw new IOException("end of stream while reading header line");
            if (point == '\r') {
                if (stream.read() != '\n')
                    throw new FileSyntaxException("CR not followed by LF in header");
                return byteStream.toString();
            }
            byteStream.write(point);
        }
        //if (stream.read(buffer) != boundary.length)
        //    throw new FileSyntaxException("end of stream");
        
        //if (!Arrays.equals(boundary, buffer))
        //    throw new FileSyntaxException("boundary expected at beginning of stream");
    }
    
    private final static class ReadEntry {
        
        private final byte[] bytes;
        private final MediaType type;
        private final String filename, name;
        private boolean last;
        
        public ReadEntry(byte[] bytes, @NotNull MediaType type, @Nullable String filename, @Nullable String name,
                         boolean last) {
            this.bytes = bytes;
            this.type = type;
            this.filename = filename;
            this.name = name;
            this.last = last;
        }
        
    }
    
    private static String toString(int ascii) {
        char c = (char) ascii;
        if (c == '\r') return "\\r";
        if (c == '\n') return "\\n";
        else return Character.toString(c);
    }
    
    private static String toString(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for (byte b : bytes)
            builder.append(toString(b));
        return builder.toString();
    }
    
}
