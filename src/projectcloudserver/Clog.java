package projectcloudserver;


import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Duka_
 */
public class Clog {
    
    private Queue<String> log;
    private ReentrantLock l;
    private Condition vazio;
    private Condition vazioC;
   
    public Clog() {
        this.log = new ArrayDeque<>();
        this.l = new ReentrantLock();
        this.vazio = this.l.newCondition();
        this.vazioC = this.l.newCondition();

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
        
}