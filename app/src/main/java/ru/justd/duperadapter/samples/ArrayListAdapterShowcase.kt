package ru.justd.duperadapter.samples

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.Toast
import ru.justd.duperadapter.R
import ru.justd.duperadapter.lib.ArrayListDuperAdapter

class ArrayListAdapterShowcase : AppCompatActivity() {

    private val adapter = ArrayListDuperAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //init layout
        val recycler = RecyclerView(this)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter
        setContentView(recycler)

        //init adapter
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


        //add items
        for (i in 1..30) {
            adapter.add(i)
        }

    }

}