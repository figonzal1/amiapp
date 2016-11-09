package techwork.ami.Offers.OffersDetails;

/**
 * Created by tataf on 27-09-2016.
 */

public class ProductModel {
    private String name;
    private String description;
    private int price;
    private String image;

    //Getters

    public String getImage() {
        return image;
    }

    public int getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    //Setters


    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
