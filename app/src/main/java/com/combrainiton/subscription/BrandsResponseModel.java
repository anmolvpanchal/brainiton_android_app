package com.combrainiton.subscription;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BrandsResponseModel {

    @SerializedName("brands")
    @Expose
    private List<AllBrands> brands = null;

    public List<AllBrands> getBrands() {
        return brands;
    }

    public void setBrands(List<AllBrands> brands) {
        this.brands = brands;
    }

}
