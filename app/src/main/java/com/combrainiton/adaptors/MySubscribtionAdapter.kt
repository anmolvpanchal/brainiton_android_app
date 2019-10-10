package com.combrainiton.adaptors

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.FragmentActivity
import com.combrainiton.R
import com.combrainiton.main.CourseHomePage
import com.combrainiton.subscription.SubscribedCourse_API
import com.squareup.picasso.Picasso
import java.util.ArrayList

class MySubscribtionAdapter(val subscribedCourcesList: ArrayList<SubscribedCourse_API>, val activity: FragmentActivity, val context: Context,val currentItem: Int) : androidx.viewpager.widget.PagerAdapter() {

    lateinit var layoutInflater: LayoutInflater


    override fun isViewFromObject(p0: View, p1: Any): Boolean {
        return p0 == p1 as View
    }

    override fun getCount(): Int {
       return subscribedCourcesList.size
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val imageView: ImageView
        val card : androidx.cardview.widget.CardView

        val obj = subscribedCourcesList.get(position)

        layoutInflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rv: View = layoutInflater.inflate(R.layout.compete_cell_item,container,false)
        container.addView(rv)

        imageView = rv.findViewById(R.id.compete_imageView)
        card = rv.findViewById(R.id.compete_cardView)

        val Sub_ID = obj.subscriptionId


        Log.e("adapter check","check data  "+Sub_ID+ "  " + obj.courseDescription)

        // use picaso as its fast and reduces the complexity of code

        if (obj.courseImage == ""){

        }else{
            Picasso.get()
                    .load(obj.courseImage)
                    .fit()
                    .into(imageView)
        }


        imageView.setOnClickListener(object  : View.OnClickListener{
            override fun onClick(v: View?) {
                //context!!.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(imagesUri[position])))

                val intent = Intent(activity, CourseHomePage:: class.java)
                intent.putExtra("subscription_id",Sub_ID)
                intent.putExtra("from_Subscription",true)
                intent.putExtra("course_name",subscribedCourcesList[position].courseName)
                intent.putExtra("position",currentItem)
                activity.startActivity(intent)
            }

        })

        return rv
    }


}
