package org.example.operators;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import java.util.List;
import org.example.http.client.Order;
import org.example.request.Components;
import org.example.request.*;

public class OrderOperators extends Order {


    @Step("Создание заказа с авторизацией")
    public Response createOrder(List<String> ingredients, String token) {
        return super.createOrder(new Order(ingredients), token);
    }

    @Step("Создание заказа без авторизации")
    public Response createOrder(List<String> ingredients) {
        return super.createOrder(new Order(ingredients));
    }

    @Step("Получить список ингредиентов")
    public Response getIngredientList() {
        return super.getIngredientList();
    }

    @Step("Получить список заказов")
    public Response getOrderList(String token) {
        return super.getOrderList(token);
    }

    @Step("Получить список всех заказов")
    public Response getAllOrderList() {
        return super.getAllOrderList();
    }
}

