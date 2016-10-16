package techwork.ami.Offer.OfferDetail;

/**
 * Created by Daniel on 15-10-2016.
 */
public class ProductModel {
    private String name;
    private String description;
    private String price;
    private String image;

    // Constructors
    public ProductModel() {};
    public ProductModel(String name, String description, String price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }

    // Getters
    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public String getPrice() {
        return price;
    }
    public String getImage(){
        return image;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setPrice(String price) {
        this.price = price;
    }
    public void setImage(String image){
        this.image = image;
    }
}
