package projectcloudserver;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
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

class CReader implements Runnable{
    
    private final Socket cs;
    private String nome;
    private final BufferedReader systemIn;
    private final BufferedReader in;
    private final ReentrantLock l;
    private Clog clog;

    public CReader(Socket cs, Clog clog) throws IOException {
        this.cs = cs;
        this.clog = clog;
        this.systemIn = new BufferedReader(new InputStreamReader(System.in)); // pode ser substituido por System.console().readLine(); se estiver a usar consola em vez do IDE
        this.in = new BufferedReader(new InputStreamReader(cs.getInputStream()));
        this.l= new ReentrantLock();
    }
    

    @Override
    public void run() {
        String scan;
        String[] scanner;
        try {
            while((scan = in.readLine())!=null){
                scanner = scan.split(" ");
                String choice = scanner[0];
                switch(choice) {
                    case "notify" :
                    System.out.println("A sua reserva do server obtido em leilao foi cancelada!" + scanner[1]);
                    break;
                        
                    case "notifyNL" :
                        System.out.println("Reserva perdida devido a nova licitação ! "+scanner[1]);
                        break;
                        
                    default :     
                        clog.addC(scan); 
                    }
                }
        } catch (IOException ex) {
            Logger.getLogger(CReader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}



class CHandler implements Runnable{
    
    private final Socket cs;
    private final PrintWriter out;
    private final ReentrantLock l;
    private Clog clog;

    public CHandler(Socket cs, Clog clog) throws IOException {
        this.cs = cs;
        this.out = new PrintWriter(cs.getOutputStream(), true);
        this.l= new ReentrantLock();
        this.clog = clog;
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
                    displayLeiloes();
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
                        if(clog.getLog().equals("true")) displayMenuP();
                        else {
                            System.out.println("Username já está a ser usado!");
                            displayMenuP();
                        }
                     
                break;
                
                case 2 :
                    trataLogin();
                    linha = clog.getLog();
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
        System.out.println("0 - Menu principal");
        Scanner scanner = new Scanner(System.in);
            int choice  = scanner.nextInt();
            String linha;
            int i = 0;
            switch(choice){
                case 1 : 
                    out.println("lservers");
                    out.flush();
                    linha = clog.getLog();
                    while(!(linha.equals("termina")) ){
                        System.out.println(linha);
                        i++;
                        linha = clog.getLog();
                    }
                    if(i>0){
                        System.out.println("1 - Cancelar reserva servidor?");
                        System.out.println("2 - Menu principal");
                        scanner = new Scanner(System.in);
                        if (scanner.nextInt() == 1){
                            trataCancel();
                        }
                        else{
                            System.out.println("Opção invalida...");
                            displayMenuLogged();
                        }
                    }
                    else{
                        System.out.println("Não possui servidores!");
                        trataMServers();
                    }
                break;
                
                case 2 :
                    out.println("div");
                    out.flush();
                    linha = clog.getLog();
                        System.out.println("Total em divida -> " + linha);
                break;
                
                case 0 :
                    displayMenuLogged();
                break;
                    
                default : 
                    System.out.println("Opção invalida...");
                    trataMServers();
            }
            displayMenuLogged();
        
    }
    
    private void trataCancel() throws IOException, InterruptedException{
        System.out.println("---Digite o ID do server que quer cancelar---");       
        Scanner scanner = new Scanner(System.in);
        int id = scanner.nextInt();
            out.println("cancelS" + " " + id);
            out.flush();
            String linha;
            String[] divide;
            linha = clog.getLog();
                divide = linha.split(" ");      
                if(Integer.parseInt(divide[0]) == 1){
                    System.out.println("Reserva de servidor com ID " + id + " cancelada!! Total a pagar -> "+ (((Integer.parseInt(divide[0]))/60)*(Integer.parseInt(divide[1]))));
                }
                else {
                    System.out.println("ID nao corresponde aos seus servidores!");
                }
                System.out.println("************");
    }
    
    private void verServers() throws IOException, InterruptedException{
        System.out.println("1 - Ver m5.large");
        System.out.println("2 - Ver t3.micro");
        System.out.println("3 - Ver a1.medium");
        System.out.println("4 - Ver c5.large");
        System.out.println("5 - Ver i3.metal");
        
        String tipo = null;
        Scanner scanner = new Scanner(System.in);
            int choice  = scanner.nextInt();
            String linha;
            switch(choice){
                case 1 :
                    tipo = "m5.large";
                    break;
                case 2 :
                    tipo = "t3.micro";
                    break;
                case 3 :
                    tipo = "a1.medium";
                    break;
                case 4 :
                    tipo = "c5.large";
                    break;
                case 5 :
                    tipo = "i3.metal";
                    break;
                default : 
                    System.out.println("Opçao invalida....");
                    verServers();
            }
        out.println("ls"+" "+tipo);
        out.flush();
        System.out.println("**********************");
        linha = clog.getLog();
        while(!(linha.equals("termina")) ){
            System.out.println(linha);
            linha = clog.getLog();
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
                    linha = clog.getLog();
                    if(linha.equals("-1")){
                            System.out.println("---Sem servidores disponveis---");
                            System.out.println("Deseja ficar em fila de espera?");
                            System.out.println("1 - Sim ");
                            System.out.println("2 - Nao ");
                            choice  = scanner.nextInt();
                            switch(choice){
                                case 1 :
                                    out.println("sim");
                                    out.flush();
                                    linha = clog.getLog();
                                    System.out.println("Ficou em fila de espera em "+ linha + "º lugar");
                                    linha = clog.getLog();
                                    System.out.println("Servidor " + linha +" alugado!");
                                break;
                                    
                                case 2 : 
                                    displayMenuLogged();
                                break;
                                default :
                                    System.out.println("Opçao invalida....");
                                    displayMenuLogged();
                            }
                            
                        }
                        else{
                            System.out.println("Servidor com ID " + linha+ " reservado!");
                            displayMenuLogged();
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
    
    private void displayLeiloes() throws IOException, InterruptedException {
        System.out.println("Escolha servidor que pretende Leilão :");
        System.out.println("1 - Ver m5.large");
        System.out.println("2 - Ver t3.micro");
        System.out.println("3 - Ver a1.medium");
        System.out.println("4 - Ver c5.large");
        System.out.println("5 - Ver i3.metal");
        String tipo = null;
        Scanner scanner = new Scanner(System.in);
            int choice  = scanner.nextInt();
            String linha;
            switch(choice){
                case 1 :
                    tipo = "m5.large";
                    break;
                case 2 :
                    tipo = "t3.micro";
                    break;
                case 3 :
                    tipo = "a1.medium";
                    break;
                case 4 :
                    tipo = "c5.large";
                    break;
                case 5 :
                    tipo = "i3.metal";
                    break;
                default : 
                    System.out.println("Opçao invalida....");
            }
        out.println("auct"+" "+tipo);
        out.flush();
        System.out.println("**********************");
        int id;
        linha = clog.getLog();
        if(!linha.equals("-1")) {
            id = Integer.parseInt(linha);
            //aqui pode ter que levar linha = clog.getLog();!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1
            while(!(linha.equals("termina"))){
                System.out.println(linha);
                linha = clog.getLog();
            } 
        System.out.println("**********************\n");
        verLeilao(id);
        }
        else { System.out.println("---Leilao indisponível---");
                            System.out.println("Ver outro leilão?");
                            System.out.println("1 - Sim ");
                            System.out.println("2 - Nao ");
                            choice  = scanner.nextInt();
                            switch(choice){
                                case 1 :
                                    displayLeiloes();
                                    break;
                                case 2 : 
                                    displayMenuLogged();
                                default :
                                    System.out.println("Opçao invalida....");
                            }
        }
    }


    private void verLeilao(int id) throws IOException, InterruptedException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("1 - Licitar servidor");
        System.out.println("0 - Voltar ao menu dos leilões");
        String linha;
        int choice  = scanner.nextInt();

        switch(choice){
                case 1 :
                    out.println("lic"+" "+id);
                    linha = clog.getLog();
                    System.out.println("Servidor reservado com id : "+linha);
                    displayMenuLogged();
                    break;

                case 0 :
                    displayLeiloes();
                    break;
                default :
                    System.out.println("Opçao invalida....");
                    verLeilao(id);
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
        Clog clog = new Clog();
        String nome = "";
        
        Thread th = new Thread(new CHandler(cs, clog));
        Thread tw = new Thread(new CReader(cs, clog));
        th.start();
        tw.start();
    }
}