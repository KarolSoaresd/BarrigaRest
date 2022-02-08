package br.ce.wcaquino.rest.tests.refac;

import br.ce.wcaquino.rest.core.BaseTest;
import br.ce.wcaquino.rest.tests.Movimentacao;
import br.ce.wcaquino.rest.utils.BarrigaUtils;
import br.ce.wcaquino.rest.utils.DataUtils;
import io.restassured.http.ContentType;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

public class MovimentacaoTest extends BaseTest {

    private Movimentacao getMovimentacaoValida(){
        Movimentacao mov = new Movimentacao();
        mov.setConta_id(BarrigaUtils.getIdContaPeloNome("Conta para movimentacoes"));
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
    public void deveInserirMovimentaçãoComSucesso(){

        Movimentacao mov = getMovimentacaoValida();

        given()
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
    public void deveValidarCamposObrigatóriosNaMovimentacao(){

        Movimentacao mov = new Movimentacao();
        mov.setConta_id(BarrigaUtils.getIdContaPeloNome("Conta para movimentacoes"));
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
    public void naoDeveRemoverUmaContaComMovimentacao(){

        Integer CONTA_ID = BarrigaUtils.getIdContaPeloNome("Conta com movimentacao");
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
    public void deveRemoverUmaMovimentacao(){

        Integer MOV_ID = BarrigaUtils.getIdPelaDescrição("Movimentacao para exclusao");

        given()
                .pathParam("id", MOV_ID)
        .when()
                .delete("transacoes/{id}")
        .then()
                .log().all()
                .statusCode(204)
        ;
    }
}
