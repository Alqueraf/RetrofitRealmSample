package com.alexqueudotrafel.retrofitrealmsample.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.alexqueudotrafel.retrofitrealmsample.BuildConfig;
import com.alexqueudotrafel.retrofitrealmsample.MyApp;
import com.alexqueudotrafel.retrofitrealmsample.R;
import com.alexqueudotrafel.retrofitrealmsample.adapter.QuestionsRecyclerViewAdapter;
import com.alexqueudotrafel.retrofitrealmsample.components.EndlessRecyclerViewScrollListener;
import com.alexqueudotrafel.retrofitrealmsample.db.RealmHelper;
import com.alexqueudotrafel.retrofitrealmsample.model.Question;
import com.alexqueudotrafel.retrofitrealmsample.network.StackOverflowService;
import com.alexqueudotrafel.retrofitrealmsample.network.util.DialogWaitManager;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
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

    /* Realm database variable */
    private Realm mRealm;

    /* SwipeToRefresh */
    private SwipeRefreshLayout mSwipeRefreshContainer;

    /* RecyclerView */
    private RecyclerView mRecyclerView;
    private QuestionsRecyclerViewAdapter mRecyclerAdapter;

    private final static int SNACKBAR_LENGTH_SHORT = 1500;
    private final static int SNACKBAR_LENGTH_LONG = 2750;
    private final static int PENDING_REMOVAL_TIMEOUT = 3000;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        /* Init Recycler View and Adapter */
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerAdapter = new QuestionsRecyclerViewAdapter();
        mRecyclerView.setAdapter(mRecyclerAdapter);
        /* Init Swipe To Dissmis */
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                final int position = viewHolder.getAdapterPosition();
                final Question item = mRecyclerAdapter.getItem(position);
                /* Notify Adapter that item has been removed, for redraw */
                mRecyclerAdapter.notifyItemRemoved(position);

                /* Delete from Realm delayed */
                final Handler handler = new Handler();
                final Runnable pendingRemovalRunnable = new Runnable() {
                    @Override
                    public void run() {
                        RealmHelper.removeEntry(item);
                    }
                };
                handler.postDelayed(pendingRemovalRunnable, SNACKBAR_LENGTH_LONG);
                /* Show UNDO Snackbar */
                Snackbar.make(viewHolder.itemView, getResources().getString(R.string.snackbar_item_removed), Snackbar.LENGTH_LONG)
                        .setAction(getResources().getString(R.string.action_undo), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                /* Remove callback for removal Runnable */
                                handler.removeCallbacks(pendingRemovalRunnable);
                                /* Notify Adapter item is added again, for redraw */
                                mRecyclerAdapter.notifyItemInserted(position);
                                /* Fix awkward ui behaviour when deleting first element on RecycleView */
                                if (position == 0) mRecyclerView.scrollToPosition(position);
                            }
                        }).show();
            }
        });
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
        /* TODO Init Endless Scrolling */
        mRecyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener((LinearLayoutManager) mRecyclerView.getLayoutManager()) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                /* Fetch Data */
                //retrieveQuestions(page);

                /* update the adapter, saving the last known size */
//                int curSize = mRecyclerAdapter.getItemCount();
//                mRecyclerAdapter.getList().addAll(newlyRetrievedQuestionList);

                /* for efficiency purposes, only notify the adapter of what elements that got changed
                curSize will equal to the index of the first element inserted because the list is 0-indexed */
//                mRecyclerAdapter.notifyItemRangeInserted(curSize, newlyRetrievedQuestionList.size() - 1);
            }
        });

        /* Init Swipe to Refresh  */
        mSwipeRefreshContainer = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeContainer);
        mSwipeRefreshContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                retrieveQuestions();
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        mRealm = Realm.getDefaultInstance();

        /* First Retrieve questions locally (async) */
        mRecyclerAdapter.setList(mRealm.where(Question.class).findAllSortedAsync("unixDate", Sort.DESCENDING));

        /* Then Retrieve questions from the API */
        retrieveQuestions();
    }

    @Override
    public void onResume() {
        super.onResume();
        /* Set Change Listener */
        mRecyclerAdapter.getList().addChangeListener(questionsCallback);
    }

    @Override
    public void onPause() {
        super.onPause();
        /* Remove Change Listener */
        mRecyclerAdapter.getList().removeChangeListener(questionsCallback);
    }

    /**
     * Get Data from backend
     */
    private void retrieveQuestions() {
        /* Show Dialog iff no questions are loaded */
        if (mRecyclerAdapter.isEmpty())
            DialogWaitManager.getInstance().showDialog(getContext());
        /* Retrieve questions from the Backend */
        final StackOverflowService stackOverflowService = StackOverflowService.retrofit.create(StackOverflowService.class);
        stackOverflowService.getQuestions().enqueue(new Callback<List<Question>>() {
            @Override
            public void onResponse(Call<List<Question>> call, Response<List<Question>> response) {
                DialogWaitManager.getInstance().dismissDialog();
                try {
                    if (D) Log.d(TAG, "onResponse: " + response.body().toString());
                    /* Save data to Realm */
                    if (response.body().size() > 0 && !mRealm.isClosed()) {
                        RealmHelper.copyOrUpdate(response.body());
                        //RealmHelper.copyOrUpdateAsync(response.body()); /* Asynchronously looks really awkward, refreshing on screen row by row */
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
                if (isAdded()) { /* Check the user did not quit before updating on UI */
                    mSwipeRefreshContainer.setRefreshing(false);
                    Toast.makeText(getContext(), getResources().getString(!MyApp.getInstance().isOnline() ? R.string.error_nointernet : R.string.error_serverfailure), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * Callback for Realm changes
     */
    private RealmChangeListener questionsCallback = new RealmChangeListener() {
        @Override
        public void onChange() {
            /* Dismiss dialog if it was activated for Online Retrieval but a usable list has been loaded from Realm */
            if (!mRecyclerAdapter.isEmpty())
                DialogWaitManager.getInstance().dismissDialog();
            /* Notify Adapter that the data has changed */
            mRecyclerAdapter.notifyDataSetChanged();
        }
    };


    @Override
    public void onStop() {
        super.onStop();
        /* Cancel Transactions */
        /*if (mRealm.isInTransaction()) {
            mRealm.cancelTransaction();
        }*/
        /* Remove Listeners */
        // mRealm.removeAllChangeListeners(); //Redundant, we already clear our only listener onPause
        /* Close Realm */
        mRealm.close();
    }
}
