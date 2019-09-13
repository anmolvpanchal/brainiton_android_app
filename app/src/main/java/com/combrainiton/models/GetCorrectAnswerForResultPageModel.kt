package com.combrainiton.models

import android.os.Parcel
import android.os.Parcelable

class GetCorrectAnswerForResultPageModel() : Parcelable{

    var correctAnswer: String? = null

    constructor(parcel: Parcel) : this() {
        correctAnswer = parcel.readString()
    }

    constructor(answer: String?) : this() {
        this.correctAnswer = answer
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(correctAnswer)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GetCorrectAnswerForResultPageModel> {
        override fun createFromParcel(parcel: Parcel): GetCorrectAnswerForResultPageModel {
            return GetCorrectAnswerForResultPageModel(parcel)
        }

        override fun newArray(size: Int): Array<GetCorrectAnswerForResultPageModel?> {
            return arrayOfNulls(size)
        }
    }
}