package com.speedybuy.speedybuy;

import java.util.ArrayList;

public class WishlistModel {

    private String productId;
    private String productImage;
    private String productTitle;
   // private Long freeCoupens;
    private String rating;
    private String totalRatings;
    private String productPrice;
    private String cutterPrice;
    private boolean COD;
    private boolean inStock;
    private ArrayList<String> tags;

    public WishlistModel(String productId,String productImage, String productTitle,  String rating, String totalRatings, String productPrice, String cutterPrice, boolean COD, boolean inStock) {
        this.productId = productId;
        this.productImage = productImage;
        this.productTitle = productTitle;
        //this.freeCoupens = freeCoupens;
        this.rating = rating;
        this.totalRatings = totalRatings;
        this.productPrice = productPrice;
        this.cutterPrice = cutterPrice;
        this.inStock = inStock;
        this.COD = COD;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public String getProductId() {
        return productId;
    }

    public boolean isInStock() {
        return inStock;
    }

    public void setInStock(boolean inStock) {
        this.inStock = inStock;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

  /*  public Long getFreeCoupens() {
        return freeCoupens;
    }

    public void setFreeCoupens(Long freeCoupens) {
        this.freeCoupens = freeCoupens;
    }*/

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getTotalRatings() {
        return totalRatings;
    }

    public void setTotalRatings(String totalRatings) {
        this.totalRatings = totalRatings;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getCutterPrice() {
        return cutterPrice;
    }

    public void setCutterPrice(String cutterPrice) {
        this.cutterPrice = cutterPrice;
    }

    public boolean isCOD() {
        return COD;
    }

    public void setCOD(boolean COD) {
        this.COD = COD;
    }
}
