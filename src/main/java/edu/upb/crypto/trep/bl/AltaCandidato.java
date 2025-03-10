package edu.upb.crypto.trep.bl;

import edu.upb.crypto.trep.DataBase.models.Candidato;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Getter
@Setter
public class AltaCandidato extends Comando{
    private Candidato candidato;


    public AltaCandidato(Candidato candidato, String ip) {
        this.setIp(ip);
        this.setCodigoComando(ComandoCodigo.ALTA_CANDIDATO);
        this.candidato = candidato;
        setPublic(true);
    }
    public AltaCandidato(String ip){
        super();
        this.setCodigoComando(ComandoCodigo.ALTA_CANDIDATO);
        setIp(ip);
    }

    @Override
    public void parsear(String comando) {
        System.out.println(comando);
        String[] tokens = comando.split(Pattern.quote("|"));
        if(tokens.length == 2){
            setCodigoComando(tokens[0]);
            this.candidato = new Candidato(tokens[1]);

        }
    }

    @Override
    public String getComando() {
        return getCodigoComando()+"|"+getCandidato()+ System.lineSeparator();
    }

}
