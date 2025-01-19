import com.github.javafaker.Faker;
import io.qameta.allure.*;
import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.junit4.Tag;
import io.restassured.response.Response;
import org.example.operators.OperatorsCheck;
import org.example.operators.OrderOperators;
import org.example.operators.UserOperators;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.apache.http.HttpStatus.*;
import java.util.ArrayList;

@Link(url = "https://code.s3.yandex.net/qa-automation-engineer/java/cheatsheets/paid-track/diplom/api-documentation.pdf")
@Tag("log in user")
@Epic("Диплом 2")
@Feature("Логин пользователя в Stellar Burgers")
@DisplayName("Логин пользователя")

public class UserLoginTest {
    private String email;
    private String password;
    private String name;
    private ArrayList<String> tokens = new ArrayList<>();
    private final OrderOperators orderAPI = new OrderOperators();
    private final UserOperators userAPI = new UserOperators();
    private final OperatorsCheck checkResponse = new OperatorsCheck();
    private final Faker faker = new Faker();

    @Before
    @Step("Подготовка тестовых данных")
    public void prepareTestData() {
        this.email = faker.internet().safeEmailAddress();
        this.password = faker.letterify("12345678");
        this.name = faker.name().firstName();

        Response response = userAPI.registerUser(email, password, name);
        if (response.getStatusCode() == SC_OK) {
            tokens.add(userAPI.getToken(response));
        }
    }

    @After
    @Step ("Удаление данных после теста")
    public void clearAfterTests() {
        if(tokens.isEmpty())
            return;
        for (String token: tokens) {
            checkResponse.checkStatusCode(userAPI.deleteUser(token), SC_ACCEPTED);
        }
    }

    @Test
    @DisplayName("Логин пользователя")
    @Description("Логин пользователя. " +
            "ОР - логин зарегистрирован")
    public void loginUserIsSuccessTest() {
        Response response = userAPI.loginUser(email, password);

        checkResponse.checkStatusCode(response, SC_OK);
        checkResponse.checkSuccessStatus(response, "true");
    }

    @Test
    @DisplayName("Логин пользователя без email")
    @Description("Тест API логин пользователя без email. " +
            "ОР - логин не зарегистрирован")
    public void loginUserWithoutEmailIsFailedTest() {
        Response response = userAPI.loginUser("",password);

        checkResponse.checkStatusCode(response, SC_UNAUTHORIZED);
        checkResponse.checkSuccessStatus(response, "false");
        checkResponse.checkMessageText(response, "email or password are incorrect");
    }

    @Test
    @DisplayName("Логин пользователя без пароля")
    @Description("Логин пользователя без пароля. " +
            "ОР - логин не зарегистрирован")
    public void loginUserWithoutPasswordIsFailedTest() {
        Response response = userAPI.loginUser(email, null);

        checkResponse.checkStatusCode(response, SC_UNAUTHORIZED);
        checkResponse.checkSuccessStatus(response, "false");
        checkResponse.checkMessageText(response, "email or password are incorrect");
    }

    @Test
    @DisplayName("Логин пользователя c некорректным email")
    @Description("Логин пользователя с некорректным email. " +
            "ОР - логин не зарегистрирован")
    public void loginUserWithIncorrectEmailIsFailedTest() {
        Response response = userAPI.loginUser(email + "qwe", password);

        checkResponse.checkStatusCode(response, SC_UNAUTHORIZED);
        checkResponse.checkSuccessStatus(response, "false");
        checkResponse.checkMessageText(response, "email or password are incorrect");
    }

    @Test
    @DisplayName("Логин пользователя c некорректным паролем")
    @Description("Логин пользователя с некорректным паролем. " +
            "ОР - логин не зарегистрирован")
    public void loginUserWithIncorrectPassIsFailedTest() {
        Response response = userAPI.loginUser(email, password + "qwe");

        checkResponse.checkStatusCode(response, SC_UNAUTHORIZED);
        checkResponse.checkSuccessStatus(response, "false");
        checkResponse.checkMessageText(response, "email or password are incorrect");
    }
}
