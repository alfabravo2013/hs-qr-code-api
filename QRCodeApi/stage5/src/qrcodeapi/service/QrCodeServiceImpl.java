package qrcodeapi.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.util.Map;

@Service
public class QrCodeServiceImpl implements ImageService {
    private static final int minSize = 150;
    private static final int maxSize = 350;

    @Override
    public BufferedImage createImage(String contents, int size, String correction) {
        if (contents == null || contents.isBlank()) {
            throw new RuntimeException("Contents cannot be null or blank");
        }

        if (size < minSize || size > maxSize) {
            throw new RuntimeException("Image size must be between %d and %d pixels".formatted(minSize, maxSize));
        }

        QRCodeWriter writer = new QRCodeWriter();
        ErrorCorrectionLevel correctionLevel = getErrorCorrectionLevel(correction);
        Map<EncodeHintType, ?> hints = Map.of(EncodeHintType.ERROR_CORRECTION, correctionLevel);
        try {
            BitMatrix matrix = writer.encode(
                    contents,
                    BarcodeFormat.QR_CODE,
                    size,
                    size,
                    hints
            );
            return MatrixToImageWriter.toBufferedImage(matrix);
        } catch (WriterException e) {
            throw new RuntimeException(e);
        }
    }

    private ErrorCorrectionLevel getErrorCorrectionLevel(String correction) {
        return switch (correction.toUpperCase()) {
            case "H" -> ErrorCorrectionLevel.H;
            case "Q" -> ErrorCorrectionLevel.Q;
            case "M" -> ErrorCorrectionLevel.M;
            case "L" -> ErrorCorrectionLevel.L;
            default -> throw new RuntimeException("Permitted error correction levels are L, M, Q, H");
        };
    }
}
