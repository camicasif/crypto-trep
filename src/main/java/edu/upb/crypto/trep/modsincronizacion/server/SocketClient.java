/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.upb.crypto.trep.modsincronizacion.server;


import edu.upb.crypto.trep.bl.Comando;
import edu.upb.crypto.trep.bl.SincronizacionCandidatos;
import edu.upb.crypto.trep.bl.SincronizacionNodos;
import edu.upb.crypto.trep.modsincronizacion.server.event.SocketEvent;
import lombok.Getter;

import javax.swing.event.EventListenerList;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

/**
 * @author rlaredo
 */
@Getter
public class SocketClient extends Thread {
    private static final EventListenerList listenerList = new EventListenerList();
    private final Socket socket;
    private final String ip;
    private final DataOutputStream dout;
    private final BufferedReader br;


    public SocketClient(Socket socket) throws IOException {
        this.socket = socket;
        this.ip = socket.getInetAddress().getHostAddress();
        dout = new DataOutputStream(socket.getOutputStream());
        br = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
    }

    @Override
    public void run() {
        try {
            String message;
            while ((message = br.readLine()) != null) {
                String[] tokens = message.split(Pattern.quote("|"));
                System.out.println(message);
                Comando comando = null;
                switch (tokens[0]) {
                    case "0001":
                        comando = new SincronizacionNodos(this.ip);
                        comando.parsear(message);
                        break;
                    case "0002":
                        comando = new SincronizacionCandidatos(this.ip);
                        comando.parsear(message);
                        break;

                }

                notificar(comando);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void send(String mensaje) throws IOException {
        try {
            dout.write(mensaje.getBytes(StandardCharsets.UTF_8));
            dout.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addListerner(SocketEvent e) {
        listenerList.add(SocketEvent.class, e);
    }

    public void notificar(Comando comando) {
        for (SocketEvent e : listenerList.getListeners(SocketEvent.class)) {
            e.onMessage(comando);
        }
    }


}