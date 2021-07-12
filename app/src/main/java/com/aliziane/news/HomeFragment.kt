package com.aliziane.news

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.createViewModelLazy
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.aliziane.news.databinding.FragmentHomeBinding
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Provider

class HomeFragment : Fragment(R.layout.fragment_home) {

    @Inject
    lateinit var homeViewModelFactory: Provider<HomeViewModel.Factory>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentHomeBinding.bind(view)

        val navController = findNavController()
        val appBarConfig =
            AppBarConfiguration(setOf(R.id.homeFragment, R.id.searchFragment, R.id.booksFragment))
        binding.toolbar.setupWithNavController(navController, appBarConfig)

        val providerFactory =
            object : AbstractSavedStateViewModelFactory(this, arguments) {
                override fun <T : ViewModel?> create(
                    key: String,
                    modelClass: Class<T>,
                    handle: SavedStateHandle
                ): T {
                    if (modelClass == HomeViewModel::class.java) {
                        return homeViewModelFactory.get().create(handle) as T
                    } else {
                        throw IllegalArgumentException("Unknown ViewModel.")
                    }
                }

            }
        val viewModel = ViewModelProvider(this, providerFactory).get(HomeViewModel::class.java)

        val epoxyController = HomeEpoxyController()
        binding.recyclerView.setController(epoxyController)
        binding.recyclerView.setItemSpacingDp(16)

        viewModel.posts.observe(viewLifecycleOwner) {
            epoxyController.articles = it
            epoxyController.requestModelBuild()
        }
    }

    override fun onAttach(context: Context) {
        (requireActivity().application as NyTimesApplication).appComponent.inject(this)
        super.onAttach(context)
    }
}