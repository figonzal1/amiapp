package techwork.ami.Need;


public class NeedModel {
    String id;
    String tittle;
    String description;
    String dateFin;
    String priceMin;
    String lat;
    String lon;
    String radio;

    //Getters
    public String getId() {
        return id;
    }
    public String getTittle(){
        return tittle;
    }
    public String getRadio() {
        return radio;
    }

    public String getDescription() {
        return description;
    }

    public String getDateFin() {
        return dateFin;
    }

    public String getLat() {
        return lat;
    }

    public String getLon() {
        return lon;
    }

    public String getPriceMin() {
        return priceMin;
    }

    //Setters


    public void setId(String id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTittle(String tittle) {
        this.tittle = tittle;
    }

    public void setDateFin(String dateFin) {
        this.dateFin = dateFin;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public void setPriceMin(String priceMin) {
        this.priceMin = priceMin;
    }

    public void setRadio(String radio) {
        this.radio = radio;
    }
}
