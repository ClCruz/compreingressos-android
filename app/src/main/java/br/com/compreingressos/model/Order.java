package br.com.compreingressos.model;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by luiszacheu on 13/04/15.
 */
@DatabaseTable(tableName = "orders")
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    @DatabaseField(allowGeneratedIdInsert = true, generatedId = true)
    private int id;
    @DatabaseField
    private String number;
    @DatabaseField
    private Date date;
    @DatabaseField
    private String total;

    @DatabaseField(foreign = true)
    private Espetaculo espetaculo;

    @ForeignCollectionField
    private ForeignCollection<Ingresso> ingressosCollection;

    private List<Ingresso> ingressos;

    @DatabaseField(columnName = "titulo_espetaculo")
    private String tituloEspetaculo;

    @DatabaseField(columnName = "endereco_espetaculo")
    private String enderecoEspetaculo;

    @DatabaseField(columnName = "nome_teatro_espetaculo")
    private String nomeTeatroEspetaculo;

    @DatabaseField(columnName = "horario_espetaculo")
    private String horarioEspetaculo;


    public Order() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


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

    public ForeignCollection<Ingresso> getIngressosCollection() {
        return ingressosCollection;
    }

    public void setIngressosCollection(ForeignCollection<Ingresso> ingressosCollection) {
        this.ingressosCollection = ingressosCollection;
    }

    public String getTituloEspetaculo() {
        return tituloEspetaculo;
    }

    public void setTituloEspetaculo(String tituloEspetaculo) {
        this.tituloEspetaculo = tituloEspetaculo;
    }

    public String getEnderecoEspetaculo() {
        return enderecoEspetaculo;
    }

    public void setEnderecoEspetaculo(String enderecoEspetaculo) {
        this.enderecoEspetaculo = enderecoEspetaculo;
    }

    public String getNomeTeatroEspetaculo() {
        return nomeTeatroEspetaculo;
    }

    public void setNomeTeatroEspetaculo(String nomeTeatroEspetaculo) {
        this.nomeTeatroEspetaculo = nomeTeatroEspetaculo;
    }

    public String getHorarioEspetaculo() {
        return horarioEspetaculo;
    }

    public void setHorarioEspetaculo(String horarioEspetaculo) {
        this.horarioEspetaculo = horarioEspetaculo;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", number='" + number + '\'' +
                ", date=" + date +
                ", total='" + total + '\'' +
                ", espetaculo=" + espetaculo +
                ", ingressosCollection=" + ingressosCollection +
                ", ingressos=" + ingressos +
                ", tituloEspetaculo='" + tituloEspetaculo + '\'' +
                ", enderecoEspetaculo='" + enderecoEspetaculo + '\'' +
                ", nomeTeatroEspetaculo='" + nomeTeatroEspetaculo + '\'' +
                ", horarioEspetaculo='" + horarioEspetaculo + '\'' +
                '}';
    }
}
