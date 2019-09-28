package com.combrainiton.subscription;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AllCourses {

    @SerializedName("course_image")
    @Expose
    private String courseImage;
    @SerializedName("course_description")
    @Expose
    private String courseDescription;
    @SerializedName("course_id")
    @Expose
    private Integer courseId;
    @SerializedName("course_brand_name")
    @Expose
    private String courseBrandName;
    @SerializedName("course_name")
    @Expose
    private String courseName;
    @SerializedName("course_brand_id")
    @Expose
    private Integer courseBrandId;

    public String getCourseImage() {
        return courseImage;
    }

    public void setCourseImage(String courseImage) {
        this.courseImage = courseImage;
    }

    public String getCourseDescription() {
        return courseDescription;
    }

    public void setCourseDescription(String courseDescription) {
        this.courseDescription = courseDescription;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public String getCourseBrandName() {
        return courseBrandName;
    }

    public void setCourseBrandName(String courseBrandName) {
        this.courseBrandName = courseBrandName;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public Integer getCourseBrandId() {
        return courseBrandId;
    }

    public void setCourseBrandId(Integer courseBrandId) {
        this.courseBrandId = courseBrandId;
    }

}
