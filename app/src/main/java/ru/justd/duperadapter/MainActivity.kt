package ru.justd.duperadapter

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup.LayoutParams
import android.widget.Toast
import ru.justd.duperadapter.lib.ArrayListDuperAdapter
import ru.justd.duperadapter.samples.JavaSampleActivity
import ru.justd.duperadapter.samples.ArrayListAdapterShowcase

class MainActivity : RecyclerActivity() {

    val adapter = ArrayListDuperAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        recycler.adapter = adapter

        adapter.addViewType<Sample, SampleWidget>(Sample::class.java)
                .addViewCreator { parent ->
                    val widget = SampleWidget(parent.context)
                    widget.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
                    widget
                }
                .addViewBinder { viewHolder, item -> viewHolder.view.bind(item) }
                .addViewHolderClickListener { viewHolder, _ ->
                    when (viewHolder.adapterPosition) {
                        0 -> startActivity(Intent(this, ArrayListAdapterShowcase::class.java))
                        1 -> Toast.makeText(this, "not implemented", Toast.LENGTH_SHORT).show()
                        2 -> startActivity(Intent(this, JavaSampleActivity::class.java))
                    }

                }
                .commit()

        adapter.addAll(listOf(
                Sample(
                        "ArrayListAdapter",
                        "Simple adapter showcase. Create adapter with custom view and set clickListeners to the root view and to it's child by specifying viewId"
                ),
                Sample(
                        "JavaSample",
                        "Use adapter from Java"
                ),
                Sample(
                        "Custom viewHolder",
                        "Showcase creating a custom view holder."
                ),
                Sample(
                        "JavaSample",
                        "Use adapter from Java"
                )
        ))

    }

}
