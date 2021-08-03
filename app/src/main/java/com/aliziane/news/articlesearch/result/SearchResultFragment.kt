package com.aliziane.news.articlesearch.result

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.aliziane.news.*
import com.aliziane.news.databinding.FragmentSearchResultBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

class SearchResultFragment : Fragment(R.layout.fragment_search_result) {
    private val viewModel by navGraphSavedStateViewModels(R.id.nav_search) {
        injector.searchViewModel.get().create(it)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentSearchResultBinding.bind(view)
        val navController = findNavController()

        val epoxyController = SearchResultEpoxyController()
        epoxyController.onSearchResultClickListener = viewModel::onArticleClick
        binding.recyclerView.setController(epoxyController)
        binding.recyclerView.setItemSpacingDp(8)
        viewModel.searchResult.observe(viewLifecycleOwner) { epoxyController.articles = it }

        binding.searchBar.setOnClickListener {
            val action =
                SearchResultFragmentDirections.actionSearchResultFragmentToSearchFragment()
            navController.navigate(action)
        }
        viewModel.searchQuery.observe(viewLifecycleOwner) { binding.searchBarQuery.text = it }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.viewFlipper.displayedChild =
                if (isLoading) Flipper.LOADING.ordinal else Flipper.CONTENT.ordinal
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
            .onEach { arg ->
                val action = NavMainDirections.actionGlobalArticleDetailsFragment(arg)
                navController.navigate(action)
            }
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .launchIn(lifecycleScope)
    }

    enum class Flipper { LOADING, CONTENT }
}