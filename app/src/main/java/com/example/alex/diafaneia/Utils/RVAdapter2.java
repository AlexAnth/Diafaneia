package com.example.alex.diafaneia.Utils;

/**
 * Created by Alex on 22/8/2016.
 */

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.alex.diafaneia.Model.Document;
import com.example.alex.diafaneia.Model.Sector;
import com.example.alex.diafaneia.Model.Signer;
import com.example.alex.diafaneia.Model.Type;
import com.example.alex.diafaneia.R;

import java.util.ArrayList;

public class RVAdapter2 extends RecyclerView.Adapter<RVAdapter2.DataObjectHolder> {

        private ArrayList<Object> JsonList = new ArrayList<>();
        private static MyClickListener myClickListener;

        public static class DataObjectHolder extends RecyclerView.ViewHolder
                implements View
                .OnClickListener {

            private TextView ltext;
            CardView cv;

            public DataObjectHolder(View itemView) {
                super(itemView);
                ltext = (TextView) itemView.findViewById(R.id.card_text);
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

        public RVAdapter2(ArrayList<Object> myDataset) {
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

        Object object = JsonList.get(position);

        if (object.getClass().equals(Sector.class)){
                holder.ltext.setText(((Sector) object).getSectorTitle());
            }else if (object.getClass().equals(Signer.class)){
                holder.ltext.setText(((Signer) object).getSignerFullName());
            }else if (object.getClass().equals(Document.class)){
                holder.ltext.setText(((Document) object).getDocumentTitle());
            }else if (object.getClass().equals(Type.class)){
                holder.ltext.setText(((Type) object).getTypeTitle());
        }

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
