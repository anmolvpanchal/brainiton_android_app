package com.combrainiton.subscription

import android.os.Parcel
import android.os.Parcelable

class LessonsDataListForAvaiable_API(val lessonName: String?,val quizImage: String?,val lessonNumber: String?,val lessonId: String?,val lessonQuizId: String) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(lessonName)
        parcel.writeString(quizImage)
        parcel.writeString(lessonNumber)
        parcel.writeString(lessonId)
        parcel.writeString(lessonQuizId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LessonsDataListForAvaiable_API> {
        override fun createFromParcel(parcel: Parcel): LessonsDataListForAvaiable_API {
            return LessonsDataListForAvaiable_API(parcel)
        }

        override fun newArray(size: Int): Array<LessonsDataListForAvaiable_API?> {
            return arrayOfNulls(size)
        }
    }

}
