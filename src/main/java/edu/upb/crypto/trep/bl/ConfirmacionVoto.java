package edu.upb.crypto.trep.bl;

import lombok.Getter;
import lombok.Setter;

import java.util.regex.Pattern;

@Getter
@Setter
public class ConfirmacionVoto extends Comando{
    private String idVoto;
    private boolean isCorrect;

    public ConfirmacionVoto(String idVoto, boolean isCorrect,String ip) {
        this.setIp(ip);
        this.setCodigoComando(ComandoCodigo.CONFIRMACION_VOTO);
        this.idVoto = idVoto;
        this.isCorrect = isCorrect;
        setPublic(false);
    }
    public ConfirmacionVoto(String ip){
        super();
        this.setCodigoComando(ComandoCodigo.CONFIRMACION_VOTO);
        setIp(ip);
        setPublic(false);
    }

    @Override
    public void parsear(String comando) {

        String[] primeraDivision = comando.split(Pattern.quote("|"), 2);

        if (primeraDivision.length == 2) {
            setCodigoComando(primeraDivision[0]);

            String[] segundaDivision = primeraDivision[1].split(Pattern.quote(","), 2);

            if (segundaDivision.length == 2) {
                setIdVoto(segundaDivision[0]);
                this.isCorrect = Boolean.parseBoolean(segundaDivision[1]);
            } else {
                throw new IllegalArgumentException("Formato incorrecto después de la coma: " + primeraDivision[1]);
            }
        } else {
            throw new IllegalArgumentException("Formato incorrecto del comando: " + comando);
        }
    }

    @Override
    public String getComando() {
        return getCodigoComando()+"|"+ getIdVoto()+","+isCorrect+ System.lineSeparator();
    }

}
