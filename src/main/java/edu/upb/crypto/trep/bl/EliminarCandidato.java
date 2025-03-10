package edu.upb.crypto.trep.bl;

import edu.upb.crypto.trep.DataBase.models.Candidato;
import lombok.Getter;
import lombok.Setter;

import java.util.regex.Pattern;

@Getter
@Setter
public class EliminarCandidato extends Comando{
    private String codigoCandidato;


    public EliminarCandidato(String codigoCandidato, String ip) {
        this.setIp(ip);
        this.setCodigoComando(ComandoCodigo.ELIMINAR_CANDIDATO);
        this.codigoCandidato = codigoCandidato;
        setPublic(true);
    }
    public EliminarCandidato(String ip){
        super();
        this.setCodigoComando(ComandoCodigo.ELIMINAR_CANDIDATO);
        setIp(ip);
    }

    @Override
    public void parsear(String comando) {
        System.out.println(comando);
        String[] tokens = comando.split(Pattern.quote("|"));
        if(tokens.length == 2){
            setCodigoComando(tokens[0]);
            this.codigoCandidato = tokens[1];

        }
    }

    @Override
    public String getComando() {
        return getCodigoComando()+"|"+ getCodigoCandidato()+ System.lineSeparator();
    }

}
