package com.iehms.strawberrymarket.model;

/**
 * 상품 정보를 담는 객체
 *
 * @author dahunkim
 */
public class Product {
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * 생성자
     * @param imageUrl  이미지 URL
     * @param title     게시글 제목
     * @param price     상품의 가격
     * @param user      판매자 이름
     * @param createdAt 게시글 생성 일자 (yyyy-MM-dd HH:mm:ss)
     */
    public Product(String imageUrl, String title, int price, String user, String createdAt) {
        this.imageUrl = imageUrl;
        this.title = title;
        this.price = price;
        this.user = user;
        this.createdAt = createdAt;
    }

    String imageUrl;
    String title;
    int price;
    String user;
    String createdAt;
}
