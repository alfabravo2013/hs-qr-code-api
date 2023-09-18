import org.hyperskill.hstest.dynamic.DynamicTest;
import org.hyperskill.hstest.dynamic.input.DynamicTesting;
import org.hyperskill.hstest.mocks.web.response.HttpResponse;
import org.hyperskill.hstest.stage.SpringTest;
import org.hyperskill.hstest.testcase.CheckResult;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

import static org.hyperskill.hstest.testing.expect.Expectation.expect;
import static org.hyperskill.hstest.testing.expect.json.JsonChecker.isObject;

public class QRCodeApiTest extends SpringTest {

    CheckResult testGetInfo() {
        var url = "/api/info";
        HttpResponse response = get(url).send();

        if (response.getStatusCode() != 200) {
            return CheckResult.wrong(
                    "GET %s should respond with status code 200, responded with %d"
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
                        .value("info", "QR code generator")
        );

        return CheckResult.correct();
    }

    CheckResult testGetQrCode(String contents, int size, String imgType, String expectedHash) {
        var url = "/api/qrcode?contents=%s&size=%d&imgType=%s"
                .formatted(encodeUrl(contents), size, imgType);
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

    CheckResult testGetQrCodeInvalidSize(int size, String imgType) {
        var url = "/api/qrcode?contents=%s&size=%d&imgType=%s"
                .formatted(encodeUrl("image size is not acceptable"), size, imgType);
        HttpResponse response = get(url).send();

        if (response.getStatusCode() != 400) {
            return CheckResult.wrong(
                    "GET %s should respond with status code 400, responded with %d"
                            .formatted(url, response.getStatusCode())
            );
        }

        return CheckResult.correct();
    }

    CheckResult testGetQrCodeBlankContents() {
        var url = "/api/qrcode?contents=%s&size=%d&imgType=%s"
                .formatted(encodeUrl("  "), 200, "image/png");
        HttpResponse response = get(url).send();

        if (response.getStatusCode() != 400) {
            return CheckResult.wrong(
                    "GET %s should respond with status code 400, responded with %d"
                            .formatted(url, response.getStatusCode())
            );
        }

        return CheckResult.correct();
    }

    String[] contents = {
            "text content",
            "mailto:name@company.com",
            "geo:-27.07,109.21",
            "tel:1234567890",
            "smsto:1234567890:texting!",
            "Here is some text",
            "https://hyperskill.org",
            """
            BEGIN:VCARD
            VERSION:3.0
            N:John Doe
            ORG:FAANG
            TITLE:CEO
            TEL:1234567890
            EMAIL:business@example.com
            END:VCARD"""
    };

    @DynamicTest
    DynamicTesting[] tests = {
            this::testGetInfo,

            () -> testGetQrCode(contents[0], 200, "png", "c2617604f9698481be698106d7b690af"),
            () -> testGetQrCode(contents[1], 200, "jpeg", "a9e1e394f5766304127ba88bd9f0bd5a"),
            () -> testGetQrCode(contents[2], 200, "gif", "3d6cc8d84284c0d10af3370c1fa883a8"),
            () -> testGetQrCode(contents[3], 400, "png", "da04f85a409d1d7381e7429753eedabe"),
            () -> testGetQrCode(contents[4], 400, "jpeg", "234cbfee11c4b82e2241261520d6e04d"),
            () -> testGetQrCode(contents[5], 400, "gif", "d4ecbb1db55a1afde5ba440b8f8a5c46"),
            () -> testGetQrCode(contents[6], 100, "jpeg", "0951748cfcd6071e9e46bae785727782"),
            () -> testGetQrCode(contents[7], 1000, "gif", "717b3e12b3cec3cd57d1161358424a9d"),

            () -> testGetQrCodeInvalidSize(99, "gif"),
            () -> testGetQrCodeInvalidSize(-10, "png"),
            () -> testGetQrCodeInvalidSize(1001, "gif"),

            this::testGetQrCodeBlankContents
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

    private String encodeUrl(String param) {
        return URLEncoder.encode(param, StandardCharsets.UTF_8);
    }
}
