package ru.yandex.sprint3;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.Response;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import java.util.List;
import io.restassured.RestAssured;



@RunWith(Parameterized.class)
public class CreateOrderParamTest {

    String makeOrder = "/api/v1/orders";
    @Step("Метод setUP:Base URL - http://qa-scooter.praktikum-services.ru/")
    @Before
    public void setUp() {
        RestAssured.baseURI = "http://qa-scooter.praktikum-services.ru/";
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }


    private final List<String> color;
    private final Matcher<Object> expected;

    public CreateOrderParamTest(List<String> color, Matcher<Object> expected) {
        this.color = color;
        this.expected = expected;

    }

    @Step("Метод в котором я проверяю разные значения Color")
    @Parameterized.Parameters
    public static Object[][] getData() {
        return new Object[][]{
                {List.of("BLACK", "GREY"), notNullValue()},
                {List.of("BLACK"), notNullValue()},
                {List.of("GREY"), notNullValue()},
                {null, notNullValue()}

        };
    }


    @Test
    @DisplayName("Создание заказа. Позитивный сценарий.")
    @Description("Проверяем,что заказ создается с разными значениями поля color.")
    public void createOrder() {
        Order order = new Order(color);

        Response response =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(order)
                        .when()
                        .post(makeOrder);
        response.then().assertThat().statusCode(201).and()
                .body("track", expected);
    }
}