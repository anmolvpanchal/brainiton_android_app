package com.combrainiton.adaptors

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.combrainiton.fragments.MySubscriptionFragment

class SubscriptionAdapter(fm: FragmentManager): FragmentPagerAdapter(fm) {

    private var fragmentList = ArrayList<Fragment>()
    private var fragmentListTitle = ArrayList<String>()

    override fun getItem(positon: Int): Fragment? {
        return fragmentList.get(positon)

    }

    override fun getCount(): Int {
        return fragmentList.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return fragmentListTitle.get(position)
    }

    fun addFragment(fragment: Fragment, title: String) {
        fragmentList.add(fragment)
        fragmentListTitle.add(title)
    }

}