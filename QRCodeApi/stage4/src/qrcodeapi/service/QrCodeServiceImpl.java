package qrcodeapi.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;

@Service
public class QrCodeServiceImpl implements ImageService {
    private static final int minSize = 100;
    private static final int maxSize = 1000;

    @Override
    public BufferedImage createImage(String contents, int size) {
        if (contents == null || contents.isBlank()) {
            throw new RuntimeException("Contents cannot be null or blank");
        }

        if (size < minSize || size > maxSize) {
            throw new RuntimeException("Min size is %d, max size is %d".formatted(minSize, maxSize));
        }

        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix matrix = writer.encode(
                    contents,
                    BarcodeFormat.QR_CODE,
                    size,
                    size
            );

            return MatrixToImageWriter.toBufferedImage(matrix);

        } catch (WriterException e) {
            throw new RuntimeException(e);
        }
    }
}
