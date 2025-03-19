package edu.upb.crypto.trep.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

public class MyProperties {
    public static boolean IS_NODO_PRINCIPAL = false;
    public static String IP_NODO_PRINCIPAL;
    public static String SECRET_KEY= "ed2eea4451174aeb9161e0cc1fdf304d4982b18497a6e2842ee3f27ea0948d28";
    static {
        Properties prop = new Properties();
        try {
            prop.load(new InputStreamReader(new FileInputStream("etc/trep.properties"), "UTF-8"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        IS_NODO_PRINCIPAL = Boolean.parseBoolean(prop.getProperty("nodo.principal"));
        IP_NODO_PRINCIPAL = prop.getProperty("nodo.principal.ip");
    }
    private MyProperties() {}
}
