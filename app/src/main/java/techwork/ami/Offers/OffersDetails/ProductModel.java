package techwork.ami.Offers.OffersDetails;

/**
 * Created by tataf on 27-09-2016.
 */

public class ProductModel {
    private String name;
    private String description;
    private int price;
    private String image;
    private String quantity;

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

    public String getQuantity() {
        return quantity;
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

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}
