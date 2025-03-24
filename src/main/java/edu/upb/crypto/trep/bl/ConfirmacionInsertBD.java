package edu.upb.crypto.trep.bl;

import lombok.Getter;
import lombok.Setter;

import java.util.regex.Pattern;

@Getter
@Setter
public class ConfirmacionInsertBD extends Comando{
    private String idVoto;


    public ConfirmacionInsertBD(String idVoto, String ip) {
        this.setIp(ip);
        this.setCodigoComando(ComandoCodigo.CONFIRMACION_INSERT_BD);
        this.idVoto = idVoto;
        setPublic(true);
    }
    public ConfirmacionInsertBD(String ip){
        super();
        this.setCodigoComando(ComandoCodigo.CONFIRMACION_INSERT_BD);
        setIp(ip);
        setPublic(true);
    }

    @Override
    public void parsear(String comando) {
        System.out.println(comando);
        String[] tokens = comando.split(Pattern.quote("|"));
        if(tokens.length == 2){
            setCodigoComando(tokens[0]);
            this.idVoto = tokens[1];

        }
    }

    @Override
    public String getComando() {
        return getCodigoComando()+"|"+ getIdVoto()+ System.lineSeparator();
    }

}
