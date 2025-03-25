package edu.upb.crypto.trep.bl;

import lombok.Getter;
import lombok.Setter;

import java.util.regex.Pattern;

@Getter
@Setter
public class RespuestaCantidadVotos extends Comando{

    private int CantidadVotos;

    public RespuestaCantidadVotos(int Cantidad) {
        this.setCodigoComando(ComandoCodigo.RESP_CANTIDAD_REGISTRO_VOTOS);
        this.CantidadVotos = Cantidad;
        setPublic(true);
    }
    public RespuestaCantidadVotos(String ip){
        super();
        this.setCodigoComando(ComandoCodigo.RESP_CANTIDAD_REGISTRO_VOTOS);
        setIp(ip);
    }

    @Override
    public void parsear(String comando) {
        String[] tokens = comando.split(Pattern.quote("|"));
        if(tokens.length == 2){
            setCodigoComando(tokens[0]);
            this.CantidadVotos = Integer.parseInt(tokens[1]);

        }
    }

    @Override
    public String getComando() {
        return getCodigoComando()+"|"+getCantidadVotos()+System.lineSeparator();
    }
}
