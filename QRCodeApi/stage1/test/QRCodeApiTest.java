import org.hyperskill.hstest.dynamic.DynamicTest;
import org.hyperskill.hstest.dynamic.input.DynamicTesting;
import org.hyperskill.hstest.exception.outcomes.WrongAnswer;
import org.hyperskill.hstest.mocks.web.response.HttpResponse;
import org.hyperskill.hstest.stage.SpringTest;
import org.hyperskill.hstest.testcase.CheckResult;

public class QRCodeApiTest extends SpringTest {

    CheckResult testGetHealth() {
        var url = "/api/health";
        HttpResponse response = get(url).send();

        checkStatusCode(response, 200);

        return CheckResult.correct();
    }

    CheckResult testGetQrCode() {
        var url = "/api/qrcode";
        HttpResponse response = get(url).send();

        checkStatusCode(response, 501);

        return CheckResult.correct();
    }

    @DynamicTest
    DynamicTesting[] tests = {
            this::testGetHealth,
            this::testGetQrCode
    };

    private void checkStatusCode(HttpResponse response, int expected) {
        var endpoint = response.getRequest().getEndpoint();
        var actual = response.getStatusCode();
        if (actual != expected) {
            throw new WrongAnswer("""
                    Request: GET %s
                    
                    Response has incorrect status code:
                    Expected %d, but responded with %d
                    
                    """.formatted(endpoint, expected, actual)
            );
        }
    }
}
