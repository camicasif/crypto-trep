package edu.upb.crypto.trep.bl;

import edu.upb.crypto.trep.DataBase.models.Candidato;
import edu.upb.crypto.trep.DataBase.models.Votante;
import lombok.Getter;
import lombok.Setter;

import java.util.regex.Pattern;

@Getter
@Setter
public class AltaVotante extends Comando{
    private Votante votante;


    public AltaVotante(Votante votante, String ip) {
        this.setIp(ip);
        this.setCodigoComando(ComandoCodigo.ALTA_VOTANTE);
        this.votante = votante;
        setPublic(true);
    }
    public AltaVotante(String ip){
        super();
        this.setCodigoComando(ComandoCodigo.ALTA_VOTANTE);
        setIp(ip);
    }

    @Override
    public void parsear(String comando) {
        System.out.println(comando);
        String[] tokens = comando.split(Pattern.quote("|"));
        if(tokens.length == 2){
            setCodigoComando(tokens[0]);
            this.votante = new Votante(tokens[1]);

        }
    }

    @Override
    public String getComando() {
        return getCodigoComando()+"|"+ getVotante()+ System.lineSeparator();
    }

}
