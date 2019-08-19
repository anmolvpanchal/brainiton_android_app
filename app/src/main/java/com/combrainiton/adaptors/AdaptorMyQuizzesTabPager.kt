package com.combrainiton.adaptors

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter


class AdaptorMyQuizzesTabPager internal constructor(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    private var mFragmentList: ArrayList<Fragment>? = ArrayList()
    private var mFragmentTitleList: ArrayList<String>? = ArrayList()

    override fun getItem(position: Int): Fragment {
        return mFragmentList!![position]
    }

    override fun getCount(): Int {
        return mFragmentList!!.size
    }

    fun addFragment(fragment: Fragment, title: String) {
        mFragmentList!!.add(fragment)
        mFragmentTitleList!!.add(title)
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return mFragmentTitleList!![position]
    }
}
