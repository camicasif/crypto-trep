package edu.upb.crypto.trep.modsincronizacion;

import edu.upb.crypto.trep.DataBase.Functions;
import edu.upb.crypto.trep.DataBase.models.Candidato;
import edu.upb.crypto.trep.DataBase.models.Votante;
import edu.upb.crypto.trep.bl.*;
import edu.upb.crypto.trep.modsincronizacion.PlanificadorMensajesSalida;
import edu.upb.crypto.trep.modsincronizacion.server.SocketClient;
import edu.upb.crypto.trep.modsincronizacion.server.event.SocketEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PlanificadorPresidente implements Runnable {

    private static final Map<String, Comando> votos =new HashMap<String,Comando>();
    public PlanificadorPresidente() {

    }


    @Override
    public void run() {
        synchronized (votos){
            for (String key: votos.keySet()){
                Votacion comando = (Votacion) votos.get(key);
                if ((System.currentTimeMillis() - comando.getTiempoCreacion() )> 10000){
                    System.out.println("Ya no es valido el registro");

                    votos.remove(key);

                    return;
                }
                if (comando.getCantidadConfirmados() ==1){
                    return;
                }


            }
        }
    }


    public static void add(Votacion comando){
        synchronized (votos){

           votos.put(comando.getVoto().getId(),comando);
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
