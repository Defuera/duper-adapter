package ru.justd.duperadapter

import android.support.annotation.IdRes
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.ViewHolder
import android.view.View
import android.view.ViewGroup

private const val CONTAINER_ID = -1

abstract class DuperAdapter : RecyclerView.Adapter<ViewHolder>() {

    private val duperCodesList = ArrayList<String>()
    val factories = HashMap<Int, Factory<*, *>>()

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val factory = getFactory<Any, View>(viewType)
        val viewHolder = factory.viewHolderCreator.invoke(viewGroup)

        factory.clickListeners.forEach { (resId, clickListener) ->
            val view = viewHolder.itemView

            val target = if (resId == CONTAINER_ID) view else view.findViewById<View>(resId)
            target.setOnClickListener { _ -> clickListener.onItemClicked(view, getItem(viewHolder.adapterPosition)) }
        }

        factory.viewHolderClickListeners.forEach { (resId, clickListener) ->
            val view = viewHolder.itemView

            val target = if (resId == CONTAINER_ID) view else view.findViewById<View>(resId)
            target.setOnClickListener { _ -> clickListener.onItemClicked(viewHolder, getItem(viewHolder.adapterPosition)) }
        }

        return viewHolder
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val factory = getFactory<Any, View>(getItemViewType(position))
        factory.viewBinder?.invoke(viewHolder.itemView, getItem(position))
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
            val viewHolderCreator: (ViewGroup) -> ViewHolder,
            val viewBinder: ((V, T) -> Unit)?,
            val clickListeners: HashMap<Int, ItemClickListener<T, V>>,
            val viewHolderClickListeners: HashMap<Int, ItemViewHolderClickListener<T, V>>
    )

    class FactoryNotCreatedException(message: String) : RuntimeException(message)

    @JavaBackCompat
    open fun <T : Any, V : View> addViewType(itemType: Class<T>): FactoryBuilder<T, V> {
        return FactoryBuilder(itemType, 0)
    }

    open fun <T : Any, V : View> addViewType(itemType: Class<T>, typeIndex: Int = 0): FactoryBuilder<T, V> {
        return FactoryBuilder(itemType, typeIndex)
    }

    open inner class FactoryBuilder<T, V : View>(val clazz: Class<T>, val type: Int = 0) {

        private lateinit var viewHolderCreator: (ViewGroup) -> ViewHolder
        private var viewBinder: ((V, T) -> Unit)? = null
        private val clickListeners = HashMap<Int, ItemClickListener<T, V>>()
        private val viewHolderClickListeners = HashMap<Int, ItemViewHolderClickListener<T, V>>()

        /**
         * Wraps your view with default [ViewHolder]. Use [addViewHolderCreator] in case you need to provide custom ViewHolder
         */
        fun addViewCreator(viewCreator: (ViewGroup) -> V): FactoryBuilder<T, V> {
            addViewHolderCreator { viewGroup -> object : RecyclerView.ViewHolder(viewCreator.invoke(viewGroup)) {} }
            return this
        }

        fun addViewHolderCreator(viewHolderCreator: (ViewGroup) -> ViewHolder): FactoryBuilder<T, V> {
            this.viewHolderCreator = viewHolderCreator
            return this
        }

        fun addViewBinder(viewBinder: (V, item: T) -> Unit): FactoryBuilder<T, V> {
            this.viewBinder = viewBinder
            return this
        }

        @OnlyKotlin
        fun addClickListener(
                @IdRes resId: Int = -1,
                itemClickListener: (view: V, item: T) -> Unit
        ): FactoryBuilder<T, V> =
                addClickListener(resId, object : ItemClickListener<T, V> {
                    override fun onItemClicked(view: V, item: T) {
                        itemClickListener.invoke(view, item)
                    }
                })

        fun addViewHolderClickListener(@IdRes resId: Int = -1, itemViewHolderClickListener: (viewHolder: ViewHolder, item: T) -> Unit): FactoryBuilder<T, V> {
            viewHolderClickListeners.put(
                    resId,
                    object : ItemViewHolderClickListener<T, V> {
                        override fun <VH : ViewHolder> onItemClicked(viewHolder: VH, item: T) {
                            itemViewHolderClickListener.invoke(viewHolder, item)
                        }
                    })
            return this

        }

        fun addClickListener(@IdRes resId: Int = -1, clickListener: ItemClickListener<T, V>): FactoryBuilder<T, V> {
            clickListeners.put(resId, clickListener)
            return this
        }


        fun commit() { //todo get rid of commit, you know how to do that

            factories.put(
                    createItemViewType(clazz, type),
                    Factory(
                            viewHolderCreator,
                            viewBinder,
                            clickListeners,
                            viewHolderClickListeners)
            )
        }

    }

    /**
     * @return Int representation of itemViewType, which is directly used by RecyclerView.Adapter
     */
    private fun <T> createItemViewType(clazz: Class<T>, typeIndex: Int): Int {
        val duperCode = createDuperCode(clazz, typeIndex)

        if (duperCodesList.contains(duperCode)) {
            throw IllegalArgumentException("Factory for type ${clazz.simpleName} with index $typeIndex already exists")
        } else {
            duperCodesList.add(duperCode)
        }


        return duperCodesList.lastIndex
    }

}

/**
 * Means that function is only accessible from Kotlin and not visible from Java
 */
annotation class OnlyKotlin

/**
 * Means that function exist only for java compatibility and optimization
 */
annotation class JavaBackCompat


interface ItemClickListener<in T, in V : View> {

    fun onItemClicked(view: V, item: T)
}

interface ItemViewHolderClickListener<in T, in V : View> { //todo do we need ViewHolder clickListener at all?

    fun <VH : ViewHolder> onItemClicked(viewHolder: VH, item: T)
}
