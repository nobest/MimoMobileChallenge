package com.meticulous.mimomobilechallenge

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import com.meticulous.mimomobilechallenge.ui.main.CongratsFragment
import com.meticulous.mimomobilechallenge.ui.main.MainActivityViewModel
import com.meticulous.mimomobilechallenge.ui.main.MainFragment
import com.meticulous.mimomobilechallenge.ui.main.MainViewModel

class MainActivity : AppCompatActivity() {
    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            loadMainFragment()
        }

        viewModel.uiNavState.observe(this) {
            when (it) {
                MainActivityViewModel.NavState.SHOW_CONGRATS_FRAGMENT -> {
                    loadCongratsFragment()
                }
                else -> Unit
            }
        }
    }

    private fun loadMainFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, MainFragment.newInstance(), MainFragment.TAG)
            .commitNow()
    }

    private fun loadCongratsFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, CongratsFragment.newInstance(), CongratsFragment.TAG)
            .commit()
    }
}