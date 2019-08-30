package com.combrainiton.adaptors

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter


class AdaptorMyQuizzesTabPager internal constructor(fm: androidx.fragment.app.FragmentManager) : androidx.fragment.app.FragmentPagerAdapter(fm) {
    private var mFragmentList: ArrayList<androidx.fragment.app.Fragment>? = ArrayList()
    private var mFragmentTitleList: ArrayList<String>? = ArrayList()

    override fun getItem(position: Int): androidx.fragment.app.Fragment {
        return mFragmentList!![position]
    }

    override fun getCount(): Int {
        return mFragmentList!!.size
    }

    fun addFragment(fragment: androidx.fragment.app.Fragment, title: String) {
        mFragmentList!!.add(fragment)
        mFragmentTitleList!!.add(title)
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return mFragmentTitleList!![position]
    }
}
