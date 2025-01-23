import com.github.javafaker.Faker;
import io.qameta.allure.*;
import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.junit4.Tag;
import io.restassured.response.Response;
import methods.MethodsForTest;
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
public class GetOrdersListTest extends MethodsForTest {
    private String email;
    private String password;
    private String name;
    private String token;
    private final OrderOperators orderAPI = new OrderOperators();
    private final UserOperators userAPI = new UserOperators();
    private final OperatorsCheck checkResponse = new OperatorsCheck();
    private final Faker faker = new Faker();


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
        response.then().log().all();

        checkResponse.checkStatusCode(response, SC_OK);
        checkResponse.checkSuccessStatus(response, "true");
    }

    @Test
    @DisplayName("Получение списка заказов авторизованного пользователя")
    @Description("Получение списка заказов авторизованного пользователя. " +
            "ОР - список заказов получен.")
    public void getAuthUsersOrdersIsSuccessTest() {
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
        try {
            Response ordersResponse = MethodsForTest.getUserOrders(accessToken);
            MethodsForTest.verifyUserOrdersRetrieval(ordersResponse);
        } finally {
            deleteUserByToken(accessToken);
        }
    }

    @Test
    @DisplayName("Получение списка заказов неавторизованного пользователя")
    @Description("Получение списка заказов неавторизованного пользователя. " +
            "ОР - список заказов не получен, сообщение об ошибке.")
    public void getNotAuthUsersOrdersIsSuccessTest() {
        Response ordersResponse = MethodsForTest.getUserOrdersWithoutAuthorization();
        MethodsForTest.verifyUnauthorizedResponse(ordersResponse);
    }
}
