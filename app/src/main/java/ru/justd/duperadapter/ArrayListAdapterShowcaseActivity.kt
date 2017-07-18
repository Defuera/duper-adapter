package ru.justd.duperadapter

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import ru.justd.duperadapter.lib.ArrayListDuperAdapter
import ru.justd.duperadapter.lib.ItemClickListener

class ArrayListAdapterShowcaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val recycler = RecyclerView(this)


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
                .addViewType<String, TextView>(String::class.java)
                .addViewCreator({ vg -> TextView(vg.context) })
                .addViewBinder({ viewHolder, item -> viewHolder.view.text = item })
                .addClickListener(object : ItemClickListener<String, TextView> {
                    override fun onItemClicked(view: TextView, item: String) {
                        Toast.makeText(view.context, "view clicked with item $item", Toast.LENGTH_SHORT).show()
                    }

                })
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
                .addViewType<Integer, CustomWidget>(Integer::class.java)
                .addViewCreator { parent -> CustomWidget(parent.context) }
                .addViewBinder { viewHolder, item ->
                    viewHolder.view.bind(item)
                }
                .addClickListener(
                        object : ItemClickListener<Integer, CustomWidget> {
                            override fun onItemClicked(view: CustomWidget, item: Integer) {
                                Toast.makeText(view.context, "view clicked with item $item", Toast.LENGTH_SHORT).show()
                            }

                        }
                )
                .addClickListener(
                        R.id.image,
                        object : ItemClickListener<Integer, CustomWidget> {
                            override fun onItemClicked(view: CustomWidget, item: Integer) {
                                Toast.makeText(view.context, "image view of item $item clicked", Toast.LENGTH_SHORT).show()
                            }

                        }
                )
                .commit()

        duperAdapter
                .addViewType<Progress, ProgressBar>(Progress::class.java)
                .addViewCreator({ vg ->
                    ProgressBar(vg.context, null, android.R.attr.progressBarStyleSmall)
                })
                .commit()

        duperAdapter.add("one")
        duperAdapter.add("two")
        duperAdapter.add("three")

        for (i in 1..30) {
            duperAdapter.add(i)
        }

        duperAdapter.add(Progress())

    }

    class Progress

}