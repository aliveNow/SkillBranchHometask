package ru.skillbranch.skillarticles.viewmodels.base

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class ViewModelDelegate<T : ViewModel>(private val clazz: Class<T>, private val arg: Any?) :
    ReadOnlyProperty<FragmentActivity, T> {
    private var value: T? = null

    override fun getValue(thisRef: FragmentActivity, property: KProperty<*>): T {
        if (value == null) {
            val vmFactory = ViewModelFactory(arg)
            value = ViewModelProviders.of(thisRef, vmFactory).get(clazz)
        }
        return value!!
    }

    private class ViewModelFactory(private val arg: Any?) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return modelClass.constructors.first().newInstance(arg) as T
        }
    }

}