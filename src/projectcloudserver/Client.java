package projectcloudserver;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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
                    displayMenuLogged();
                    break;
                case 2 :
                    //tratar de ver os leiloes!!!!!
                    break;
                case 3 :
                    trataMServers();
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
   
    
    private void trataMServers() throws IOException, InterruptedException{
        System.out.println("1 - Ver servidores");
        System.out.println("2 - Ver valor total a pagar");
        Scanner scanner = new Scanner(System.in);
            int choice  = scanner.nextInt();
            String linha;
            switch(choice){
                case 1 : 
                    out.println("lservers");
                    out.flush();
                    while( ((linha = in.readLine()) != null) && !(linha.equals("termina")) ){
                        System.out.println(linha);
                    }
                    System.out.println("1 - Cancelar reserva servidor?");
                    System.out.println("2 - Menu principal");
                    scanner = new Scanner(System.in);
                    if (scanner.nextInt() == 1){
                        trataCancel();
                    }
                    else{
                        displayMenuLogged();
                    }
                break;
                case 2 :
                    //ASDASDASDASDASD
                default : 
                    System.out.println("Opção invalida...");
                    trataMServers();
            }
            displayMenuLogged();
        
    }
    
    private void trataCancel(){
        System.out.println("1 - Digite o ID do server que quer cancelar");
        System.out.println("2 - Voltar ao menu anterior");        
        Scanner scanner = new Scanner(System.in);
        if(scanner.nextInt() == 1){
            out.println("cancelS" + " " + scanner);
            String linha;
            if((linha = in.readLine()) != null){
                if(linha.equals("sim")){
                    System.out.println("Reserva de servidor com ID " + scanner+ " cancelada!!");
                }
                else {
                    System.out.println("ID nao corresponde aos seus servidores!");
                    trataCancel();
                }
            }
        }
    }
    
    private void verServers() throws IOException, InterruptedException{
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
                    tipo = "m5";
                    break;
                case 2 :
                    tipo = "t3";
                    break;
                case 3 :
                    tipo = "a2";
                    break;
                case 4 :
                    tipo = "c1";
                    break;
                default : 
                    System.out.println("Opçao invalida....");
                    verServers();
            }
        out.println("ls"+" "+tipo);
        out.flush();
        System.out.println("**********************");
        while( ((linha = in.readLine()) != null) && !(linha.equals("termina")) ){
            System.out.println(linha);
        }
        System.out.println("**********************\n");
        ReservarServer(tipo);
    }
    
    private void ReservarServer(String tipo) throws IOException, InterruptedException{
        Scanner scanner = new Scanner(System.in);
        System.out.println("1 - Reservar servidor deste tipo");
        System.out.println("0 - Voltar ao menu anterior");
        String linha;
        int choice  = scanner.nextInt();

        switch(choice){
                case 1 :
                    // fazer um metodo com isto !!! trataReserva ..
                    out.println("res"+" "+tipo);
                    out.flush();
                    if((linha = in.readLine()) != null){
                        if(linha.equals("-1")){
                            System.out.println("Sem servidores disponveis");
                            System.out.println("Deseja ficar em fila de espera?");
                            System.out.println("1 - Sim ");
                            System.out.println("2 - Nao ");
                            choice  = scanner.nextInt();
                            switch(choice){
                                case 1 :
                                    out.println("sim");
                                    out.flush();
                                    if ((linha = in.readLine()) != null){
                                    System.out.println("Ficou em fila de espera em "+ linha + "º lugar");
                                    }
                                    if((linha = in.readLine()) != null){
                                        System.out.println("Servidor " + linha +" alugado!");
                                    }
                                    break;
                                case 2 : 
                                    displayMenuLogged();
                                default :
                                    System.out.println("Opçao invalida....");
                                    displayMenuLogged();
                            }
                            
                        }
                        else{
                            System.out.println("Servidor com ID " + linha+ "reservado!");
                            displayMenuLogged();
                        }
                    }
                    break;
                case 0 :
                    displayMenuLogged();
                    break;
                default :
                    System.out.println("Opçao invalida....");
                    ReservarServer(tipo);
        }
        displayMenuLogged();
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
