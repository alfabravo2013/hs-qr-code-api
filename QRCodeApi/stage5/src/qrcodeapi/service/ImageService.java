package qrcodeapi.service;

import java.awt.image.BufferedImage;

public interface ImageService {
    BufferedImage createImage(String contents, int size, String level);
}
