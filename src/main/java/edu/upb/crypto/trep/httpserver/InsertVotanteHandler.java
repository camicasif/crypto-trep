package edu.upb.crypto.trep.httpserver;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import edu.upb.crypto.trep.DataBase.Functions;
import edu.upb.crypto.trep.DataBase.models.Candidato;
import edu.upb.crypto.trep.DataBase.models.Votante;
import edu.upb.crypto.trep.Utils;
import edu.upb.crypto.trep.bl.AltaCandidato;
import edu.upb.crypto.trep.bl.AltaVotante;
import edu.upb.crypto.trep.config.MyProperties;
import edu.upb.crypto.trep.modsincronizacion.PlanificadorMensajesSalida;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.apache.log4j.Logger;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class InsertVotanteHandler implements HttpHandler {
    static Logger logger = Logger.getLogger(InsertVotanteHandler.class);
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody();
             OutputStream os = exchange.getResponseBody()) {
//            String xSignature = exchange.getRequestHeaders().get("X-Signature").get(0);
            // Leer el cuerpo de la solicitud
            Scanner scanner = new Scanner(is, StandardCharsets.UTF_8.name());
            String requestBody = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";

            JsonObject jsonResponse = new JsonObject();
            int statusCode = 200;

            try {
                // Parsear el JSON
                JsonObject jsonRequest = new com.google.gson.JsonParser().parse(requestBody).getAsJsonObject();
                String codigo = jsonRequest.get("codigo").getAsString();

                // Intentar insertar en la base de datos
                String llavePrivada = Functions.insertVotante(codigo);

//                String hmac = new HmacUtils(HmacAlgorithms.HMAC_SHA_256,
//                        MyProperties.SECRET_KEY.getBytes(StandardCharsets.UTF_8))
//                        .hmacHex(requestBody.getBytes(StandardCharsets.UTF_8));
//                if(xSignature.equals(hmac)){
//                    // OK
//                    System.out.println("Firma exitosa");
//                }

                if (llavePrivada != null && !llavePrivada.isEmpty()) {
                    // Si la inserción fue exitosa, añadimos el mensaje de salida
                    PlanificadorMensajesSalida.addMessage(new AltaVotante(new Votante(codigo, llavePrivada), ""));

                    jsonResponse.addProperty("status", "OK");
                    jsonResponse.addProperty("llave_privada", llavePrivada);
                } else {
                    // Si falla la inserción (null o valor inesperado)
                    logger.warn("Inserción de votante fallida para el código: " + codigo);
                    jsonResponse.addProperty("status", "ERROR");
                    jsonResponse.addProperty("message", "No se pudo registrar el votante.");
                    statusCode = 400;
                }

            } catch (com.google.gson.JsonSyntaxException e) {
                logger.error("Error al parsear JSON", e);
                jsonResponse.addProperty("status", "ERROR");
                jsonResponse.addProperty("message", "Formato JSON inválido.");
                statusCode = 400;

            } catch (Exception e) {
                logger.error("Error interno al insertar votante", e);
                jsonResponse.addProperty("status", "ERROR");
                jsonResponse.addProperty("message", "Error interno del servidor.");
                statusCode = 500;
            }

            // Enviar respuesta final
            String response = jsonResponse.toString();
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(statusCode, response.getBytes(StandardCharsets.UTF_8).length);
            os.write(response.getBytes(StandardCharsets.UTF_8));

        } finally {
            exchange.close(); // Cerrar intercambio sí o sí
        }
    }

//    @Override
//    public void handle(HttpExchange exchange) throws IOException {
//        try (InputStream is = exchange.getRequestBody();
//             OutputStream os = exchange.getResponseBody()) {
//
//            // Read request body
//            Scanner scanner = new Scanner(is, StandardCharsets.UTF_8.name());
//            String requestBody = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
//
//            // Parse JSON
//            JsonObject jsonRequest = new com.google.gson.JsonParser().parse(requestBody).getAsJsonObject();
//            String codigo = jsonRequest.get("codigo").getAsString();
//
//            // Insert into database
//            String llavePrivada = Functions.insertVotante(codigo);
//
//            PlanificadorMensajesSalida.addMessage(new AltaVotante(new Votante(codigo,llavePrivada),""));
//
//            // Send response
//            JsonObject jsonResponse = new JsonObject();
//            jsonResponse.addProperty("status", "OK");
//            jsonResponse.addProperty("llave_privada", llavePrivada);
//            String response = jsonResponse.toString();
//
//            exchange.getResponseHeaders().add("Content-Type", "application/json");
//            exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);
//            os.write(response.getBytes(StandardCharsets.UTF_8));
//
//        } catch (Exception e) {
//            logger.error("Error inserting Votante", e);
//            exchange.sendResponseHeaders(500, 0); // Send error response
//        } finally {
//            exchange.close(); // Close the exchange explicitly
//        }
//    }
}
