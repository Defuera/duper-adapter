package ru.justd.duperadapter

import android.support.annotation.IdRes
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup

abstract class DuperAdapter : RecyclerView.Adapter<DuperAdapter.DuperViewHolder<View>>() {

    private val duperCodesList = ArrayList<String>()

    val factories = HashMap<Int, Factory<*, *>>()

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): DuperViewHolder<View> {
        val factory = getFactory<Any, View>(viewType)
        val viewHolder = factory.createViewHolder(viewGroup)

        factory.clickListeners.forEach {
            resId, clickListener ->
            val view = viewHolder.view

            if (resId == CONTAINER_ID) {
                view
            } else {
                view.findViewById<View>(resId)
            }.setOnClickListener {
                v ->
                clickListener.onItemClicked(view, getItem(viewHolder.adapterPosition))
            }

        }

        return viewHolder
    }

    override fun onBindViewHolder(viewHolder: DuperViewHolder<View>, position: Int) {
        val factory = getFactory<Any, View>(getItemViewType(position))

//        if (viewHolder.view.javaClass != factory.getViewClass()) { //todo
//            throw IllegalArgumentException("view holder type is not the same as view type")
//        }

        factory.viewBinder?.invoke(viewHolder, getItem(position))
    }


    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        val typeIndex = getItemViewTypeIndex(item)
        val itemViewType = duperCodesList.indexOf(
                createDuperCode(
                        item.javaClass,
                        typeIndex
                )
        )

        if (itemViewType == -1) {
            throw IllegalArgumentException("cannot find factory for item with type ${item.javaClass} and typeIndex $typeIndex")
        }

        return itemViewType
    }

    private fun <T> createDuperCode(clazz: Class<T>, typeIndex: Int) = clazz.simpleName + "_" + typeIndex

    open fun <T> getItemViewTypeIndex(item: T) = 0

    @Suppress("UNCHECKED_CAST")
    private fun <T, V : View> getFactory(viewType: Int): Factory<T, V> {
        return (factories[viewType] ?: throw FactoryNotCreatedException("factory for viewType $viewType is missing")) as Factory<T, V>
    }

    abstract fun getItem(position: Int): Any

    inner class Factory<T, V : View>(
            val viewCreator: (ViewGroup) -> V,
            val viewBinder: ((DuperViewHolder<V>, T) -> Unit)?,
            val clickListeners: HashMap<Int, ItemClickListener<T, V>>
    ) {

        fun createViewHolder(viewGroup: ViewGroup): DuperViewHolder<V> {
            return DuperViewHolder(viewCreator(viewGroup))
        }

    }


    class FactoryNotCreatedException(message: String) : RuntimeException(message)

    fun <T : Any, V : View> addViewType(itemType: Class<T>, viewType: Int = 0): FactoryBuilder<T, V> {
        return FactoryBuilder(itemType, viewType)
    }

    inner class FactoryBuilder<T, V : View>(val clazz: Class<T>, val type: Int = 0) {

        private lateinit var viewCreator: (ViewGroup) -> V
        private var viewBinder: ((DuperViewHolder<V>, T) -> Unit)? = null
        private val clickListeners = HashMap<Int, ItemClickListener<T, V>>()

        fun addViewCreator(viewCreator: (ViewGroup) -> V): FactoryBuilder<T, V> {
            this.viewCreator = viewCreator
            return this
        }

        fun addViewBinder(viewBinder: (DuperViewHolder<V>, item: T) -> Unit): FactoryBuilder<T, V> {
            this.viewBinder = viewBinder
            return this
        }

        fun addClickListener(itemClickListener: ItemClickListener<T, V>): FactoryBuilder<T, V> {
            return addClickListener(clickListener = itemClickListener)
        }

        fun addClickListener(
                @IdRes resId: Int = -1,
                clickListener: ItemClickListener<T, V>): FactoryBuilder<T, V> {
            clickListeners.put(resId, clickListener)
            return this
        }


        fun commit() {

            factories.put(
                    createItemViewType(clazz, type),
                    Factory(viewCreator, viewBinder, clickListeners)
            )
        }

    }


    /**
     *
     * @return Int representation of itemViewType, which is directly used by RecyclerView.Adapter
     */
    private fun <T> createItemViewType(clazz: Class<T>, typeIndex: Int): Int {
        var duperCode = createDuperCode(clazz, typeIndex)

        if (duperCodesList.contains(duperCode)) {
            throw IllegalArgumentException("Factory for type ${clazz.simpleName} with index $typeIndex already exists")
        } else {
            duperCodesList.add(duperCode)
        }


        return duperCodesList.lastIndex
    }


    class DuperViewHolder<out V : View>(val view: V) : RecyclerView.ViewHolder(view)

}

val CONTAINER_ID = -1

interface ItemClickListener<in T, in V : View> {

    fun onItemClicked(view: V, item: T)
}