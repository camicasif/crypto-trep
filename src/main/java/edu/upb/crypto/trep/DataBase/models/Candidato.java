package edu.upb.crypto.trep.DataBase.models;

public class Candidato {
    private String id;
    private String nombre;

    public Candidato(String id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }
    public Candidato(String str) {
        String[] tokens = str.split(",");
        this.id = tokens[0];
        this.nombre = tokens[1];
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    @Override
    public String toString() {
        return id + "," + nombre;
    }

}
