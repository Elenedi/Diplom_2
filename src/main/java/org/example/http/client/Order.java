package org.example.http.client;

import io.restassured.response.Response;
import org.example.*;

public class Order extends Basis {

    public Response createOrder(Order order, String token) {
        return doPostRequest(
                Constants.SERVER_NAME + Constants.ORDERS,
                order,
                "application/json",
                token);
    }

    public Response createOrder(Order order) {
        return doPostRequest(
                Constants.SERVER_NAME + Constants.ORDERS,
                order,
                "application/json"
        );
    }
    public Response getComponentsList() {
        return doGetRequest(
                Constants.SERVER_NAME + Constants.INGREDIENTS);

}
    public Response getOrderList(String token) {
    return doGetRequest(
            Constants.SERVER_NAME + Constants.ORDERS,
            token);
    }

    public Response getAllOrderList() {
    return doGetRequest(
            Constants.SERVER_NAME + Constants.ALL_ORDERS);
    }
}