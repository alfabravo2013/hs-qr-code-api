import org.hyperskill.hstest.dynamic.DynamicTest;
import org.hyperskill.hstest.dynamic.input.DynamicTesting;
import org.hyperskill.hstest.mocks.web.response.HttpResponse;
import org.hyperskill.hstest.stage.SpringTest;
import org.hyperskill.hstest.testcase.CheckResult;

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

        if (response.getStatusCode() != 501) {
            return CheckResult.wrong(
                    "GET %s should respond with status code 501, responded with %d"
                            .formatted(url, response.getStatusCode())
            );
        }

        return CheckResult.correct();
    }

    @DynamicTest
    DynamicTesting[] tests = {
            this::testGetHealth,
            this::testGetQrCode
    };
}
