package qrcodeapi.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import qrcodeapi.service.ImageService;

import java.awt.image.BufferedImage;

@RestController
public class QrCodeRestController {
    private final ImageService imageService;

    public QrCodeRestController(ImageService imageService) {
        this.imageService = imageService;
    }

    @GetMapping(path = "/api/health")
    @ResponseStatus(HttpStatus.OK)
    public void ping() {

    }

    @GetMapping(path = "/api/qrcode")
    public ResponseEntity<BufferedImage> getImage() {
        var bufferedImage = imageService.createImage();
        return ResponseEntity
                .ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(bufferedImage);
    }
}
