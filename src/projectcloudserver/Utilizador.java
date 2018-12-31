/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package projectcloudserver;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;


/**
 *
 * @author Duka_
 */
public class Utilizador {
    
    private String username;
    private String password;
    private boolean logged;//true->login efetuado;false->logout efetuado
    private Map<Integer,Servidor> meuServers;
    final ReentrantLock l;
    public Condition condC;
    private double custototal;


    public Utilizador(String username, String password, boolean log) {
        this.username = username;
        this.password = password;
        this.logged = false;
        this.l = new ReentrantLock();
        this.condC = l.newCondition();
        this.meuServers = new HashMap<>();
        this.custototal = 0;
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

    public double getCustoTotal(){
        return this.custototal;
    }
    
    public void setCustoTotal(double custo){
        this.custototal += custo;
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
