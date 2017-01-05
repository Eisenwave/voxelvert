package net.grian.vv.convert;

import net.grian.vv.core.ElementSet;
import net.grian.vv.io.Serializer;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.logging.Logger;

public class SerializerModel implements Serializer<ElementSet> {

    private final Logger logger;

    public SerializerModel(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void serialize(ElementSet elements, OutputStream stream) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stream));
        serialize(elements, writer);
        writer.close();
    }

    public void serialize(ElementSet elements, BufferedWriter writer) throws IOException {

    }

}
