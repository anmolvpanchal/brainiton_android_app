package com.combrainiton.subscription

import android.os.Parcel
import android.os.Parcelable

class LeaderboardModel(val rank: String?,val score: String?,val name: String?) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(rank)
        parcel.writeString(score)
        parcel.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LeaderboardModel> {
        override fun createFromParcel(parcel: Parcel): LeaderboardModel {
            return LeaderboardModel(parcel)
        }

        override fun newArray(size: Int): Array<LeaderboardModel?> {
            return arrayOfNulls(size)
        }
    }

}
