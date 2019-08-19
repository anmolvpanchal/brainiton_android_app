package com.combrainiton.main

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.combrainiton.R
import com.combrainiton.managers.NormalQuizManagement
import com.combrainiton.utils.AppProgressDialog
import kotlinx.android.synthetic.main.fragment_my_quizzes.*

class FragmentMyQuizzes : Fragment() {

    private lateinit var mContext: Context
    private var isDataLoaded: Boolean = false
    private var isOnCreate: Boolean = false

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        isDataLoaded = isVisibleToUser
        if (isDataLoaded && isOnCreate) {
            setData()
        }
    }

    private fun setData() {
        val mDialog = AppProgressDialog(requireContext())
        mDialog.show()
        NormalQuizManagement(requireContext(), requireActivity(), mDialog).getRecentQuiz(rvQuizzes)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        mContext = activity!!
        isOnCreate = true
        return inflater.inflate(R.layout.fragment_my_quizzes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {

    }

    override fun onResume() {
        super.onResume()
        if (isDataLoaded) {
            setData()
        }
    }
}