package com.alexqueudotrafel.retrofitrealmsample.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alexqueudotrafel.retrofitrealmsample.R;
import com.alexqueudotrafel.retrofitrealmsample.model.Question;

import io.realm.RealmResults;

/**
 * Created by alexqueudotrafel on 24/03/16.
 */
public class QuestionsRecyclerViewAdapter extends RecyclerView.Adapter<QuestionsRecyclerViewAdapter.ViewHolder> {

    private RealmResults<Question> mList;

    public  QuestionsRecyclerViewAdapter(){
    }
    public QuestionsRecyclerViewAdapter(RealmResults<Question>list){
        setList(list);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        //public LinearLayout container;
        public TextView titleTextView;
        public TextView linkTextView;

        public ViewHolder(View container){
            super(container);
            //this.container = container;
            this.titleTextView = (TextView) container.findViewById(R.id.title);
            this.linkTextView = (TextView) container.findViewById(R.id.link);
        }
    }

    @Override
    public QuestionsRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_question, parent, false);

        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.titleTextView.setText(mList.get(position).getTitle());
        holder.linkTextView.setText(mList.get(position).getLink());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public Question getItem(int position){
        return mList.get(position);
    }

    public RealmResults<Question> getList(){
        return mList;
    }

    public void setList(RealmResults<Question> list){
        mList = list;
    }

    public boolean isEmpty(){
        return mList == null || mList.size()==0;
    }
}
