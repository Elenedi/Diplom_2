package org.example.http.client;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import static io.restassured.RestAssured.given;

public class Basis {
    private RequestSpecification basisRequest() {
        return new RequestSpecBuilder()
                .addFilter(new AllureRestAssured())
                .setRelaxedHTTPSValidation()
                .build();
    }

    private RequestSpecification basisRequest(String contentType) {
        return new RequestSpecBuilder()
                .addHeader("Content-type", contentType)
                .addFilter(new AllureRestAssured())
                .setRelaxedHTTPSValidation()
                .build();
    }

    public Response doPostRequest(String url, Object requestBody,
                                  String contentType) {
        return given(this.basisRequest(contentType))
                .body(requestBody)
                .log().all()
                .when()
                .post(url);
    }

    public Response doPostRequest(String url, Object requestBody,
                                  String contentType, String token) {
        return given(this.basisRequest(contentType))
                .auth().oauth2(token)
                .body(requestBody)
                .log().all()
                .when()
                .post(url);
    }

    public Response doGetRequest(String url) {
        return given(this.basisRequest())
                .log().all()
                .get(url);
    }

    public Response doGetRequest(String url, String token) {
        return given(this.basisRequest())
                .auth().oauth2(token)
                .log().all()
                .when()
                .get(url);
    }

    public Response doDeleteRequest(String url, String token) {
        return given(this.basisRequest())
                .auth().oauth2(token)
                .log().all()
                .delete(url);
    }

    public Response doPatchRequest(String url, Object requestBody,
                                   String contentType, String token) {
        return given(this.basisRequest(contentType))
                .auth().oauth2(token)
                .body(requestBody)
                .log().all()
                .when()
                .patch(url);
    }

    public Response doPatchRequest(String url,Object requestBody,
                                   String contentType) {
        return given(this.basisRequest(contentType))
                .body(requestBody)
                .log().all()
                .when()
                .patch(url);
    }
}
