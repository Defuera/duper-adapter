package ru.justd.duperadapter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.Arrays;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import ru.justd.duperadapter.lib.ArrayListDuperAdapter;
import ru.justd.duperadapter.lib.DuperAdapter;

public class JavaSampleActivity extends RecyclerActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ArrayListDuperAdapter adapter = new ArrayListDuperAdapter();
        recycler.setAdapter(adapter);

        adapter
                .<Integer, CustomWidget>addViewType(Integer.class)
                .addViewCreator(new Function1<ViewGroup, CustomWidget>() {
                    @Override
                    public CustomWidget invoke(ViewGroup viewGroup) {
                        return new CustomWidget(viewGroup.getContext());
                    }
                })
                .addViewBinder(new Function2<DuperAdapter.DuperViewHolder<? extends CustomWidget>, Integer, Unit>() {
                    @Override
                    public Unit invoke(DuperAdapter.DuperViewHolder<? extends CustomWidget> viewHolder, Integer item) {//todo returning Unit is bullshit, it should be void
                        viewHolder.getView().bind(item);
                        return null;
                    }
                })
                .addClickListener(R.id.title, new Function2<View, Integer, Unit>() {
                    @Override
                    public Unit invoke(View view, Integer $item) { //todo returning Unit is bullshit, it should be void
                        Toast.makeText(view.getContext(), "image view of item " + $item + " clicked", Toast.LENGTH_SHORT).show();
                        return null;
                    }
                })
                .commit();

        adapter.addAll(Arrays.asList(1, 2, 3, 4, 5));
    }
}
