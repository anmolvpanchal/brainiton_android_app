package com.combrainiton.adaptors

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.combrainiton.main.FragmentMyQuizzes

class MyReportsPagerAdapter internal constructor(fm: androidx.fragment.app.FragmentManager) : androidx.fragment.app.FragmentPagerAdapter(fm) {

    private val count = 1

    override fun getItem(position: Int): androidx.fragment.app.Fragment? {
        var fragment: androidx.fragment.app.Fragment? = null
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
