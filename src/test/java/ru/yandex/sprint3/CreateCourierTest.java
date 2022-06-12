package ru.yandex.sprint3;

import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.Response;
import io.restassured.RestAssured;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.io.File;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.Description;
import io.qameta.allure.Step;


public class CreateCourierTest {

    String loginCourier = "/api/v1/courier/login";
    String createCourier = "/api/v1/courier";
    String deleteCourier = "/api/v1/courier/{curierId}";


    @Step("Метод setUP:Base URL - http://qa-scooter.praktikum-services.ru/")
    @Before
    public void setUp() {
        RestAssured.baseURI = "http://qa-scooter.praktikum-services.ru/";
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());

    }
    @Step("Метод @AFTER :После каждого теста:POST /api/v1/courier/login , если id получен далее выполняю DELETE /api/v1/courier/{curierId}")
    @After
    public void tearDown() {
        File json = new File("src/test/resources/loginCourier.json");

        Response response =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(json)
                        .when()
                        .post(loginCourier);

        if (response.then().extract().statusCode() ==200) {
            Object courierId = response.then().extract().body().path("id");
            System.out.println("Получил Айди курьера = " + courierId);
            given()
                    .delete(deleteCourier, courierId)
                    .then().assertThat().statusCode(200)
                    .and().body("ok", is(true));
            System.out.println("Удалил курьера(After метод) id = " + courierId);
            System.out.println(response.getStatusCode());
            System.out.println("----------------------------------------------------------------");

        } else {

            System.out.println("Айди курьера не был получен  " );
        }

    }

    @Test
    @DisplayName("Проверяю что курьера можно создать")
    @Description("Обычное создание курьера")
    @Step("Send POST request /api/v1/courier")
    public void testCourierCanCreate() {

        File json = new File("src/test/resources/newCourier.json");

        Response response =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(json)
                        .when()
                        .post(createCourier);
        response.then().assertThat()
                .statusCode(201)
                .and().body("ok", is(true));

    }

    @Test
    @DisplayName("Проверяю что нельзя создать двух одинаковых курьеров")
    @Description("В этом тесте я создаю курьера, а затем пробую создать курьера еще раз с теми же даными, что и у первого.")
    public void testCantCreateSameCourier() {

        File json = new File("src/test/resources/newCourier.json");
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(json)
                        .when()
                        .post(createCourier);
        response.then().assertThat()
                .statusCode(201)
                .and().body("ok", is(true));
        System.out.println(" Создал первого курьера");



        File jsonTwo = new File("src/test/resources/newCourier.json");
        Response responseTwo =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(jsonTwo)
                        .when()
                        .post(createCourier);
        responseTwo.then().assertThat()
                .statusCode(409)
                .and().body(notNullValue());
        System.out.println("Попытка создать второго курьера с теми же данными");


    }

    @Test
    @DisplayName("Кейс: Чтобы создать курьера, нужно передать в ручку все обязательные поля")
    @Description("Все обязательные поля передаются в ручку")
    public void testCourierCreatingWithRequiredFields() {

        File json = new File("src/test/resources/newCourier.json");
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(json)
                        .when()
                        .post(createCourier);
        response.then().assertThat()
                .statusCode(201)
                .and().body("ok", is(true));

    }

    @Test
    @DisplayName("Кейс: Запрос возвращает правильный код ответа")
    @Description("Здесь должен вернуться код 201")
    public void testCourierCreateReturnCorrectSatusCode() {

        File json = new File("src/test/resources/newCourier.json");
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(json)
                        .when()
                        .post(createCourier);
        response.then().assertThat()
                .statusCode(201)
                .and().body("ok", is(true));

    }

    @Test
    @DisplayName("Кейс: успешный запрос возвращает ok: true")
    @Description("Здесь должен вернуться код 201 с телом ok: true")
    public void testCourierCreatSuccessBodyOkTrue() {

        File json = new File("src/test/resources/newCourier.json");
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(json)
                        .when()
                        .post(createCourier);
        response.then().assertThat()
                .statusCode(201)
                .and().body("ok", is(true));

    }

    @Test
    @DisplayName("Кейс: Если одного из полей нет, запрос возвращает ошибку")
    @Description("Здесь должен вернуться код 400 с телом (message:Недостаточно данных для создания учетной записи")
    public void testCourierCreatReturnErrorIfNoOneField() {

        File json = new File("src/test/resources/createCourierWithoutField.json");
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(json)
                        .when()
                        .post(createCourier);
        response.then().assertThat()
                .statusCode(400)
                .and().body("message", is("Недостаточно данных для создания учетной записи"));

    }


    @Test
    @DisplayName("Кейс: если создать пользователя с логином, который уже есть, возвращается ошибка.")
    @Description("Сначала создается первый пользователь, Затем создается второй пользователь с тем же логином но другим именем пользователя. Должен вернуться код 409 с телом (message:Этот логин уже используется")
    public void testCourierCreatReturnErrorIfLoginIsCreate() {

        File json = new File("src/test/resources/newCourier.json");
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(json)
                        .when()
                        .post(createCourier);
        response.then().assertThat()
                .statusCode(201)
                .and().body("ok", is(true));
        System.out.println(" Создал первого курьера");


        File jsonTwo = new File("src/test/resources/sameLoginCourier.json");
        Response responseTwo =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(jsonTwo)
                        .when()
                        .post(createCourier);
        responseTwo.then().assertThat()
                .statusCode(409)
                .and().body("message", is("Этот логин уже используется. Попробуйте другой."));
        System.out.println("Попытка создать второго курьера с тем же Логином");
    }
}