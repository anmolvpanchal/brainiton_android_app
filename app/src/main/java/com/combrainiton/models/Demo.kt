package com.combrainiton.models

import android.os.Parcel
import android.os.Parcelable

class Demo(){

    lateinit var question: String
    lateinit var answer: String

    constructor(question: String,answer: String) : this() {
        this.question = question
        this.answer = answer
    }
}