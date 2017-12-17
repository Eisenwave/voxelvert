package eisenwave.vv.ui.util;

import eisenwave.spatium.util.Strings;

import java.util.ArrayList;
import java.util.Arrays;

public class StringTable extends ArrayList<String[]> {
    
    private final int columns;
    private final int[] lengths;
    private final String[] separators;
    
    public StringTable(int capacity, String[] separators) {
        super(capacity);
        this.columns = separators.length;
        this.lengths = new int[columns];
        this.separators = separators;
    }
    
    public StringTable(int capacity, int columns, String separator) {
        super(capacity);
        this.columns = columns;
        this.lengths = new int[columns];
        this.separators = new String[columns];
        Arrays.fill(separators, separator);
    }
    
    public StringTable(String... seperators) {
        this(16, seperators);
    }
    
    public boolean add(String... row) {
        for (int i = 0; i < columns; i++) {
            String entry = row[i];
            lengths[i] = Math.max(lengths[i], entry.length());
        }
        
        return super.add(row);
    }
    
    public boolean addEmpty() {
        String[] row = new String[columns];
        Arrays.fill(row, "");
        
        return super.add(row);
    }
    
    public String printRow(int index) {
        String[] row = get(index);
        
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < columns;) {
            String entry = row[i];
            builder.append(entry);
            int trail = lengths[i] - entry.length();
            
            if (++i < columns) {
                builder
                    .append(Strings.repeat(' ', trail))
                    .append(separators[i]);
            }
        }
        
        return builder.toString();
    }
    
}
