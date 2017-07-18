package ru.justd.duperadapter

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup.LayoutParams
import ru.justd.duperadapter.lib.ArrayListDuperAdapter
import ru.justd.duperadapter.lib.ItemClickListener

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recycler = findViewById<RecyclerView>(R.id.recycler)

        recycler.layoutManager = LinearLayoutManager(this)
        val adapter = ArrayListDuperAdapter()

        adapter.addViewType<Sample, SampleWidget>(Sample::class.java)
                .addViewCreator { parent ->
                    val widget = SampleWidget(parent.context)
                    widget.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
                    widget
                }
                .addViewBinder { viewHolder, item -> viewHolder.view.bind(item) }
                .addClickListener(
                        object : ItemClickListener<Sample, SampleWidget> {
                            override fun onItemClicked(view: SampleWidget, item: Sample) {
                                startActivity(Intent(this@MainActivity, ArrayListAdapterShowcaseActivity::class.java))
                            }
                        }
                )
                .commit()

        recycler.adapter = adapter

        adapter.add(
                Sample("ArrayListAdapter",
                        "Simplest adapter showcase. Adapter creation in 3 lines"
                )
        )

    }

}
