package br.com.sincronismo.client;

import br.com.sincronismo.server.RelogioServerInterface;
import br.com.sincronismo.server.RelogioServerInterfaceImpl;
import java.io.*;
import java.net.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.JOptionPane;

public class UDPClient {

    static ArrayList<RelogioServerInterface> serversClientes;
    static PrintWriter log = null;

    public static void main(String args[]) throws Exception {
        String masterArgs[] = {"-m", "192.168.0.100:8080", "13:05:10", "15", "slaves", "clock_master_log"};

        String flag = masterArgs[0]; //flag
        String ip = masterArgs[1].split(":")[0]; //ip:port
        int port = Integer.parseInt(masterArgs[1].split(":")[1]); //ip:port
        String time = masterArgs[2]; //time
        String d = masterArgs[3]; //d
        String slaves = masterArgs[4]; //slavesfile
        String logFileStr = masterArgs[5]; //logfile

        ByteArrayOutputStream outByte = null;
        ObjectOutputStream oos = null;
        while (JOptionPane.showConfirmDialog(null, "Deseja avaliar o horario novamente ?") == JOptionPane.YES_OPTION) {
            try {

                File logFile = new File("C:\\Temp\\log\\" + logFileStr + ".txt");
                FileWriter fw = new FileWriter(logFile, true);
                log = new PrintWriter(fw, true);
                serversClientes = new ArrayList<>();
                log.println("-----------------------------------------");
                log.println("(" + getHoraAtual() + ") Iniciando a Execução do Client");

                BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
                DatagramSocket clientSocket = new DatagramSocket();
                InetAddress IPAddress = InetAddress.getByName("localhost");
                byte[] sendData = new byte[1024];
                byte[] receiveData = new byte[1024];

                declaraCliente(slaves);

                sendData = respostaCliente(time, d);

                outByte = new ByteArrayOutputStream();
                oos = new ObjectOutputStream(outByte);
                oos.writeObject(serversClientes.toArray());
                oos.flush();

                //sendData = outByte.toByteArray();
                //sendData = ("Olá, sou o Cliente").getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);
                System.out.println("SEND-DATA: " + sendData);
                clientSocket.send(sendPacket);

                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length, IPAddress, 9876);
                clientSocket.receive(receivePacket);
                
                log.println("(" + getHoraAtual() + ") Resposta do Servidor");
                log.println("(" + getHoraAtual() + ") Ajustando o(s) relogio(s) do(s) Cliente(s)...");
                String modifiedSentence = new String(receivePacket.getData());
                System.out.println("FROM SERVER:" + modifiedSentence);

                int timeInt = converteHora(time);
                int limit = Integer.parseInt(d);
                int media = calculaMedia(timeInt, limit);
                ajustaTempo(media);
                acertarHorario();
                randomClient();
                log.println("(" + getHoraAtual() + ") Processamento Concluido!");
                clientSocket.close();

            } catch (Exception ex) {
                System.out.println(ex);
                ex.printStackTrace();
            } finally {

                if (oos != null) {
                    oos.close();
                }
                if (outByte != null) {
                    outByte.close();
                }

                log.println("(" + getHoraAtual() + ") Client Encerrado");
                log.println("-----------------------------------------");
                log.close();
            }
        }
    }

    public static void declaraCliente(String slaves) throws RemoteException, NotBoundException {
        //log.println();
        log.println("(" + getHoraAtual() + ") Cliente(s)");
        ArrayList<String> result = new ArrayList<>();
        if (JOptionPane.showConfirmDialog(null, "Execução Local ?") == JOptionPane.YES_OPTION) {
            RelogioServerInterface c = new RelogioServerInterfaceImpl();
            c.setIpPort("localhost:8080");
            serversClientes.add(c);
            log.println("(" + getHoraAtual() + ") Execução de Cliente Local");
            return;
        }
        log.println("(" + getHoraAtual() + ") Execução de Cliente UDP");
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
                RelogioServerInterface c = new RelogioServerInterfaceImpl();
                c.setIpPort(ipPort);
                serversClientes.add(c);
                log.println("(" + getHoraAtual() + ") Cliente <" + ipPort + "> adicionado : " + c.getTime());
            }
        }
    }

    public static int calculaMedia(int time, int d) throws RemoteException {
        log.println("(" + getHoraAtual() + ") Calculando a Media");
        log.println();
        int total = 0;
        for (RelogioServerInterface server : serversClientes) {
            //int diferenca = server.getTime() - servers.get(0).getTime();
            int diferenca = server.getTime() - time;
            if (diferenca < d) {
                server.setDiferenca(diferenca);
            } else {
                server.setDiferenca(0);
            }
            total += server.getDiferenca();
        }
        int media = total / serversClientes.size();
        log.println("(" + getHoraAtual() + ") Media: " + media);
        return media;
    }

    public static void ajustaTempo(int media) throws RemoteException {
        log.println();
        log.println("(" + getHoraAtual() + ") Ajustando o Relogio");
        for (RelogioServerInterface server : serversClientes) {
            server.setTime(media + (-1 * server.getDiferenca()));
            log.println("(" + getHoraAtual() + ") " + media + (-1 * server.getDiferenca()));
        }
    }

    public static void randomClient() throws RemoteException {
        for (RelogioServerInterface server : serversClientes) {
            server.random();
        }
    }

    public static void acertarHorario() throws RemoteException {
        log.println();
        log.println("(" + getHoraAtual() + ") Acertando o horario");
        for (RelogioServerInterface server : serversClientes) {
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

    public static byte[] respostaCliente(String time, String d) {
        byte[] sendData = new byte[1024];
        try {

            int i = 0;
            for (RelogioServerInterface server : serversClientes) {
                System.out.println("Diferença: " + server.getDiferenca());
                sendData[i++] = server.getDiferenca().byteValue();
            }
//            RelogioServerInterface relogio = new RelogioServerInterfaceImpl();
//            String clock = TimeControl.getFormat(TimeControl.getNow());
//            sendData = clock.getBytes();

//            sendData = sentence.getBytes();
            log.println("(" + getHoraAtual() + ") Enviando dados ao servidor...");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return sendData;
    }
}
