package br.com.compreingressos.model;

import java.io.Serializable;

/**
 * Created by luiszacheu on 08/04/15.
 */
public class Banner implements Serializable {

    private String titulo;
    private String imagem;
    private String url;

    public Banner() {
        super();
    }

    public Banner(String imagem, String url) {
        this.titulo = titulo;
        this.imagem = imagem;
        this.url = url;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    @Override
    public String toString() {
        return "Banner{" +
                "imagem='" + imagem + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
