package br.com.sincronismo.client;

import br.com.sincronismo.server.RelogioServerInterface;
import java.io.*;
import java.net.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

class UDPClient {

    static ArrayList<RelogioServerInterface> servers;
    static PrintWriter log = null;

    public static void main(String args[]) throws Exception {
        String masterArgs[] = {"-m", "192.168.0.100:8080", "13:05:10", "15", "slaves", "clock_master_log"};

        String flag = masterArgs[0]; //flag
        String ip = masterArgs[1].split(":")[0]; //ip:port
        String port = masterArgs[1].split(":")[1]; //ip:port
        String time = masterArgs[2]; //time
        String d = masterArgs[3]; //d
        String slaves = masterArgs[4]; //slavesfile
        String logFileStr = masterArgs[5]; //logfile

        try {
            
            File logFile = new File("C:\\Temp\\log\\" + logFileStr + ".txt");
            FileWriter fw = new FileWriter(logFile, true);
            log = new PrintWriter(fw, true);
            servers = new ArrayList<>();
            int media = 0;
            log.println("(" + getHoraAtual() + ") Iniciando a Execução do Client");
            
            BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
            DatagramSocket clientSocket = new DatagramSocket();
            InetAddress IPAddress = InetAddress.getByName(ip);
            byte[] sendData = new byte[1024];
            byte[] receiveData = new byte[1024];
            
            //String sentence = inFromUser.readLine();
            
            declaraCliente(slaves);
            
            int timeInt = converteHora(time);
            int limit = Integer.parseInt(d);
            
            media = calculaMedia(timeInt, limit);
            ajustaTempo(media);
            acertarHorario();
            randomClient();
            
            int i = 0;
            for (RelogioServerInterface server : servers) {
                sendData[i++] = server.getDiferenca().byteValue();
            }
//            RelogioServerInterface relogio = new RelogioServerInterfaceImpl();
//            String clock = TimeControl.getFormat(TimeControl.getNow());
//            sendData = clock.getBytes();

//            sendData = sentence.getBytes();
            log.println("(" + getHoraAtual() + ") Enviando dados ao servidor...");
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);
            clientSocket.send(sendPacket);

            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            clientSocket.receive(receivePacket);
            String modifiedSentence = new String(receivePacket.getData());
            System.out.println("FROM SERVER:" + modifiedSentence);
            clientSocket.close();

        } catch (Exception ex) {
            System.out.println(ex);
            ex.printStackTrace();
        } finally {
            log.println("(" + getHoraAtual() + ") Client Encerrado");
            log.println("-----------------------------------------");
            log.close();
        }
    }

    public static void declaraCliente(String slaves) throws RemoteException, NotBoundException {
        //log.println();
        log.println("(" + getHoraAtual() + ") Cliente(s)");
        ArrayList<String> result = new ArrayList<>();
        try {
            File slavesFile = new File("C:\\Temp\\" + slaves + ".txt");
            FileReader fr = new FileReader(slavesFile);
            BufferedReader br = new BufferedReader(fr);
            String linha = br.readLine();
            while (linha != null) {
                //System.out.println(linha);
                linha = br.readLine();
                result.add(linha);
            }
            br.close();
            fr.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        if (!result.isEmpty()) {
            for (String ipPort : result) {   
                Registry registry = LocateRegistry.getRegistry(ipPort.split(":")[0]);
                RelogioServerInterface c = (RelogioServerInterface) registry.lookup("RelogioServerInterfaceImpl");
                servers.add(c);
                log.println("(" + getHoraAtual() + ") Cliente <" + ipPort +"> adicionado : "+ c.getTime());
            }
        }
    }

    public static int calculaMedia(int time, int d) throws RemoteException {
        log.println("(" + getHoraAtual() + ") Calculando a Media");
        log.println();
        int total = 0;
        for (RelogioServerInterface server : servers) {
            //int diferenca = server.getTime() - servers.get(0).getTime();
            int diferenca = server.getTime() - time;
            if (diferenca < d) {
                server.setDiferenca(diferenca);
            } else {
                server.setDiferenca(0);
            }
            total += server.getDiferenca();
        }
        int media = total / servers.size();
        log.println("(" + getHoraAtual() + ") Media: " + media);
        return media;
    }

    public static void ajustaTempo(int media) throws RemoteException {
        log.println();
        log.println("(" + getHoraAtual() + ") Ajustando o Relogio");
        for (RelogioServerInterface server : servers) {
            server.setTime(media + (-1 * server.getDiferenca()));
            log.println("(" + getHoraAtual() + ") " + media + (-1 * server.getDiferenca()));
        }
    }

    public static void randomClient() throws RemoteException {
        for (RelogioServerInterface server : servers) {
            server.random();
        }
    }

    public static void acertarHorario() throws RemoteException {
        log.println();
        log.println("(" + getHoraAtual() + ") Acertando o horario");
        for (RelogioServerInterface server : servers) {
            int inteira = server.getTime() / 60;
            int resto = server.getTime() % 60;
            log.println("(" + getHoraAtual() + ") " + inteira + ":" + resto);
        }
    }

    private static int converteHora(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        try {
            return (int) (sdf.parse(time).getTime());
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        return -1;
    }
    
    private static String getHoraAtual() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(new Date());
    }
}
