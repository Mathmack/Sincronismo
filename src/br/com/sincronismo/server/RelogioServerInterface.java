/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sincronismo.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author 31507646
 */
public interface RelogioServerInterface extends Remote {
    
    public Integer getTime() throws RemoteException;
    public void setTime(Integer t) throws RemoteException;
    public void setDiferenca(Integer di) throws RemoteException;
    public Integer getDiferenca() throws RemoteException;
    public void random() throws RemoteException;
    public void setIpPort(String ipPort) throws RemoteException;
    public String[] getIpPort() throws RemoteException;
    
}
