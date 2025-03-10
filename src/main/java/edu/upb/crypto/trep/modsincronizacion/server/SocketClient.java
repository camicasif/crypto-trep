/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.upb.crypto.trep.modsincronizacion.server;


import edu.upb.crypto.trep.bl.*;
import edu.upb.crypto.trep.modsincronizacion.PlanificadorMensajesSalida;
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
                Comando comando = null;
                switch (tokens[0]) {
                    case ComandoCodigo.SINCRONIZACION_NODOS:
                        comando = new SincronizacionNodos(this.ip);
                        comando.parsear(message);
                        break;
                    case ComandoCodigo.SINCRONIZACION_CANDIDATOS:
                        comando = new SincronizacionCandidatos(this.ip);
                        comando.parsear(message);
                        break;
                    case ComandoCodigo.ALTA_CANDIDATO:
                        comando = new AltaCandidato(this.ip);
                        comando.parsear(message);
                        break;
                    case ComandoCodigo.ELIMINAR_CANDIDATO:
                        comando = new EliminarCandidato(this.ip);
                        comando.parsear(message);
                        break;
                    case ComandoCodigo.SINCRONIZACION_VOTANTES:
                        comando = new SincronizacionVotantes(this.ip);
                        comando.parsear(message);
                        break;
                    case ComandoCodigo.ALTA_VOTANTE:
                        comando = new AltaVotante(this.ip);
                        comando.parsear(message);
                        break;
                    case ComandoCodigo.ELIMINAR_VOTANTE:
                        comando = new EliminarVotante(this.ip);
                        comando.parsear(message);
                        break;
                    default:
                        System.out.println("Comando no identificado: "+ message);
                        break;

                }

                notificar(comando);
            }
        } catch (IOException e) {
            System.err.println("Error en la conexión con el nodo: " + ip);
            eliminarSocket(this); // Eliminar el nodo de la lista en
            e.printStackTrace();
        } finally {
            try {
                if (br != null) br.close();
                if (dout != null) dout.close();
                if (socket != null) socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
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

    public void eliminarSocket(SocketClient socketClient) {
        for (SocketEvent e : listenerList.getListeners(SocketEvent.class)) {
            e.onCloseNodo(socketClient);
        }
    }


}
