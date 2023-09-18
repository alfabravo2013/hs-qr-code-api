package qrcodeapi.controller;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.awt.image.BufferedImage;

@RestController
public class QrCodeRestController {

    @GetMapping(path = "/api/qrcode")
    public ResponseEntity<String> stub() {
        return ResponseEntity
                .status(HttpStatus.NOT_IMPLEMENTED)
                .build();
    }

    @GetMapping(path = "/qrcode")
    public ResponseEntity<BufferedImage> getImage(@RequestParam String content,
                                                  @RequestParam(required = false, defaultValue = "200") int size,
                                                  @RequestParam(required = false, defaultValue = "image/png") String type) {
        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix matrix = writer.encode(
                    content,
                    BarcodeFormat.QR_CODE,
                    size,
                    size
            );

            BufferedImage image = MatrixToImageWriter.toBufferedImage(matrix);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .contentType(MediaType.parseMediaType(type))
                    .body(image);

        } catch (WriterException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
