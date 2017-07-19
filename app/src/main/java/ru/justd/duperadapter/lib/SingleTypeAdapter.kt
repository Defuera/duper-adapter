package ru.justd.duperadapter.lib

import android.view.View

/**
 * Created by defuera on 19/07/2017.
 */
class SingleTypeAdapter<T : Any>(val clazz: Class<T>) : DuperAdapter() {

    val items = ArrayList<Any>()

    override fun getItem(position: Int): Any = items[position]

    override fun getItemCount(): Int {
        return items.size
    }

    fun add(item: Any) {
        items.add(item)
    }

    fun addAll(items: List<Any>) {
        this.items.addAll(items)
    }




    fun <V : View> builder(): DuperAdapter.FactoryBuilder<T, V> {
        return super.addViewType<T, V>(clazz)
    }

    inner class FactoryBuilder<V : View>(type: Int = 0) : DuperAdapter.FactoryBuilder<T, V>(clazz, type) {

    }
}