package edu.upb.crypto.trep.modsincronizacion;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import edu.upb.crypto.trep.DataBase.Functions;
import edu.upb.crypto.trep.DataBase.models.Candidato;
import edu.upb.crypto.trep.DataBase.models.Votante;
import edu.upb.crypto.trep.DataBase.models.Voto;
import edu.upb.crypto.trep.bl.*;
import edu.upb.crypto.trep.modsincronizacion.server.SocketClient;
import edu.upb.crypto.trep.modsincronizacion.server.event.SocketEvent;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
@Slf4j
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
                    proceesarComando1((SincronizacionNodos) comando);
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

                case ComandoCodigo.SINCRONIZACION_BLOQUES:
                    sincronizarBloques((SincronizacionBloques) comando);

                    break;

                case ComandoCodigo.VOTACION:
                    procesarVotacion((Votacion) comando);

                    break;
                case ComandoCodigo.CONFIRMACION_VOTO:
                    procesarConfirmacionVoto((ConfirmacionVoto) comando);

                    break;
                case ComandoCodigo.CONFIRMACION_INSERT_BD:
                    procesarConfirmacionInsercionBD((ConfirmacionInsertBD) comando);
                    break;

                case ComandoCodigo.CANTIDAD_REGISTROS_VOTOS:
                    procesarCantidadRegistros((ConfirmacionInsertBD) comando);
                    break;

                case ComandoCodigo.MOSTRAR_REGISTROS_VOTOS:
                    procesarMostrarRegistros((ConfirmacionInsertBD) comando);
                    break;

                default:
                    System.out.println("Comando no identificado: " +comando.getCodigoComando());
                    break;
            }
        }
    }

    private void proceesarComando1(SincronizacionNodos comando) {
        // Conectarse a todos los clientes
        for (String ip : comando.getIps()) {
            try {
                log.info("IP A CONECTAR: " + ip);
                if (ip.equals("127.0.0.1") || ip.equals("localhost")) {
                    return;
                }
                if (!isMyIP(ip)) {
                    SocketClient client = new SocketClient(new Socket(ip, 1825));
                    client.start();
                    PlanificadorMensajesSalida.addNode(client);
                    log.info("PME - Conectado al nodo: " + ip);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isMyIP(String ip) {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                if (iface.isLoopback() || !iface.isUp()) {
                    continue;
                }
                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    if (ip.equals(addr.getHostAddress())) {
                        return true;
                    }
                }
            }
        } catch (SocketException e) {
            System.err.println("Error al obtener las interfaces de red: " + e.getMessage());
        }
        return false;
    }


    private void procesarComandoSincronizacionCandidatos(SincronizacionCandidatos comando) {

//        List<Candidato> candidatos = Functions.getAllCandidatos();
        for (Candidato candidato : comando.getCandidatoes()) {
            Functions.insertCandidato(candidato.getId(), candidato.getNombre());
        }
//        comando.setCandidatoes(candidatos);
//        PlanificadorMensajesSalida.addMessage(comando);
    }

    private void procesarComandoAltaCandidato(AltaCandidato comando) {

        Functions.insertCandidato(comando.getCandidato().getId(), comando.getCandidato().getNombre());
//        PlanificadorMensajesSalida.addMessage(comando);
    }

    private void procesarEliminarCandidato(EliminarCandidato comando) {
        Functions.deleteCandidato(comando.getCodigoCandidato());
//        PlanificadorMensajesSalida.addMessage(comando);
    }

    private void procesarComandoSincronizacionVotantes(SincronizacionVotantes comando) {

        for (Votante votante : comando.getVotantes()) {
            System.out.println("Votante "+ votante.getCodigo().toString());
            Functions.insertVotante(votante.getCodigo(), votante.getLlavePrivada());
        }
        //
//        List<Votante> votantes = Functions.getAllVotantes();
//        comando.setVotantes(votantes);

//        PlanificadorMensajesSalida.addMessage(comando);
    }

    private void procesarComandoAltaVotante(AltaVotante comando) {

       Functions.insertVotante(comando.getVotante().getCodigo(), comando.getVotante().getLlavePrivada());
//        PlanificadorMensajesSalida.addMessage(comando);
    }

    private void procesarEliminarVotante(EliminarVotante comando) {
        Functions.deleteVotante(comando.getCodigoVotante());
//        PlanificadorMensajesSalida.addMessage(comando);
    }

    private void sincronizarBloques(SincronizacionBloques comando){


    }

    private void procesarVotacion(Votacion comando){

        Voto voto = comando.getVoto();
        boolean isValidVote = Functions.isVotoValido(voto.getCodigoVotante(),
                voto.getCodigoCandidato());
        if (isValidVote){
            PlanificadorTransacciones.add(comando);
        }
        ConfirmacionVoto cv = new ConfirmacionVoto(comando.getVoto().getId(),isValidVote,comando.getIp());
        PlanificadorMensajesSalida.sendCommand(comando.getIp(), cv);
    }



    private void procesarConfirmacionVoto(ConfirmacionVoto comando){
        if (comando.isCorrect())
            PlanificadorPresidente.confirmarVoto(comando);
    }

    private void procesarConfirmacionInsercionBD(ConfirmacionInsertBD comando){
        PlanificadorTransacciones.commitVoto(comando);

    }

    private void procesarCantidadRegistros(Comando comando){



    }

    private void procesarMostrarRegistros(Comando comando){
        log.info("Comando 13: ",comando.getComando());
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

    public static void onMessage2(Comando comando) {
        synchronized (messages){
            messages.add(comando);
            messages.notify();
        }
    }
}
