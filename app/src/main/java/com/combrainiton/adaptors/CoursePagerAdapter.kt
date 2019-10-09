package com.combrainiton.adaptors

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter

class CoursePagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    private var fragmentList = ArrayList<androidx.fragment.app.Fragment>()
    private var fragmentListTitle = ArrayList<String>()

    override fun getItem(positon: Int): Fragment {
        return fragmentList.get(positon)
    }


    override fun getCount(): Int {
        return fragmentList.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return fragmentListTitle.get(position)
    }

    fun addFragment(fragment: androidx.fragment.app.Fragment, title: String) {
        fragmentList.add(fragment)
        fragmentListTitle.add(title)
    }

}