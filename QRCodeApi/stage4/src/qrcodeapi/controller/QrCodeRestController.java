package qrcodeapi.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import qrcodeapi.service.ImageService;

import java.util.Map;

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
    public ResponseEntity<?> getImage(
            @RequestParam String contents,
            @RequestParam int size,
            @RequestParam String type
    ) {
        try {
            var image = imageService.createImage(contents, size);
            return ResponseEntity
                    .ok()
                    .contentType(getMediaType(type))
                    .body(image);
        } catch (RuntimeException e) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    private MediaType getMediaType(String imgType) {
        return switch (imgType) {
            case "png", "jpeg", "gif" -> MediaType.parseMediaType("image/" + imgType);
            default -> throw new  RuntimeException("Only png, jpeg and gif image types are supported");
        };
    }
}
