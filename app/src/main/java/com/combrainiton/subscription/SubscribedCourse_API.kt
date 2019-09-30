package com.combrainiton.subscription

import android.os.Parcel
import android.os.Parcelable

class SubscribedCourse_API(val courseImage: String?,val lessonId: String?,val lessonQuiz: String?,val courseName: String?,val subscriptionId: String?,val courseId: String?,val lessonName: String?,val lessonNumber: String?,val courseDescription: String?) : Parcelable {
    constructor(parcel: Parcel) : this(
            TODO("courseImage"),
            TODO("lessonId"),
            TODO("lessonQuiz"),
            TODO("courseName"),
            TODO("subscriptionId"),
            TODO("courseId"),
            TODO("lessonName"),
            TODO("lessonNumber"),
            TODO("courseDescription")) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SubscribedCourse_API> {
        override fun createFromParcel(parcel: Parcel): SubscribedCourse_API {
            return SubscribedCourse_API(parcel)
        }

        override fun newArray(size: Int): Array<SubscribedCourse_API?> {
            return arrayOfNulls(size)
        }
    }

}
