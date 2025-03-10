package edu.upb.crypto.trep.modsincronizacion;

import edu.upb.crypto.trep.DataBase.models.Candidato;
import edu.upb.crypto.trep.DataBase.models.Votante;
import edu.upb.crypto.trep.bl.*;
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

        //TODO BD sacar los candidatos de una lista
        List<Candidato> candidatos = new ArrayList<>();
        candidatos.add(new Candidato("1", "Alejandra"));
        candidatos.add(new Candidato("2", "Casita"));
        candidatos.add(new Candidato("3", "Lucas"));
        comando.setCandidatoes(candidatos);

        PlanificadorMensajesSalida.addMessage(comando);
    }

    private void procesarComandoAltaCandidato(AltaCandidato comando) {

        //TODO BD verificar que no exista un candidato con el mismo codigo

        PlanificadorMensajesSalida.addMessage(comando);
    }

    private void procesarEliminarCandidato(EliminarCandidato comando) {

        //TODO BD verificar que  exista un candidato con el mismo codigo y eliminarlo

        PlanificadorMensajesSalida.addMessage(comando);
    }

    private void procesarComandoSincronizacionVotantes(SincronizacionVotantes comando) {

        //TODO BD sacar los votantes en una lista
        List<Votante> votantes = new ArrayList<>();
        votantes.add(new Votante("00045242", "dasdsadasdasdas"));
        votantes.add(new Votante("0136464", "fsdfsdfsdfdsfds"));
        votantes.add(new Votante("0000465", "asdasdasdasdasdas"));
        comando.setVotantes(votantes);

        PlanificadorMensajesSalida.addMessage(comando);
    }

    private void procesarComandoAltaVotante(AltaVotante comando) {

        //TODO BD verificar que no exista un votante con el mismo codigo

        PlanificadorMensajesSalida.addMessage(comando);
    }

    private void procesarEliminarVotante(EliminarVotante comando) {

        //TODO BD verificar que  exista un votante con el mismo codigo y eliminarlo

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
