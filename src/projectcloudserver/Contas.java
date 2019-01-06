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
        this.utilizadores = new HashMap<>();
        this.l = new ReentrantLock();
    }

    public Map<String, Utilizador> getUtilizadores() {
        return utilizadores;
    }

    public void setUtilizadores(Map<String, Utilizador> utilizadores) {
        this.utilizadores = utilizadores;
    }
    
    public boolean registaUser(String username, String password) throws ClienteExistenteException{
        l.lock();
        try {
            if(!utilizadores.containsKey(username)){
                System.out.println("Cliente registado!!");
                Utilizador user = new Utilizador(username,password,false);
                utilizadores.put(username,user);
                return true;
            }
            return false;
        } finally {
            l.unlock();
        }
    }
    
    public int efetuaLogin(String username, String password) throws ClienteExistenteException{
        l.lock();
        try {
            if((utilizadores.containsKey(username))){
                if((utilizadores.get(username).getPassword().equals(password))){
                    if((utilizadores.get(username).getLog() == false)){
                        System.out.println("DADOS:::: "+utilizadores.get(username).getPassword());
                        System.out.println("Login efetuado");
                        utilizadores.get(username).setLog(true);
                        return 1;
                    }
                    else {
                        System.out.println("Utilizador já se encontra loggado!");
                        return 0;
                    }
                }
                else{
                    System.out.println("Password errada!");
                    return -1;
                }
            }
            System.out.println("Username não existe!");
        } finally {
            l.unlock();
        }
        return -2;
    }
    
    public void efetuaLogout(String username){
        l.lock();
        try{
            utilizadores.get(username).setLog(false);
        }finally{
            l.unlock();
        }
    }

    
}
