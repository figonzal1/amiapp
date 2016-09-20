package techwork.ami.ReservationsOffers;

/**
 * Created by Daniel on 15-08-2016.
 */
public class ReservationOffer {
    private String idReservationOffer;
    private String title;
    private String description;
    private String promotionCode;
    private String initialDate;
    private String finalDate;
    private int price;
    private int state;
    private String image;
    private String company;
    private String quantity;
    private String reservationDate;
    private String paymentDate;

    //Getters
    public String getIdReservationOffer() {
        return idReservationOffer;
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

    //Setters
    public void setIdReservationOffer(String idReservationOffer) {
        this.idReservationOffer= idReservationOffer;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setPromotionCode(String promotionCode) {
        this.promotionCode = promotionCode;
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
}
