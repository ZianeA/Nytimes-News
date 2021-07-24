package com.aliziane.news.articlesearch.search

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.aliziane.news.R
import com.aliziane.news.databinding.FragmentSearchBinding
import com.aliziane.news.fragmentSavedStateViewModels
import com.aliziane.news.injector
import com.aliziane.news.navGraphSavedStateViewModels
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SearchFragment : Fragment(R.layout.fragment_search) {
    private val viewModel by navGraphSavedStateViewModels(R.id.nav_search) {
        injector.searchViewModel.get().create(it)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentSearchBinding.bind(view)
        val navController = findNavController()

        val epoxyController = SearchEpoxyController()
        binding.recyclerView.setController(epoxyController)
        binding.recyclerView.setItemSpacingDp(8)
        epoxyController.history = listOf("My knee", "Balloon")

        binding.searchBoxLayout.setStartIconOnClickListener { navController.navigateUp() }
        binding.searchBox.apply {
            focusAndShowKeyboard()
            setOnEditorActionListener { v, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    viewModel.onQuerySubmit(v.text?.toString())
                    navController.navigateUp()
                    true
                } else {
                    false
                }
            }
            doAfterTextChanged { text -> viewModel.onQueryChange(text?.toString()) }
            viewModel.searchQuery.observe(viewLifecycleOwner) {
                // Prevent typed query from being overridden by the submitted query
                if (text == null) setText(it)
            }
        }

        viewModel.searchSuggestions.observe(viewLifecycleOwner) { epoxyController.suggestions = it }
    }
}