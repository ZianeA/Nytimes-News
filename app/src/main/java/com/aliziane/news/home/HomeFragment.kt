package com.aliziane.news.home

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.ViewFlipper
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import com.aliziane.news.R
import com.aliziane.news.common.NyTimesApplication
import com.aliziane.news.databinding.FragmentHomeBinding
import com.aliziane.news.setupAppBar
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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
        setupOptionsMenu(binding.toolbar)

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

        viewModel.articles.observe(viewLifecycleOwner, Observer {
            epoxyController.articles = it
            epoxyController.requestModelBuild()
        })

        viewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            binding.viewFlipper.displayedChild =
                if (isLoading) Flipper.LOADING.ordinal else Flipper.CONTENT.ordinal
        })

        viewModel.navigateToArticleDetails
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { arg ->
                val action =
                    HomeFragmentDirections.actionHomeFragmentToArticleDetailsFragment(arg)
                navController.navigate(action)
            }
            .launchIn(lifecycleScope)

        viewModel.message
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { msg ->
                Snackbar.make(view, msg, Snackbar.LENGTH_LONG)
                    .apply { anchorView = binding.snackbarAnchor }
                    .show()
            }
            .launchIn(lifecycleScope)
    }

    private fun setupOptionsMenu(toolbar: Toolbar) {
        toolbar.inflateMenu(R.menu.menu_home)
        val modeToggle = toolbar.menu.findItem(R.id.dark_mode_toggle)
        if (isNightModeOn()) {
            modeToggle.icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_light_mode)
        } else {
            modeToggle.icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_dark_mode)
        }

        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.dark_mode_toggle -> {
                    if (isNightModeOn()) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    } else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    }
                    true
                }
                else -> false
            }
        }
    }

    override fun onAttach(context: Context) {
        (requireActivity().application as NyTimesApplication).appComponent.inject(this)
        super.onAttach(context)
    }

    private fun isNightModeOn() =
        AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES || isSystemNightModeOn()

    private fun isSystemNightModeOn() = resources.configuration.uiMode and
            Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES

    enum class Flipper { LOADING, CONTENT }
}