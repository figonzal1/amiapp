package techwork.ami.Need.NeedReservations;

/**
 * Created by tataf on 24-10-2016.
 */

public class NeedReservationsModel {
    String idOffer;
    String idNeed;
    String idLocal;
    String tittle;
    String description;
    String codPromotion;
    String stock;
    String quantity;
    String cashed;
    String calification;
    String dateReserv;
    String dateCashed;
    String dateIni;
    String dateFin;
    int price;


    //Getters


    public String getIdOffer() {
        return idOffer;
    }

    public String getIdNeed() {
        return idNeed;
    }

    public String getIdLocal() {
        return idLocal;
    }

    public String getTittle() {
        return tittle;
    }

    public String getDescription() {
        return description;
    }

    public String getStock() {
        return stock;
    }

    public String getCodPromotion() {
        return codPromotion;
    }

    public String getCalification() {
        return calification;
    }

    public String getCashed() {
        return cashed;
    }

    public String getDateCashed() {
        return dateCashed;
    }

    public String getDateReserv() {
        return dateReserv;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getDateIni() {
        return dateIni;
    }

    public String getDateFin() {
        return dateFin;
    }

    public int getPrice() {
        return price;
    }





    //Setters


    public void setIdLocal(String idLocal) {
        this.idLocal = idLocal;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public void setCalification(String calification) {
        this.calification = calification;
    }

    public void setCashed(String cashed) {
        this.cashed = cashed;
    }

    public void setCodPromotion(String codPromotion) {
        this.codPromotion = codPromotion;
    }

    public void setDateCashed(String dateCashed) {
        this.dateCashed = dateCashed;
    }

    public void setDateFin(String dateFin) {
        this.dateFin = dateFin;
    }

    public void setDateIni(String dateIni) {
        this.dateIni = dateIni;
    }

    public void setDateReserv(String dateReserv) {
        this.dateReserv = dateReserv;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setIdNeed(String idNeed) {
        this.idNeed = idNeed;
    }

    public void setIdOffer(String idOffer) {
        this.idOffer = idOffer;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setTittle(String tittle) {
        this.tittle = tittle;
    }

}