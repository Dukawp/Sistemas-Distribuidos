/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectcloudserver;

import java.util.Date;
import java.util.concurrent.locks.ReentrantLock;

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
    private Date dataf;
    private double valorL;
    
    
    public Servidor(String servername, double preco, int id){
        this.servername = servername;
        this.preco = preco;
        this.id = id;
        this.disponivel = true;
        this.leilao = false;
        this.tempoRes = 0;
    }
    
    public Servidor(String servername, double preco, int id,double valor,Date df){
        this.servername = servername;
        this.preco = preco;
        this.id = id;
        this.disponivel = true;
        this.leilao = true;
        this.valorL = valor;
        this.dataf = df;
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
    
    public void setTempoInicial(){
        this.tempoRes = System.currentTimeMillis(); 
    }
    
    public void setDisponivel(boolean disponivel){
        this.disponivel = disponivel;
    }
    
    public void setLeilao(boolean leilao){
        this.leilao = leilao;
    }
    
    public double getValorL() {
        return valorL;
    }

    public void setValorL(double valorL) {
        this.valorL = valorL;
    }
    
    public void addValorL() {
        this.valorL = this.getValorL() + 0.05;
    }

    public Date getDataf() {
        return dataf;
    }

    public void setDataf(Date dataf) {
        this.dataf = dataf;
    }
   
}
