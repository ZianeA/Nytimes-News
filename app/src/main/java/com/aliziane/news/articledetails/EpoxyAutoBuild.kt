package com.aliziane.news.articledetails

import com.airbnb.epoxy.EpoxyController
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class EpoxyAutoBuild<T>(defaultValue: T) :
    ReadWriteProperty<Any?, T> {
    private var value: T = defaultValue
    override fun getValue(thisRef: Any?, property: KProperty<*>) = value
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        this.value = value
        if (thisRef !is EpoxyController) {
            throw IllegalStateException("The delegated property owner must be an EpoxyController.")
        }
        thisRef.requestModelBuild()
    }
}