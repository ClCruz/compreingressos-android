package br.com.compreingressos.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luiszacheu on 02/04/15.
 */
public class Espetaculos {
    @SerializedName("espetaculos")
    private ArrayList<Espetaculo> espetaculos = new ArrayList<>();

    public ArrayList<Espetaculo> getEspetaculos() {
        return espetaculos;
    }
}
