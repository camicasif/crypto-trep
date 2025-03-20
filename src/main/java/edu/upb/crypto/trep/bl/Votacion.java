package edu.upb.crypto.trep.bl;

import edu.upb.crypto.trep.DataBase.models.Voto;
import lombok.Getter;
import lombok.Setter;

import java.util.regex.Pattern;

@Getter
@Setter
public class Votacion extends Comando{


    private Long tiempoCreacion;
    private int cantidadConfirmados = 0;
    private Voto voto;
    private String firma;

    public Votacion(Voto voto, String firma) {
        this.setCodigoComando(ComandoCodigo.VOTACION);
        setPublic(true);
        this.voto = voto;
        this.firma = firma;
        this.tiempoCreacion = System.currentTimeMillis();
    }

    @Override
    public void parsear(String comando) {
        // Dividir el comando usando el delimitador "|"
        String[] tokens = comando.split(Pattern.quote("|"));

        // Verificar que el comando tenga el formato correcto
        if (tokens.length != 3) {
            throw new IllegalArgumentException("Formato de comando inválido. Se esperaban 3 partes separadas por '|'.");
        }

        // Extraer el código del comando (primera parte)
        setCodigoComando(tokens[0]);

        // Extraer los datos del voto (segunda parte)
        String[] datosVoto = tokens[1].split(",");
        if (datosVoto.length != 4) {
            throw new IllegalArgumentException("Formato de datos de voto inválido. Se esperaban 4 valores separados por ','.");
        }

        // Crear un nuevo objeto Voto con los datos extraídos
        String idVoto = datosVoto[0];
        String codigoVotante = datosVoto[1];
        String codigoCandidato = datosVoto[2];
        this.voto = new Voto(idVoto, codigoVotante, codigoCandidato);

        // Extraer la firma (tercera parte)
        this.firma = tokens[2];

        // Inicializar el tiempo de creación con el tiempo actual en milisegundos
        this.tiempoCreacion = System.currentTimeMillis();
    }

    @Override
    public String getComando() {
        return getCodigoComando()+"|"+ getVoto().getId()+","+getVoto().getCodigoVotante()+"," +getVoto().getCodigoCandidato()+","+null+
                "|"+getFirma()+ System.lineSeparator();
    }

}
