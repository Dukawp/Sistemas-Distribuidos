/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package projectcloudserver;

import java.util.HashMap;
import java.util.Map;


/**
 *
 * @author Duka_
 */
public class Utilizador {
    
    private String username;
    private String password;
    private boolean logged;//true->login efetuado;false->logout efetuado
    private Map<Integer,Servidor> meuServers;


    public Utilizador(String username, String password, boolean log) {
        this.username = username;
        this.password = password;
        this.logged = false;
        this.meuServers = new HashMap<>();
    }

     public Map<Integer,Servidor> getMeuServers(){
        return this.meuServers;
    }
   
    public boolean getLog(){
        return this.logged;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

  
    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setLog(boolean log) {
        this.logged = log;
    }
    
    public void logout(){
        this.setLog(false);
    }
    
}
