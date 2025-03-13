package edu.upb.crypto.trep.httpserver;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import edu.upb.crypto.trep.DataBase.Functions;
import edu.upb.crypto.trep.DataBase.models.Candidato;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class GetAllCandidatosHandler implements HttpHandler {
    static Logger logger = Logger.getLogger(GetAllCandidatosHandler.class);

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            List<Candidato> candidatos = Functions.getAllCandidatos();

            JsonArray jsonArray = new JsonArray();
            for (Candidato candidato : candidatos) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("id", candidato.getId());
                jsonObject.addProperty("nombre", candidato.getNombre());
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