
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
class CReader implements Runnable{
    
    private Socket cs;

    public CReader(Socket cs) {
        this.cs = cs;
    }
    

    @Override
    public void run() {
        try {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(cs.getInputStream()));
                String scan;
                String[] scanner;
                while((scan = in.readLine())!=null){
                    //scanner = scan.split(" ");
                    System.out.println("SCAN: " + scan);
                }
            } catch (IOException ex) {}
            in.close();
            try {
                cs.close();
            } catch (IOException ex) {
                Logger.getLogger(CReader.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (IOException ex) {Logger.getLogger(CReader.class.getName()).log(Level.SEVERE, null, ex);
}
    }
    
    
}

class CWriter implements Runnable{
    
    private final Socket cs; 
    private PrintWriter out;
    private ReentrantLock l;
    private Condition aguardaH;
    private int count;
    
    public CWriter(Socket cs, int count) throws IOException {
        this.cs = cs;
        out = new PrintWriter(cs.getOutputStream(), true);
        this.l= new ReentrantLock();
        this.count = count;
    }

    @Override
    public void run() {
        try {
            try {
                executarMenu();
            } catch (InterruptedException ex) {
                Logger.getLogger(CWriter.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (IOException ex) {
            Logger.getLogger(CWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void executarMenu() throws IOException, InterruptedException{
        displayMenuP();
    }
    public void displayMenuP() throws IOException, InterruptedException{
        System.out.println("1 - Registar");
        System.out.println("2 - Login");
        executar();

    }
    
    public void displayMenuLogged() throws IOException, InterruptedException{
        System.out.println("1 - Ver Servidores");
        System.out.println("0 - Logout");
        executar();
    }
    //VER SE HA FORMA DE DIZER EM QUE MENU ESTA PARA ENTRAR EM CASE DIFERENTE .. EX MENUP - REGISTAR -> CASE P1
    private void executar() throws IOException, InterruptedException{
            Scanner scanner = new Scanner(System.in);
            int choice  = scanner.nextInt();
            
            
            switch(choice){
                case 1 :
                    trataRegisto();
                    break;
                case 2 :
                    trataLogin();
                    //FALTA VERIFICAR SE LOGIN TEVE SUCESSO PARA DECIDIR QUE MENU MOSTRAR!!!!
                    /*if(login deu True){
                        displayMenuLogged();
                    }
                    else{
                        trataLogin();
                    }*/
                    trataLogin();
                    break;
                case 0 :
                    logout();
                    break;
                default : 
                    System.out.println("Opçao invalida....");
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
        
        int c = 0;
        
        
        
        Thread tr = new Thread(new CReader(cs));
        Thread tw = new Thread(new CWriter(cs, c));
        tr.start();
        tw.start();
        
       
    }
}
