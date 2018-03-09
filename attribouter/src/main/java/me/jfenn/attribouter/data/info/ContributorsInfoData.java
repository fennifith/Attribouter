package me.jfenn.attribouter.data.info;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import me.jfenn.attribouter.R;

public class ContributorsInfoData extends InfoData<ContributorsInfoData.ViewHolder> {

    public ContributorsInfoData(XmlResourceParser parser) {
        super(R.layout.item_attribouter_contributors);
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public void bind(Context context, ViewHolder viewHolder) {

    }

    class ViewHolder extends InfoData.ViewHolder {

        private View topThreeView;
        private View firstView;
        private ImageView firstImageView;
        private TextView firstNameView;
        private TextView firstTaskView;
        private View secondView;
        private ImageView secondImageView;
        private TextView secondNameView;
        private TextView secondTaskView;
        private View thirdView;
        private ImageView thirdImageView;
        private TextView thirdNameView;
        private TextView thirdTaskView;
        private RecyclerView recycler;

        ViewHolder(View v) {
            super(v);

            topThreeView = v.findViewById(R.id.topThree);
            firstView = v.findViewById(R.id.first);
            firstImageView = v.findViewById(R.id.firstImage);
            firstNameView = v.findViewById(R.id.firstName);
            firstTaskView = v.findViewById(R.id.firstTask);
            secondView = v.findViewById(R.id.second);
            secondImageView = v.findViewById(R.id.secondImage);
            secondNameView = v.findViewById(R.id.secondName);
            secondTaskView = v.findViewById(R.id.secondTask);
            thirdView = v.findViewById(R.id.third);
            thirdImageView = v.findViewById(R.id.thirdImage);
            thirdNameView = v.findViewById(R.id.thirdName);
            thirdTaskView = v.findViewById(R.id.thirdTask);
            recycler = v.findViewById(R.id.recycler);
        }
    }

}
