package com.speedybuy.speedybuy;

public class CategoryModel {

    private String categoryIconLink;
    private static String categoryName;

    public CategoryModel(String categoryIconLink, String categoryName) {
        this.categoryIconLink = categoryIconLink;
        CategoryModel.categoryName = categoryName;
    }

    public String getCategoryIconLink() {
        return categoryIconLink;
    }

    public void setCategoryIconLink(String categoryIconLink) {
        this.categoryIconLink = categoryIconLink;
    }

    public static String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        CategoryModel.categoryName = categoryName;
    }
}
