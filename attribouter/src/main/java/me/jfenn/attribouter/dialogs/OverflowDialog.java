package me.jfenn.attribouter.dialogs;

import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;

import java.util.List;

import me.jfenn.attribouter.R;
import me.jfenn.attribouter.adapters.InfoAdapter;
import me.jfenn.attribouter.wedges.Wedge;
import me.jfenn.attribouter.utils.ResourceUtils;

public class OverflowDialog extends AppCompatDialog {

    private String title;
    private List<Wedge> items;

    public OverflowDialog(Context context, String title, List<Wedge> items) {
        super(context, ResourceUtils.getThemeResourceAttribute(context, R.styleable.AttribouterTheme_overflowDialogTheme, R.style.AttribouterTheme_Dialog_Fullscreen));
        this.title = title;
        this.items = items;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_attribouter_overflow);

        Toolbar toolbar = findViewById(R.id.toolbar);
        RecyclerView recycler = findViewById(R.id.recycler);

        toolbar.setTitle(ResourceUtils.getString(getContext(), title));
        toolbar.setNavigationIcon(R.drawable.ic_attribouter_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler.setAdapter(new InfoAdapter(items));
    }
}
