package br.com.sincronismo.server;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;

class UDPServer {

    private static PrintWriter log = null;

    public static void main(String args[]) throws Exception {
        try {
        
        String slaveArgs[] = {"-s", "192.168.0.50:8080", "12:50", "clock_slave_log"};
        String flag = slaveArgs[0];
        String ipPort = slaveArgs[1];
        String time = slaveArgs[2];
        String logFileStr = slaveArgs[3];
        
        File logFile = new File("C:\\Temp\\log\\" + logFileStr + ".txt");
        FileWriter fw = new FileWriter(logFile, true);
        log = new PrintWriter(fw, true);
        log.println("(" + getHoraAtual() + ") Iniciando o servidor...");
        
        DatagramSocket serverSocket = new DatagramSocket(9876);
        byte[] receiveData = new byte[1024];
        byte[] sendData = new byte[1024];
        while (true) {
            
            log.println("(" + getHoraAtual() + ") Servidor iniciado!");
            log.println("(" + getHoraAtual() + ") Recebendo dados do(s) Cliente(s)...");
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(receivePacket);
            String sentence = new String(receivePacket.getData());
            System.out.println("RECEIVED: " + sentence);
            InetAddress IPAddress = receivePacket.getAddress();
            int port = receivePacket.getPort();
            
            log.println("(" + getHoraAtual() + ") Respondendo Cliente(s)...");
            String capitalizedSentence = sentence.toUpperCase();
            sendData = capitalizedSentence.getBytes();
            DatagramPacket sendPacket
                    = new DatagramPacket(sendData, sendData.length, IPAddress, port);
            serverSocket.send(sendPacket);
            log.println("(" + getHoraAtual() + ") Resposta enviada!");
        }
        
//         try {
//            RelogioServerInterface clock = new RelogioServerInterfaceImpl();
//            Registry registro = LocateRegistry.getRegistry();
//            registro.rebind("RelogioServerInterfaceImpl", clock);
//            System.out.println("Servidor Relogio " + clock + " registrado e pronto para aceitar solicitações.");
//        } catch (Exception ex) {
//            System.out.println("Houve um erro: " + ex.getMessage());
//        }
        } finally {
            log.println("(" + getHoraAtual() + ") Servidor encerrado.");
            log.println("--------------------------------------------");
            log.close();
        }
    }

    private static String getHoraAtual() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(new Date());
    }
}
