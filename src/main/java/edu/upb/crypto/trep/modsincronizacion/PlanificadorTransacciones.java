package edu.upb.crypto.trep.modsincronizacion;

import edu.upb.crypto.trep.DataBase.Functions;
import edu.upb.crypto.trep.DataBase.models.Candidato;
import edu.upb.crypto.trep.DataBase.models.Votante;
import edu.upb.crypto.trep.DataBase.models.Voto;
import edu.upb.crypto.trep.bl.*;
import edu.upb.crypto.trep.modsincronizacion.server.SocketClient;
import edu.upb.crypto.trep.modsincronizacion.server.event.SocketEvent;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
@Slf4j
public class PlanificadorTransacciones implements Runnable {

    private static final Map<String, Comando> votos =new HashMap<String,Comando>();

    public PlanificadorTransacciones() {

    }


    @Override
    public void run() {
       for (Comando comando:votos.values()){
           if (comando.getCodigoComando().equals(ComandoCodigo.VOTACION)){
               Votacion vc = (Votacion) comando;
               log.info("Comando id: " + ((Votacion) comando).getVoto().getId());
               //Arreglar esto
               if (System.currentTimeMillis() - vc.getTiempoCreacion() > 10000){
                   removeItem(((Votacion) comando).getVoto().getId());
                   log.info("Tiempo de espera finalizado para el voto: "+((Votacion) comando).getVoto().getId());
               }

               //tiene que tener un metodo por el cual agregamos el planificador de
               //transacciones

               //que deberia hacer cuando recibe el comando 11
                ///obteenr el voto del hashmap y
           }
       }
    }

    public static void removeItem(String key){
        synchronized (votos){

            votos.remove(key);
            votos.notify();


        }
    }

    public static void add(Votacion comando){
        synchronized (votos){

            //ver si existe en bd
            //si no existe devolver con falso

            //si existe agregar a la lista
            votos.put(comando.getVoto().getId(),comando);
            votos.notify();


        }
    }

    public static void commitVoto(ConfirmacionInsertBD comando){
        synchronized (votos) {
            // Verificar si el voto existe en el mapa
            Votacion vc = (Votacion) votos.remove(comando.getIdVoto());

            if (vc == null) {
                // Si el voto no existe, registrar un error en el log
                log.error("No se encontró el voto con ID: " + comando.getIdVoto());
                return; // Salir del método para evitar errores de NullPointerException
            }

            // Si el voto existe, continuar con el proceso
            log.info("Voto listo para registrar a base de datos");
            Voto voto = vc.getVoto();

            // Insertar el voto en la base de datos
            Functions.insertVoto(voto.getId(), voto.getCodigoVotante(), voto.getCodigoCandidato(),
                    vc.getTiempoCreacion());
        }
    }


}
