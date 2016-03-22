package com.alexqueudotrafel.retrofitrealmsample.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.alexqueudotrafel.retrofitrealmsample.R;
import com.alexqueudotrafel.retrofitrealmsample.model.Question;

import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;

/**
 * Created by alexqueudotrafel on 16/03/16.
 */
public class QuestionsAdapter extends RealmBaseAdapter<Question> implements ListAdapter{

    public QuestionsAdapter(Context context, RealmResults<Question> questions){
        super(context, questions, true /*automatic updates*/);
    }

    private static class ViewHolder{
        TextView title;
        TextView link;
    }

    public RealmResults<Question> getQuestionsList() {
        return realmResults;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        // Try to reuse view, otherwise inflate it
        ViewHolder viewHolder;
        if(view==null){
            // Inflate view
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.item_question, parent, false);
            // Create, set and save viewHolder
            viewHolder = new ViewHolder();
            viewHolder.title = (TextView) view.findViewById(R.id.title);
            viewHolder.link = (TextView) view.findViewById(R.id.link);
            view.setTag(viewHolder);
        } else{
            viewHolder = (ViewHolder) view.getTag();
        }

        // Get item for this position
        Question question = getItem(position);

        // Populate the data
        viewHolder.title.setText(question.getTitle());
        viewHolder.link.setText(question.getLink());

        // Return the view for UI rendering
        return view;
    }
}
