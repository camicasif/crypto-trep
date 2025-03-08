package edu.upb.crypto.trep.modsincronizacion;

import edu.upb.crypto.trep.bl.Comando;
import edu.upb.crypto.trep.modsincronizacion.server.SocketClient;
import edu.upb.crypto.trep.modsincronizacion.server.event.SocketEvent;

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
            System.out.println(comando.getComando());
            switch (comando.getCodigoComando()){
                case "0001":

                    break;

            }
        }
    }

    private void proceesarComando1(Comando comando){
        // Conectarse a todos los clientes
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
