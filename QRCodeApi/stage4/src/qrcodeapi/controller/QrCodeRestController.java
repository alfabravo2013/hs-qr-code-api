package qrcodeapi.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import qrcodeapi.service.ImageService;

import java.util.Map;

@RestController
public class QrCodeRestController {
    private final ImageService imageService;

    public QrCodeRestController(ImageService imageService) {
        this.imageService = imageService;
    }

    @GetMapping(path = "/api/info", produces = "application/json")
    public Map<String, String> getInfo() {
        return Map.of("info", "QR code generator");
    }

    @GetMapping(path = "/api/qrcode")
    public ResponseEntity<?> getImage(
            @RequestParam String contents,
            @RequestParam int size,
            @RequestParam String imgType
    ) {
        try {
            var image = imageService.createImage(contents, size);
            return ResponseEntity
                    .ok()
                    .contentType(getMediaType(imgType))
                    .body(image);
        } catch (RuntimeException e) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    private MediaType getMediaType(String imgType) {
        return MediaType.parseMediaType("image/" + imgType);
    }
}
