package org.example.operators;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.example.http.client.UserApi;
import org.example.request.UserModel;
import org.example.response.UserResponse;
import org.hamcrest.MatcherAssert;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;

public class UserOperators extends UserApi {

    @Step("Создание нового пользователя")
    public Response registerUser(String email, String password, String name) {
        return super.registerUser(email, password, name);
    }

    @Step("Получение токена авторизации")
    public String getToken(Response response) {
        checkStatusCode(response, SC_OK);
        String token = response.body().as(UserResponse.class).getAccessToken().split(" ")[1];
        Allure.addAttachment("Код и статус: ", response.getStatusLine());
        Allure.addAttachment("Токен: ", token);
        return token;
    }

    @Step("Логин пользователя")
    public Response loginUser(String email, String password) {
        return super.loginUser(email, password);
    }

    @Step("Удаление пользователя")
    public Response deleteUser(String token) {
        return super.deleteUser(token);
    }

    @Step("Обновление данных пользователя")
    public Response updateUser(String email, String password, String name, String token) {
        return super.updateUser(new UserApi(), token);
    }

    @Step("Обновление данных пользователя без токена")
    public Response updateUser(String email, String password, String name) {
        return super.updateUser(new UserApi());
    }

    @Step("Проверка данных пользователя")
    public void checkUserData(Response response, String expectedEmail, String expectedPassword, String expectedName) {
        checkStatusCode(response, SC_OK);
        UserModel currentUser = response.body().as(UserResponse.class).getUser();
        Allure.addAttachment("Новый пользователь", currentUser.toString());

        MatcherAssert.assertThat("Email не совпадает", currentUser.getEmail(), equalTo(expectedEmail));
        MatcherAssert.assertThat("Имя не совпадает", currentUser.getName(), equalTo(expectedName));
        checkStatusCode(loginUser(expectedEmail, expectedPassword), SC_OK);
    }

    private void checkStatusCode(Response response, int expectedStatusCode) {
        MatcherAssert.assertThat("Неверный код ответа", response.getStatusCode(), equalTo(expectedStatusCode));
    }
}

