package br.edu.ifsul.controle;

import br.edu.ifsul.servico.correios.CResultado;
import br.edu.ifsul.servico.correios.CalcPrecoPrazoWS;
import br.edu.ifsul.servicopessoa.Pessoa;
import br.edu.ifsul.servicopessoa.ServicoPessoaService;
import br.edu.ifsul.util.Util;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 *
 * @author devuser
 */
@Named(value = "controlePessoa")
@SessionScoped
public class ControlePessoa implements Serializable{
    
    private List<Pessoa> lista = new ArrayList<>();    
    private Pessoa objeto;
    private Boolean editando;
    
    private ServicoPessoaService dao;
    private CalcPrecoPrazoWS correios;
    
    public ControlePessoa(){
        dao = new ServicoPessoaService();
        correios = new CalcPrecoPrazoWS();
        editando = false;
    }
    
    public String listar(){
        editando = false;
        return "/pessoa/listar?faces-redirect=true";
    }
    
    public String novo(){
        objeto = new Pessoa();
        objeto.setId(0);
        editando = true;
        return "/pessoa/form?faces-redirect=true";
    }
    
    public String alterar(Pessoa objeto){
        this.objeto = objeto;
        return "/pessoa/form?faces-redirect=true";
    }
    
    public void excluir(Integer id){
        dao.getServicoPessoaPort().remover(id);
    }
    
    public String salvar(){
        objeto.setCepOrigem(objeto.getCepOrigem().replace("-", ""));
        objeto.setCepDestino(objeto.getCepDestino().replace("-", ""));
        if(objeto.getId() == 0){
            dao.getServicoPessoaPort().inserir(objeto);
            Util.mensagemInformacao("Persistência realizada com sucesso");
        }else{
            dao.getServicoPessoaPort().alterar(objeto);
            Util.mensagemInformacao("Update realizada com sucesso");
        }
        
        return "/pessoa/listar?faces-redirect=true";
    }

    public List<Pessoa> getLista() {
        this.lista = dao.getServicoPessoaPort().listaPessoas();
        return this.lista;
    }

    public Pessoa getObjeto() {
        return objeto;
    }

    public void setObjeto(Pessoa objeto) {
        this.objeto = objeto;
    }

    public Boolean getEditando() {
        return editando;
    }

    public void setEditando(Boolean editando) {
        this.editando = editando;
    }
    
    public void calcular(){
        this.calculaDataMaxima();
        this.calculaPreco();
    }
    
    public void calculaDataMaxima(){
        objeto.setPrazoEntrega(correios.getCalcPrecoPrazoWSSoap()
                .calcPrazo(objeto.getFrete(), objeto.getCepOrigem(), objeto.getCepDestino()).getServicos().getCServico().get(0).getDataMaxEntrega());
        
    }
    
    public void calculaPreco(){
        CResultado res = correios.getCalcPrecoPrazoWSSoap()
                .calcPreco("", "", objeto.getFrete(), objeto.getCepOrigem(), objeto.getCepDestino(),
                        objeto.getPeso(), //PESO MIN 20
                        objeto.getFormato(),  // 1 CAIXA, 2 ROLO PRISMA; 3 ENVELOPE
                        new BigDecimal(objeto.getComprimento()), //COMPRIMENTO MIN 16
                        new BigDecimal(objeto.getAltura()), //ALTURA MIN 20
                        new BigDecimal(objeto.getLargura()), //LARGURA MIN 11
                        new BigDecimal(objeto.getDiametro()),  //DIAMETRO MIN 20
                        objeto.getMaoPropria(),    //SERVIÇO ADICIONAL MÃO PRÓPRIA
                        new BigDecimal(objeto.getValorDeclarado()), // VALOR DECLARADO ??
                        objeto.getAvisoRecebimento()); //SERVIÇO AVISO DE RECEBIMENTO S/N
        
        if(!res.getServicos().getCServico().get(0).getErro().isEmpty()){
            Util.mensagemErro(res.getServicos().getCServico().get(0).getMsgErro());
        }else{
            Util.mensagemInformacao("Informações atualizadas");
        }
        
        
        objeto.setValorFrete(Double.parseDouble(res.getServicos().getCServico().get(0).getValor().replace(",", ".")));
        objeto.setValorTotal((objeto.getValorCompra() + objeto.getValorFrete()));
        
    }

    
}
