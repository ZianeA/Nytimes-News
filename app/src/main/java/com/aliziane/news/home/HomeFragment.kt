package com.aliziane.news.home

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import com.aliziane.news.R
import com.aliziane.news.common.NyTimesApplication
import com.aliziane.news.databinding.FragmentHomeBinding
import com.aliziane.news.setupAppBar
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

class HomeFragment : Fragment(R.layout.fragment_home) {

    @Inject
    lateinit var homeViewModelFactory: Provider<HomeViewModel.Factory>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentHomeBinding.bind(view)

        val navController = findNavController()
        setupAppBar(binding.toolbar, navController)

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
        epoxyController.onArticleClickListener = viewModel::onArticleClick

        viewModel.posts.observe(viewLifecycleOwner, Observer {
            epoxyController.articles = it
            epoxyController.requestModelBuild()
        })

        lifecycleScope.launch {
            viewModel.navigateToArticleDetails
                .flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .collect { arg ->
                    val action =
                        HomeFragmentDirections.actionHomeFragmentToArticleDetailsFragment(arg)
                    navController.navigate(action)
                }
        }
    }

    override fun onAttach(context: Context) {
        (requireActivity().application as NyTimesApplication).appComponent.inject(this)
        super.onAttach(context)
    }
}