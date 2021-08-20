package com.meticulous.mimomobilechallenge.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.meticulous.mimomobilechallenge.databinding.MainFragmentBinding
import com.meticulous.mimomobilechallenge.ui.main.customviews.TextDisplayView
import com.meticulous.mimomobilechallenge.ui.main.customviews.TextInputView


class MainFragment : Fragment() {
    private lateinit var contentLayout: LinearLayoutCompat
    private val viewModel: MainViewModel by viewModels()
    private val activityViewModel: MainActivityViewModel by activityViewModels()

    companion object {
        const val TAG = "MIMO_MainFrag"
        fun newInstance() = MainFragment()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = MainFragmentBinding.inflate(inflater, container, false)
        contentLayout = binding.contentLayout
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.uiStateAction.observe(
            viewLifecycleOwner,
            {
                when (it) {
                    is MainViewModel.UiState.BindTextDisplay -> {
                        val textDisplayView = TextDisplayView(requireContext(), it.content)
                        contentLayout.addView(textDisplayView)
                    }
                    is MainViewModel.UiState.BindTextInput -> {
                        val textInputView = TextInputView(requireContext(), it.content)
                        textInputView.addTextChangedListener(viewModel.textWatcher)
                        contentLayout.addView(textInputView)
                    }
                    is MainViewModel.UiState.Answered -> {
                        contentLayout.removeAllViews()
                    }
                    is MainViewModel.UiState.Done -> {
                        activityViewModel.uiNavState.value =
                            MainActivityViewModel.NavState.SHOW_CONGRATS_FRAGMENT
                    }
                }
            }
        )
    }

}