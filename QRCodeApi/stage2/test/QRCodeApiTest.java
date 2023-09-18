import org.hyperskill.hstest.dynamic.DynamicTest;
import org.hyperskill.hstest.dynamic.input.DynamicTesting;
import org.hyperskill.hstest.mocks.web.response.HttpResponse;
import org.hyperskill.hstest.stage.SpringTest;
import org.hyperskill.hstest.testcase.CheckResult;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public class QRCodeApiTest extends SpringTest {

    CheckResult testGetHealth() {
        var url = "/api/health";
        HttpResponse response = get(url).send();

        if (response.getStatusCode() != 200) {
            return CheckResult.wrong(
                    "GET %s should respond with status code 200, responded with %d"
                            .formatted(url, response.getStatusCode())
            );
        }

        return CheckResult.correct();
    }

    CheckResult testGetQrCode() {
        var url = "/api/qrcode";
        HttpResponse response = get(url).send();

        if (response.getStatusCode() != 200) {
            return CheckResult.wrong(
                    "GET %s should respond with status code 200, responded with %d"
                            .formatted(url, response.getStatusCode())
            );
        }

        var expectedContentType = "image/png";
        var contentType = response.getHeaders().get("Content-Type");
        if (!Objects.equals(expectedContentType, contentType)) {
            return CheckResult.wrong("""
                    GET %s returned incorrect 'Content-Type' header. Expected "%s" but was "%s"
                     """.formatted(url, expectedContentType, contentType)
            );
        }

        var expectedHash = "a370a8d3e1ee0f0184132a3c3b5d2952";
        var contentHash = getMD5Hash(response.getRawContent());
        if (!contentHash.equals(expectedHash)) {
            return CheckResult.wrong("""
                    GET %s failed to return a correct image.
                    Expected image hash %s, but was %s
                    
                    Make sure the size, the contents and the format of the image are correct.
                    """.formatted(url, expectedHash, contentHash)
            );
        }

        return CheckResult.correct();
    }

    @DynamicTest
    DynamicTesting[] tests = {
            this::testGetHealth,
            this::testGetQrCode
    };

    private String getMD5Hash(byte[] rawContent) {
        try {
            var md = MessageDigest.getInstance("MD5");
            var hash = md.digest(rawContent);
            var hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append("%02x".formatted(b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
