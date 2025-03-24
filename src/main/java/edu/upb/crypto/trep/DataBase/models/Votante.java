package edu.upb.crypto.trep.DataBase.models;

import edu.upb.crypto.trep.Utils;

public class Votante {
    private String codigo;
    private String llavePrivada;

    public Votante(String codigo, String llavePrivada) {
        this.codigo = codigo;
        this.llavePrivada = llavePrivada;
    }
    public Votante(String str) {
        System.out.println("votante: " + str);
        String[] tokens = str.split(",");
        this.codigo = tokens[0];

        // Validación para la llave privada
        if (tokens.length > 1 && tokens[1] != null && !tokens[1].isEmpty()) {
            this.llavePrivada = tokens[1];  // Usa la llave proporcionada
        } else {
            String llavePrivada = Utils.generateUniqueKey();  // Genera una nueva llave
            this.llavePrivada = llavePrivada;
            System.out.println("Se generó una nueva llave privada: " + llavePrivada);
        }
    }
    public String getCodigo() {
        return codigo;
    }

    public String getLlavePrivada() {
        return llavePrivada;
    }

    @Override
    public String toString() {
        return codigo + "," + llavePrivada;
    }
}
