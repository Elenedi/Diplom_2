import com.github.javafaker.Faker;
import io.qameta.allure.*;
import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.junit4.Tag;
import io.restassured.response.Response;
import methods.MethodsForTest;
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

public class RegisterUserTest extends MethodsForTest {
    private String email;
    private String password;
    private String name;
    private ArrayList<String> tokens = new ArrayList<>();
    private final OrderOperators orderAPI = new OrderOperators();
    private final UserOperators userAPI = new UserOperators();
    private final OperatorsCheck checkResponse = new OperatorsCheck();
    private final Faker faker = new Faker();
    private final MethodsForTest userChanges = new MethodsForTest();
    private MethodsForTest methodsUserLogin = new MethodsForTest();

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
        String email = generateUniqueEmail();
        String password = generateUniquePassword();
        String name = generateUniqueName();

        Response response = createUniqueUser(email, password, name);
    }

    @Test
    @DisplayName("Логин пользователя без email")
    @Description("Тест API логин пользователя без email. " +
            "ОР - логин не зарегистрирован")
    public void loginUserWithoutEmailIsFailedTest() {
        String password = generateUniquePassword();
        String name = generateUniqueName();

        Response response = createUniqueUserWithoutEmail(password, name);
        userChanges.verifyUserCreationFailureEmail(response, "Email, password and name are required fields");
    }

    @Test
    @DisplayName("Логин пользователя без пароля")
    @Description("Логин пользователя без пароля. " +
            "ОР - логин не зарегистрирован")
    public void loginUserWithoutPasswordIsFailedTest() {
        String email = generateUniqueEmail();
        String name = generateUniqueName();

        Response response = createUniqueUserWithoutPassword(email, name);
        userChanges.verifyUserCreationFailurePassword(response);
    }

    @Test
    @DisplayName("Логин пользователя c некорректным email")
    @Description("Логин пользователя с некорректным email. " +
            "ОР - логин не зарегистрирован")
    public void loginUserWithIncorrectEmailIsFailedTest() {
        String email = generateUniqueEmail();
        String password = generateUniquePassword();
        String name = generateUniqueName();

        Response createUserResponse = createUniqueUser(email, password, name);
        verifyUserCreation(createUserResponse, email, name);

        String accessToken = createUserResponse.jsonPath().getString("accessToken");
        String invalidEmail = "invalid" + email;
        Response loginResponse = methodsUserLogin.loginWithUser(invalidEmail, password, name);
        MethodsForTest.verifyLoginWithInvalidCredentials(loginResponse);
        deleteUserByToken(accessToken);
    }

    @Test
    @DisplayName("Логин пользователя c некорректным паролем")
    @Description("Логин пользователя с некорректным паролем. " +
            "ОР - логин не зарегистрирован")
    public void loginUserWithIncorrectPassIsFailedTest() {
        String email = generateUniqueEmail();
        String password = generateUniquePassword();
        String name = generateUniqueName();

        Response createUserResponse = createUniqueUser(email, password, name);
        verifyUserCreation(createUserResponse, email, name);

        String accessToken = createUserResponse.jsonPath().getString("accessToken");
        String invalidPassword = "wrong" + password;
        Response loginResponse = methodsUserLogin.loginWithUser(email, invalidPassword, name);
        MethodsForTest.verifyLoginWithInvalidCredentials(loginResponse);
        deleteUserByToken(accessToken);
    }
}
