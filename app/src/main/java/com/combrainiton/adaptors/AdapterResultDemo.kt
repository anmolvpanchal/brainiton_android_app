package com.combrainiton.adaptors

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.combrainiton.R

class AdapterResultDemo(var context: Context,var answer: Array<Boolean>) : androidx.recyclerview.widget.RecyclerView.Adapter<AdapterResultDemo.MyViewHolder>() {

    var index:Int = 1

    class MyViewHolder : androidx.recyclerview.widget.RecyclerView.ViewHolder {

        var textView: TextView
        lateinit var layoutR: RelativeLayout

        constructor(view: View) : super(view){
            textView = view.findViewById(R.id.demo_item_tv)
            layoutR = view.findViewById(R.id.demo_item_layout)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return AdapterResultDemo.MyViewHolder(LayoutInflater.from(context).inflate(R.layout.demo_item, parent, false))
    }

    override fun getItemCount(): Int {
            return  answer.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.textView.text = (position+1).toString()

        if(answer[position]){
            holder.layoutR.setBackgroundResource(R.drawable.circle_for_result_green)
        } else{
            holder.layoutR.setBackgroundResource(R.drawable.circle_for_result_red)
        }
    }

    fun setItems(answer: Array<Boolean>){
        this.answer = answer
    }

}