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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Date;



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
    private final Condition condS;
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
        this.condS = l.newCondition();
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
                                    if(clientOut.getCout().containsKey(nome)){
                                        System.out.println("MAU ERA CRL!!");
                                    }
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
                                //RETIREI AQUI ESTE IF SO PARA VER SE ESTAVA A FUNCIONAR
                                if(s.getLeilao() && !(s.getOwner().equals(nome))){
                                    String ntmp = s.getOwner();        
                                    System.out.println("Encontrei o owner -> " +ntmp);
                                    servidores.getServidores().get(i).setLeilao(false);
                                    if(clientOut.getCout().containsKey(ntmp)){
                                        System.out.println("ESTOu aquI CRL");
                                        PrintWriter bw = clientOut.getCout().get(ntmp);
                                        bw.println("notify"+ " "+ "Reservar do seu server por leilao foi cancelada!");
                                        bw.flush();
                                        System.out.println("PASSEI DO FLUSH");
                                    }
                                }
                                s.setOwner(nome);
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
                                        System.out.println("Vou ficar a espera do signal!!!!!");
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
                            //meuS = contas.getUtilizadores().get(nome).getMeuServers();
                            System.out.println(divide[1]);
                            int id = Integer.parseInt(divide[1]);
                            if(meuS.containsKey(id)){
                                servidores.getServidores().get(id).setDisponivel(true);
                                servidores.getServidores().get(id).setNoOwner();
                                long total = servidores.getServidores().get(id).geTempoTotal();
                                double preco = meuS.get(id).getPreco();
                                String sname = meuS.get(id).getServerName();
                                System.out.println("Consegui ver o server " + sname);
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
                                System.out.println("Tempo em horas -> "+ s.geTempoTotal()/60.0);
                                sum += ((s.geTempoTotal()/60.0) * s.getPreco());
                                System.out.println("Total do preço - > " +sum);
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
                            int id = Integer.parseInt(divide[1]);
                            Utilizador u = contas.getUtilizadores().get(nome);
                            servidores.getServidores().get(id).addValorL();
                            String prevowner = servidores.getServidores().get(id).getOwner();
                            servidores.getServidores().get(id).setOwner(nome);
                            /*regista licitacao*/
                            PrintWriter bw = clientOut.getCout().get(nome);
                            bw.println(id+nome);
                            bw.flush();
                            bw = clientOut.getCout().get(prevowner);
                            bw.println("notityNL"+" "+id+prevowner);
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
        
        Date df =  new Date(2019,0,4,21,26,40);
        Servidor s = new Servidor("m5", 0.99, 1,df,0.90);
        Servidor s1 = new Servidor("m5",0.99,2,df,0.89);
        s1.setLeilao(true);
        s1.setOwner("b");
        v.getServidores().put(1, s);
        v.getServidores().put(2,s1);
        
        System.out.println("TEMPO -> " + LocalDateTime.now());
        while(true){
            Socket cs = ss.accept();
            
            System.out.println("Novo Cliente!!"); // so para ver se esta tudo direito....
            
            Thread ts = new Thread(new SHandler(cs, c, v, q, cO));
            
            ts.start();
        }
            
        }
}
    
