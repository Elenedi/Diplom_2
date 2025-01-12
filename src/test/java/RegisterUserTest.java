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
import org.example.*;

@Link(url = "https://code.s3.yandex.net/qa-automation-engineer/java/cheatsheets/paid-track/diplom/api-documentation.pdf")
@Tag("register new user")
@Epic("Диплом 2")
@Feature("Создание нового пользователя в Stellar Burgers")
@DisplayName("Создание нового пользователя")

public class RegisterUserTest {
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
    @DisplayName("Регистрация нового пользователя")
    @Description("Создание нового пользователя. " +
            "ОР - пользователь создан")
    public void registerUserIsSuccess() {
        Response response = userAPI.registerUser(email, password, name);
        if (response.getStatusCode() == SC_OK) {
            tokens.add(userAPI.getToken(response));
        }
        checkResponse.checkStatusCode(response, SC_OK);
        checkResponse.checkSuccessStatus(response, "true");
    }

    @Test
    @DisplayName("Регистрация двух пользователей с одинаковыми данными")
    @Description("Создание двух пользователей с одинаковыми данными. " +
            "ОР - одинаковых пользователей создать нельзя.")
    public void registerSameUserIsFailed() {
        Response responseX = userAPI.registerUser(email, password, name);
        Response responseY = userAPI.registerUser(email, password, name);
        if (responseX.getStatusCode() == SC_OK) {
            tokens.add(userAPI.getToken(responseX));
        }
        if (responseY.getStatusCode() == SC_OK) {
            tokens.add(userAPI.getToken(responseY));
        }

        checkResponse.checkStatusCode(responseY, SC_FORBIDDEN);
        checkResponse.checkSuccessStatus(responseY, "false");
        checkResponse.checkMessageText(responseY,"User already exists");
    }

    @Test
    @DisplayName("Регистрация пользователя без email")
    @Description("Регистрация пользователя без email. " +
            "ОР - пользователя без email создать нельзя")
    public void registerUserWithoutEmailIsFailed() {
        Response response = userAPI.registerUser(null, password, name);
        if (response.getStatusCode() == SC_OK) {
            tokens.add(userAPI.getToken(response));
        }
        checkResponse.checkStatusCode(response, SC_FORBIDDEN);
        checkResponse.checkSuccessStatus(response, "false");
        checkResponse.checkMessageText(response, "Email, password and name are required fields");
    }

    @Test
    @DisplayName("Регистрация пользователя без пароля")
    @Description("Регистрация пользователя без пароля. " +
            "ОР - пользователя без пароля создать нельзя")
    public void registerUserWithoutPasswordIsFailed() {
        Response response = userAPI.registerUser(email, null, name);
        if (response.getStatusCode() == SC_OK) {
            tokens.add(userAPI.getToken(response));
        }
        checkResponse.checkStatusCode(response, SC_FORBIDDEN);
        checkResponse.checkSuccessStatus(response, "false");
        checkResponse.checkMessageText(response, "Email, password and name are required fields");
    }

    @Test
    @DisplayName("Регистрация пользователя без имени")
    @Description("Регистрация пользователя без имени. " +
            "ОР - пользователя без имени создать нельзя")
    public void registerUserWithoutNameIsFailed() {
        Response response = userAPI.registerUser(email, null, name);
        if (response.getStatusCode() == SC_OK) {
            tokens.add(userAPI.getToken(response));
        }
        checkResponse.checkStatusCode(response, SC_FORBIDDEN);
        checkResponse.checkSuccessStatus(response, "false");
        checkResponse.checkMessageText(response, "Email, password and name are required fields");
    }

    @Test
    @DisplayName("Регистрация пользователя без данных")
    @Description("Регистрация пользователя без данных. " +
            "ОР - пользователя без данных создать нельзя")
    public void registerUserWithoutDataIsFailed() {
        Response response = userAPI.registerUser(null, null, null);
        if (response.getStatusCode() == SC_OK) {
            tokens.add(userAPI.getToken(response));
        }
        checkResponse.checkStatusCode(response, SC_FORBIDDEN);
        checkResponse.checkSuccessStatus(response, "false");
        checkResponse.checkMessageText(response, "Email, password and name are required fields");
    }
}

