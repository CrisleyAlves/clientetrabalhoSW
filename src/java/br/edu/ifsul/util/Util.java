package br.edu.ifsul.util;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

/**
 *
 * @author crisley
 */
public class Util {
    
    public static String getMensagemErro(Exception e){
        
        String retorno;
        
        while(e.getCause() != null){
            e = (Exception) e.getCause();
        }
        
        retorno = e.getMessage();
        
        if(retorno.contains("chave estrangeira")){
            retorno = "O registro não pode ser removido por possuir referências em outros objetos";
        }
        
        
        return retorno;
    }
    
    public static void mensagemInformacao(String msg){
        FacesMessage mensagem = new FacesMessage(FacesMessage.SEVERITY_INFO, msg, "");
        FacesContext.getCurrentInstance().addMessage(null, mensagem);
    }
    
    public static void mensagemErro(String msg){
        FacesMessage mensagem = new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, "");
        FacesContext.getCurrentInstance().addMessage(null, mensagem);
    }
    
}
