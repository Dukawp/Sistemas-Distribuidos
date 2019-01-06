/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectcloudserver;

import java.time.LocalDateTime;
import java.time.Duration;

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
    private LocalDateTime tempoRes;
    private String owner;
    private LocalDateTime dataf;
    private double valorL;
    
    public Servidor(String servername, double preco, int id, LocalDateTime df, double valorL){
        this.servername = servername;
        this.preco = preco;
        this.id = id;
        this.disponivel = true;
        this.leilao = false;
        this.tempoRes = LocalDateTime.now();
        this.owner = "";
        this.dataf = df;
        this.valorL = valorL;
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

    public LocalDateTime getDataf() {
        return dataf;
    }

    public void setDataf(LocalDateTime dataf) {
        this.dataf = dataf;
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
    
    public LocalDateTime getTempo(){
        return this.tempoRes;
    }
    
    public long geTempoTotal(){
        return ((Duration.between(this.tempoRes,LocalDateTime.now())).toMinutes());
    }
    
    public String getOwner(){
        return this.owner;
    }
    
    public void setTempoInicial(){
        this.tempoRes = LocalDateTime.now(); 
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
