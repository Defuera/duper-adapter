# duper-adapter
ElAdapter is dead long live DuperAdapter

# How it works

```kotlin
    ArrayListDuperAdapter()
                //add type by class (Integer::class.java) and map widget (CustomWidget) to it
                .addViewType<Integer, CustomWidget>(Integer::class.java)
                //just return a widget, view holder is handled for you
                .addViewCreator { parent -> CustomWidget(parent.context) }
                //fill the data
                .addViewBinder { widget, item -> widget.bind(item) }
                //add click listener to CustomWidget root view
                .addClickListener { view, item -> Toast.makeText(view.context, "view clicked with item $item", Toast.LENGTH_SHORT).show() }
                //add click listener to a specific view which belongs to CustomWidget
                .addClickListener(R.id.image) {
                    view, item ->
                    Toast.makeText(view.context, "image view of item $item clicked", Toast.LENGTH_SHORT).show()
                }
                .commit()
```
