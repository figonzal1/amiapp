package techwork.ami.Promotion.MyPromotions;

/**
 * Created by Daniel on 15-08-2016.
 */
public class MyReservationPromotionModel {
    private String idReservationOffer;
    private String idLocal;
    private String title;
    private String description;
    private String promotionCode;
    private String calification;
    private String initialDate;
    private String finalDate;
    private int price;
    private int state;
    private String image;
    private String company;
    private String quantity;
    private String charged;
    private String reservationDate;
    private String paymentDate;
    private String localCode;
    private int MaxPPerson;
    private int stock;
    private int totalPrice;
    private String finalDateTime;

    //Getters
    public String getIdReservationOffer() {
        return idReservationOffer;
    }

    public String getIdLocal() {
        return idLocal;
    }

    public String getTitle() {
        return title;
    }
    public String getDescription() {
        return description;
    }
    public String getPromotionCode() {
        return promotionCode;
    }
    public String getCalification() {
        return  calification;
    }
    public String getInitialDate() {
        return initialDate;
    }
    public String getFinalDate() {
        return finalDate;
    }
    public int getPrice() {
        return price;
    }
    public int getState() {
        return state;
    }

    public String getCharged() {
        return charged;
    }

    public String getImage() {
        return image;
    }
    public String getCompany() {
        return company;
    }
    public String getQuantity() {
        return quantity;
    }
    public String getReservationDate() {
        return reservationDate;
    }
    public String getPaymentDate() {
        return paymentDate;
    }
    public String getLocalCode(){
        return localCode;
    }
    public int getMaxPPerson() {
        return MaxPPerson;
    }
    public int getStock() {
        return stock;
    }
    public int getTotalPrice() {
        return totalPrice;
    }
    public String getFinalDateTime() {
        return finalDateTime;
    }


    //Setters
    public void setIdReservationOffer(String idReservationOffer) {
        this.idReservationOffer= idReservationOffer;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public void setIdLocal(String idLocal) {
        this.idLocal = idLocal;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public void setPromotionCode(String promotionCode) {
        this.promotionCode = promotionCode;
    }
    public void setCalification(String calification){
        this.calification = calification;
    }
    public void setInitialDate(String initialDate) {
        this.initialDate = initialDate;
    }
    public void setFinalDate(String finalDate) {
        this.finalDate = finalDate;
    }
    public void setPrice(int price) {
        this.price = price;
    }
    public void setState(int state) {
        this.state = state;
    }
    public void setImage(String image) {
        this.image = image;
    }
    public void setCompany(String company) {
        this.company = company;
    }
    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
    public void setReservationDate(String reservationDate) {
        this.reservationDate = reservationDate;
    }
    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }
    public void setLocalCode(String localCode){
        this.localCode = localCode;
    }

    public void setCharged(String charged) {
        this.charged = charged;
    }

    public void setMaxPPerson(int maxPPerson) {
        MaxPPerson = maxPPerson;
    }
    public void setStock(int stock) {
        this.stock = stock;
    }
    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }
    public void setFinalDateTime(String finalDateTime) {
        this.finalDateTime = finalDateTime;
    }
}
