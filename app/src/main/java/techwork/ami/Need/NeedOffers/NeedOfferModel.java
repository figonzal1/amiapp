package techwork.ami.Need.NeedOffers;


public class NeedOfferModel {
    String idNeedOffer;
    String tittle;
    String description;
    String stock;
    String codPromotion;
    String dateInit;
    String dateFin;
    String price;
    String image;


    //Getters
    public String getIdNeedOffer() {
        return idNeedOffer;
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

    public String getDateInit() {
        return dateInit;
    }

    public String getDateFin() {
        return dateFin;
    }

    public String getPrice() {
        return price;
    }

    public String getImage() {
        return image;
    }

    //Setter

    public void setIdNeedOffer(String idNeedOffer) {
        this.idNeedOffer = idNeedOffer;
    }

    public void setTittle(String tittle) {
        this.tittle = tittle;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCodPromotion(String codPromotion) {
        this.codPromotion = codPromotion;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public void setDateInit(String dateInit) {
        this.dateInit = dateInit;
    }

    public void setDateFin(String dateFin) {
        this.dateFin = dateFin;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setImage(String image) {
        this.image = image;
    }
}

