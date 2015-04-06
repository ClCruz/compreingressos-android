package br.com.compreingressos.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by luiszacheu on 02/04/15.
 */
public class Espetaculo {

    public final static String TITULO =  "titulo";
    public final static String GENERO = "genero";
    public final static String TEATRO = "teatro";
    public final static String CIDADE = "cidade";
    public final static String ESTADO = "estado";
    public final static String MINIATURA =  "miniatura";
    public final static String URL = "url";
    public final static String DATA = "data";
    public final static String RELEVANCIA  = "relevancia";
    public final static String UPDATED_AT = "updated_at";
    public final static String TEATRO_ID = "teatro_id";
    public final static String DISTANCE = "distance";


    private String titulo;
    private String genero;
    @SerializedName("nome_teatro")
    private String teatro;
    private String cidade;
    private String estado;
    private String miniatura;
    private String url;
//    private Date data;
    private int relevancia;
//    private Date updated_at;
    @SerializedName("teatro_id")
    private int teatroId;
    private long distance;

    public Espetaculo(){

    }

    public Espetaculo(String titulo, String genero, String teatro, String cidade, String estado, String miniatura, String url, Date data, int relevancia) {
        this.titulo = titulo;
        this.genero = genero;
        this.teatro = teatro;
        this.cidade = cidade;
        this.estado = estado;
        this.miniatura = miniatura;
        this.url = url;
//        this.data = data;
        this.relevancia = relevancia;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getTeatro() {
        return teatro;
    }

    public void setTeatro(String teatro) {
        this.teatro = teatro;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getMiniatura() {
        return miniatura;
    }

    public void setMiniatura(String miniatura) {
        this.miniatura = miniatura;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

//    public Date getData() {
//        return data;
//    }
//
//    public void setData(Date data) {
//        this.data = data;
//    }

    public int getRelevancia() {
        return relevancia;
    }

    public void setRelevancia(int relevancia) {
        this.relevancia = relevancia;
    }

//    public Date getUpdated_at() {
//        return updated_at;
//    }
//
//    public void setUpdated_at(Date updated_at) {
//        this.updated_at = updated_at;
//    }

    public int getTeatroId() {
        return teatroId;
    }

    public void setTeatroId(int teatroId) {
        this.teatroId = teatroId;
    }

    public long getDistance() {
        return distance;
    }

    public void setDistance(long distance) {
        this.distance = distance;
    }

    @Override
    public String toString() {
        return "Espetaculo{" +
                "titulo='" + titulo + '\'' +
                ", genero='" + genero + '\'' +
                ", teatro='" + teatro + '\'' +
                ", cidade='" + cidade + '\'' +
                ", estado='" + estado + '\'' +
                ", miniatura='" + miniatura + '\'' +
                '}';
    }
}
