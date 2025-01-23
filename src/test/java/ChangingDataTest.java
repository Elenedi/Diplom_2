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
import org.example.http.client.*;
import methods.MethodsForTest;

@Link(url = "https://code.s3.yandex.net/qa-automation-engineer/java/cheatsheets/paid-track/diplom/api-documentation.pdf")
@Tag("change user data")
@Epic("Диплом 2")
@Feature("Редактирование данных пользователя в Stellar Burgers")
@DisplayName("Редактирование данных пользователя")
public class ChangingDataTest extends MethodsForTest {
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
        String email = generateUniqueEmail();
        String password = generateUniquePassword();
        String name = generateUniqueName();

        Response createUserResponse = createUniqueUser(email, password, name);
        verifyUserCreation(createUserResponse, email, name);
        String accessToken = createUserResponse.jsonPath().getString("accessToken");
        String newEmail = generateUniqueEmail();
        String requestBody = "{\"email\":\"" + newEmail + "\", \"name\":\"" + name + "\", \"password\":\"" + password + "\"}";
        logRequest(accessToken, requestBody);
        Response updateResponse = updateUserEmail(accessToken, newEmail, password, name);

        logResponse(updateResponse);
        validateUpdateResponse(updateResponse, newEmail, name);
        deleteUserByToken(accessToken);
    }

    @Test
    @DisplayName("Изменение пароля с авторизацией")
    @Description("Редактирование пароля авторизованного пользователя. " +
            "ОР - пароль изменен")
    public void changeUserPassWithAuthIsSuccessTest() {
        String email = generateUniqueEmail();
        String password = generateUniquePassword();
        String name = generateUniqueName();
        Response createUserResponse = createUniqueUser(email, password, name);
        verifyUserCreation(createUserResponse, email, name);

        String accessToken = createUserResponse.jsonPath().getString("accessToken");
        String newPassword = generateUniquePassword();
        String requestBody = "{\"email\":\"" + email + "\", \"name\":\"" + name + "\", \"password\":\"" +newPassword + "\"}";
        logRequestPassword(accessToken, requestBody);
        Response updateResponse = updateUserPassword(accessToken, newPassword);
        logResponsePassword(updateResponse);
        validateUpdatePasswordResponse(updateResponse, newPassword);
        deleteUserByToken(accessToken);
    }

     @Test
    @DisplayName("Изменение имени с авторизацией")
    @Description("Редактирование имени авторизованного пользователя. " +
            "ОР - имя изменено")
    public void changeUserNameWithAuthIsSuccess() {
         String email = generateUniqueEmail();
         String password = generateUniquePassword();
         String name = generateUniqueName();
         Response createUserResponse = createUniqueUser(email, password, name);
         verifyUserCreation(createUserResponse, email, name);
         String accessToken = createUserResponse.jsonPath().getString("accessToken");

         String newName = generateUniqueName();
         String requestBody = "{\"email\":\"" + email + "\", \"name\":\"" + newName + "\", \"password\":\"" + password + "\"}";
         logRequestName(accessToken, requestBody);
         Response updateResponse = updateUserName(accessToken, email, password, newName);
         logResponseName(updateResponse);
         validateUpdateNameResponse(updateResponse, newName, email);
         deleteUserByToken(accessToken);
    }

    @Test
    @DisplayName("Изменение email без авторизации")
    @Description("Редактирование email неавторизованного пользователя. " +
            "ОР - email не меняется, сообщение об ошибке")
    public void changeUserEmailWithoutAuthIsSuccess() {
        String newEmail = "four_" + faker.internet().safeEmailAddress();

        Response response = userAPI.updateUser(newEmail, password, name);
        response.then().log().all();

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
        response.then().log().all();

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
        response.then().log().all();

        checkResponse.checkStatusCode(response, SC_UNAUTHORIZED);
        checkResponse.checkSuccessStatus(response, "false");
        checkResponse.checkMessageText(response, "You should be authorised");
    }
}
