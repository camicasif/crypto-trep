package edu.upb.crypto.trep.DataBase.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class Voto {
    private String id;
    private String codigoVotante;
    private String codigoCandidato;


    @Override
    public String toString() {
        return id + "," +codigoVotante+","+codigoCandidato;
    }
}
