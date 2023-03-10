package techwork.ami.Promotion.PromotionDetail;

/**
 * Created by Daniel on 15-10-2016.
 */
public class ProductModel {
    private String name;
    private String description;
    private int price;
    private String image;
    private String quantity;

    // Constructors
    public ProductModel() {};
    public ProductModel(String name, String description, int price) {
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
    public int getPrice() {
        return price;
    }
    public String getImage(){
        return image;
    }
    public String getQuantity(){
        return quantity;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setPrice(int price) {
        this.price = price;
    }
    public void setImage(String image){
        this.image = image;
    }
    public void setQuantity(String quantity){
        this.quantity = quantity;
    }
}
