package ru.justd.duperadapter.lib

import android.support.annotation.IdRes
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup

abstract class DuperAdapter : RecyclerView.Adapter<DuperAdapter.DuperViewHolder<View>>() {

    val CONTAINER_ID = -1

    private val duperCodesList = ArrayList<String>()

    val factories = HashMap<Int, Factory<*, *>>()

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): DuperViewHolder<View> {
        val factory = getFactory<Any, View>(viewType)
        val viewHolder = factory.createViewHolder(viewGroup)

        factory.clickListeners.forEach {
            resId, clickListener ->
            val view = viewHolder.view

            val target = if (resId == CONTAINER_ID) view else view.findViewById<View>(resId)
            target.setOnClickListener { _ -> clickListener.onItemClicked(view, getItem(viewHolder.adapterPosition)) }
        }

        factory.viewHolderClickListeners.forEach {
            resId, clickListener ->
            val view = viewHolder.view

            val target = if (resId == CONTAINER_ID) view else view.findViewById<View>(resId)
            target.setOnClickListener { _ -> clickListener.onItemClicked(viewHolder, getItem(viewHolder.adapterPosition)) }
        }

        return viewHolder
    }

    override fun onBindViewHolder(viewHolder: DuperViewHolder<View>, position: Int) {
        val factory = getFactory<Any, View>(getItemViewType(position))

//        if (viewHolder.view.javaClass != factory.getViewClass()) { //todo do I want explicit exception here?
//            throw IllegalArgumentException("view holder type is not the same as view type")
//        }

        factory.viewBinder?.invoke(viewHolder, getItem(position))
    }


    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        val typeIndex = getItemViewTypeIndex(position, item)
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

    open fun <T> getItemViewTypeIndex(position: Int, item: T) = 0

    @Suppress("UNCHECKED_CAST")
    private fun <T, V : View> getFactory(viewType: Int): Factory<T, V> {
        return (factories[viewType] ?: throw FactoryNotCreatedException("factory for viewType $viewType is missing")) as Factory<T, V>
    }

    abstract fun getItem(position: Int): Any

    inner class Factory<T, V : View> constructor(
            val viewCreator: (ViewGroup) -> V,
            val viewBinder: ((DuperViewHolder<V>, T) -> Unit)?,
            val clickListeners: HashMap<Int, ItemClickListener<T, V>>,
            val viewHolderClickListeners: HashMap<Int, ItemViewHolderClickListener<T, V>>
    ) {

        fun createViewHolder(viewGroup: ViewGroup): DuperViewHolder<V> {
            return DuperViewHolder(viewCreator(viewGroup))
        }

    }


    class FactoryNotCreatedException(message: String) : RuntimeException(message)

    @JavaBackCompat
    open fun <T : Any, V : View> addViewType(itemType: Class<T>): FactoryBuilder<T, V> {
        return FactoryBuilder(itemType, 0)
    }

    open fun <T : Any, V : View> addViewType(itemType: Class<T>, typeIndex: Int = 0): FactoryBuilder<T, V> {
        return FactoryBuilder(itemType, typeIndex)
    }

    open inner class FactoryBuilder<T, V : View>(val clazz: Class<T>, val type: Int = 0) { //todo is there's a way to assure order (Chainer)?

        private lateinit var viewCreator: (ViewGroup) -> V
        private var viewBinder: ((DuperViewHolder<V>, T) -> Unit)? = null
        private val clickListeners = HashMap<Int, ItemClickListener<T, V>>()
        private val viewHolderClickListeners = HashMap<Int, ItemViewHolderClickListener<T, V>>()

        fun addViewCreator(viewCreator: (ViewGroup) -> V): FactoryBuilder<T, V> {
            this.viewCreator = viewCreator
            return this
        }

        fun addViewBinder(viewBinder: (DuperViewHolder<V>, item: T) -> Unit): FactoryBuilder<T, V> {
            this.viewBinder = viewBinder
            return this
        }

//        fun addClickListener(itemClickListener: (view: V, item: T) -> Unit): FactoryBuilder<T, V> {
//            return addClickListener(object : ItemClickListener<T, V> {
//                override fun onItemClicked(view: V, item: T) {
//                    itemClickListener.invoke(view, item)
//                }
//            })
//        }

        @OnlyKotlin
        fun addClickListener(@IdRes resId: Int = -1, itemClickListener: (view: V, item: T) -> Unit): FactoryBuilder<T, V> {
            return addClickListener(resId, object : ItemClickListener<T, V> {
                override fun onItemClicked(view: V, item: T) {
                    itemClickListener.invoke(view, item)
                }
            })
        }

        fun addViewHolderClickListener(@IdRes resId: Int = -1, itemViewHolderClickListener: (viewHolder: DuperViewHolder<V>, item: T) -> Unit): FactoryBuilder<T, V> {
            viewHolderClickListeners.put(
                    resId,
                    object : ItemViewHolderClickListener<T, V> {
                        override fun <VH : DuperViewHolder<V>> onItemClicked(viewHolder: VH, item: T) {
                            itemViewHolderClickListener.invoke(viewHolder, item)
                        }
                    })
            return this

        }

//        fun addClickListener(itemClickListener: ItemClickListener<T, V>): FactoryBuilder<T, V> {
//            return addClickListener(clickListener = itemClickListener)
//        }

        fun addClickListener(@IdRes resId: Int = -1, clickListener: ItemClickListener<T, V>): FactoryBuilder<T, V> {
            clickListeners.put(resId, clickListener)
            return this
        }


        fun commit() { //todo get rid of commit, you know how to do that

            factories.put(
                    createItemViewType(clazz, type),
                    Factory(viewCreator, viewBinder, clickListeners, viewHolderClickListeners)
            )
        }

    }

    /**
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

/**
 * Means that function is only accessible obly from Kotlin and not visible from Java
 */
annotation class OnlyKotlin

/**
 * Means that function exist only for java compatibility and optimisation
 */
annotation class JavaBackCompat


interface ItemClickListener<in T, in V : View> {

    fun onItemClicked(view: V, item: T)
}

interface ItemViewHolderClickListener<in T, in V : View> {

    fun <VH : DuperAdapter.DuperViewHolder<V>> onItemClicked(viewHolder: VH, item: T)
}
