package techwork.ami.category;

/**
 * Created by tataf on 20-08-2016.
 */
public class CategoryModel {
    private String name;
    private int id;
    private String image;

    //Setters
    public void setId(int id){
        this.id=id;
    }
    public void setName(String name){
        this.name=name;
    }
    public void setImage(String imageUrl) {
        this.image = imageUrl;
    }

    //Getters
    public int getId(){
        return id;
    }
    public String getName(){
        return name;
    }
    public String getImage() {
        return image;
    }


}
