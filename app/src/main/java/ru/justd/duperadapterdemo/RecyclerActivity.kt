package ru.justd.duperadapterdemo

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView

/**
 * Created by defuera on 18/07/2017.
 */
abstract class RecyclerActivity : AppCompatActivity() {

    lateinit var recycler: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simple_list)

        recycler = findViewById(R.id.recycler)
        recycler.layoutManager = LinearLayoutManager(this)

        val itemDecoration = DividerItemDecoration(recycler.context, DividerItemDecoration.VERTICAL)
        val drawable = ContextCompat.getDrawable(this, R.drawable.item_decoration_padding)
        if (drawable != null) {
            itemDecoration.setDrawable(drawable)
        }
        recycler.addItemDecoration(itemDecoration)

    }

}
