/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package edu.upb.crypto.trep;


import edu.upb.crypto.trep.config.MyProperties;
import edu.upb.crypto.trep.httpserver.ApacheServer;
import edu.upb.crypto.trep.mosincronizacion.server.Server;

import java.io.IOException;


/**
 *
 * @author rlaredo
 */
public class CryptoTrep {

    public static void main(String[] args) throws IOException {
        System.out.println(":::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
        System.out.println(":::::::::::::::: Iniciando Crypto Trep ::::::::::::::::::");
        System.out.println(":::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
        new Server().start();
        new ApacheServer().start();
        System.out.println(":::::::::::::::: Crypto Trep Iniciando ::::::::::::::::::");

        System.out.println(":::::::::::::::: NODO PRINCIPAL: "+MyProperties.IS_NODO_PRINCIPAL+" :::::::::::::::::::");
        System.out.println(":::::::::::::::: IP NODO PRINCIPAL: "+MyProperties.IP_NODO_PRINCIPAL+" :::::::::");
    }
}
