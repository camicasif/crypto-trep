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

    private void sendMessage(Comando comando){
        Iterator<SocketClient> iterator = nodos.values().iterator();
        while (iterator.hasNext()) {
            SocketClient nodo = iterator.next();
            try {
                nodo.send(comando.getComando());
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }


    @Override
    public void onNewNodo(SocketClient client) {
        synchronized (nodos) {
            nodos.put(client.getIp(), client);
        }
        System.out.println("Nuevo Nodo " + client.getIp());
        if(MyProperties.IS_NODO_PRINCIPAL){
            System.out.println("Es nodo pro " + client.getIp());
            // preparar el comando y enviar  a todo
            Comando comando = new SincronizacionNodos(new ArrayList<>(nodos.keySet()));
            try {
                client.send(comando.getComando());
            }catch (Exception e){
                e.printStackTrace();
            }
            // enviar candidatos
            List<Candidato> candidatoes = new ArrayList<>();
            candidatoes.add(new Candidato("1", "Alejandra"));
            candidatoes.add(new Candidato("2", "Casita"));
            candidatoes.add(new Candidato("3", "Lucas"));
            comando = new SincronizacionCandidatos(candidatoes);
            try {
                client.send(comando.getComando());
            }catch (Exception e){
                e.printStackTrace();
            }
            // enviar votantes

            // enviar bloques
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
