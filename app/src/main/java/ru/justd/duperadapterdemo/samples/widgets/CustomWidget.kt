package ru.justd.duperadapterdemo.samples.widgets

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import ru.justd.duperadapterdemo.R

class CustomWidget(context: Context) : FrameLayout(context) {

    private val title: TextView
    private val subtitle: TextView
    private var image: ImageView

    init {
        View.inflate(context, R.layout.widget_custom, this)
        title = findViewById(R.id.title)
        subtitle = findViewById(R.id.subtitle)
        image = findViewById(R.id.image)
    }

    fun bind(item: Integer) {
        val number: Int = item.toInt()
        title.text = "Title_$item"
        subtitle.text = "Subtitle_${item}_000000000000000000000000"

        if (number == 10) {
            Log.v("DensTest", "bind: " + number + " even? ${number % 2 == 0}")
        }

        Log.v("DensTest", "dr: ${image.drawable}")
        if (number % 2 == 0) {
            image.setImageResource(R.drawable.ic_sunny)
        } else {
            image.setImageResource(R.drawable.ic_snow)
        }
    }

}
