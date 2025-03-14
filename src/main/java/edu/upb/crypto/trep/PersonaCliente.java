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
public class PersonaCliente {
    
    public static void main(String[] arg) {
        enviarMensaje();
    }

    public static void enviarMensaje() {
        try (Socket socket = new Socket("127.0.0.1", 1825);
             DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            System.out.println("Cliente conectado...");

//             Enviar un mensaje inicial al servidor
            String mensaje2 = "0002" + System.lineSeparator();
            String mensaje3 = "0003|1,Asv" + System.lineSeparator();
            String mensaje4 = "0004|1" + System.lineSeparator();
//            String mensaje5 = "0005|00045242,dasdsadasdasdas;0136464,fsdfsdfsdfdsfds;0000465,asdasdasdasdasdas" + System.lineSeparator();
            String mensaje5 = "0005" + System.lineSeparator();
            String mensaje6 = "0006|00045242,dasdsadasdasdas" + System.lineSeparator();
            String mensaje7 = "0007|00045242" + System.lineSeparator();



            dataOutputStream.write(mensaje5.getBytes("UTF-8"));
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
