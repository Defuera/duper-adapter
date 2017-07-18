package ru.justd.duperadapter

import android.content.Context
import android.support.v7.widget.CardView
import android.widget.TextView

class SampleWidget(context: Context) : CardView(context) {

    var title: TextView
    var description: TextView

    init {
        inflate(context, R.layout.widget_sample, this)
        title = findViewById(R.id.title)
        description = findViewById(R.id.description)
    }

    fun bind(sample: Sample) {
        title.text = sample.title
        description.text = sample.description
    }

}