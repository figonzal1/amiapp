package techwork.ami.Offers.OffersLocalDetails;

/**
 * Created by tataf on 20-10-2016.
 */

public class LocalModel {
    String idLocal;
    String idCommune;
    String lat;
    String lon;
    String address;
    String web;
    String image;

    //Getters


    public String getIdLocal() {
        return idLocal;
    }

    public String getIdCommune() {
        return idCommune;
    }
    public String getLat(){
        return lat;
    }

    public String getAddress() {
        return address;
    }

    public String getImage() {
        return image;
    }

    public String getWeb() {
        return web;
    }

    public String getLon() {
        return lon;
    }


    //Setters

    public void setIdLocal(String idLocal) {
        this.idLocal = idLocal;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setIdCommune(String idCommune) {
        this.idCommune = idCommune;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public void setWeb(String web) {
        this.web = web;
    }
}
