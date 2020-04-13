package ru.justd.duperadapterdemo.samples

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.Toast
import ru.justd.duperadapter.ArrayListDuperAdapter
import ru.justd.duperadapter.R
import ru.justd.duperadapterdemo.samples.widgets.CustomWidget

class ArrayListAdapterShowcase : AppCompatActivity() {

    private val adapter = ArrayListDuperAdapter().apply {
        addViewType<Integer, CustomWidget>(Integer::class.java)
            .addViewCreator { parent -> CustomWidget(parent.context) }
            .addViewBinder { widget, item -> widget.bind(item) }
            .addClickListener { view, item -> Toast.makeText(view.context, "view clicked with item $item", Toast.LENGTH_SHORT).show() }
            .addClickListener(R.id.image) { view, item ->
                Toast.makeText(view.context, "image view of item $item clicked", Toast.LENGTH_SHORT).show()
            }

            .addLongClickListener { view, item -> Toast.makeText(view.context, "view LONG  clicked with item $item", Toast.LENGTH_SHORT).show() }
            .addLongClickListener(R.id.image) { view, item ->
                Toast.makeText(view.context, "image view of item $item LONG  clicked", Toast.LENGTH_SHORT).show()
            }
            .commit()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //init layout
        val recycler = RecyclerView(this)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter
        setContentView(recycler)

        //add items
        for (i in 1..30) {
            adapter.add(i)
        }

    }

}
