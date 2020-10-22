package com.example.jose.gps30;

import java.io.Serializable;

public class Mark implements Serializable {
    public String pais;
    public String provincia;
    public String localidad;

    public Mark(String pais, String provincia, String localidad){
        this.pais = pais;
        this.provincia = provincia;
        this.localidad = localidad;
    }

    public String getPais() {
        return pais;
    }

    public String getLocalidad() {
        return localidad;
    }

    public String getProvincia() {
        return provincia;
    }
}
