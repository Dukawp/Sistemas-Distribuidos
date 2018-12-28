/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectcloudserver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author Duka_
 */
public class UserQueue {

    private Map<String,ArrayList<String>> queue;  // <servername,username>
    private ReentrantLock l = new ReentrantLock();  
    private Condition cond = l.newCondition();
    
    private UserQueue(){
        this.queue = new HashMap<>();
        this.l = new ReentrantLock();
        this.cond = l.newCondition();
    }
    
    
    public Map<String, ArrayList<String>> getUQ(){
        return this.queue;
    }
    
    public void add(String servername, String username){
        try{
        l.lock();
        ArrayList<String> aux = new ArrayList<>();
        aux = queue.get(servername);  
        aux.add(username);
        put(servername,aux);   
        }finally{
            l.unlock();
        }
    }

    public void remove(String servername){
        try{
            l.lock();
            
        }finally{
            l.unlock();
        }
        
       
    }
    
    
    
}
