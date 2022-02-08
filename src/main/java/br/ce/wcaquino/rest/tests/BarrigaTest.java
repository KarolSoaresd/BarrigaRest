package br.ce.wcaquino.rest.tests;

import br.ce.wcaquino.rest.core.BaseTest;
import br.ce.wcaquino.rest.utils.DataUtils;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.FilterableRequestSpecification;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.requestSpecification;
import static org.hamcrest.Matchers.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING) //ordem alfabética de execução
public class BarrigaTest extends BaseTest {

    private static String CONTA_NAME = "conta" + System.nanoTime();
    private static Integer CONTA_ID;
    private static Integer MOV_ID;

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
    }


    @Test
    public void B_deveIncluirContaComSucesso(){

         CONTA_ID = given()
                 .body("{\"nome\":\""+CONTA_NAME+"\"}") //variavel para massa
        .when()
                .post("/contas")
        .then()
                 .statusCode(201)
                 .extract().path("id")
         ;
    }

    @Test
    public void C_deveAlterarContaComSucesso(){

         given()
                 .pathParam("id", CONTA_ID)
                 .body("{\"nome\":\""+CONTA_NAME+" alterada\"}")
        .when()
                .put("/contas/{id}")
        .then()
                 .statusCode(200)
                 .body("nome", is(CONTA_NAME+" alterada"))
         ;
    }

    @Test
    public void D_naoDeveIncluirContaComNomeRepetido(){

         given()
                 .body("{\"nome\":\"conta qualquer\"}")
        .when()
                .post("/contas")
        .then()
                 .statusCode(400)
                 .body("error", is("Já existe uma conta com esse nome!"))
         ;
    }

    @Test
    public void F_deveInserirMovimentaçãoComSucesso(){

        MOV_ID = given()
                .body(getMovimentacaoValida())
        .when()
                .post("/transacoes")
        .then()
                .statusCode(201)
                .body("descricao", is("Descrição movimentação teste"))
                .body("valor", is("1000.00"))
                .extract().path("id")
         ;
    }

    @Test
    public void G_deveValidarCamposObrigatóriosNaMovimentacao(){

        Movimentacao mov = new Movimentacao();
        mov.setConta_id(1016188);
        mov.setDescricao("Descrição movimentação teste");
        mov.setEnvolvido("Envolvido na movimentação");
        mov.setData_transacao("01/01/2020");
        mov.setData_pagamento("10/05/2020");

        given()
                .body(mov)
                .contentType(ContentType.JSON)
        .when()
                .post("/transacoes")
        .then()
                .statusCode(400)
                .body("msg[0]", is("Valor é obrigatório"))
                .body("msg[1]", is("Valor deve ser um número"))
                .body("msg[2]", is("Situação é obrigatório"))
         ;
    }

    @Test
    public void H_naoDeveInserirMovimentacaoFutura(){

        Movimentacao mov = getMovimentacaoValida();
        mov.setData_transacao(DataUtils.getDataDiferencaDias(2));

        given()
                .body(mov)
                .contentType(ContentType.JSON)
        .when()
                .post("/transacoes")
        .then()
                .statusCode(400)
                .body("msg[0]", is("Data da Movimentação deve ser menor ou igual à data atual"))

         ;
    }

    @Test
    public void I_naoDeveRemoverUmaContaComMovimentacao(){

        given()
                .pathParam("id", CONTA_ID)
        .when()
                .delete("/contas/{id}")
        .then()
                 .statusCode(500)
                 .body("name", is("error"))
                 .body("constraint", is("transacoes_conta_id_foreign"))
         ;
    }

    @Test
    public void J_deveCalcularSaldoContas(){

        given()
        .when()
                .get("/saldo")
        .then()
                 .log().all()
                .statusCode(200)
                .body("find{it.conta_id == "+CONTA_ID+"}.saldo", is("1000.00"))
         ;
    }

    @Test
    public void K_deveRemoverUmaMovimentacao(){

        given()
                .pathParam("id", MOV_ID)
        .when()
                .delete("transacoes/{id}")
        .then()
                .log().all()
                .statusCode(204)
         ;
    }

    private Movimentacao getMovimentacaoValida(){
        Movimentacao mov = new Movimentacao();
        mov.setConta_id(CONTA_ID);
        mov.setDescricao("Descrição movimentação teste");
        mov.setEnvolvido("Envolvido na movimentação");
        mov.setTipo("REC");
        mov.setData_transacao(DataUtils.getDataDiferencaDias(-1));
        mov.setData_pagamento(DataUtils.getDataDiferencaDias(5));
        mov.setValor(1000f);
        mov.setStatus(true);
        return mov;
    }

    @Test
    public void L_naoDeveAcessarAPISemToken(){

        FilterableRequestSpecification req = (FilterableRequestSpecification) requestSpecification;
        req.removeHeader("Authorization");
        given()
                .when()
                .get("/contas")
                .then()
                .statusCode(401)
        ;
    }
}
