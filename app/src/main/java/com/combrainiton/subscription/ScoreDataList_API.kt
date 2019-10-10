package com.combrainiton.subscription

import android.os.Parcel
import android.os.Parcelable

class ScoreDataList_API(val scoreInside: String?,val questionQuestionNumber: String?,val questionId: String?) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(scoreInside)
        parcel.writeString(questionQuestionNumber)
        parcel.writeString(questionId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ScoreDataList_API> {
        override fun createFromParcel(parcel: Parcel): ScoreDataList_API {
            return ScoreDataList_API(parcel)
        }

        override fun newArray(size: Int): Array<ScoreDataList_API?> {
            return arrayOfNulls(size)
        }
    }

}
