package edu.upb.crypto.trep.bl;

import edu.upb.crypto.trep.DataBase.models.Votante;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Getter
@Setter
public class SincronizacionVotantes extends Comando{
    private List<Votante> votantes;
    public SincronizacionVotantes(List<Votante> votantes) {
        this.setCodigoComando(ComandoCodigo.SINCRONIZACION_VOTANTES);
        this.votantes = votantes;
        setPublic(false);
    }

    public SincronizacionVotantes(String ip){
        super();
        this.setCodigoComando(ComandoCodigo.SINCRONIZACION_VOTANTES);

        setIp(ip);
        setPublic(false);
        this.votantes = new ArrayList<>();
    }

    @Override
    public void parsear(String comando) {
        System.out.println(comando);
        String[] tokens = comando.split(Pattern.quote("|"));
        if(tokens.length == 2){
            setCodigoComando(tokens[0]);
            String[] contAry = tokens[1].split(";");
            for (String s : contAry) {
                this.votantes.add(new Votante(s));
            }
        }
    }

    @Override
    public String getComando() {
        return getCodigoComando()+"|"+ votantesToString() + System.lineSeparator();
    }
    private String votantesToString(){
        if(votantes.isEmpty()) return "";
        StringBuilder str = new StringBuilder();
        str.append(votantes.get(0).toString());
        for (int i = 1; i < votantes.size(); i++) {
            str.append(";");
            str.append(votantes.get(i).toString());
        }

        return str.toString();
    }
}
