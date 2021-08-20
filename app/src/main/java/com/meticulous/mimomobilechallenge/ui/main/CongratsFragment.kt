package com.meticulous.mimomobilechallenge.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.meticulous.mimomobilechallenge.R

class CongratsFragment : Fragment() {

    companion object {
        val TAG = "MIMO_CongratsFrag"

        fun newInstance() = CongratsFragment()
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.congrats_fragment, container, false)
    }
}