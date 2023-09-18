package qrcodeapi.service;

import org.springframework.stereotype.Service;


import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

@Service
public class StaticServiceImpl implements ImageService {
    private final Color defaultColor = Color.WHITE;
    private final int defaultSize = 250;

    @Override
    public BufferedImage createImage() {
        BufferedImage image = new BufferedImage(
                defaultSize,
                defaultSize,
                BufferedImage.TYPE_INT_RGB
        );

        Graphics2D g = image.createGraphics();

        g.setColor(defaultColor);
        g.fillRect(0, 0, defaultSize, defaultSize);
        g.dispose();

        return image;
    }
}
