package techwork.ami.Need.ListOfferCompanies;

/**
 * Created by tataf on 25-09-2016.
 */

public class OffersModel {
    String idOffer;
    String idNeed;
    String idLocal;
    String tittle;
    String description;
    String stock;
    String codPromotion;
    String dateIni;
    String dateTimeFin;
    String dateFin;
    int price;
    String maxPPerson;
    String company;
    String image;

    //Getters


    public String getIdOferta() {
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

    public String getDateIni() {
        return dateIni;
    }

    public String getDateFin() {
        return dateFin;
    }

    public int getPrice() {
        return price;
    }

    public String getMaxPPerson() {
        return maxPPerson;
    }

    public String getCompany() {
        return company;
    }

    public String getDateTimeFin() {
        return dateTimeFin;
    }

    public String getImage() {
        return image;
    }

    //Setters

    public void setIdNeed(String idNeed) {
        this.idNeed = idNeed;
    }

    public void setIdOferta(String idOffer) {
        this.idOffer = idOffer;
    }

    public void setIdLocal(String idLocal) {
        this.idLocal = idLocal;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public void setMaxPPerson(String maxPPerson) {
        this.maxPPerson = maxPPerson;
    }

    public void setCodPromotion(String codPromotion) {
        this.codPromotion = codPromotion;
    }

    public void setDateFin(String dateFin) {
        this.dateFin = dateFin;
    }

    public void setDateIni(String dateIni) {
        this.dateIni = dateIni;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setTittle(String tittle) {
        this.tittle = tittle;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public void setDateTimeFin(String dateTimeFin) {
        this.dateTimeFin = dateTimeFin;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
