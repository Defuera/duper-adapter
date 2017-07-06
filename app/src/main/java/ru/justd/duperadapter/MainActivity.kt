package ru.justd.duperadapter

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_main)
        super.onCreate(savedInstanceState)

        val recycler = findViewById<RecyclerView>(R.id.recycler)
        recycler.layoutManager = LinearLayoutManager(this)

        val duperAdapter = object : ArrayListDuperAdapter() {

            override fun <T> getItemViewTypeIndex(item: T): Int {
                if (item is String) {
                    return if (item == "two") 1 else 0
                } else {
                    return super.getItemViewTypeIndex(item)
                }
            }
        }

        recycler.adapter = duperAdapter

        duperAdapter
                .addViewType<String, TextView>(String::class.java,1)
                .addViewCreator({ vg -> TextView(vg.context) })
                .addViewBinder({ viewHolder, item -> viewHolder.view.text = item })
                .commit()

        duperAdapter
                .addViewType<String, TextView>(String::class.java, 1)
                .addViewCreator({ vg ->
                    val textView = TextView(vg.context)
                    val p = 16
                    textView.setPadding(p, p, p, p)
                    textView.textSize = 14f
                    textView.setTextColor(Color.BLACK)
                    textView
                })
                .addViewBinder({ viewHolder, item -> viewHolder.view.text = item })
                .commit()

        duperAdapter
                .addViewType<Integer, TextView>(Integer::class.java)
                .addViewCreator({ vg ->
                    val textView = TextView(vg.context)
                    textView.setTextColor(Color.BLUE)
                    textView
                })
                .addViewBinder({ viewHolder, item -> viewHolder.view.text = item.toString() })
                .commit()

        duperAdapter.add("one")
        duperAdapter.add("two")
        duperAdapter.add("three")

        duperAdapter.add(1)
        duperAdapter.add(2)
        duperAdapter.add(3)

    }

}
