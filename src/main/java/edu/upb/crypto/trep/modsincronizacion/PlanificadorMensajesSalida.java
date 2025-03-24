package edu.upb.crypto.trep.modsincronizacion;

import edu.upb.crypto.trep.DataBase.Functions;
import edu.upb.crypto.trep.DataBase.models.Candidato;
import edu.upb.crypto.trep.DataBase.models.Votante;
import edu.upb.crypto.trep.bl.Comando;
import edu.upb.crypto.trep.bl.SincronizacionCandidatos;
import edu.upb.crypto.trep.bl.SincronizacionNodos;
import edu.upb.crypto.trep.bl.SincronizacionVotantes;
import edu.upb.crypto.trep.config.MyProperties;
import edu.upb.crypto.trep.modsincronizacion.server.SocketClient;
import edu.upb.crypto.trep.modsincronizacion.server.event.SocketEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.*;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class PlanificadorMensajesSalida extends Thread implements SocketEvent {


    private static final ConcurrentLinkedQueue<Comando> messages =new ConcurrentLinkedQueue<>();
    private static final ConcurrentHashMap<String, SocketClient> nodos =new ConcurrentHashMap<>();
    public PlanificadorMensajesSalida() {

    }

    @Override
    public void run() {
        while (true) {
            Comando comando ;
            synchronized (messages) {
                if (messages.isEmpty()) {
                    try {
                        messages.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                comando = messages.poll();
                sendMessage(comando);
            }
        }
    }

    public static void addMessage(Comando comando){
        synchronized (messages){
            messages.add(comando);
            messages.notify();
        }
    }

//    private void sendMessage(Comando comando){
//        Iterator<SocketClient> iterator = nodos.values().iterator();
//        while (iterator.hasNext()) {
//            SocketClient nodo = iterator.next();
//            try {
//                nodo.send(comando.getComando());
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//        }
//
//    }

    public static void sendCommand(String ip, Comando comando) {
        SocketClient client = nodos.get(ip);
        if (client != null) {
            client.send(comando);
        }
    }


    private void sendMessage(Comando comando) {
        for (SocketClient nodo : nodos.values()) {
            if (!nodo.isConnected()) {
                log.info("Eliminando nodo porque no esta conectado: {}", nodo.getIp());

                nodos.remove(nodo.getIp());
                return;
            }

            try {
                nodo.send(comando);
                log.info("Comando [ {} ] Enviado a IP:{}", comando.getComando(), nodo.getIp());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
    @Override
    public void onNewNodo(SocketClient client) {
        synchronized (nodos) {
            nodos.put(client.getIp(), client);
        }
        if (MyProperties.IS_NODO_PRINCIPAL) {
            List<String> listaIps = new ArrayList<>(nodos.keySet());
            Comando comando = new SincronizacionNodos(listaIps);

            List<Candidato> candidatos = Functions.getAllCandidatos();
            SincronizacionCandidatos sincronizacionCandidatos = new SincronizacionCandidatos(candidatos);
            List<Votante> votantes = Functions.getAllVotantes();
            SincronizacionVotantes sincronizacionVotantes = new SincronizacionVotantes(votantes);

            try {
                client.send(comando.getComando());
                client.send(sincronizacionCandidatos.getComando());
                client.send(sincronizacionVotantes.getComando());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onCloseNodo(SocketClient client) {
        //Todo
//        synchronized (nodos) {
//            nodos.remove(client.getIp());
//            System.out.println("Nodo " + client.getIp() + " removido de la lista de nodos.");
//        }
    }

    @Override
    public void onMessage(Comando comando) {
        // no implementar
    }

    public static void removeCliente(String ip) {
        synchronized (nodos) {
            nodos.remove(ip);
            System.out.println("Eliminando nodo");
        }
    }
    public static int getCantidadNodos(){
        return nodos.size();
    }
}
