package org.example.operators;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.example.http.client.User;
import org.example.response.UserResponse;
import org.hamcrest.MatcherAssert;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;
import org.example.*;

public class UserOperators extends User {

    @Step("Создание нового пользователя")
    public Response registerUser(String email, String password, String name) {
        return super.registerUser(new User());
    }

    @Step("Получение токена авторизации")
    public String getToken(Response response) {
        String token = response.body().as(UserResponse.class).getAccessToken().split(" ")[1];
        Allure.addAttachment("Код и статус: ", response.getStatusLine());
        Allure.addAttachment("Токен: ", token);
        return token;
    }

    @Step("Логин пользователя")
    public Response loginUser(String email, String password) {
        return super.loginUser(new User());
    }

    @Step("Удаление пользователя")
    public Response deleteUser(String token) {
        return super.deleteUser(token);
    }

    @Step("Обновление данных пользователя")
    public Response updateUser(String email, String password, String name, String token) {
        return super.updateUser(new User(), token);
    }

    @Step("Обновление данных пользователя без токена")
    public Response updateUser(String email, String password, String name) {
        return super.updateUser(new User());
    }

    @Step("Проверка данных пользователя")
    public void checkUserData(Response response, String expectedEmail, String expectedPassword, String expectedName) {
        org.example.request.User currentUser = response.body().as(UserResponse.class).getUser();
        Allure.addAttachment("Новый пользователь", currentUser.toString());

        MatcherAssert.assertThat("Email не совпадает", currentUser.getEmail(), equalTo(expectedEmail));
        MatcherAssert.assertThat("Имя не совпадает", currentUser.getName(), equalTo(expectedName));

        new OperatorsCheck().checkStatusCode(loginUser(expectedEmail, expectedPassword), SC_OK);
    }
}

