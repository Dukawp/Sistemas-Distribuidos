
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
        System.out.println("0 - Logout");
        executarL();
    }
    
    private void executarL() throws IOException, InterruptedException{
        Scanner scanner = new Scanner(System.in);
            int choice  = scanner.nextInt();
            
            
            switch(choice){
                case 1:
                    //dicidir que casos temos 1º
                default :
                    System.out.println("Opçao invalida....");
                    displayMenuLogged();
            }
    }
    //VER SE HA FORMA DE DIZER EM QUE MENU ESTA PARA ENTRAR EM CASE DIFERENTE .. EX MENUP - REGISTAR -> CASE P1
    private void executarP() throws IOException, InterruptedException{
            Scanner scanner = new Scanner(System.in);
            int choice  = scanner.nextInt();
            String linha;
            
            switch(choice){
                case 1 :
                    trataRegisto();
                    if( (linha=in.readLine()) != null){
                        if(linha.equals("true")) displayMenuP();
                        else trataRegisto();
                    } 
                    break;
                case 2 :
                    trataLogin();
                    //FALTA VERIFICAR SE LOGIN TEVE SUCESSO PARA DECIDIR QUE MENU MOSTRAR!!!!
                    if( (linha=in.readLine()) != null){
                        if(linha.equals("true")) displayMenuLogged();
                        else trataLogin();
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
