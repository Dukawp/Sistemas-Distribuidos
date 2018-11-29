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
public class ClienteExistenteException extends Exception {

    public ClienteExistenteException() {
        super();
    }
    
    public ClienteExistenteException(String m) {
        super(m);
    }
}
