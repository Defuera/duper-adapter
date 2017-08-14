package ru.justd.duperadapter.samples

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.TextView
import android.widget.Toast
import ru.justd.duperadapter.R
import ru.justd.duperadapter.lib.ArrayListDuperAdapter

class MultipleTypesAdapterShowcase : AppCompatActivity() {

    private val HEADER_VIEW_TYPE_INDEX = 0
    private val FOOTER_VIEW_TYPE_INDEX = 1

    private val adapter = object : ArrayListDuperAdapter() {

        override fun <T> getItemViewTypeIndex(position: Int, item: T): Int {
            return when (position) {
                0 -> HEADER_VIEW_TYPE_INDEX
                itemCount - 1 -> FOOTER_VIEW_TYPE_INDEX
                else -> super.getItemViewTypeIndex(position, item)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val padding = resources.getDimensionPixelOffset(R.dimen.padding_16)

        //init layout
        val recycler = RecyclerView(this)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter
        setContentView(recycler)

        //add header type
        adapter
                .addViewType<String, TextView>(String::class.java, HEADER_VIEW_TYPE_INDEX)
                .addViewCreator { parent -> val textView = TextView(parent.context)
                    textView.setPadding(padding, padding, padding, padding)
                    textView
                }
                .addViewBinder { viewHolder, _ -> viewHolder.view.text = "I'm a header, man" }
                .commit()

        //add item type
        adapter
                .addViewType<Integer, CustomWidget>(Integer::class.java)
                .addViewCreator { parent -> CustomWidget(parent.context) }
                .addViewBinder { viewHolder, item -> viewHolder.view.bind(item) }
                .addClickListener { view, item -> Toast.makeText(view.context, "view clicked with item $item", Toast.LENGTH_SHORT).show() }
                .addClickListener(R.id.image) {
                    view, item ->
                    Toast.makeText(view.context, "image view of item $item clicked", Toast.LENGTH_SHORT).show()
                }
                .commit()

        //add footer type
        //Notice: footer do not have binder
        adapter
                .addViewType<String, TextView>(String::class.java, FOOTER_VIEW_TYPE_INDEX)
                .addViewCreator { parent -> val textView = TextView(parent.context)
                    textView.text = "I'm a footer"
                    textView.setPadding(padding, padding, padding, padding)
                    textView
                }
                .commit()

        adapter.add("Header title")
        //add items
        for (i in 1..30) {
            adapter.add(i)
        }
        adapter.add("Footer")
    }

}