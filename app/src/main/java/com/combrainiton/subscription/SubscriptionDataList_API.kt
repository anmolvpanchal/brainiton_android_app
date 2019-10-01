package com.combrainiton.subscription

import android.os.Parcel
import android.os.Parcelable

class SubscriptionDataList_API(val currentLessonId: String?,val currentLessonName: String?,val subscriptionId: String?,val currentLessonNumber: String?,val lastLessonNumber: String?,val courseId: String?,val currentLessonQuiz: String?,val courseName: String?) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(currentLessonId)
        parcel.writeString(currentLessonName)
        parcel.writeString(subscriptionId)
        parcel.writeString(currentLessonNumber)
        parcel.writeString(lastLessonNumber)
        parcel.writeString(courseId)
        parcel.writeString(currentLessonQuiz)
        parcel.writeString(courseName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SubscriptionDataList_API> {
        override fun createFromParcel(parcel: Parcel): SubscriptionDataList_API {
            return SubscriptionDataList_API(parcel)
        }

        override fun newArray(size: Int): Array<SubscriptionDataList_API?> {
            return arrayOfNulls(size)
        }
    }

}
