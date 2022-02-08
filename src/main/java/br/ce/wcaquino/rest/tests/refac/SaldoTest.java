package br.ce.wcaquino.rest.tests.refac;

import br.ce.wcaquino.rest.core.BaseTest;
import io.restassured.RestAssured;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

public class SaldoTest extends BaseTest {

    public Integer getIdContaPeloNome(String nome){
        return RestAssured.get("/contas?nome="+nome).then().extract().path("id[0]");
    }

    public Integer getIdPelaDescrição(String desc){
        return RestAssured.get("/transacoes?descricao="+desc).then().extract().path("id[0]");
    }

    @Test
    public void deveCalcularSaldoContas(){

        Integer CONTA_ID = getIdContaPeloNome("Conta para saldo");

        given()
        .when()
                .get("/saldo")
        .then()
                .statusCode(200)
                .body("find{it.conta_id == "+CONTA_ID+"}.saldo", is("534.00"))
        ;
    }
}
