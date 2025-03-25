package edu.upb.crypto.trep.bl;

public class CantidadVotos extends Comando {

    public CantidadVotos(){
        super();
        this.setCodigoComando(ComandoCodigo.CANTIDAD_REGISTRO_VOTOS);

    }

    public CantidadVotos(String ip){
        super();
        this.setIp(ip);
        this.setCodigoComando(ComandoCodigo.CANTIDAD_REGISTRO_VOTOS);

    }

    @Override
    public void parsear(String comando) {

    }

    @Override
    public String getComando() {
        return getCodigoComando()+System.lineSeparator();
    }
}
