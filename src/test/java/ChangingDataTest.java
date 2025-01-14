import com.github.javafaker.Faker;
import io.qameta.allure.*;
import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.junit4.Tag;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.example.operators.UserOperators;
import org.example.operators.OperatorsCheck;
import static org.apache.http.HttpStatus.*;
import java.util.ArrayList;
import org.example.Constants;

@Link(url = "https://code.s3.yandex.net/qa-automation-engineer/java/cheatsheets/paid-track/diplom/api-documentation.pdf")
@Tag("change user data")
@Epic("Диплом 2")
@Feature("Редактирование данных пользователя в Stellar Burgers")
@DisplayName("Редактирование данных пользователя")
public class ChangingDataTest {
    private String email;
    private String password;
    private String name;
    private String token;
    private final ArrayList<String> tokens = new ArrayList<>();
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
        checkResponse.checkStatusCode(response, SC_OK);

        if (response.getStatusCode() == SC_OK) {
            token = userAPI.getToken(response);
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
    @DisplayName("Изменение email с авторизацией")
    @Description("Редактирование email авторизованного пользователя. " +
            "ОР - email изменен")
    public void changeUserEmailWithAuthIsSuccess() {
        String newEmail = "one_" + faker.internet().safeEmailAddress();

        Response response = userAPI.updateUser(newEmail, password, name, token);

        checkResponse.checkStatusCode(response, SC_OK);
        checkResponse.checkSuccessStatus(response, "true");
        userAPI.checkUserData(response, newEmail, password, name);
    }

    @Test
    @DisplayName("Изменение пароля с авторизацией")
    @Description("Редактирование пароля авторизованного пользователя. " +
            "ОР - пароль изменен")
    public void changeUserPasswordWithAuthIsSuccess() {
        Response resetRequestResponse = userAPI.requestPasswordReset(email);

        checkResponse.checkStatusCode(resetRequestResponse, SC_OK);
        checkResponse.checkSuccessStatus(resetRequestResponse, "true");
        String newPassword = "two_" + faker.letterify("12345677");
        Response changePasswordResponse = userAPI.resetPassword(newPassword, token);

        checkResponse.checkStatusCode(changePasswordResponse, SC_OK);
        checkResponse.checkSuccessStatus(changePasswordResponse, "true");
    }

    @Test
    @DisplayName("Изменение имени с авторизацией")
    @Description("Редактирование имени авторизованного пользователя. " +
            "ОР - имя изменено")
    public void changeUserNameWithAuthIsSuccess() {
        String newName = "tres_" + faker.name().firstName();

        Response response = userAPI.updateUser(email, password, newName, token);

        checkResponse.checkStatusCode(response, SC_OK);
        checkResponse.checkSuccessStatus(response, "true");
        userAPI.checkUserData(response, email, password, newName);
    }

    @Test
    @DisplayName("Изменение email без авторизации")
    @Description("Редактирование email неавторизованного пользователя. " +
            "ОР - email не меняется, сообщение об ошибке")
    public void changeUserEmailWithoutAuthIsSuccess() {
        String newEmail = "four_" + faker.internet().safeEmailAddress();

        Response response = userAPI.updateUser(newEmail, password, name);

        checkResponse.checkStatusCode(response, SC_UNAUTHORIZED);
        checkResponse.checkSuccessStatus(response, "false");
        checkResponse.checkMessageText(response, "You should be authorised");
    }

    @Test
    @DisplayName("Изменение пароля без авторизации")
    @Description("Редактирование пароля неавторизованного пользователя. " +
            "ОР - пароль не меняется, сообщение об ошибке")
    public void changeUserPasswordWithoutAuthIsSuccess() {
        String newPassword = "five_" + faker.letterify("12345678");;

        Response response = userAPI.updateUser(email, newPassword, name);

        checkResponse.checkStatusCode(response, SC_UNAUTHORIZED);
        checkResponse.checkSuccessStatus(response, "false");
        checkResponse.checkMessageText(response, "You should be authorised");
    }

    @Test
    @DisplayName("Изменение имени без авторизации")
    @Description("Редактирование имени неавторизованного пользователя. " +
            "ОР - имя не меняется, сообщение об ошибке")
    public void changeUserNameWithoutAuthIsSuccess() {
        String newName = "six_" + faker.name().firstName();

        Response response = userAPI.updateUser(email, password, newName);

        checkResponse.checkStatusCode(response, SC_UNAUTHORIZED);
        checkResponse.checkSuccessStatus(response, "false");
        checkResponse.checkMessageText(response, "You should be authorised");
    }
}
