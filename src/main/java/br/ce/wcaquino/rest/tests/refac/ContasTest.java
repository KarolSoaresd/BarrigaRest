package br.ce.wcaquino.rest.tests.refac;

import br.ce.wcaquino.rest.core.BaseTest;
import br.ce.wcaquino.rest.utils.BarrigaUtils;
import io.restassured.RestAssured;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

public class ContasTest extends BaseTest {

    @Test
    public void deveIncluirContaComSucesso(){

        given()
                .body("{\"nome\":\"Conta inserida\"}") //variavel para massa
        .when()
                .post("/contas")
        .then()
                .statusCode(201)
        ;
    }

    @Test
    public void deveAlterarContaComSucesso(){

        Integer CONTA_ID = BarrigaUtils.getIdContaPeloNome("Conta para alterar");
        given()
                .pathParam("id", CONTA_ID)
                .body("{\"nome\":\"Conta alterada\"}")
        .when()
                .put("/contas/{id}")
        .then()
                .statusCode(200)
                .body("nome", is("Conta alterada"))
        ;
    }

    @Test
    public void naoDeveIncluirContaComNomeRepetido(){

        given()
                .body("{\"nome\":\"Conta mesmo nome\"}")
        .when()
                .post("/contas")
        .then()
                .statusCode(400)
                .body("error", is("Já existe uma conta com esse nome!"))
        ;
    }

}
