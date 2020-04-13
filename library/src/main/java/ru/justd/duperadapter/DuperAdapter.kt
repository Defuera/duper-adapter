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

            val target = if (resId == CONTAINER_ID) view else view.findViewById(resId)
            target.setOnClickListener { clickListener(view, getItem(viewHolder.adapterPosition)) }
        }

        factory.viewHolderClickListeners.forEach { (resId, clickListener) ->
            val view = viewHolder.itemView

            val target = if (resId == CONTAINER_ID) view else view.findViewById(resId)
            target.setOnClickListener { clickListener.onItemClicked(viewHolder, getItem(viewHolder.adapterPosition)) }
        }

        factory.longClickListeners.forEach { (resId, clickListener) ->
            val view = viewHolder.itemView

            val target = if (resId == CONTAINER_ID) view else view.findViewById(resId)
            target.setOnLongClickListener {
                clickListener(view, getItem(viewHolder.adapterPosition))
                return@setOnLongClickListener true
            }
        }

        factory.viewHolderLongClickListeners.forEach { (resId, clickListener) ->
            val view = viewHolder.itemView

            val target = if (resId == CONTAINER_ID) view else view.findViewById<View>(resId)
            target.setOnLongClickListener {
                clickListener.onItemClicked(viewHolder, getItem(viewHolder.adapterPosition))
                return@setOnLongClickListener true
            }
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
            val clickListeners: HashMap<Int, (view: V, item: T) -> Unit>,
            val viewHolderClickListeners: HashMap<Int, ItemViewHolderClickListener<T, V>>,
            val longClickListeners: HashMap<Int, (view: V, item: T) -> Unit>,
            val viewHolderLongClickListeners: HashMap<Int, ItemViewHolderClickListener<T, V>>
    )

    class FactoryNotCreatedException(message: String) : RuntimeException(message)

    inline fun <reified T : Any, V : View> addViewType(typeIndex: Int = 0): FactoryBuilder<T, V> {
        return FactoryBuilder(T::class.java, typeIndex)
    }

    open inner class FactoryBuilder<T, V : View>(val clazz: Class<T>, val type: Int = 0) {

        private lateinit var viewHolderCreator: (ViewGroup) -> ViewHolder
        private var viewBinder: ((V, T) -> Unit)? = null
        private val clickListeners = HashMap<Int, (view: V, item: T) -> Unit>()
        private val longClickListeners = HashMap<Int, (view: V, item: T) -> Unit>()
        private val viewHolderClickListeners = HashMap<Int, ItemViewHolderClickListener<T, V>>()
        private val viewHolderLongClickListeners = HashMap<Int, ItemViewHolderClickListener<T, V>>()

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

        //region click listener
        fun addClickListener(@IdRes resId: Int = -1, itemClickListener: (view: V, item: T) -> Unit): FactoryBuilder<T, V> = apply {
            clickListeners[resId] = itemClickListener
        }

        fun addViewHolderClickListener(@IdRes resId: Int = -1, itemViewHolderClickListener: (viewHolder: ViewHolder, item: T) -> Unit): FactoryBuilder<T, V> {
            viewHolderClickListeners[resId] = object : ItemViewHolderClickListener<T, V> {
                override fun <VH : ViewHolder> onItemClicked(viewHolder: VH, item: T) {
                    itemViewHolderClickListener.invoke(viewHolder, item)
                }
            }
            return this
        }
        //end region

        //region long click listener
        fun addLongClickListener(@IdRes resId: Int = -1, longClickListener: (view: V, item: T) -> Unit): FactoryBuilder<T, V> = apply {
            longClickListeners[resId] = longClickListener
        }

        fun addViewHolderLongClickListener(@IdRes resId: Int = -1, itemViewHolderClickListener: (viewHolder: ViewHolder, item: T) -> Unit): FactoryBuilder<T, V> {
            viewHolderLongClickListeners[resId] = object : ItemViewHolderClickListener<T, V> {
                override fun <VH : ViewHolder> onItemClicked(viewHolder: VH, item: T) {
                    itemViewHolderClickListener.invoke(viewHolder, item)
                }
            }
            return this
        }
        //end region

        fun commit() {
            factories[createItemViewType(clazz, type)] = Factory(
                viewHolderCreator,
                viewBinder,
                clickListeners,
                viewHolderClickListeners,
                longClickListeners,
                viewHolderLongClickListeners
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

interface ItemViewHolderClickListener<in T, in V : View> {

    fun <VH : ViewHolder> onItemClicked(viewHolder: VH, item: T)
}
