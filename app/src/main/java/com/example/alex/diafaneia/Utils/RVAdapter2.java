package com.example.alex.diafaneia.Utils;

/**
 * Created by Alex on 22/8/2016.
 */

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.alex.diafaneia.Model.Search;
import com.example.alex.diafaneia.R;

import java.util.ArrayList;

import static android.text.Html.*;
import static android.text.Html.fromHtml;

public class RVAdapter2 extends RecyclerView.Adapter<RVAdapter2.DataObjectHolder> {

        private ArrayList<Search> JsonList = new ArrayList<>();
        private static MyClickListener myClickListener;

        public static class DataObjectHolder extends RecyclerView.ViewHolder
                implements View
                .OnClickListener {

            private TextView ADA;
            private TextView protoc_num;
            private TextView date;
            private TextView sectors;
            private TextView text_1;
            private TextView text_2;
            private TextView text_3;
            CardView cv;

            public DataObjectHolder(View itemView) {
                super(itemView);
                ADA = (TextView) itemView.findViewById(R.id.ADA);
                protoc_num= (TextView) itemView.findViewById(R.id.protoc_num);
                protoc_num= (TextView) itemView.findViewById(R.id.protoc_num);
                date= (TextView) itemView.findViewById(R.id.date);
                sectors= (TextView) itemView.findViewById(R.id.Sectors);
                text_1= (TextView) itemView.findViewById(R.id.text_bull_1);
                text_2= (TextView) itemView.findViewById(R.id.text_bull_2);
                text_3= (TextView) itemView.findViewById(R.id.text_bull_3);
                cv = (CardView) itemView.findViewById(R.id.cv);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                myClickListener.onItemClick(getAdapterPosition(), v);
            }
        }

        public void setOnItemClickListener(MyClickListener myClickListener) {
            this.myClickListener = myClickListener;
        }

        public RVAdapter2(ArrayList<Search> myDataset) {
            JsonList = myDataset;
        }

        @Override
        public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.results_card, parent, false);

            DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
            return dataObjectHolder;
        }


    @Override
        public void onBindViewHolder(DataObjectHolder holder, int position) {

        Search search = JsonList.get(position);

        holder.ADA.setText(search.getADA());
        holder.date.setText(search.getPublishDate());
        holder.protoc_num.setText(search.getProtoc_Num());
//        if (search.getType()!=null){
            holder.sectors.setText(search.getSector()+", "+search.getType());
//        }else{
//            holder.sectors.setText(search.getSector());
//        }
        holder.text_1.setText(search.getSbject());
        String span="<font color='#007AB2'>"+"ΘΕΜΑΤΙΚΗ ΕΝΟΤΗΤΑ"+"</font>"+" : "+search.getDocument();
        holder.text_2.setText(Html.fromHtml(span));
        span= "<font color='#007AB2'>"+search.getSigner()+"</font>";
        holder.text_3.setText(Html.fromHtml(span));

    }

        @Override
        public int getItemCount() {
            return JsonList.size();
        }

        public interface MyClickListener {
            public void onItemClick(int position, View v);
        }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
