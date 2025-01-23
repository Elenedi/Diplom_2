import com.github.javafaker.Faker;
import io.qameta.allure.*;
import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.junit4.Tag;
import io.restassured.response.Response;
import methods.MethodsForTest;
import org.example.request.Components;
import org.example.response.ComponentsResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.apache.http.HttpStatus.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.example.request.OrderModel;
import org.example.operators.OrderOperators;
import org.example.operators.UserOperators;
import org.example.operators.OperatorsCheck;

@Link(url = "https://code.s3.yandex.net/qa-automation-engineer/java/cheatsheets/paid-track/diplom/api-documentation.pdf")
@Tag("creating new order")
@Epic("Диплом 2")
@Feature("Создание нового заказа в Stellar Burgers")
@DisplayName("Создание нового заказа")

public class CreateOrderTest extends MethodsForTest {
    private String email;
    private String password;
    private String name;
    private String token;
    private List<Components> components = new ArrayList<>();
    private final OrderOperators orderAPI = new OrderOperators();
    private final UserOperators userAPI = new UserOperators();
    private final OperatorsCheck checkResponse = new OperatorsCheck();
    private final Faker faker = new Faker();
    private MethodsForTest methodsUserLogin = new MethodsForTest();

    @After
    @Step("Удаление данных после теста")
    public void clearAfterTests() {
        if (token != null) {
            checkResponse.checkStatusCode(userAPI.deleteUser(token), SC_ACCEPTED);
        }
    }

    @Test
    @DisplayName("Создание заказа с авторизацией и случайными ингредиентами")
    @Description("Cоздание заказа с авторизацией + случайные ингредиенты из списка. " +
            "ОP - заказ успешно создан.")
    public void createOrderWithAuthAndRandomComponentsTest() {
        String email = generateUniqueEmail();
        String password = generateUniquePassword();
        String name = generateUniqueName();

        Response createUserResponse = createUniqueUser(email, password, name);
        createUserResponse.then().statusCode(200);
        Response loginResponse = loginWithUser(email, password, name);
        loginResponse.then().statusCode(200);
        String accessToken = loginResponse.jsonPath().getString("accessToken");

        Response orderResponse = MethodsForTest.createOrderWithIngredients(accessToken);
        orderResponse.then().log().all();
        MethodsForTest.verifyOrderCreation(orderResponse);
        deleteUserByToken(accessToken);
    }

    @Test
    @DisplayName("Создание заказа без авторизации и случайными ингредиентами")
    @Description("Создание заказа с авторизацией + случайные ингредиенты из списка. " +
            "ОР - заказ успешно создан.")
    public void createOrderWithoutAuthAndRandomComponentsTest() {
        Response orderResponse = MethodsForTest.createOrderWithoutAuthorization();
        orderResponse.then().log().all();
        MethodsForTest.verifyOrderCreationUnauthorized(orderResponse);
    }

    @Test
    @DisplayName("Создание заказа с авторизацией и без ингредиентов")
    @Description("Создание заказа с авторизацией без ингредиентов. " +
            "ОР - заказ не создан, сообщение об ошибке.")
    public void createOrderWithAuthAndWithoutComponentsTest() {
        String email = generateUniqueEmail();
        String password = generateUniquePassword();
        String name = generateUniqueName();

        Response createUserResponse = createUniqueUser(email, password, name);
        createUserResponse.then().statusCode(200);
        Response loginResponse = loginWithUser(email, password, name);
        loginResponse.then().statusCode(200);

        String accessToken = loginResponse.jsonPath().getString("accessToken");
        Response orderResponse = MethodsForTest.createOrderWitNoIngredients(accessToken);
        orderResponse.then().log().all();
        MethodsForTest.verifyOrderCreationNoIngredients(orderResponse);
        deleteUserByToken(accessToken);
    }

    @Test
    @DisplayName("Создание заказа без авторизации и без ингредиентов")
    @Description("Создание заказа без авторизации и ингредиентов. " +
            "ОР - заказ не создан, сообщение об ошибке.")
    public void createOrderWithoutAuthAndWithoutComponentsTest() {
        Response orderResponseWithoutAuthorization = MethodsForTest.createOrderWithoutAuthorizationAndIngredients();
        orderResponseWithoutAuthorization.then().log().all();
        MethodsForTest.verifyOrderCreationNoIngredientsUnauthorized(orderResponseWithoutAuthorization);
        Response orderResponseWithoutIngredients = MethodsForTest.createOrderWithoutAuthorizationAndIngredients();
        MethodsForTest.verifyOrderCreationNoAuthorizedAndNoIngredients(orderResponseWithoutIngredients);
    }

    @Test
    @DisplayName("Создание заказа с неверным hash ингредиентов")
    @Description("Создание заказа с неверным hash ингредиентов" +
            "ОР - заказ не создан, сообщение об ошибке.")
    public void createOrderWithInvalidIngredientsHash() {
        String email = generateUniqueEmail();
        String password = generateUniquePassword();
        String name = generateUniqueName();

        Response createUserResponse = createUniqueUser(email, password, name);
        createUserResponse.then().statusCode(200);
        Response loginResponse = loginWithUser(email, password, name);
        loginResponse.then().statusCode(200);
        String accessToken = loginResponse.jsonPath().getString("accessToken");
        try {
            Response orderResponse = MethodsForTest.createOrderWithInvalidIngredientsHash(accessToken);
            orderResponse.then().log().all();

            MethodsForTest.verifyOrderCreationInvalidIngredientsHash(orderResponse);
            orderResponse.then().statusCode(400);
        } catch (Exception e) {
            System.out.println("Ошибка при создании заказа: " + e.getMessage());
        } finally {

            deleteUserByToken(accessToken);
        }
    }
}