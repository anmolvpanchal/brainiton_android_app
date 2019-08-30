package com.combrainiton.adaptors

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.combrainiton.fragments.MySubscriptionFragment

class SubscriptionAdapter(fm: androidx.fragment.app.FragmentManager): androidx.fragment.app.FragmentPagerAdapter(fm) {

    private var fragmentList = ArrayList<androidx.fragment.app.Fragment>()
    private var fragmentListTitle = ArrayList<String>()

    override fun getItem(positon: Int): androidx.fragment.app.Fragment? {
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