package br.com.sincronismo.server;


import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 *
 * @author 31507646
 */
public class RelogioServerInterfaceImpl extends UnicastRemoteObject implements RelogioServerInterface, Serializable {
    
    private Integer hora;
    private Integer diferenca;
    
    private String ip;
    private int port;
    
    public RelogioServerInterfaceImpl() throws RemoteException {
        Integer i = (int) (Math.random() * 23);
        Integer j = (int) (Math.random() * 59);
        
        hora = (1 * 60) + j;
    }

    @Override
    public Integer getTime() throws RemoteException {
        return (int) System.currentTimeMillis();
    }

    @Override
    public void setTime(Integer t) throws RemoteException {
        this.hora += t;
    }

    @Override
    public void setDiferenca(Integer di) throws RemoteException {
        this.diferenca = di;
    }

    @Override
    public Integer getDiferenca() throws RemoteException {
        return diferenca;
    }

    @Override
    public void random() throws RemoteException {
        
        Integer i = (int) (Math.random() * 23);
        Integer j = (int) (Math.random() * 59);
        
        hora = (i * 60) + j;
    }
    
    @Override
    public void setIpPort(String ipPort) {
        this.ip = ipPort.split(":")[0];
        this.port = Integer.parseInt(ipPort.split(":")[1]);
    }
    
    @Override
    public String[] getIpPort() {
        return new String[] {ip, port + ""};
    }
    
}
