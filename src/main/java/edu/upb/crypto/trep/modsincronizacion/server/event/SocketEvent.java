/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package edu.upb.crypto.trep.modsincronizacion.server.event;

import edu.upb.crypto.trep.bl.Comando;
import edu.upb.crypto.trep.modsincronizacion.server.SocketClient;

import java.util.EventListener;

/**
 *
 * @author lucas
 */
public interface SocketEvent extends EventListener {
    void onNewNodo(SocketClient client);
    void onCloseNodo(SocketClient client);
    void onMessage(Comando comando);
}
