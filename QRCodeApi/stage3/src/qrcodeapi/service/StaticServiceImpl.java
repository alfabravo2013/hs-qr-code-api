package qrcodeapi.service;

import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.image.BufferedImage;

@Service
public class StaticServiceImpl implements ImageService {
    private static final Color defaultColor = Color.WHITE;
    private static final int minSize = 150;
    private static final int maxSize = 350;

    @Override
    public BufferedImage createImage(int size) {
        if (size < minSize || size > maxSize) {
            throw new RuntimeException(
                    "Image size must be between %d and %d pixels".formatted(minSize, maxSize)
            );
        }

        BufferedImage image = new BufferedImage(
                size,
                size,
                BufferedImage.TYPE_INT_RGB
        );

        Graphics2D g = image.createGraphics();

        g.setColor(defaultColor);
        g.fillRect(0, 0, size, size);
        g.dispose();

        return image;
    }
}
