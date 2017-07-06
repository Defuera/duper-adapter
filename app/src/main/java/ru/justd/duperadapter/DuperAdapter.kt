package ru.justd.duperadapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup

abstract class DuperAdapter : RecyclerView.Adapter<DuperAdapter.DuperViewHolder<View>>() {

    private val duperCodesList = ArrayList<String>()

    val factories = HashMap<Int, Factory<*, *>>()

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): DuperViewHolder<View> {
        return getFactory<Any, View>(viewType).createViewHolder(viewGroup)
    }

    override fun onBindViewHolder(viewHolder: DuperViewHolder<View>, position: Int) {
        val factory = getFactory<Any, View>(getItemViewType(position))

//        if (viewHolder.view.javaClass != factory.getViewClass()) { //todo
//            throw IllegalArgumentException("view holder type is not the same as view type")
//        }

        factory.viewBinder(viewHolder, getItem(position))
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

    inner class Factory<in T, V : View> constructor(
            val viewCreator: (ViewGroup) -> V,
            val viewBinder: (DuperViewHolder<V>, T) -> Unit
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
        private lateinit var viewBinder: (DuperViewHolder<V>, T) -> Unit

        fun addViewCreator(viewCreator: (ViewGroup) -> V): FactoryBuilder<T, V> {
            this.viewCreator = viewCreator
            return this
        }

        fun addViewBinder(viewBinder: (DuperViewHolder<V>, item: T) -> Unit): FactoryBuilder<T, V> {
            this.viewBinder = viewBinder
            return this
        }

        fun commit() {

            factories.put(
                    createItemViewType(clazz, type),
                    Factory(viewCreator, viewBinder)
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