package edu.upb.crypto.trep.httpserver;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import edu.upb.crypto.trep.DataBase.Functions;
import edu.upb.crypto.trep.DataBase.models.Candidato;
import edu.upb.crypto.trep.DataBase.models.Voto;
import edu.upb.crypto.trep.bl.CantidadVotos;
import edu.upb.crypto.trep.modsincronizacion.PlanificadorMensajesSalida;
import lombok.extern.slf4j.Slf4j;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


@Slf4j
public class GetCantidadVotosHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            int cantidadVotos = Functions.getCantidadVotos();

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("cantidad", cantidadVotos);
            PlanificadorMensajesSalida.addMessage(new CantidadVotos());
//            PlanificadorMensajesSalida.addMessage(new RespuestaCantidadVotos(cantidadVotos));

            String response = jsonObject.toString();

            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes(StandardCharsets.UTF_8));
            }
        }
    }
}
