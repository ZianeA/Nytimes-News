package com.aliziane.news.articlesearch.search

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.findNavController
import com.aliziane.news.NavMainDirections
import com.aliziane.news.R
import com.aliziane.news.common.showIf
import com.aliziane.news.databinding.FragmentSearchBinding
import com.aliziane.news.injector
import com.aliziane.news.navGraphSavedStateViewModels
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class SearchFragment : Fragment(R.layout.fragment_search),
    NavController.OnDestinationChangedListener {

    private val viewModel by navGraphSavedStateViewModels(R.id.nav_search) {
        injector.searchViewModel.get().create(it)
    }

    private val navController by lazy { findNavController() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navController.addOnDestinationChangedListener(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentSearchBinding.bind(view)

        val epoxyController = SearchEpoxyController()
        epoxyController.onSuggestionClickListener = viewModel::onArticleClick
        epoxyController.onHistoryClickListener = ::submitQuery
        epoxyController.onHistoryDeleteListener = viewModel::onDeleteSearchHistoryItem

        binding.recyclerView.setController(epoxyController)
        binding.recyclerView.setItemSpacingDp(8)

        binding.searchBoxLayout.setStartIconOnClickListener { navController.navigateUp() }
        binding.searchBox.apply {
            focusAndShowKeyboard()
            setOnEditorActionListener { tv, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    submitQuery(tv.text?.toString())
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
        viewModel.searchHistory.observe(viewLifecycleOwner) { epoxyController.history = it }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressIndicator.showIf { isLoading }
        }

        viewModel.message
            .onEach { msg ->
                Snackbar.make(view, getString(msg), Snackbar.LENGTH_LONG)
                    .apply { anchorView = binding.snackbarAnchor }
                    .show()
            }
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .launchIn(lifecycleScope)

        viewModel.navigateToArticleDetails
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { arg ->
                val action = NavMainDirections.actionGlobalArticleDetailsFragment(arg)
                navController.navigate(action)
            }
            .launchIn(lifecycleScope)
    }

    private fun submitQuery(query: String?) {
        viewModel.onQuerySubmit(query)
        navController.navigateUp()
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        if (destination.id == R.id.searchResultFragment) {
            viewModel.onQueryChange(null)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        navController.removeOnDestinationChangedListener(this)
    }
}