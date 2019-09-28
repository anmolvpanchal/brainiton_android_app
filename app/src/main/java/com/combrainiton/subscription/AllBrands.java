package com.combrainiton.subscription;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AllBrands {

    @SerializedName("brand_name")
    @Expose
    private String brandName;
    @SerializedName("brand_banner")
    @Expose
    private String brandBanner;
    @SerializedName("brand_card")
    @Expose
    private String brandCard;
    @SerializedName("brand_id")
    @Expose
    private Integer brandId;
    @SerializedName("brand_author")
    @Expose
    private String brandAuthor;

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getBrandBanner() {
        return brandBanner;
    }

    public void setBrandBanner(String brandBanner) {
        this.brandBanner = brandBanner;
    }

    public String getBrandCard() {
        return brandCard;
    }

    public void setBrandCard(String brandCard) {
        this.brandCard = brandCard;
    }

    public Integer getBrandId() {
        return brandId;
    }

    public void setBrandId(Integer brandId) {
        this.brandId = brandId;
    }

    public String getBrandAuthor() {
        return brandAuthor;
    }

    public void setBrandAuthor(String brandAuthor) {
        this.brandAuthor = brandAuthor;
    }

}
