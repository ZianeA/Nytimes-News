package com.aliziane.news.articledetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.view.ContextThemeWrapper
import androidx.navigation.fragment.findNavController
import com.aliziane.news.R
import com.aliziane.news.databinding.DialogSortByBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.elevation.ElevationOverlayProvider
import com.google.android.material.textview.MaterialTextView

class SortByDialog : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = DialogSortByBinding.inflate(layoutInflater, container, false)
        val navController = findNavController()
        setNavigationBarColor()

        SortBy.values().forEach { sortBy ->
            val contextTheme =
                ContextThemeWrapper(context, R.style.Widget_MaterialComponents_TextView_Checklist)

            MaterialTextView(contextTheme).apply {
                text = getString(sortBy.displayName)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                val selection =
                    requireNotNull(requireArguments().getSerializable(KEY_SELECTION)) as SortBy
                if (sortBy == selection) {
                    setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_check, 0)
                }

                setOnClickListener {
                    val savedStateHandle = navController.previousBackStackEntry!!.savedStateHandle
                    savedStateHandle.set(KEY_SELECTION, sortBy)
                    dismiss()
                }

                binding.root.addView(this)
            }
        }

        return binding.root
    }

    private fun setNavigationBarColor() {
        val dialogElevation = resources.getDimension(R.dimen.dialog_elevation)
        val barColor = ElevationOverlayProvider(requireContext())
            .compositeOverlayWithThemeSurfaceColorIfNeeded(dialogElevation)
        dialog?.window?.navigationBarColor = barColor
    }

    override fun getTheme() = R.style.RoundedBottomSheetDialog

    companion object {
        const val KEY_SELECTION = "selected_sort_by"
    }
}