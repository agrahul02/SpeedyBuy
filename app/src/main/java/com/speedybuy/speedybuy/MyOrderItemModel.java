package com.speedybuy.speedybuy;

import java.util.Date;

public class MyOrderItemModel {

    private String productId;
    private String productTitle;
    private String productImage;
    private final String orderStatus;


    private String address;
    private String couponId;
    private String cuttedprice;
    private Date orderedDate;
    private Date packedDate;
    private Date shippedDate;
    private Date deliveryDate;
    private Date canceledDate;
    private String discountedPrice;
    private String fullName;
    private String orderId;
    private String paymentMethod;
    private String pincode;
    private String productPrice;
    private Long productQuantity;
    private String userId;
    private String deliveryPrice;
    private boolean cancellationRequested;

    private int rating = 0;

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public MyOrderItemModel(String productId, String orderStatus, String address, String couponId, String cuttedprice, Date orderedDate, Date packedDate, Date shippedDate, Date deliveryDate, Date canceledDate, String discountedPrice, String fullName, String orderId, String paymentMethod, String pincode, String productPrice, Long productQuantity, String userId, String productImage, String productTitle,String deliveryPrice,boolean cancellationRequested) {

        this.productImage = productImage;
        this.productTitle = productTitle;
        this.productId = productId;
        this.orderStatus = orderStatus;
        this.address = address;
        this.couponId = couponId;
        this.cuttedprice = cuttedprice;
        this.orderedDate = orderedDate;
        this.packedDate = packedDate;
        this.shippedDate = shippedDate;
        this.deliveryDate = deliveryDate;
        this.canceledDate = canceledDate;
        this.discountedPrice = discountedPrice;
        this.fullName = fullName;
        this.orderId = orderId;
        this.paymentMethod = paymentMethod;
        this.pincode = pincode;
        this.productPrice = productPrice;
        this.productQuantity = productQuantity;
        this.userId = userId;
        this.deliveryPrice = deliveryPrice;
        this.cancellationRequested = cancellationRequested;

    }

    public boolean isCancellationRequested() {
        return cancellationRequested;
    }

    public void setCancellationRequested(boolean cancellationRequested) {
        this.cancellationRequested = cancellationRequested;
    }

    public String getDeliveryPrice() {
        return deliveryPrice;
    }

    public void setDeliveryPrice(String deliveryPrice) {
        this.deliveryPrice = deliveryPrice;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        orderStatus = orderStatus;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCouponId() {
        return couponId;
    }

    public void setCouponId(String couponId) {
        this.couponId = couponId;
    }

    public String getCuttedprice() {
        return cuttedprice;
    }

    public void setCuttedprice(String cuttedprice) {
        this.cuttedprice = cuttedprice;
    }

    public Date getOrderedDate() {
        return orderedDate;
    }

    public void setOrderedDate(Date orderedDate) {
        this.orderedDate = orderedDate;
    }

    public Date getPackedDate() {
        return packedDate;
    }

    public void setPackedDate(Date packedDate) {
        this.packedDate = packedDate;
    }

    public Date getShippedDate() {
        return shippedDate;
    }

    public void setShippedDate(Date shippedDate) {
        this.shippedDate = shippedDate;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public Date getCanceledDate() {
        return canceledDate;
    }

    public void setCanceledDate(Date canceledDate) {
        this.canceledDate = canceledDate;
    }

    public String getDiscountedPrice() {
        return discountedPrice;
    }

    public void setDiscountedPrice(String discountedPrice) {
        this.discountedPrice = discountedPrice;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public Long getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(Long productQuantity) {
        this.productQuantity = productQuantity;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
