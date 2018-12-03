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
 
    Map<Integer,Servidor> servidores;  // identificador de reserva ???
    ReentrantLock l = new ReentrantLock();    
    
    public Servidores(){
        this.servidores = new HashMap<>();
        this.l = new ReentrantLock();
    }
    
    public Map<Integer,Servidor> getServidores(){
        return this.servidores;
    }
    
    public boolean efetuaReserva(int id){
        l.lock();
        boolean r = false;
        try {
            Servidor s;
            s = servidores.get(id);
            if(s.getDisponivel()){
                r = true;
                s.setDisponivel(!r);
            }
        } finally {
            l.unlock();
        }
        return r;
        
    }
    
    
}
