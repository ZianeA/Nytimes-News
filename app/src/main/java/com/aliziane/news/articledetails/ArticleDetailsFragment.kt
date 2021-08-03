package com.aliziane.news.articledetails

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
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

    @Inject
    lateinit var viewModelFactory: Provider<ArticleDetailsViewModel.Factory>

    private val viewModel by fragmentSavedStateViewModels { viewModelFactory.get().create(it) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentArticleDetailsBinding.bind(view)

        val navController = findNavController()
        setupAppBar(binding.toolbar, navController)

        val epoxyController = ArticleDetailsEpoxyController()
        binding.recyclerView.setController(epoxyController)
        binding.recyclerView.setItemSpacingDp(8)

        viewModel.article.observe(viewLifecycleOwner) { epoxyController.article = it }
        viewModel.comments.observe(viewLifecycleOwner) { epoxyController.comments = it }
        viewModel.isLoading.observe(viewLifecycleOwner) { epoxyController.isLoading = it }
        viewModel.sort.observe(viewLifecycleOwner) { epoxyController.sortBy = getString(it) }

        viewModel.message
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { msg ->
                Snackbar.make(view, msg, Snackbar.LENGTH_LONG)
                    .apply { anchorView = binding.snackbarAnchor }
                    .show()
            }
            .launchIn(lifecycleScope)
    }

    override fun onAttach(context: Context) {
        injector.inject(this)
        super.onAttach(context)
    }
}