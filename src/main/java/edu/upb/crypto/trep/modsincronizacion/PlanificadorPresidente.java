package edu.upb.crypto.trep.modsincronizacion;

import edu.upb.crypto.trep.DataBase.Functions;
import edu.upb.crypto.trep.DataBase.models.Candidato;
import edu.upb.crypto.trep.DataBase.models.Votante;
import edu.upb.crypto.trep.DataBase.models.Voto;
import edu.upb.crypto.trep.bl.*;
import edu.upb.crypto.trep.modsincronizacion.PlanificadorMensajesSalida;
import edu.upb.crypto.trep.modsincronizacion.server.SocketClient;
import edu.upb.crypto.trep.modsincronizacion.server.event.SocketEvent;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
@Slf4j

public class PlanificadorPresidente implements Runnable {

    private static final Map<String, Comando> votos =new HashMap<String,Comando>();
    public PlanificadorPresidente() {

    }


    @Override
    public void run() {
        synchronized (votos){
            for (String key: votos.keySet()){
                Votacion comando = (Votacion) votos.get(key);
                log.info(comando.getCantidadConfirmados()+ " cantidad de confirmados");
                if (comando.getCantidadConfirmados() == (PlanificadorMensajesSalida.getCantidadNodos())) {

                    log.info("Ya está con las confirmaciones necesarios. Se envia mensaje a todos" +
                            " los nodos");
                    Voto voto = comando.getVoto();
                    removeItem(voto.getId());
                    PlanificadorMensajesSalida.addMessage(new ConfirmacionInsertBD(voto.getId(),
                            comando.getIp()));
                    Functions.insertVoto(voto.getId(),voto.getCodigoVotante(),voto.getCodigoCandidato(),
                            comando.getTiempoCreacion());
                    continue;
                }

                if ((System.currentTimeMillis() - comando.getTiempoCreacion() )> 10000){
                    log.info("Ya no es valido el registro");

                    removeItem(key);

                    return;
                }



            }
        }
    }

    private void removeItem(String key) {
        synchronized (votos) {
            votos.remove(key);
            votos.notify();
        }

    }

    public static void add(Votacion comando){
        synchronized (votos){

           votos.put(comando.getVoto().getId(),comando);
           votos.notify();
        }
    }

    public static void confirmarVoto(ConfirmacionVoto comando){

        log.info("Cantidad votos: "+ votos.size());
        log.info("Buscando voto con ID: "+comando.getIdVoto());
        synchronized (votos){
            Comando comandoRecuperado = votos.get(comando.getIdVoto());

            if (comandoRecuperado instanceof Votacion comando09) {
                comando09.setCantidadConfirmados(comando09.getCantidadConfirmados() + 1);
            } else {
                log.error("Error: El objeto recuperado no es de tipo Votacion");
            }

            votos.notify();
        }

    }

    public static void add(String idVoto){
        synchronized (votos){
            Votacion cv = (Votacion) votos.get(idVoto);
            cv.setCantidadConfirmados(cv.getCantidadConfirmados()+1);
            votos.notify();
        }
    }


}
