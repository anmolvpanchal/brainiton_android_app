package com.combrainiton.utils

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.combrainiton.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.w3c.dom.Text
import java.lang.ClassCastException
import java.lang.Exception

class DescriptionBottomSheetDialog : BottomSheetDialogFragment() {

    private lateinit var mListener: BottomSheetListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.description_bottom_sheet_layout,container,false)

        val nextQuestion:Button = view.findViewById(R.id.bottom_sheet_next_question)
        val description:TextView = view.findViewById(R.id.bottom_sheet_description)
        val anwerTextView:TextView = view.findViewById(R.id.bottom_sheet_answerText)

        nextQuestion.setOnClickListener {
            mListener.onButtonClicked()
        }

        return  view
    }

    interface BottomSheetListener{
        fun onButtonClicked()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        try {
            mListener = context as BottomSheetListener
        } catch (e: Exception){
            throw ClassCastException(context.toString() + "must implement BottomSheetListener")
        }

    }
}