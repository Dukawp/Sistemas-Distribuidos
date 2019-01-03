/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectcloudserver;

import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author Duka_
 */
class Clog {
    
    Queue<String> log;
    Queue<String> cls;
    ReentrantLock l;
    Condition vazio;
    Condition vazioC;
    Condition vazioH;
    Condition vazioX;
    List<Utilizador> equipa1;
    List<Utilizador> equipa2;
    Map<Integer,Utilizador> tmp;
   
    public Clog() {
        this.log = new ArrayDeque<>() ;
        this.cls = new ArrayDeque<>() ;
        this.l = new ReentrantLock();
        this.vazio = this.l.newCondition();
        this.vazioC = this.l.newCondition();
        this.tmp=new HashMap<>();
    }
    
    public void addS(String s){
        l.lock();
        try {
            log.add(s);
            vazio.signalAll(); 
        } finally {
            l.unlock();
        }
    }
    
    public void addC(String s){
        l.lock();
        try {
            log.add(s);
            vazioC.signalAll();
        } finally {
            l.unlock();
        }
    }
    
    
    public String getLog() throws InterruptedException{
        String s;
        l.lock();
        try {
            while(log.isEmpty()==true){vazioC.await();}
            s = log.poll();
        } finally {
            l.unlock();
        }
        
        return s;
    }
    
    public void writeToClient(PrintWriter out,String message){
            
            l.lock();
            try {
                out.println(message);
                out.flush();
            } finally {
                l.unlock();
            }
    }
    
    public void writeServer(PrintWriter out){
        try {
            
            while(true){
            
                l.lock();
                
                while(log.isEmpty()){vazio.await();}
                    out.println(log.poll());
                    out.flush();
                l.unlock();
            
            //out.close();
            //cs.close();
            
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(SWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
