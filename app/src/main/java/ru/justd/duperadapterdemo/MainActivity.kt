package ru.justd.duperadapterdemo

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup.LayoutParams
import android.widget.TextView
import ru.justd.duperadapter.ArrayListDuperAdapter
import ru.justd.duperadapterdemo.samples.ArrayListAdapterShowcase
import ru.justd.duperadapterdemo.samples.MultipleTypesAdapterShowcase

class MainActivity : RecyclerActivity() {

    private val adapter = ArrayListDuperAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        recycler.adapter = adapter


        adapter.addViewType<String, TextView>()
                .addViewHolderCreator { viewGroup ->
                    val textView = TextView(viewGroup.context)
                    textView.text = "Samples:"
                    val p = viewGroup.context.resources.getDimensionPixelOffset(R.dimen.padding_16)
                    textView.setPadding(p, p, p, p)
                    object : RecyclerView.ViewHolder(textView) {}
                }
                .commit()

        adapter.addViewType<Sample, SampleWidget>()
                .addViewCreator { parent ->
                    val widget = SampleWidget(parent.context)
                    widget.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
                    widget
                }
                .addViewBinder { view, item -> view.bind(item) }
                .addViewHolderClickListener { viewHolder, _ ->
                    when (viewHolder.adapterPosition) {
                        1 -> startActivity(Intent(this, ArrayListAdapterShowcase::class.java))
                        2 -> startActivity(Intent(this, MultipleTypesAdapterShowcase::class.java))
                    }

                }
                .commit()

        adapter.add( "Samples:")
        adapter.addAll(listOf(
                Sample(
                        "ArrayListAdapter",
                        "Simple adapter showcase. Create adapter with custom view and set clickListeners to the root view and to it's child by specifying viewId"
                ),
                Sample(
                        "Adapter with multiple types",
                        "Adapter with header and footer. It's also shows how you can register multiple different view types for the same class"
                )
        ))

    }

}
