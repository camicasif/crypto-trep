package edu.upb.crypto.trep.modsincronizacion;

import edu.upb.crypto.trep.DataBase.Functions;
import edu.upb.crypto.trep.DataBase.models.Candidato;
import edu.upb.crypto.trep.DataBase.models.Votante;
import edu.upb.crypto.trep.bl.*;
import edu.upb.crypto.trep.modsincronizacion.server.SocketClient;
import edu.upb.crypto.trep.modsincronizacion.server.event.SocketEvent;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PlanificadorTransacciones extends Thread implements SocketEvent {

    private static final ConcurrentLinkedQueue<Comando> messages =new ConcurrentLinkedQueue<>();
    public PlanificadorTransacciones() {

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
                case ComandoCodigo.SINCRONIZACION_NODOS:
                    // Manejo del comando 0001 (ya implementado)
                    break;

                case ComandoCodigo.SINCRONIZACION_CANDIDATOS:
                    procesarComandoSincronizacionCandidatos((SincronizacionCandidatos) comando);
                    break;

                case ComandoCodigo.ALTA_CANDIDATO:
                    procesarComandoAltaCandidato((AltaCandidato) comando);
                    break;
                case ComandoCodigo.ELIMINAR_CANDIDATO:
                    procesarEliminarCandidato((EliminarCandidato) comando);
                    break;
                case ComandoCodigo.SINCRONIZACION_VOTANTES:
                    procesarComandoSincronizacionVotantes((SincronizacionVotantes) comando);

                    break;
                case ComandoCodigo.ALTA_VOTANTE:
                    procesarComandoAltaVotante((AltaVotante) comando);

                    break;
                case ComandoCodigo.ELIMINAR_VOTANTE:
                    procesarEliminarVotante((EliminarVotante) comando);

                    break;
                default:
                    System.out.println("Comando no identificado: " +comando.getCodigoComando());
                    break;
            }
        }
    }

    private void procesarComandoSincronizacionCandidatos(SincronizacionCandidatos comando) {

        List<Candidato> candidatos = Functions.getAllCandidatos();
        comando.setCandidatoes(candidatos);
        PlanificadorMensajesSalida.addMessage(comando);
    }

    private void procesarComandoAltaCandidato(AltaCandidato comando) {

        Functions.insertCandidato(comando.getCandidato().getId(), comando.getCandidato().getNombre());
        PlanificadorMensajesSalida.addMessage(comando);
    }

    private void procesarEliminarCandidato(EliminarCandidato comando) {
        Functions.deleteCandidato(comando.getCodigoCandidato());
        PlanificadorMensajesSalida.addMessage(comando);
    }

    private void procesarComandoSincronizacionVotantes(SincronizacionVotantes comando) {

        List<Votante> votantes = Functions.getAllVotantes();
        comando.setVotantes(votantes);

        PlanificadorMensajesSalida.addMessage(comando);
    }

    private void procesarComandoAltaVotante(AltaVotante comando) {

       Functions.insertVotante(comando.getVotante().getCodigo());
        PlanificadorMensajesSalida.addMessage(comando);
    }

    private void procesarEliminarVotante(EliminarVotante comando) {
        Functions.deleteVotante(comando.getCodigoVotante());
        PlanificadorMensajesSalida.addMessage(comando);
    }


    @Override
    public void onNewNodo(SocketClient client) {
        // no implementar
    }

    @Override
    public void onCloseNodo(SocketClient client) {
        // no implementar

    }

    @Override
    public void onMessage(Comando comando) {
        synchronized (messages){
            messages.add(comando);
            messages.notify();
        }
    }
}
