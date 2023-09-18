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

public class QRCodeApiTest extends SpringTest {

    CheckResult testGet() {
        HttpResponse response = get("/api/qrcode").send();

        if (response.getStatusCode() != 501) {
            return CheckResult.wrong(
                    "GET /api/qrcode should respond with status code 501, responded with %d\n\n"
                            .formatted(response.getStatusCode())
            );
        }

        return CheckResult.correct();
    }

    CheckResult testGetImage(String name, String mediaType, int expectedStatusCode, String expectedHash) {
        var path = "/image?name=" + name + "&mediaType=" + mediaType;
        HttpResponse response = get(path).send();

        if (response.getStatusCode() != expectedStatusCode) {
            return CheckResult.wrong("""
                    GET %s should respond with status code %d, responded with %d
                    """.formatted(path, expectedStatusCode, response.getStatusCode())
            );
        }

        var contentHash = calcHash(response.getRawContent());
        if (!Objects.equals(contentHash, expectedHash)) {
            return CheckResult.wrong("""
                    GET /%s should return body with hash %s, returned %s
                    """.formatted(path, expectedHash, contentHash));
        }

        return CheckResult.correct();
    }

    CheckResult testGetQrCode(String content, String mediaType, int expectedStatusCode, String expectedHash) {
        var path = "/qrcode?content=" + content + "&mediaType=" + mediaType;
        HttpResponse response = get(path).send();

        if (response.getStatusCode() != expectedStatusCode) {
            return CheckResult.wrong("""
                    GET %s should respond with status code %d, responded with %d
                    """.formatted(path, expectedStatusCode, response.getStatusCode())
            );
        }

        var contentHash = calcHash(response.getRawContent());
        if (!Objects.equals(contentHash, expectedHash)) {
            return CheckResult.wrong("""
                    GET /%s should return body with hash %s, returned %s
                    """.formatted(path, expectedHash, contentHash));
        }

        return CheckResult.correct();
    }

    @DynamicTest
    DynamicTesting[] tests = {
            this::testGet,
            () -> testGetImage("green", "image/png", 200, "b2b431df778b6314e8d7e350016e616e"),
            () -> testGetImage("green", "image/jpeg", 200, "273ebd2462c4cabcd1491c9e406783f5"),
            () -> testGetImage("green", "image/gif", 200, "d8e4903b3a31cc1c6fcd2a3ba13b792c"),
            () -> testGetImage("magenta", "image/png", 200, "a423be96567485e9e82262f1bfb09266"),
            () -> testGetImage("magenta", "image/jpeg", 200, "7bef570f1cda7b07c9fc0f7d92dba9f6"),
            () -> testGetImage("blue", "image/jpeg", 404, "d41d8cd98f00b204e9800998ecf8427e"), // of empty body
            () -> testGetImage("purple", "image/jpeg", 404, "d41d8cd98f00b204e9800998ecf8427e"), // of empty body

            () -> testGetQrCode(encodeUrl("what do we have here?"), "image/png", 200, "006d6445dae5c6c749a4a80adf1b8c9b"),
            () -> testGetQrCode(encodeUrl("https://hyperskill.org"), "image/jpg", 200, "6f28b59a8570a158b950926fb4e07c95"),
            () -> testGetQrCode(encodeUrl("geo:-33.918861,18.423300"), "image/gif", 200, "d0d5bac96d870f013b29dcd9c8ccc0b8"),
    };

    private String calcHash(byte[] rawContent) {
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
