package techwork.ami.Offer;

/**
 * Created by Daniel on 15-08-2016.
 */
public class  OfferModel {
    private String id;
    private String title;
    private String description;
    private String initialDate;
    private String finalDate;
    private int state;
    private int price;
    private int maxPPerson;
    private int stock;
    private String promotionCode;
    private String image;
    private String company;


    //Setters
    public void setId(String id){
        this.id=id;
    }
    public void setTitle(String title){
        this.title=title;
    }
    public void setDescription(String description){
        this.description=description;
    }
    public void setInitialDate(String initialDate){
        this.initialDate=initialDate;
    }
    public void setFinalDate(String finalDate){
        this.finalDate=finalDate;
    }
    public void setState(int state){
        this.state=state;
    }
    public void setPrice(int price){
        this.price=price;
    }
    public void setMaxPPerson(int maxPPerson){
        this.maxPPerson=maxPPerson;
    }
    public void setStock(int stock){
        this.stock=stock;
    }
    public void setPromotionCode(String promotionCode){
        this.promotionCode=promotionCode;
    }
    public void setImage(String image){
        this.image=image;
    }
    public void setCompany(String company){this.company=company;}

    //Getters
    public String getId(){return id;}
    public String getTitle(){return title;}
    public String getDescription(){return description;}
    public String getInitialDate(){return initialDate;}
    public String getFinalDate(){return finalDate;}
    public int getState(){return state;}
    public int getPrice(){return price;}
    public int getMaxPPerson(){return maxPPerson;}
    public int getStock(){return stock;}
    public String getPromotionCode(){return promotionCode;}
    public String getImage(){return image;}
    public String getCompany(){return company;}

}
