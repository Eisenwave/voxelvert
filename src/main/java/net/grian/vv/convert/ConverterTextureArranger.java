package net.grian.vv.convert;

import net.grian.vv.core.Texture;
import org.bukkit.entity.Player;
import net.grian.vv.convert.ConverterTextureArranger.TileMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class ConverterTextureArranger implements Converter<Texture[], TileMap> {

    @Override
    public Class<Texture[]> getFrom() {
        return Texture[].class;
    }

    @Override
    public Class<TileMap> getTo() {
        return TileMap.class;
    }

    @Override
    public TileMap invoke(Texture[] from, Object[] args) {
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

    public static class TileMap {

        private final Texture texture;
        private final Texture.Tile[] tiles;

        public TileMap(Texture texture, Texture.Tile[] tiles) {
            Objects.requireNonNull(texture);
            Objects.requireNonNull(tiles);
            this.texture = texture;
            this.tiles = tiles;
        }

        public Texture getTexture() {
            return texture;
        }

        public Texture.Tile[] getTiles() {
            return tiles;
        }

    }

}
