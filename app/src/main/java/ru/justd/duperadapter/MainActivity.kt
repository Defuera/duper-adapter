package ru.justd.duperadapter

import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup.LayoutParams
import ru.justd.duperadapter.lib.ArrayListDuperAdapter

class MainActivity : AppCompatActivity() {

    val adapter = ArrayListDuperAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recycler = findViewById<RecyclerView>(R.id.recycler)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        val itemDecoration = DividerItemDecoration(recycler.context, DividerItemDecoration.VERTICAL)
        itemDecoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.item_decoration_padding))
        recycler.addItemDecoration(itemDecoration)

        adapter.addViewType<Sample, SampleWidget>(Sample::class.java)
                .addViewCreator { parent ->
                    val widget = SampleWidget(parent.context)
                    widget.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
                    widget
                }
                .addViewBinder { viewHolder, item -> viewHolder.view.bind(item) }
                .addClickListener { view, item -> startActivity(Intent(this@MainActivity, ArrayListAdapterShowcaseActivity::class.java)) }
                .commit()

        adapter.addAll(listOf(
                Sample(
                        "ArrayListAdapter",
                        "Simple adapter showcase. Create adapter with custom view and set clickListeners to the root view and to it's child by specifying viewId"
                ),
                Sample(
                        "Custom viewHolder",
                        "Showcase creating a custom view holder."
                )
        ))

    }

}
