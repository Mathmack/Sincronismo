package br.com.sincronismo.server;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class UDPServer {

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
                int value = convertByteToInt(receiveData);

                String sentence = new String(receivePacket.getData());
                sentence = value + "";
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

    public static int convertByteToInt(byte[] b) {
        int value = 0;
        for (int i = 0; i < b.length; i++) {
            int n = (b[i] < 0 ? (int) b[i] + 256 : (int) b[i]) << (8 * i);
            value += n;
        }
        return value;
    }
}
