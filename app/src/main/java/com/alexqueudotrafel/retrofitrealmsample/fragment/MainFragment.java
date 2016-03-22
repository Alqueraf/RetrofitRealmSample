package com.alexqueudotrafel.retrofitrealmsample.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.alexqueudotrafel.retrofitrealmsample.BuildConfig;
import com.alexqueudotrafel.retrofitrealmsample.MyApp;
import com.alexqueudotrafel.retrofitrealmsample.R;
import com.alexqueudotrafel.retrofitrealmsample.adapter.QuestionsAdapter;
import com.alexqueudotrafel.retrofitrealmsample.db.RealmHelper;
import com.alexqueudotrafel.retrofitrealmsample.model.Question;
import com.alexqueudotrafel.retrofitrealmsample.network.StackOverflowService;
import com.alexqueudotrafel.retrofitrealmsample.network.util.DialogWaitManager;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment {

    private static final String TAG = "MainFragment";
    private static final boolean D = BuildConfig.DEBUG;

    // Realm database variable
    private Realm mRealm;

    // Pull-To-Refresh Container
    private SwipeRefreshLayout mSwipeRefreshContainer;
    // Questions Adapter
    private QuestionsAdapter mQuestionsAdapter;

    public MainFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        mRealm = Realm.getDefaultInstance();

        //Init Swipe to Refresh
        initSwipeToRefresh();

        // Retrieve questions locally (async)
        RealmResults<Question> questionList = mRealm.where(Question.class).findAllSortedAsync("unixDate", Sort.DESCENDING);
        questionList.addChangeListener(questionsCallback);
        initListView(questionList);

        // Retrieve questions from the API
        retrieveQuestions();
    }

    /**
     * UI method - Initialize SwipetoRefresh
     */
    private void initSwipeToRefresh(){
        mSwipeRefreshContainer = (SwipeRefreshLayout) getActivity().findViewById(R.id.swipeContainer);
        mSwipeRefreshContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                retrieveQuestions();
            }
        });
    }

    /**
     * UI Method - Initialize ListView
     * @param questionList Data to bind to the Listview's Adapter
     */
    private void initListView(RealmResults<Question> questionList){
        // Initialize Adapter and set to ListView
        mQuestionsAdapter = new QuestionsAdapter(getContext(), questionList);
        final ListView listview = (ListView) getActivity().findViewById(R.id.listview);
        if(listview.getAdapter()==null) {
            listview.setAdapter(mQuestionsAdapter);
        }

    }

    /**
     * Get Data from backend
     */
    private void retrieveQuestions() {
        // Show Dialog if no questions are loaded
        if(mQuestionsAdapter!=null && mQuestionsAdapter.getQuestionsList().size()==0)
            DialogWaitManager.getInstance().showDialog(getContext());
        // Retrieve questions from the Backend
        final StackOverflowService stackOverflowService = StackOverflowService.retrofit.create(StackOverflowService.class);
        stackOverflowService.getQuestions().enqueue(new Callback<List<Question>>() {
            @Override
            public void onResponse(Call<List<Question>> call, Response<List<Question>> response) {
                DialogWaitManager.getInstance().dismissDialog();
                try {
                    if (D) Log.d(TAG, "onResponse: " + response.body().toString());
                    // Save data to Realm
                    if (response.body().size() > 0 && !mRealm.isClosed()) {
                        RealmHelper.copyOrUpdate(response.body());
                        //RealmHelper.copyOrUpdateAsync(response.body()); // Asynchronously looks really awkward, refreshing on screen row by row
                    }
                    mSwipeRefreshContainer.setRefreshing(false);
                } catch (NullPointerException ex) {
                    if (D) Log.e(TAG, "Null Body response\n" + ex.getMessage());
                }
            }

            @Override
            public void onFailure(Call<List<Question>> call, Throwable t) {
                DialogWaitManager.getInstance().dismissDialog();
                if (D) Log.e(TAG, "onFailure: " + t.toString());
                if (isAdded()) { // Check the user did not leave the activity before updating on UI
                    mSwipeRefreshContainer.setRefreshing(false);
                    Toast.makeText(getContext(), getResources().getString(!MyApp.getInstance().isOnline() ? R.string.error_nointernet : R.string.error_serverfailure), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    // Callback for Realm changes
    private RealmChangeListener questionsCallback = new RealmChangeListener() {
        @Override
        public void onChange() {
            if(D) Log.i(TAG, "Realm change: " + mQuestionsAdapter.getQuestionsList().toString());
            // Dismiss dialog if it was activated for Retrieve but model was loaded from Realm first
            if(mQuestionsAdapter.getQuestionsList().size()>0)
                DialogWaitManager.getInstance().dismissDialog();
        }
    };


    @Override
    public void onStop() {
        super.onStop();

        // Realm, cancel transactions, remove all listeners and close
        if(mRealm.isInTransaction()){
            mRealm.cancelTransaction();
        }
        mRealm.removeAllChangeListeners();
        mRealm.close();
    }
}
