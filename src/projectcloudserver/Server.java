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
    private final Servidores servidores; 
    private final ReentrantLock l;
    private final BufferedReader in;
    private final PrintWriter out;
    private String nome;

    
    public SHandler(Socket cs, Contas contas,Servidores servidores) throws IOException{
        this.cs = cs;
        this.contas = contas;
        this.servidores = servidores;
        this.out = new PrintWriter(cs.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(cs.getInputStream()));
        this.nome = null;
        this.l = new ReentrantLock();
    }
    
    @Override
    public void run(){
        try{
            boolean r;
            String scan;
            int i;
            String[] divide;
            while((scan=in.readLine())!= null){
                divide = scan.split(" ");
                
                switch(divide[0]){
                    
                    case"reg": //Registar
                    
                        try{
                            l.lock();
                            try {
                                r = contas.registaUser(divide[1],divide[2]);
                                if(r) {
                                    out.println("true");
                                }
                                else out.println("false");
                                out.flush();
                            } catch (ClienteExistenteException e) {
                                Logger.getLogger(SHandler.class.getName()).log(Level.SEVERE, null, e);
                            }
                        } finally {
                            l.unlock();
                        }
                    
                    break;       
                    
                    case "ls": //lista Servers
                        try{
                            l.lock();
                            int count = 0;
                            for(Servidor s : servidores.getServidores().values()){
                                if((s.getServerName().equals(divide[1])) && s.getDisponivel()){
                                        count++;
                                    }
                                out.println("Tipo -> " + s.getServerName());
                                out.println("Preço -> " + s.getPreco());
                                out.println("Total disponiveis ->" + count);
                                out.flush();
                            }
                        }finally{
                            l.unlock();
                        }
                    break;       
                    
                    case "res": //Reserva Server
                        try{
                            l.lock();
                            
                            
                        }finally{
                            l.unlock();
                        }
                    break;
                        
                    case"logi": // Login
                        try{
                            l.lock();
                            try {
                                String username = divide[1];
                                i = contas.efetuaLogin(username, divide[2]);
                                if(i==1) {
                                    nome = username;
                                }
                                out.println(i);
                                out.flush();
                            } catch (ClienteExistenteException ex) {
                                Logger.getLogger(SHandler.class.getName()).log(Level.SEVERE, null, ex);
                            } 
                        }finally {
                        l.unlock();
                        }
                    break;
                    
                    case "lo":
                        try{
                            l.lock();
                            //FALTA METER O UTILIZADOR COM O LOGGED A FALSE
                            cs.shutdownInput();
                            System.out.println("Cliente saiu!!"+ nome);
                        }finally{
                            l.unlock();
                        }
                    break;
                }
            }
                
        } catch (IOException ei) {
            Logger.getLogger(SHandler.class.getName()).log(Level.SEVERE, null, ei);
        }
    }
    
}


public class Server {
   
    
   public static void main(String[] args) throws Exception{
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
    
