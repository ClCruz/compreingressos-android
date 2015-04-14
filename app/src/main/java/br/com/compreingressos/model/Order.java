package br.com.compreingressos.model;

import java.util.Date;
import java.util.List;

/**
 * Created by luiszacheu on 13/04/15.
 */
public class Order {
    private String number;
    private Date date;
    private String total;
    private Espetaculo espetaculo;
    private List<Ingresso> ingressos;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public Espetaculo getEspetaculo() {
        return espetaculo;
    }

    public void setEspetaculo(Espetaculo espetaculo) {
        this.espetaculo = espetaculo;
    }

    public List<Ingresso> getIngressos() {
        return ingressos;
    }

    public void setIngressos(List<Ingresso> ingressos) {
        this.ingressos = ingressos;
    }

    @Override
    public String toString() {
        return "Order{" +
                "number='" + number + '\'' +
                ", date=" + date +
                ", total='" + total + '\'' +
                ", espetaculo=" + espetaculo +
                ", ingressos=" + ingressos +
                '}';
    }
}
