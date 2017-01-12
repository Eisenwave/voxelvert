package net.grian.vv.convert;

import net.grian.vv.core.BaseRectangle;
import net.grian.vv.core.RectangleArrangement;
import net.grian.vv.core.Texture;
import net.grian.vv.util.tuple.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class ConverterRectangleArranger implements Converter<BaseRectangle[], RectangleArrangement> {

    @Override
    public Class<BaseRectangle[]> getFrom() {
        return BaseRectangle[].class;
    }

    @Override
    public Class<RectangleArrangement> getTo() {
        return RectangleArrangement.class;
    }

    @Override
    public RectangleArrangement invoke(BaseRectangle[] from, Object[] args) {
        Arrays.sort(from);

        return null;
    }

    public static Texture render(TextureBucket[] buckets) {
        if (buckets.length == 0) throw new IllegalArgumentException("no textures");
        int width = buckets[0].getWidth(), height = 0;
        for (TextureBucket bucket : buckets)
            height += bucket.getHeight();

        Texture texture = new Texture(width, height);

        return texture;
    }

    public static class TextureBucket extends ArrayList<Texture> {

        private int width, height;

        public TextureBucket(int width, int height) {
            if (width < 1) throw new IllegalArgumentException("width < 1");
            if (height < 1) throw new IllegalArgumentException("height < 1");
            this.width = width;
            this.height = height;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        public int getContentWidth() {
            int result = 0;
            for (Texture t : this)
                result += t.getWidth();
            return result;
        }

        public int getContentHeight() {
            int result = 0;
            for (Texture t : this)
                result += t.getHeight();
            return result;
        }

    }

}
