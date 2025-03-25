package edu.upb.crypto.trep.bl;

public class CantidadVotos extends Comando {

    public CantidadVotos(){
        super();
    }

    public CantidadVotos(String ip){
        super();
        this.setIp(ip);
    }

    @Override
    public void parsear(String comando) {

    }

    @Override
    public String getComando() {
        return getCodigoComando()+System.lineSeparator();
    }
}
