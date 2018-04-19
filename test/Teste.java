
import br.edu.ifsul.servicopessoa.ServicoPessoaService;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author devuser
 */
public class Teste {
    
    public static void main(String[] args) {
        
        ServicoPessoaService a = new ServicoPessoaService();
        
        System.out.println(a.getServicoPessoaPort().listaPessoas().get(0).getId());
        
        
    }
    
}
