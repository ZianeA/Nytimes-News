package com.aliziane.news

import android.view.View

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.hide() {
    this.visibility = View.GONE
}

inline fun <T : View> T.showIf(condition: () -> Boolean): T {
    if (condition()) show() else hide()

    return this
}

inline fun <T : View> T.hideIf(condition: () -> Boolean): T {
    if (condition()) hide() else show()

    return this
}