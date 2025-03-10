package edu.upb.crypto.trep.DataBase.models;

public class Votante {
    private String codigo;
    private String llavePrivada;

    public Votante(String codigo, String llavePrivada) {
        this.codigo = codigo;
        this.llavePrivada = llavePrivada;
    }
    public Votante(String str) {
        String[] tokens = str.split(",");
        this.codigo = tokens[0];
        this.llavePrivada = tokens[1];
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
