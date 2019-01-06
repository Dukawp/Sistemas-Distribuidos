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
public class Servidores {
 
    Map<Integer,Servidor> servidores;  // identificador de reserva 
    ReentrantLock l = new ReentrantLock();    
    
    public Servidores(){
        this.servidores = new HashMap<>();
        this.l = new ReentrantLock();
    }
    
    public Map<Integer,Servidor> getServidores(){
        return this.servidores;
    }
    
    public int efetuaReserva(String tipo){
        l.lock();
        int r = -1 ;
        try {
            for(Servidor s : servidores.values()){
                if(s.getServerName().equals(tipo) && s.getDisponivel() && !s.getLeilao()){
                    r = s.getID();
                    s.setDisponivel(false);
                }
            }
            if(r==-1){
                for(Servidor s : servidores.values()){
                    if(s.getServerName().equals(tipo) && (s.getLeilao() && !s.getDisponivel() && (s.getValorL() < s.getPreco()) ) ){
                        r = s.getID();
                    }
                }
            }
        } finally {
            l.unlock();
        }
        return r;
        
    }

    public int libertaServer(int id){
        l.lock();
        int r = -1 ; // -1 erro a libertar!!!!!!!!
        try{
            if(servidores.containsKey(id)){
                servidores.get(id).setDisponivel(true);
                
                // DAR O SIGNAL PARA A QUEUE
                r = 0; // libertado com sucesso!!!
            }
        } finally {
            l.unlock();
        }
        return r;  
    }
    
    public HashMap<Integer,Servidor> getLeiloes() {
        HashMap<Integer,Servidor> ret = new HashMap();
        for(Servidor a : servidores.values()) {
            if(a.getLeilao() && a.getDisponivel() && a.getPreco() > a.getValorL()){
                ret.put(a.getID(),a);
            }
        }
        return ret;
    }
    
    public void efectuaLicitacao(int id){
        l.lock();
        try {
            servidores.get(id).addValorL();
        }finally {
            l.unlock();
        }       
    }
    
}
