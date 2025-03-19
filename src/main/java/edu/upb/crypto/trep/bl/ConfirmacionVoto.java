package edu.upb.crypto.trep.bl;

import lombok.Getter;
import lombok.Setter;

import java.util.regex.Pattern;

@Getter
@Setter
public class ConfirmacionVoto extends Comando{
    private String codigoVotante;


    public ConfirmacionVoto(String codigoVotante, String ip) {
        this.setIp(ip);
        this.setCodigoComando(ComandoCodigo.CONFIRMACION_VOTO);
        this.codigoVotante = codigoVotante;
        setPublic(true);
    }
    public ConfirmacionVoto(String ip){
        super();
        this.setCodigoComando(ComandoCodigo.CONFIRMACION_VOTO);
        setIp(ip);
    }

    @Override
    public void parsear(String comando) {
        System.out.println(comando);
        String[] tokens = comando.split(Pattern.quote("|"));
        if(tokens.length == 2){
            setCodigoComando(tokens[0]);
            this.codigoVotante = tokens[1];

        }
    }

    @Override
    public String getComando() {
        return getCodigoComando()+"|"+ getCodigoVotante()+ System.lineSeparator();
    }

}
