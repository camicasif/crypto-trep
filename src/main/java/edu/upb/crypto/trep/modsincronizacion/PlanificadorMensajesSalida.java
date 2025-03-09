package edu.upb.crypto.trep.modsincronizacion;

import edu.upb.crypto.trep.DataBase.models.Candidato;
import edu.upb.crypto.trep.bl.Comando;
import edu.upb.crypto.trep.bl.SincronizacionCandidatos;
import edu.upb.crypto.trep.bl.SincronizacionNodos;
import edu.upb.crypto.trep.config.MyProperties;
import edu.upb.crypto.trep.modsincronizacion.server.SocketClient;
import edu.upb.crypto.trep.modsincronizacion.server.event.SocketEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

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

    private void sendMessage(Comando comando) {
        if (comando.getIp() != null) {
            // Enviar solo a un nodo específico
            SocketClient nodo = nodos.get(comando.getIp());
            if (nodo != null) {
                try {
                    nodo.send(comando.getComando());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            // Enviar a todos los nodos
            for (SocketClient nodo : nodos.values()) {
                try {
                    nodo.send(comando.getComando());
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
            try {
                client.send(comando.getComando());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onCloseNodo(SocketClient client) {

    }

    @Override
    public void onMessage(Comando comando) {
        // no implementar
    }
}
