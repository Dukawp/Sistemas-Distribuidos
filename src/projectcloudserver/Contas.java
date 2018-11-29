/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectcloudserver;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author Duka_
 */
public class Contas {
    
    Map<String,Utilizador> utilizadores;//<username,utilizador>
    ReentrantLock l = new ReentrantLock();

    public Contas(){
        this.utilizadores = new HashMap<String,Utilizador>();
        this.l = new ReentrantLock();
    }

    /*public Contas(Map<String, Utilizador> utilizadores) {
        this.utilizadores = utilizadores;
    }*/

    public Map<String, Utilizador> getUtilizadores() {
        return utilizadores;
    }

    public void setUtilizadores(Map<String, Utilizador> utilizadores) {
        this.utilizadores = utilizadores;
    }
    
    public void registaUser(String username, String password) throws ClienteExistenteException{
        l.lock();
        try {
            if(!utilizadores.containsKey(username)){
                Utilizador user = new Utilizador(username,password,false);
                utilizadores.put(username,user);
                System.out.println("Utilizador registado com sucesso!!");
            }
            else{
                System.out.println("Utilizador ja existe");
                throw new ClienteExistenteException("Utilizador: " + username + " já existe");
            }
        } finally {
            l.unlock();
        }
    }
    
    public boolean efetuaLogin(String username, String password) throws ClienteExistenteException{
        l.lock();
        //System.out.println("ENTRALOGIN");
        if((utilizadores.containsKey(username))&&(utilizadores.get(username).getPassword().equals(password))){
            //System.out.println("DADOS::::"+utilizadores.get(username).getPassword());
            //System.out.println("Login efetuado");
            l.unlock();
            return true;
        }
        else{
            System.out.println("Password ou username inválidos!");
            l.unlock();
            return false;
        }
    }    
}
