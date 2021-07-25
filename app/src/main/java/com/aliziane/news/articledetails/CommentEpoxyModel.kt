package com.aliziane.news.articledetails

import android.text.format.DateUtils
import coil.load
import com.aliziane.news.R
import com.aliziane.news.common.ViewBindingKotlinModel
import com.aliziane.news.databinding.ItemCommentBinding
import kotlinx.datetime.Instant
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*
import kotlin.math.abs

data class CommentEpoxyModel(private val comment: Comment) :
    ViewBindingKotlinModel<ItemCommentBinding>(R.layout.item_comment) {

    override fun ItemCommentBinding.bind() {
        avatar.load(comment.avatarUrl) {
            placeholder(R.drawable.ic_person)
            fallback(R.drawable.ic_person)
            crossfade(true)
        }
        author.text = comment.author
        publishedDate.text =
            DateUtils.getRelativeTimeSpanString(comment.publishedDate.toEpochMilliseconds())
        body.text = comment.body
        recommendations.text = comment.recommendations.toPrettyCount()
        replyCount.text = comment.replyCount.toPrettyCount()
    }

    private fun Int.toPrettyCount(): String {
        val formatSymbols = DecimalFormatSymbols(Locale.getDefault())
        formatSymbols.decimalSeparator = '.'
        val formatter = DecimalFormat("#.#", formatSymbols)

        return when {
            abs(this / 1000000) >= 1 -> formatter.format(this / 1000000.0) + "m"
            abs(this / 1000) >= 1 -> formatter.format(this / 1000.0) + "k"
            else -> this.toString()
        }
    }
}
