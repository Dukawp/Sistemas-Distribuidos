/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectcloudserver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author Duka_
 */
public class UserQueue {

    private Map<String,ArrayList<Utilizador>> tqueue;  // <servername,username>
    private ReentrantLock l = new ReentrantLock();  
    //private Condition cond = l.newCondition();
    
    public UserQueue(){
        this.tqueue = new HashMap<>();
        this.l = new ReentrantLock();
        //this.cond = l.newCondition();
    }
    
    
    public Map<String, ArrayList<Utilizador>> getUQ(){
        return this.tqueue;
    }
    
    public void add(String servername, Utilizador user){
        try{
        l.lock();
        ArrayList<Utilizador> aux = new ArrayList<>();
        aux = tqueue.get(servername);  
        aux.add(user);
        tqueue.put(servername,aux);   
        }finally{
            l.unlock();
        }
    }

    public void remove(String servername){
        try{
            l.lock();
            Utilizador u = tqueue.get(servername).get(0);
            tqueue.get(servername).remove(0);
            u.condC.signal();
        }finally{
            l.unlock();
        }
        
       
    }
    
    
    
}
