import org.hyperskill.hstest.dynamic.DynamicTest;
import org.hyperskill.hstest.dynamic.input.DynamicTesting;
import org.hyperskill.hstest.mocks.web.response.HttpResponse;
import org.hyperskill.hstest.stage.SpringTest;
import org.hyperskill.hstest.testcase.CheckResult;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

import static org.hyperskill.hstest.testing.expect.Expectation.expect;
import static org.hyperskill.hstest.testing.expect.json.JsonChecker.isObject;
import static org.hyperskill.hstest.testing.expect.json.JsonChecker.isString;

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

    CheckResult testGetQrCode(int size, String imgType, String expectedHash) {
        var url = "/api/qrcode?size=" + size + "&type=" + imgType;
        HttpResponse response = get(url).send();

        if (response.getStatusCode() != 200) {
            return CheckResult.wrong(
                    "GET %s should respond with status code 200, responded with %d"
                            .formatted(url, response.getStatusCode())
            );
        }

        var expectedContentType = "image/" + imgType;
        var contentType = response.getHeaders().get("Content-Type");
        if (!Objects.equals(expectedContentType, contentType)) {
            return CheckResult.wrong("""
                    GET %s returned incorrect 'Content-Type' header. Expected "%s" but was "%s"
                     """.formatted(url, expectedContentType, contentType)
            );
        }

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

    CheckResult testGetQrCodeInvalidParams(int size, String imgType) {
        var url = "/api/qrcode?size=" + size + "&type=" + imgType;
        HttpResponse response = get(url).send();

        if (response.getStatusCode() != 400) {
            return CheckResult.wrong(
                    "GET %s should respond with status code 400, responded with %d"
                            .formatted(url, response.getStatusCode())
            );
        }

        if (!response.getJson().isJsonObject()) {
            return CheckResult.wrong(
                    "GET %s returned a wrong object, expected JSON but was %s"
                            .formatted(url, response.getContent().getClass())
            );
        }

        expect(response.getContent()).asJson().check(
                isObject()
                        .value("error", isString(str -> {
                            var correctMessage = (size < 150 || size > 350)
                                    ? "Image size must be between 150 and 350 pixels"
                                    : "Only png, jpeg and gif image types are supported";
                            return correctMessage.equalsIgnoreCase(str);
                        }))
        );

        return CheckResult.correct();
    }

    @DynamicTest
    DynamicTesting[] tests = {
            this::testGetHealth,

            () -> testGetQrCode(150, "png", "b67a6f17fe353b997585e65e2903ab7b"),
            () -> testGetQrCode(350, "jpeg", "f614890233a60b13e8e40c7ff554a92c"),
            () -> testGetQrCode(250, "gif", "cc9d9b226e2fab856cb5d008c94c5475"),

            () -> testGetQrCodeInvalidParams(99, "gif"),
            () -> testGetQrCodeInvalidParams(351, "png"),
            () -> testGetQrCodeInvalidParams(451, "webp")
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
