import com.github.javafaker.Faker;
import io.qameta.allure.*;
import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.junit4.Tag;
import io.restassured.response.Response;
import org.example.request.Components;
import org.example.response.ComponentsResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.apache.http.HttpStatus.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.example.request.Order;
import org.example.operators.OrderOperators;
import org.example.operators.UserOperators;
import org.example.operators.OperatorsCheck;

@Link(url = "https://code.s3.yandex.net/qa-automation-engineer/java/cheatsheets/paid-track/diplom/api-documentation.pdf")
@Tag("creating new order")
@Epic("Диплом 2")
@Feature("Создание нового заказа в Stellar Burgers")
@DisplayName("Создание нового заказа")

public class CreateOrderTest {
    private String email;
    private String password;
    private String name;
    private String token;
    private List<Components> components = new ArrayList<>();
    private final OrderOperators orderAPI = new OrderOperators();
    private final UserOperators userAPI = new UserOperators();
    private final OperatorsCheck checkResponse = new OperatorsCheck();
    private final Faker faker = new Faker();

    @Before
    @Step("Подготовка тестовых данных")
    public void prepareTestData() {
        this.email = faker.internet().safeEmailAddress();
        this.password = faker.letterify("123456787");
        this.name = faker.name().firstName();

        Response response = userAPI.registerUser(email, password, name);
        checkResponse.checkStatusCode(response, SC_OK);

        if (response.getStatusCode() == SC_OK) {
            token = userAPI.getToken(response);
        }

        response = orderAPI.getComponentsList();
        checkResponse.checkStatusCode(response, SC_OK);
        components = response.body().as(ComponentsResponse.class).getData();
    }

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
    public void createOrderWithAuthAndRandomComponents() {
        int numberOfComponents = faker.number().numberBetween(2, 6);
        List<String> selectedComponents = new ArrayList<>();
        for (int i = 0; i < numberOfComponents; i++) {
            Components randomComponent = components.get(faker.number().numberBetween(0, components.size()));
            selectedComponents.add(randomComponent.get_id());
        }
        Response response = orderAPI.createOrder(selectedComponents, token);

        checkResponse.checkStatusCode(response, SC_OK);
        checkResponse.checkSuccessStatus(response, "true");
    }

    @Test
    @DisplayName("Создание заказа без авторизации и случайными ингредиентами")
    @Description("Создание заказа с авторизацией + случайные ингредиенты из списка. " +
            "ОР - заказ успешно создан.")
    public void createOrderWithoutAuthAndRandomComponents() {
        int numberOfComponent = faker.number().numberBetween(2, 6);
        List<String> selectedComponents = new ArrayList<>();
        for (int i = 0; i < numberOfComponent; i++) {
            Components randomComponent = components.get(faker.number().numberBetween(0, components.size()));
            selectedComponents.add(randomComponent.get_id());
        }
        Response response = orderAPI.createOrder((List<String>) new Order(selectedComponents));

        checkResponse.checkStatusCode(response, SC_OK);
        checkResponse.checkSuccessStatus(response, "true");
        //в документации нет информации об ожидаемом коде и статусе, но тест не падает если ожидать 200 ОК
    }

    @Test
    @DisplayName("Создание заказа с авторизацией и без ингредиентов")
    @Description("Создание заказа с авторизацией без ингредиентов. " +
            "ОР - заказ не создан, сообщение об ошибке.")
    public void createOrderWithAuthAndWithoutComponents() {
        List<String> emptyComponents = new ArrayList<>();
        Response response = orderAPI.createOrder(emptyComponents, token);

        checkResponse.checkStatusCode(response, SC_BAD_REQUEST);
        checkResponse.checkSuccessStatus(response, "false");
        checkResponse.checkMessageText(response, "Components' ids must be provided");
    }

    @Test
    @DisplayName("Создание заказа без авторизации и без ингредиентов")
    @Description("Создание заказа без авторизации и ингредиентов. " +
            "ОР - заказ не создан, сообщение об ошибке.")
    public void createOrderWithoutAuthAndWithoutComponents() {
        List<String> emptyComponents = new ArrayList<>();
        Response response = orderAPI.createOrder(emptyComponents);

        checkResponse.checkStatusCode(response, SC_BAD_REQUEST);
        checkResponse.checkSuccessStatus(response, "false");
        checkResponse.checkMessageText(response, "Components' ids must be provided");
    }

    @Test
    @DisplayName("Создание заказа с авторизацией и с некорректным hash ингредиентов")
    @Description("Создание заказа с авторизацией и неверным hash ингредиентов. " +
            "ОР - заказ не создан, сообщение об ошибке.")
    public void createOrderWithoutAuthAndWithWrongHash() {
        List<String> testComponents = Arrays.asList(
                faker.internet().uuid(),
                faker.internet().uuid());
        Response response = orderAPI.createOrder(testComponents, token);

        checkResponse.checkStatusCode(response, SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("Создание заказа без авторизации и с некорректным hash ингредиентов")
    @Description("Создание заказа без авторизации и некорректным hash ингредиентов. " +
            "ОР - заказ не создан, сообщение об ошибке.")
    public void createOrderWithAuthAndWithWrongHash() {
        List<String> testComponents = Arrays.asList(
                faker.internet().uuid(),
                faker.internet().uuid());
        Response response = orderAPI.createOrder(testComponents);

        checkResponse.checkStatusCode(response, SC_INTERNAL_SERVER_ERROR);
    }
}
