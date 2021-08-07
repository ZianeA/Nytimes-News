package com.aliziane.news.articledetails

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.aliziane.news.*
import com.aliziane.news.databinding.FragmentArticleDetailsBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import javax.inject.Provider

class ArticleDetailsFragment : Fragment(R.layout.fragment_article_details) {

    private val viewModel by fragmentSavedStateViewModels {
        injector.articleDetailsViewModel.get().create(it)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentArticleDetailsBinding.bind(view)

        val navController = findNavController()
        setupAppBar(binding.toolbar, navController)

        val epoxyController = ArticleDetailsEpoxyController()
        epoxyController.onSortByClickListener = {
            val action =
                ArticleDetailsFragmentDirections.actionArticleDetailsFragmentToSortByDialog(
                    requireNotNull(viewModel.sortBy.value)
                )
            navController.navigate(action)
        }
        epoxyController.onReadMoreClickListener = viewModel::onReadMoreClick
        binding.recyclerView.setController(epoxyController)
        binding.recyclerView.setItemSpacingDp(8)

        viewModel.article.observe(viewLifecycleOwner) { epoxyController.article = it }
        viewModel.comments.observe(viewLifecycleOwner) { epoxyController.comments = it }
        viewModel.isLoading.observe(viewLifecycleOwner) { epoxyController.isLoading = it }
        viewModel.sortBy.observe(viewLifecycleOwner) {
            epoxyController.sortBy = getString(it.displayName)
        }
        val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
        savedStateHandle?.getLiveData<SortBy>(SortByDialog.KEY_SELECTION)
            ?.observe(viewLifecycleOwner) { viewModel.onSortByChange(it) }

        viewModel.message
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { msg ->
                Snackbar.make(view, msg, Snackbar.LENGTH_LONG)
                    .apply { anchorView = binding.snackbarAnchor }
                    .show()
            }
            .launchIn(lifecycleScope)

        viewModel.navigateToFullArticle
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { url ->
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                requireContext().startActivity(intent)
            }
            .launchIn(lifecycleScope)
    }
}