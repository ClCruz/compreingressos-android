package br.com.compreingressos.model;

/**
 * Created by luiszacheu on 19/03/15.
 */
public class Genero {
    private String nome;
    private int cover;

    public Genero(String nome, int cover) {
        this.nome = nome;
        this.cover = cover;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getCover() {
        return cover;
    }

    public void setCover(int cover) {
        this.cover = cover;
    }


}
