/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectcloudserver;

/**
 *
 * @author Duka_
 */
public class Servidor {
    
    private final String servername;
    private final double preco; //preç´o é fixo!
    private final int id;
    private boolean disponivel;
    
    public Servidor(String servername, double preco, int id){
        this.servername = servername;
        this.preco = preco;
        this.id = id;
        this.disponivel = true;
    }
    
    public boolean getDisponivel(){
        return this.disponivel;
    }
    
    public String getServerName(){
        return this.servername;
    }
    
    public double getPreco(){
        return this.preco;
    }
    
    public int getID(){
        return this.id;
    }
    
    public void setDisponivel(boolean disponivel){
        this.disponivel = disponivel;
    }
   
}
