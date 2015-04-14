package br.com.compreingressos.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by luiszacheu on 13/04/15.
 */
@DatabaseTable(tableName = "ingressos")
public class Ingresso {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private String qrcode;

    @DatabaseField
    private String local;

    @DatabaseField
    private String type;

    @DatabaseField
    private String price;

    @DatabaseField
    private String service_price;

    @DatabaseField
    private String total;

    @DatabaseField(foreign=true, foreignAutoCreate=true, foreignAutoRefresh=true)
    private Order order;

    public Ingresso() {
    }

    public Ingresso(String qrcode, String local, String type, String price, String service_price, String total) {
        this.qrcode = qrcode;
        this.local = local;
        this.type = type;
        this.price = price;
        this.service_price = service_price;
        this.total = total;
    }

    public String getQrcode() {
        return qrcode;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getService_price() {
        return service_price;
    }

    public void setService_price(String service_price) {
        this.service_price = service_price;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "Ingresso{" +
                "qrcode='" + qrcode + '\'' +
                ", local='" + local + '\'' +
                ", type='" + type + '\'' +
                ", price='" + price + '\'' +
                ", service_price='" + service_price + '\'' +
                ", total='" + total + '\'' +
                '}';
    }
}
