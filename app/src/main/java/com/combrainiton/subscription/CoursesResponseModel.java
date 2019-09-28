package com.combrainiton.subscription;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CoursesResponseModel {

    @SerializedName("courses")
    @Expose
    private List<AllCourses> courses = null;

    public List<AllCourses> getCourses() {
        return courses;
    }

    public void setCourses(List<AllCourses> courses) {
        this.courses = courses;
    }

}
