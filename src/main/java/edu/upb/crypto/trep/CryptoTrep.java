/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package edu.upb.crypto.trep;


import edu.upb.crypto.trep.config.MyProperties;
import edu.upb.crypto.trep.httpserver.ApacheServer;
import edu.upb.crypto.trep.modsincronizacion.PlanificadorMensajesEntrada;
import edu.upb.crypto.trep.modsincronizacion.PlanificadorMensajesSalida;
import edu.upb.crypto.trep.modsincronizacion.server.Server;
import edu.upb.crypto.trep.modsincronizacion.server.SocketClient;

import java.io.IOException;
import java.net.Socket;


/**
 *
 * @author rlaredo
 */
public class CryptoTrep {

    public static void main(String[] args) throws IOException {
        System.out.println(":::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
        System.out.println(":::::::::::::::: Iniciando Crypto Trep ::::::::::::::::::");
        System.out.println(":::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
        PlanificadorMensajesSalida ps = new PlanificadorMensajesSalida();
        ps.start();
        PlanificadorMensajesEntrada pe = new PlanificadorMensajesEntrada();
        pe.start();

        Server server = new Server();
        server.start();
        server.addListener(ps);// Planificador de salida se suscribe a los eventos del server
        server.addPlanificadorEntrada(pe);
        ApacheServer apacheServer = new ApacheServer();
        apacheServer.start();

        System.out.println(":::::::::::::::: Crypto Trep Iniciando ::::::::::::::::::");
        System.out.println(":::::::::::::::: NODO PRINCIPAL: "+MyProperties.IS_NODO_PRINCIPAL+" :::::::::::::::::::");
        System.out.println(":::::::::::::::: IP NODO PRINCIPAL: "+MyProperties.IP_NODO_PRINCIPAL+" :::::::::");
        if(!MyProperties.IS_NODO_PRINCIPAL){
            SocketClient socketClient = new SocketClient(new Socket(MyProperties.IP_NODO_PRINCIPAL, 1825));
            socketClient.start();
            ps.onNewNodo(socketClient);
            socketClient.addListerner(pe);

        }

    }
}
