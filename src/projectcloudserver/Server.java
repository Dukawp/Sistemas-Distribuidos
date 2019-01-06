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
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;



/**
 *
 * @author Duka_
 */

class ClientOut {
    
    private HashMap<String, PrintWriter> cout;
    
    public ClientOut(){
        this.cout = new HashMap<>();
    }
    
    public void registarOut(String nome, PrintWriter out){
        this.cout.put(nome, out);
    }
    
    public HashMap<String, PrintWriter> getCout(){
        return this.cout;
    }
}

class SHandler implements Runnable {
    private final Socket cs;
    private final Contas contas;
    private final Servidores servidores; 
    private final UserQueue userQ;
    private final ReentrantLock l;
    private final BufferedReader in;
    private final PrintWriter out;
    private String nome;
    private final ClientOut clientOut; 

    
    public SHandler(Socket cs, Contas contas,Servidores servidores, UserQueue userQ, ClientOut clientOut) throws IOException{
        this.cs = cs;
        this.contas = contas;
        this.servidores = servidores;
        this.userQ = userQ;
        this.out = new PrintWriter(cs.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(cs.getInputStream()));
        this.nome = null;
        this.l = new ReentrantLock();
        this.clientOut = clientOut;
    }
    
    @Override
    public void run(){
        try{
            boolean r;
            Map<Integer,Servidor> meuS = new HashMap<>();
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
                            double preco = 0;
                            for(Servidor s : servidores.getServidores().values()){
                                if((s.getServerName().equals(divide[1])) && s.getDisponivel()){
                                        count++;
                                        preco = s.getPreco();
                                    }
                            }
                            out.println("Tipo -> " + divide[1]);
                            out.println("Preço -> " + preco);
                            out.println("Total disponiveis ->" + count);
                            out.println("termina");
                            out.flush();

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
                                    clientOut.registarOut(nome, out);
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
                    
                    case "lservers":
                        try{
                            l.lock();
                            meuS = contas.getUtilizadores().get(nome).getMeuServers();
                            for(Servidor s : meuS.values()){
                                out.println(s.getID() +" "+ s.getServerName());
                            }
                            out.println("termina");
                            out.flush();
                        }finally{
                            l.unlock();
                        }
                    break;
                    
                    case "res": //Reserva Server
                        try{
                            l.lock();
                            i = servidores.efetuaReserva(divide[1]);
                            if( i >= 0){
                                Servidor s = servidores.getServidores().get(i);
                                System.out.println("Servidor com id "+s.getID() );
                                String ntmp = s.getOwner();
                                s.setOwner(nome);
                                if(s.getLeilao() && !(ntmp.equals("")) ){
                                    servidores.getServidores().get(i).setLeilao(false);
                                    contas.getUtilizadores().get(ntmp).getMeuServers().remove(i);
                                    contas.getUtilizadores().get(ntmp).setCustoTotal((s.geTempoTotal()/60) * s.getValorL());
                                    if(clientOut.getCout().containsKey(ntmp)){
                                        PrintWriter bw = clientOut.getCout().get(ntmp);
                                        bw.println("notify"+ " "+ "ID : " +i);
                                        bw.flush();
                                    }
                                }
                                s.setOwner(nome);
                                s.setTempoInicial();
                                contas.getUtilizadores().get(nome).getMeuServers().put(i,s);
                                out.println(s.getID());
                            }
                            else{
                                out.println("-1");
                                out.flush();
                                String linha = in.readLine();
                                if(linha.equals("sim")){
                                    //COLOCAR USER EM FILA DE ESPERA -- UTILIZAR UMA QUEUE 
                                    Utilizador u = contas.getUtilizadores().get(nome);
                                    userQ.add(divide[1], u);
                                    out.println(userQ.getUQ().get(divide[1]).size());
                                    out.flush();
                                    while( ( userQ.getUQ().get(divide[1]).contains(u) ) ){// ver qual a condiçao de paragem!!!
                                        try{
                                            u.l.lock();
                                            u.condC.await();
                                        }finally{
                                            u.l.unlock();
                                        }
                                    }
                                    i = servidores.efetuaReserva(divide[1]);
                                    Servidor s = servidores.getServidores().get(i);
                                    s.setOwner(nome);
                                    System.out.println("Servidor com id "+s.getID() );
                                    contas.getUtilizadores().get(nome).getMeuServers().put(i,s);
                                    out.println(s.getID());
                                }
                            }
                            
                            out.flush();
                            
                            } catch (InterruptedException ex) {
                                Logger.getLogger(SHandler.class.getName()).log(Level.SEVERE, null, ex);
                            }finally{
                                l.unlock();
                            }
                    break;
                    
                    case "cancelS":
                        try{
                            l.lock();
                            System.out.println(divide[1]);
                            int id = Integer.parseInt(divide[1]);
                            double preco;
                            if(meuS.containsKey(id)){
                                servidores.getServidores().get(id).setDisponivel(true);
                                servidores.getServidores().get(id).setNoOwner();
                                long total = servidores.getServidores().get(id).geTempoTotal();
                                if(meuS.get(id).getLeilao()){
                                    preco = meuS.get(id).getValorL();
                                }
                                else{
                                    preco = meuS.get(id).getPreco();
                                }
                                String sname = meuS.get(id).getServerName();
                                contas.getUtilizadores().get(nome).getMeuServers().remove(id);
                                contas.getUtilizadores().get(nome).setCustoTotal((preco*total));
                                nome = userQ.remove(sname);
                                if(contas.getUtilizadores().containsKey(nome)){
                                    Utilizador u = contas.getUtilizadores().get(nome);
                                    try{
                                        u.l.lock();
                                        u.condC.signal();
                                    }finally{
                                        u.l.unlock();
                                    }
                                }
                                System.out.println("Total de tempo -> "+total+" e preco de server -> "+preco);
                                out.println("1"+" "+total + " " +preco);
                            }
                            else{
                                out.println("0");
                            }
                            out.flush();
                        }finally{
                            l.unlock();
                        }
                    break;
                    
                    case "div":
                        double sum = 0;
                        meuS = contas.getUtilizadores().get(nome).getMeuServers();
                        if(!(meuS.isEmpty())){
                            for(Servidor s : meuS.values()){
                                System.out.println(s.geTempoTotal());
                                sum += ((s.geTempoTotal()/60.0) * s.getPreco());
                            }
                            contas.getUtilizadores().get(nome).setCustoTotal(sum);
                        }
                        out.println(sum);
                        out.flush();
                    break;
                    
                    case "lo":
                        try{
                            l.lock();
                            contas.efetuaLogout(nome);
                            cs.shutdownInput();
                            System.out.println("Cliente saiu!!"+ nome);
                        }finally{
                            l.unlock();
                        }
                    break;
                    case "auct":
                        try {
                            l.lock();
                            int flag = 0;
                            HashMap<Integer,Servidor> aux = servidores.getLeiloes();
                            for(Servidor a : aux.values()){
                                if(a.getServerName().equals(divide[1])){
                                out.println(a.getID());
                                out.println("Tipo -> " + divide[1]);
                                out.println("Valor -> " + a.getValorL());
                                out.println("termina -> "+a.getDataf());
                                flag++;
                                out.println("termina");
                                out.flush();
                            }
                        }
                            if(flag==0){
                                    out.println("-1");
                                    out.flush();
                                }

                        }finally {
                            l.unlock();
                        }
                        break;

                    case "lic":
                        try {
                            l.lock();
                            PrintWriter bw;
                            int id = Integer.parseInt(divide[1]);
                            Utilizador u = contas.getUtilizadores().get(nome);
                            Servidor s = servidores.getServidores().get(id);
                           
                            String prevOwner = servidores.getServidores().get(id).getOwner();
                            if(!prevOwner.equals("")){
                                Utilizador a = contas.getUtilizadores().get(prevOwner);
                                a.setCustoTotal((s.geTempoTotal()/60.0) * s.getPreco());
                                a.getMeuServers().remove(id);
                                bw = clientOut.getCout().get(prevOwner);
                                bw.println("notifyNL" + " "+ "Server custou -> " + (s.geTempoTotal()/60.0) * s.getPreco());
                                bw.flush();
                            }
                            
                            s.setOwner(nome);
                            s.addValorL();
                            s.setTempoInicial();
                            u.getMeuServers().put(id, s);
                            
                            
                            bw = clientOut.getCout().get(nome);
                            bw.println(id);
                            bw.flush();
                        }finally {
                            l.unlock();
                        } 
                        break;
                    //default :
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
        UserQueue q = new UserQueue();
        ClientOut cO = new ClientOut();
        
        //contas para teste...
        c.registaUser("a", "a");
        c.registaUser("b", "b");
        c.registaUser("c", "c");
        c.registaUser("d", "d");
        
        LocalDateTime df =  LocalDateTime.of(2019,6,1,21,26);
        Servidor s = new Servidor("m5.large", 0.99,1,df,0.80);
        Servidor s1 = new Servidor("m5.large",0.99,2,df,0.80);
        Servidor s2 = new Servidor("i3.metal",1.20,3,df,0.90);
        Servidor s3 = new Servidor("i3.metal",1.20,4,df,0.90);
        Servidor s4 = new Servidor("c5.large",1.40,5,df,1.00);
        Servidor s5 = new Servidor("c5.large",1.40,6,df,1.00);
        Servidor s6 = new Servidor("a1.medium",0.75,7,df,0.45);
        Servidor s7 = new Servidor("t3.micro",0.50,8,df,0.30);
        
        s1.setTempoInicial();
        s1.setLeilao(true);
        s1.setDisponivel(false);
        s1.setOwner("b");
        
        s4.setTempoInicial();
        s4.setLeilao(true);
        s4.setDisponivel(false);
        s4.setOwner("b");
        
        v.getServidores().put(1,s);
        v.getServidores().put(2,s1);
        v.getServidores().put(3,s2);
        v.getServidores().put(4,s3);
        v.getServidores().put(5,s4);
        v.getServidores().put(6,s5);
        v.getServidores().put(7,s6);
        v.getServidores().put(8,s7);
        
        System.out.println("TEMPO -> " + LocalDateTime.now());
        while(true){
            Socket cs = ss.accept();
            
            System.out.println("Novo Cliente!!"); // so para ver se esta tudo direito....
            
            Thread ts = new Thread(new SHandler(cs, c, v, q, cO));
            
            ts.start();
        }
            
        }
}
    
