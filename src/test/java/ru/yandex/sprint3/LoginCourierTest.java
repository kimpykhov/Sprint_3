package ru.yandex.sprint3;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.Response;
import io.restassured.RestAssured;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;


import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;

public class LoginCourierTest {

    ScooterRegisterCourier createCourier = new ScooterRegisterCourier();
    private AuthCourier  currentCourier;
    String loginCourier = "/api/v1/courier/login";
    String deleteCourier = "/api/v1/courier/{curierId}";


    @Step("Метод setUP:Base URL - http://qa-scooter.praktikum-services.ru/")
    @Before
    public void setUp() {
        RestAssured.baseURI = "http://qa-scooter.praktikum-services.ru/";
        currentCourier=getCorrectAuth();
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }

    @Step("Метод @AFTER :Проверяю if (currentCourier.id > 0) тогда   DELETE /api/v1/courier/{curierId}")
    @After
    public void tearDown() {
        if (currentCourier.id > 0) {
            Response response =
                    given()
                            .delete(deleteCourier, currentCourier.id);

            System.out.println("Удалил курьера(After метод) id = " + currentCourier.id);
            System.out.println(response.getStatusCode());
            System.out.println("----------------------------------------------------------------");
            currentCourier=null;

        }
    }

    @Test
    @DisplayName("Кейс: Проверяю что Курьер может авторизоваться")
    @Description("Отправляю POST /api/v1/courier/login")
    public void testCourierLogin() {
        int id=given()
                .header("Content-type", "application/json")
                .and()
                .body(currentCourier)
                .when()
                .post(loginCourier)
                .then().assertThat().statusCode(200)
                .and().extract().body().path("id");
        assertEquals(id,currentCourier.id);
        System.out.println("Курьер авторизовался." + "\n" + "Айди курьера = " + currentCourier.id);

    }

    @Test
    @DisplayName("Кейс: для авторизации нужно передать все обязательные поля")
    @Description("Отправляю все обязательные поля")
    public void testCourierAuthWithRequiredFields() {

        given()
                .header("Content-type", "application/json")
                .and()
                .body(currentCourier)
                .when()
                .post(loginCourier)
                .then().assertThat().statusCode(200)
                .and().extract().body().path("id");
        System.out.println("Курьер авторизовался." + "\n" + "Айди курьера = " + currentCourier.id);
    }

    @Test
    @DisplayName("Кейс: система вернёт ошибку, если неправильно указать логин или пароль")
    @Description("Указываю некорректный Пароль.Должен вернуться статус код 404 с телом (message:Учетная запись не найдена)")
    public void testSystemErrorWithIncorrectPassword() {

        Response response =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(new AuthCourier(currentCourier.login,"****WrongPassword****"))
                        .when()
                        .post(loginCourier);
        response.then().assertThat().statusCode(404)
                .and().body("message", is("Учетная запись не найдена"));
        System.out.println(response.getBody().asString() + " Авторизация курьера");


    }

    @Test
    @DisplayName("Кейс: система вернёт ошибку, если неправильно указать логин или пароль")
    @Description("Указываю некорректный Логин.Должен вернуться статус код 404 с телом (message:Учетная запись не найдена)")
    public void testSystemErrorWithIncorrectLogin() {

        Response response =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(new AuthCourier("****WrongLogin****",currentCourier.password))
                        .when()
                        .post(loginCourier);
        response.then().assertThat().statusCode(404)
                .and().body("message", is("Учетная запись не найдена"));
        System.out.println(response.getBody().asString() + " Авторизация курьера");

    }

    @Test
    @DisplayName("Кейс: если какого-то поля нет, запрос возвращает ошибку")
    @Description("Оставляю поле  Логин пустым .Должен вернуться статус код 404 с телом (message:Недостаточно данных для входа)")
    public void testAuthWithNullField() {

        Response response =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(new AuthCourier("",currentCourier.password))
                        .when()
                        .post(loginCourier);
        response.then().assertThat().statusCode(400)
                .and().body("message", is("Недостаточно данных для входа"));
        System.out.println(response.getBody().asString() + " Авторизация курьера");

    }

    @Test
    @DisplayName("Кейс:если авторизоваться под несуществующим пользователем, запрос возвращает ошибку")
    @Description("Указываю несуществующие Логин и Пароль.Должен вернуться статус код 404 с телом (message:Учетная запись не найдена)")
    public void testNonExistUser() {

        Response response =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(new AuthCourier("NonExsistUser3000","NonExsistPassword3000"))
                        .when()
                        .post(loginCourier);
        response.then().assertThat().statusCode(404)
                .and().body("message", is("Учетная запись не найдена"));
        System.out.println(response.getBody().asString() + " Авторизация курьера");

    }

    @Test
    @DisplayName("Кейс:Успешный запрос возвращает id")
    @Description("Получаю id курьера")
    public void testLoginCourierReturnId() {

        int id=given()
                .header("Content-type", "application/json")
                .and()
                .body(currentCourier)
                .when()
                .post(loginCourier)
                .then().assertThat().statusCode(200)
                .and().extract().body().path("id");
        assert id != 0;
        assertEquals(id,currentCourier.id);
        System.out.println("Курьер авторизовался." + "\n" + "Айди курьера = " + currentCourier.id);

    }

    @Test
    @DisplayName("Кейс: система вернёт ошибку, если  не указать логин или пароль")
    @Description("Указываю null значение для пароля.")
    public void testSystemErrorWithNullValuePassword() {

        Response response =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(new AuthCourier(currentCourier.login,null))
                        .when()
                        .post(loginCourier);
        response.then().assertThat().statusCode(404)
                .and().body("message", is("Недостаточно данных для входа"));
    }

    @Step("Генерирую случайного курьера, что бы использовать в тестах и получаю его id.Чтобы удалять после выполнения тестов" )
    private AuthCourier getCorrectAuth() {
        ArrayList<String> authCourier = createCourier.registerNewCourierAndReturnLoginPassword();
        String myLoginCourier = authCourier.get(0);
        String myPasswordCourier = authCourier.get(1);
        AuthCourier auth = new AuthCourier(myLoginCourier, myPasswordCourier);
        saveId(auth);
        System.out.println(auth.id);
        return auth;
    }
    @Step("Метод получения id курьера" )
    private void saveId(AuthCourier courier){
        int id= given()
                .header("Content-type", "application/json")
                .and()
                .body(courier)
                .when()
                .post(loginCourier)
                .then().assertThat().statusCode(200)
                .and().extract().body().path("id");
        courier.id=id;
    }
}