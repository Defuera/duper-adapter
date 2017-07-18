package ru.justd.duperadapter.lib

open class ArrayListDuperAdapter : DuperAdapter() {

    val items = ArrayList<Any>()

    override fun getItem(position: Int): Any = items[position]

    override fun getItemCount(): Int {
        return items.size
    }

    fun add(item: Any) {
        items.add(item)
    }

    fun addAll(item: List<Any>) {
        items.addAll(item)
    }

}