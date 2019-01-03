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
    private final double preco; //preço é fixo!
    private final int id;
    private boolean disponivel;
    private boolean leilao;
    private double tempoRes;
    private String owner;
    
    
    public Servidor(String servername, double preco, int id){
        this.servername = servername;
        this.preco = preco;
        this.id = id;
        this.disponivel = true;
        this.leilao = false;
        this.tempoRes = 0;
        this.owner = "";
    }
    
    public boolean getDisponivel(){
        return this.disponivel;
    }
    
    public boolean getLeilao(){
        return this.leilao;
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
    
    public double getTempo(){
        return this.tempoRes;
    }
    
    public double geTempoTotal(){
        return ((System.currentTimeMillis() - this.tempoRes) / (60 * 1000)); 
    }
    
    public String getOwner(){
        return this.owner;
    }
    
    public void setTempoInicial(){
        this.tempoRes = System.currentTimeMillis(); 
    }
    
    public void setDisponivel(boolean disponivel){
        this.disponivel = disponivel;
    }
    
    public void setLeilao(boolean leilao){
        this.leilao = leilao;
    }
    
    public void setOwner(String owner){
        this.owner = owner;
    }
    
    public void setNoOwner(){
        this.owner = "";
    }
   
}
