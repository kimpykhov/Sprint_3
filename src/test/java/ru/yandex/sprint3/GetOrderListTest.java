package ru.yandex.sprint3;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.Response;
import io.restassured.RestAssured;
import org.junit.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;


public class GetOrderListTest {

    String getOrderList = "/api/v1/orders?limit=10&page=0";

    @Test
    @DisplayName("Кейс: Получаю список доступных заказов и  проверяю,что в тело ответа возвращается список заказов.")
    public void testGetOrderList() {
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
        System.out.println("Кейс: Получаю список доступных заказов и  проверяю,что в тело ответа возвращается список заказов.");
        RestAssured.baseURI = "http://qa-scooter.praktikum-services.ru/";

        Response response =given()
                .header("Content-type", "application/json")
                .and()
                .when()
                .get(getOrderList);
        response.then().assertThat().statusCode(200)
                .and().body(notNullValue());
    }
}
