package com.aliziane.news.books

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.aliziane.news.R
import com.aliziane.news.databinding.FragmentBooksBinding
import com.aliziane.news.fragmentViewModels
import com.aliziane.news.injector
import com.aliziane.news.setupAppBar
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class BooksFragment : Fragment(R.layout.fragment_books) {
    private val viewModel by fragmentViewModels { injector.booksViewModel.get() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentBooksBinding.bind(view)
        val navController = findNavController()
        setupAppBar(binding.toolbar, navController)

        val epoxyController = BooksEpoxyController()
        binding.recyclerView.setController(epoxyController)
        binding.recyclerView.setItemSpacingDp(8)

        viewModel.bestsellersLists.observe(viewLifecycleOwner) { epoxyController.lists = it }
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.viewFlipper.displayedChild =
                if (isLoading) Flipper.LOADING.ordinal else Flipper.CONTENT.ordinal
        }
        viewModel.message
            .onEach { msg ->
                Snackbar.make(view, msg, Snackbar.LENGTH_LONG)
                    .apply { anchorView = binding.snackbarAnchor }
                    .show()
            }
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .launchIn(lifecycleScope)
    }

    enum class Flipper { LOADING, CONTENT }
}