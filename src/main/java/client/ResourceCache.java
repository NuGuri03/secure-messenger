package client;

import java.awt.Image;
import java.util.Hashtable;

import javax.swing.ImageIcon;

public class ResourceCache {
    private static Hashtable<String, Hashtable<Integer, ImageIcon>> iconCache;

    public static ImageIcon getIcon(String path, int size) {
        if (iconCache == null) {
            iconCache = new Hashtable<>();
        }

        var sizeCache = iconCache.computeIfAbsent(path, k -> new Hashtable<>());
        return sizeCache.computeIfAbsent(size, k -> {
            var defaultIcon = new ImageIcon(ResourceCache.class.getResource(path));
            var scaledImage = defaultIcon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        });
    }
}
