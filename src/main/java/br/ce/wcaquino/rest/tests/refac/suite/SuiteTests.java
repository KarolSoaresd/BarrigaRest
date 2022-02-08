package br.ce.wcaquino.rest.tests.refac.suite;

import br.ce.wcaquino.rest.core.BaseTest;
import br.ce.wcaquino.rest.tests.refac.AuthTest;
import br.ce.wcaquino.rest.tests.refac.ContasTest;
import br.ce.wcaquino.rest.tests.refac.MovimentacaoTest;
import br.ce.wcaquino.rest.tests.refac.SaldoTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        ContasTest.class,
        MovimentacaoTest.class,
        SaldoTest.class,
        AuthTest.class
})
public class SuiteTests extends BaseTest {

    @BeforeClass
    public static void login(){
        Map<String, String> login = new HashMap<String, String>();
        login.put("email", "karolinesoares071@gmail.com");
        login.put("senha", "12345678");

        String TOKEN = given()
                .body(login)
                .contentType(ContentType.JSON)
                .when()
                .post("/signin")
                .then()
                .statusCode(200)
                .extract().path("token")
                ;

        RestAssured.requestSpecification.header("Authorization", "JWT " + TOKEN);

        RestAssured.get("/reset").then().statusCode(200);
    }
}
