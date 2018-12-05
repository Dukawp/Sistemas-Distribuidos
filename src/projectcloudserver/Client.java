
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import static java.lang.System.in;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Duka_
 */


class CHandler implements Runnable{
    
    private final Socket cs;
    private final BufferedReader systemIn;
    private final BufferedReader in;
    private final PrintWriter out;
    private final ReentrantLock l;
    private Condition aguardaH;

    public CHandler(Socket cs) throws IOException {
        this.cs = cs;
        this.out = new PrintWriter(cs.getOutputStream(), true);
        this.systemIn = new BufferedReader(new InputStreamReader(System.in)); // pode ser substituido por System.console().readLine(); se estiver a usar consola em vez do IDE
        this.in = new BufferedReader(new InputStreamReader(cs.getInputStream()));
        this.l= new ReentrantLock();
    }

    @Override
    public void run() {
        try {
            try {
                displayMenuP();
            } catch (InterruptedException ex) {
                Logger.getLogger(CHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (IOException ex) {
            Logger.getLogger(CHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void displayMenuP() throws IOException, InterruptedException{
        System.out.println("1 - Registar");
        System.out.println("2 - Login");
        executarP();
    }
    
    public void displayMenuLogged() throws IOException, InterruptedException{
        System.out.println("1 - Ver Servidores");
        System.out.println("2 - Ver leiloes");
        System.out.println("3 - Ver meus servidores e valor em divida");
        System.out.println("0 - Logout");
        executarL();
    }
    
    private void executarL() throws IOException, InterruptedException{
            Scanner scanner = new Scanner(System.in);
            int choice  = scanner.nextInt();
            String linha;

            switch(choice){
                case 1 :
                    verServers();
                    if( (linha=in.readLine()) != null){
                        System.out.println(linha);
                    } 
                    break;
                case 0 :
                    logout();
                    break;
                default : 
                    System.out.println("Opçao invalida....");
                    displayMenuLogged();
            }
    }
    
    private void executarP() throws IOException, InterruptedException{
            Scanner scanner = new Scanner(System.in);
            int choice  = scanner.nextInt();
            String linha;
            
            switch(choice){
                case 1 :
                    trataRegisto();
                    if( (linha=in.readLine()) != null){
                        if(linha.equals("true")) displayMenuP();
                        else {
                            System.out.println("Username já está a ser usado!");
                            displayMenuP();
                        }
                    } 
                    break;
                case 2 :
                    trataLogin();
                    if( (linha=in.readLine()) != null){
                        int num = Integer.parseInt(linha);
                        if( num == 1 ) {
                            System.out.println("Bem vindo !");
                            displayMenuLogged();
                        }
                        if( num == 0 ){
                            System.out.println("Utilizador já está loggado");
                            displayMenuP();
                        }
                        if( num == -1){
                            System.out.println("Password errada!");
                            displayMenuP();
                        }
                        else {
                            System.out.println("Utilizador não existe!");
                            displayMenuP();
                        }
                    } 
                    break;
                case 0 :
                    logout();
                    break;
                default : 
                    System.out.println("Opçao invalida....");
                    displayMenuP();
            }
    }
   
    
    private void verServers() throws IOException{
        System.out.println("1 - Ver m5...");
        System.out.println("2 - Ver t3...");
        System.out.println("3 - Ver a2...");
        System.out.println("4 - Ver c1...");
        String tipo = null;
        Scanner scanner = new Scanner(System.in);
            int choice  = scanner.nextInt();
            String linha;
            switch(choice){
                case 1 :
                    out.println("ls "+" "+"m5");
                    tipo = "m5";
                    out.flush();
                    break;
                case 2 :
                    out.println("ls "+" "+"t3");
                    tipo = "t3";
                    out.flush();
                    break;
                case 3 :
                    out.println("ls "+" "+"a2");
                    tipo = "a2";
                    out.flush();
                    break;
                
                case 4 :
                    out.println("ls "+" "+"c1");
                    tipo = "c1";
                    out.flush();
                    break;
                default : 
                    System.out.println("Opçao invalida....");
                    verServers();
            }
        if( (linha=in.readLine()) != null){
            System.out.println(linha);
        }
        try {
            ReservarServer(tipo);
        } catch (InterruptedException ex) {
            Logger.getLogger(CHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void ReservarServer(String tipo) throws IOException, InterruptedException{
        Scanner scanner = new Scanner(System.in);
        int choice  = scanner.nextInt();
        System.out.println("1 - Reservar servidor");
        System.out.println("0 - Voltar ao menu anterior");
        String linha;
        switch(choice){
                case 1 :
                    out.println("res "+" "+tipo);
                    out.flush();
                    if((linha = in.readLine()) != null){
                        if(linha.equals("-1")){
                            System.out.println("Ja esta reservado!");
                        }
                        else{
                            System.out.println("Servidor com ID" + linha+ "reservado!");
                            displayMenuLogged();
                        }
                    }
                    //FALTA ADICIONAR ARRAY COM ID DO SERVER
                case 0 :
                    displayMenuLogged();
                default :
                    System.out.println("Opçao invalida....");
                    ReservarServer(tipo);
        }
    }
    
    
    private void trataRegisto(){
        System.out.println("Username: ");
        Scanner sc = new Scanner(System.in);
        String u = sc.nextLine();
        System.out.println("Password: ");
        String p = sc.nextLine();
        out.println("reg"+" "+u+" "+p);
        out.flush();
    }
    
    private void trataLogin() throws IOException{
        System.out.println("Username: ");
        Scanner sc = new Scanner(System.in);
        String u = sc.nextLine();
        System.out.println("Password: ");
        String p = sc.nextLine();
        out.println("logi"+" "+u+" "+p);
        out.flush();
    }
    

    private void logout() throws IOException {
        out.print("lo");
        out.close();
        cs.close();
        System.out.println("Logged out!! Cya");
    }
 
}

public class Client {
    
    public static void main(String[] args) throws IOException{
        
        Socket cs = new Socket("127.0.0.1", 1234);
        
        
        
        Thread th = new Thread(new CHandler(cs));
        th.start();
        
       
    }
}
