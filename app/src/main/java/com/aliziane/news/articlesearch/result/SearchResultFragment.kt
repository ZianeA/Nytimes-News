package com.aliziane.news.articlesearch.result

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.aliziane.news.R
import com.aliziane.news.databinding.FragmentSearchResultBinding
import com.aliziane.news.fragmentSavedStateViewModels
import com.aliziane.news.injector
import com.aliziane.news.navGraphSavedStateViewModels
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
        binding.recyclerView.setController(epoxyController)
        binding.recyclerView.setItemSpacingDp(8)
        viewModel.searchResult.observe(viewLifecycleOwner) { epoxyController.articles = it }

        binding.searchBar.setOnClickListener {
            val action =
                SearchResultFragmentDirections.actionSearchResultFragmentToSearchFragment()
            navController.navigate(action)
        }
        viewModel.searchQuery.observe(viewLifecycleOwner) { binding.searchBarQuery.text = it }
    }
}