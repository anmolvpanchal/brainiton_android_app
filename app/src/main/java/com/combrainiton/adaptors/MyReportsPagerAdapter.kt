package com.combrainiton.adaptors

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.combrainiton.main.FragmentMyQuizzes

class MyReportsPagerAdapter internal constructor(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    private val count = 1

    override fun getItem(position: Int): Fragment? {
        var fragment: Fragment? = null
        when (position) {
            0 -> fragment = FragmentMyQuizzes()
        }
        return fragment
    }

    override fun getCount(): Int {
        return count
    }

    override fun getPageTitle(position: Int): CharSequence? {
        var title = ""
        when(position){
            0 -> title = "Recent"
        }
        return title
    }

}
