/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectcloudserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import static java.lang.Thread.sleep;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Duka_
 */

class SHandler implements Runnable {
    private final Socket cs;
    private final Contas contas;
    private Servidores servidores; 
    private final ReentrantLock l;
    
    public SHandler(Socket cs, Contas contas,Servidores servidores){
        this.cs = cs;
        this.contas = contas;
        this.servidores = servidores;
        this.l = new ReentrantLock();
    }
    
    @Override
    public void run(){
        try{
            BufferedReader in = new BufferedReader(new InputStreamReader(cs.getInputStream()));
            String scan;
            String[] divide;
            while((scan=in.readLine())!= null){
                divide = scan.split(" ");
                
                switch(divide[0]){
                    
                    case"reg":
                    
                        try{
                            l.lock();
                            try {
                                contas.registaUser(divide[1],divide[2]);
                            } catch (ClienteExistenteException e) {
                                Logger.getLogger(SHandler.class.getName()).log(Level.SEVERE, null, e);
                            }
                        } finally {
                            l.unlock();
                        }
                    
                    break;       
                    
                    case"logi":
                    try{
                        l.lock();
                        try {
                            String username = divide[1];
                            boolean r;
                            r = contas.efetuaLogin(username, divide[2]);
                            // TER AQUI UM OUT PARA MANDAR PARA O CLIENT?? OU ADICIONAR A UMA ESPECIE DE LOG(QUEUE) E O SWRITER TRATA DISSO ???
                            //AVISAR O CLIENT QUE LOGIN TEVE SUCESSO
                        } catch (ClienteExistenteException ex) {
                            Logger.getLogger(SHandler.class.getName()).log(Level.SEVERE, null, ex);
                        } 
                    }finally {
                    l.unlock();
                    }
                }
            }
                
        } catch (IOException ei) {
            Logger.getLogger(SHandler.class.getName()).log(Level.SEVERE, null, ei);
        }
    }
    
}


public class Server {
   
    
   public static void main(String[] args) throws IOException, InterruptedException, ClienteExistenteException{
        int port = 1234;
        ServerSocket ss = new ServerSocket(port);
        Contas c = new Contas();
        Servidores v = new Servidores();
        
        //contas para teste...
        c.registaUser("a", "a");
        c.registaUser("b", "b");
        c.registaUser("c", "c");
        c.registaUser("d", "d");
        
        while(true){
            Socket cs = ss.accept();
            
            System.out.println("Novo Cliente!!"); // so para ver se esta tudo direito....
            
            Thread ts = new Thread(new SHandler(cs, c,v));
            
            ts.start();
        }
            
        }
}
    
