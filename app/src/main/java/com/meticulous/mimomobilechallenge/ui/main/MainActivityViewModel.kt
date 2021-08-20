package com.meticulous.mimomobilechallenge.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainActivityViewModel : ViewModel() {
    val uiNavState: MutableLiveData<NavState> = MutableLiveData()

    enum class NavState {
        SHOW_CONGRATS_FRAGMENT
    }
}