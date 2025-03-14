package edu.upb.crypto.trep.httpserver;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import edu.upb.crypto.trep.DataBase.Functions;
import edu.upb.crypto.trep.DataBase.models.Candidato;
import edu.upb.crypto.trep.DataBase.models.Votante;
import edu.upb.crypto.trep.bl.EliminarCandidato;
import edu.upb.crypto.trep.bl.EliminarVotante;
import edu.upb.crypto.trep.modsincronizacion.PlanificadorMensajesSalida;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class EliminarCandidatoHandler implements HttpHandler {
    static Logger logger = Logger.getLogger(EliminarVotanteHandler.class);
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody();
             OutputStream os = exchange.getResponseBody()) {

            // Leer el cuerpo de la solicitud
            Scanner scanner = new Scanner(is, StandardCharsets.UTF_8.name());
            String requestBody = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";

            JsonObject jsonResponse = new JsonObject();
            int statusCode = 200;

            try {
                // Parsear el JSON
                JsonObject jsonRequest = new com.google.gson.JsonParser().parse(requestBody).getAsJsonObject();
                String id = jsonRequest.get("id").getAsString();

                boolean eliminado = Functions.deleteCandidato(id);

                if (eliminado) {
                    PlanificadorMensajesSalida.addMessage(new EliminarCandidato(id,""));

                    jsonResponse.addProperty("status", "OK");
                    jsonResponse.addProperty("message", "Candidato eliminado correctamente.");
                } else {
                    logger.warn("No se pudo eliminar el votante con código: " + id);
                    jsonResponse.addProperty("status", "ERROR");
                    jsonResponse.addProperty("message", "No se pudo eliminar el votante.");
                    statusCode = 404; // Not Found
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
}
