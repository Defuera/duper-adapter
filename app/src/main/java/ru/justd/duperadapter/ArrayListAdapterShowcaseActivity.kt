package ru.justd.duperadapter

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.Toast
import ru.justd.duperadapter.lib.ArrayListDuperAdapter
import ru.justd.duperadapter.lib.ItemClickListener

class ArrayListAdapterShowcaseActivity : AppCompatActivity() {

    val duperAdapter = ArrayListDuperAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //init layout
        val recycler = RecyclerView(this)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = duperAdapter
        setContentView(recycler)

        //init adapter
        duperAdapter
                .addViewType<Integer, CustomWidget>(Integer::class.java) //todo not working with kotlin Int
                .addViewCreator { parent -> CustomWidget(parent.context) }
                .addViewBinder { viewHolder, item ->
                    viewHolder.view.bind(item)
                }
                .addClickListener(
                        object : ItemClickListener<Integer, CustomWidget> { //todo why types are not infered
                            override fun onItemClicked(view: CustomWidget, item: Integer) {
                                Toast.makeText(view.context, "view clicked with item $item", Toast.LENGTH_SHORT).show()
                            }

                        }
                )
                .addClickListener(
                        R.id.image,
                        object : ItemClickListener<Integer, CustomWidget> { //todo why can't I use lambda?
                            override fun onItemClicked(view: CustomWidget, item: Integer) {
                                Toast.makeText(view.context, "image view of item $item clicked", Toast.LENGTH_SHORT).show()
                            }

                        }
                )
                .commit()


        //add items
        for (i in 1..30) {
            duperAdapter.add(i)
        }

    }

}