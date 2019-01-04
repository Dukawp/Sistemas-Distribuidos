/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectcloudserver;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author Duka_
 */
class Clog {
    
    //Queue<String> log;
    ReentrantLock l;
    Condition vazio;
    Map<String,Queue<String>> clientlogs;
   
    public Clog() {
        //this.log = new ArrayDeque<>() ;
        this.l = new ReentrantLock();
        this.vazio = this.l.newCondition();
        this.clientlogs = new HashMap<>();
    }
    
    public void addS(String nome, String s){
        l.lock();
        try {
            Queue<String> lg = clientlogs.get(nome);
            lg.add(s);
            clientlogs.put(nome, lg);
            //log.add(s);
            vazio.signalAll(); 
        } finally {
            l.unlock();
        }
    }    
    
    public String getLog(String nome) throws InterruptedException{
        String s;
        l.lock();
        try {
            while(clientlogs.get(nome).isEmpty()==true){
                vazio.await();
            }
            s = clientlogs.get(nome).poll();
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
}
    

