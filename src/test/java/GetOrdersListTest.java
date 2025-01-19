import com.github.javafaker.Faker;
import io.qameta.allure.*;
import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.junit4.Tag;
import io.restassured.response.Response;
import org.example.operators.OperatorsCheck;
import org.example.operators.OrderOperators;
import org.example.operators.UserOperators;
import org.example.request.Components;
import org.example.response.ComponentsResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;
import static org.apache.http.HttpStatus.*;

@Link(url = "https://code.s3.yandex.net/qa-automation-engineer/java/cheatsheets/paid-track/diplom/api-documentation.pdf")
@Tag("get order list")
@Epic("Диплом 2")
@Feature("Получение списка заказов в Stellar Burgers")
@DisplayName("Получение списка заказов")
public class GetOrdersListTest {
    private String email;
    private String password;
    private String name;
    private String token;
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
        checkResponse.checkStatusCode(response, SC_OK);

        if (response.getStatusCode() == SC_OK) {
            token = userAPI.getToken(response);
        }

        response = orderAPI.getComponentsList();
        checkResponse.checkStatusCode(response, SC_OK);
        List<Components> components = response.body().as(ComponentsResponse.class).getData();

        int numberOfComponents = faker.number().numberBetween(2, 6);
        List<String> selectedComponents = new ArrayList<>();
        for (int i = 0; i < numberOfComponents; i++) {
            Components randomComponent = components.get(faker.number().numberBetween(0, components.size()));
            selectedComponents.add(randomComponent.get_id());
        }

        response = orderAPI.createOrder(selectedComponents, token);
        checkResponse.checkStatusCode(response, SC_OK);
    }

    @After
    @Step("Удаление данных после теста")
    public void clearAfterTests() {
        if (token != null) {
            checkResponse.checkStatusCode(userAPI.deleteUser(token), SC_ACCEPTED);
        }
    }

    @Test
    @DisplayName("Получение всех заказов")
    @Description("Получение списка заказов. " +
            "ОР - список заказов получен.")
    public void getAllOrdersIsSuccessTest() {
        Response response = orderAPI.getAllOrderList();

        checkResponse.checkStatusCode(response, SC_OK);
        checkResponse.checkSuccessStatus(response, "true");
    }

    @Test
    @DisplayName("Получение списка заказов авторизованного пользователя")
    @Description("Получение списка заказов авторизованного пользователя. " +
            "ОР - список заказов получен.")
    public void getAuthUsersOrdersIsSuccessTest() {
        Response response = orderAPI.getOrderList(token);
        checkResponse.checkStatusCode(response, SC_OK);
        checkResponse.checkSuccessStatus(response, "true");
    }

    @Test
    @DisplayName("Получение списка заказов неавторизованного пользователя")
    @Description("Получение списка заказов неавторизованного пользователя. " +
            "ОР - список заказов не получен, сообщение об ошибке.")
    public void getNotAuthUsersOrdersIsSuccessTest() {
        Response response = orderAPI.getOrderList("");
        checkResponse.checkStatusCode(response, SC_UNAUTHORIZED);
        checkResponse.checkSuccessStatus(response, "false");
        checkResponse.checkMessageText(response,"You should be authorised");
    }
}
