package edu.upb.crypto.trep;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 *
 * @author Camila
 */
public class PersonaCliente2 {
    
    public static void main(String[] arg) {
        enviarMensaje();
    }

    public static void enviarMensaje() {
        try (Socket socket = new Socket("172.16.73.202", 1825);
             DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            System.out.println("Cliente conectado...");

            // Enviar un mensaje inicial al servidor
            String mensaje1 = "0001" + System.lineSeparator();
            dataOutputStream.write(mensaje1.getBytes("UTF-8"));
            dataOutputStream.flush();

            // Leer respuesta del servidor en bucle
            String respuesta;
            while ((respuesta = reader.readLine()) != null) {
                System.out.println("Respuesta del servidor: " + respuesta);

                // Salir si el servidor envía "bye"
                if ("bye".equalsIgnoreCase(respuesta.trim())) {
                    System.out.println("El servidor ha finalizado la comunicación.");
                    break;
                }
            }

        } catch (IOException e) {
            System.err.println("Error en la comunicación con el servidor: " + e.getMessage());
        }
    }



}
