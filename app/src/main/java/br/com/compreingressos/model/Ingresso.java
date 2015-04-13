package br.com.compreingressos.model;

/**
 * Created by luiszacheu on 13/04/15.
 */
public class Ingresso {
    private String qrcode;
    private String local;
    private String type;
    private String price;
    private String service_price;
    private String total;

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
