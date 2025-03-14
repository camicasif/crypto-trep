package edu.upb.crypto.trep.httpserver;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import edu.upb.crypto.trep.DataBase.Functions;
import edu.upb.crypto.trep.DataBase.models.Votante;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class GetAllVotantesHandler implements HttpHandler {
    static Logger logger = Logger.getLogger(GetAllVotantesHandler.class);
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            List<Votante> votantes = Functions.getAllVotantes();

            JsonArray jsonArray = new JsonArray();
            for (Votante votante : votantes) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("codigo", votante.getCodigo());
                jsonObject.addProperty("llave_privada", votante.getLlavePrivada());
                jsonArray.add(jsonObject);
            }

            String response = jsonArray.toString();

            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes(StandardCharsets.UTF_8));
            }
        }
    }
}