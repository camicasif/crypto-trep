package edu.upb.crypto.trep.bl;

import lombok.Getter;
import lombok.Setter;

import java.util.regex.Pattern;

@Getter
@Setter
public class SincronizacionBloques extends Comando{
    private String codigoVotante;


    public SincronizacionBloques(String codigoVotante, String ip) {
        this.setIp(ip);
        this.setCodigoComando(ComandoCodigo.SINCRONIZACION_BLOQUES);
        this.codigoVotante = codigoVotante;
        setPublic(true);
    }
    public SincronizacionBloques(String ip){
        super();
        this.setCodigoComando(ComandoCodigo.SINCRONIZACION_BLOQUES);
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
