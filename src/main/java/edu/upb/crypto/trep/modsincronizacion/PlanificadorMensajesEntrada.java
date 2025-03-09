package edu.upb.crypto.trep.modsincronizacion;

import edu.upb.crypto.trep.DataBase.models.Candidato;
import edu.upb.crypto.trep.bl.Comando;
import edu.upb.crypto.trep.bl.SincronizacionCandidatos;
import edu.upb.crypto.trep.modsincronizacion.server.SocketClient;
import edu.upb.crypto.trep.modsincronizacion.server.event.SocketEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PlanificadorMensajesEntrada extends Thread implements SocketEvent {

    private static final ConcurrentLinkedQueue<Comando> messages =new ConcurrentLinkedQueue<>();
    public PlanificadorMensajesEntrada() {

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
            }
            assert comando != null;

            switch (comando.getCodigoComando()) {
                case "0001":
                    // Manejo del comando 0001 (ya implementado)
                    break;

                case "0002":
                    procesarComando2(comando);
                    break;

                // Aquí irán más comandos en el futuro (0003, 0004, etc.)
            }
        }
    }

    private void procesarComando2(Comando comando) {

        //TODO BD sacar los candidatos de una lista
        List<Candidato> candidatos = new ArrayList<>();
        candidatos.add(new Candidato("1", "Alejandra"));
        candidatos.add(new Candidato("2", "Casita"));
        candidatos.add(new Candidato("3", "Lucas"));

        // Crear el mensaje
        Comando respuesta = new SincronizacionCandidatos(candidatos, comando.getIp());

        // Enviar solo al nodo que hizo la petición
        PlanificadorMensajesSalida.addMessage(respuesta);
    }


    @Override
    public void onNewNodo(SocketClient client) {
        // no implementar
    }

    @Override
    public void onCloseNodo(SocketClient client) {

    }

    @Override
    public void onMessage(Comando comando) {
        synchronized (messages){
            messages.add(comando);
            messages.notify();
        }
    }
}
