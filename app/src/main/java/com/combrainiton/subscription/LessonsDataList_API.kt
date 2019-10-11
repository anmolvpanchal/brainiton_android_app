package com.combrainiton.subscription

import android.os.Parcel
import android.os.Parcelable

class LessonsDataList_API(val lessonId: String?,val lessonName: String?,val lessonNumber: String?,val quizImage: String?,val lessonQuiz: String?,val courseDescription: String?) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(lessonId)
        parcel.writeString(lessonName)
        parcel.writeString(lessonNumber)
        parcel.writeString(quizImage)
        parcel.writeString(lessonQuiz)
        parcel.writeString(courseDescription)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LessonsDataList_API> {
        override fun createFromParcel(parcel: Parcel): LessonsDataList_API {
            return LessonsDataList_API(parcel)
        }

        override fun newArray(size: Int): Array<LessonsDataList_API?> {
            return arrayOfNulls(size)
        }
    }

}
