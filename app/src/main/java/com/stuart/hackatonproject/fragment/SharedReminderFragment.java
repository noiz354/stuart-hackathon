package com.stuart.hackatonproject.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.stuart.hackatonproject.R;
import com.stuart.hackatonproject.activity.DetailActivity;
import com.stuart.hackatonproject.adapter.ReminderHolder;
import com.stuart.hackatonproject.model.ReminderDB;
import com.stuart.hackatonproject.model.UserDB;
import com.stuart.hackatonproject.util.FirebaseUtils;

/**
 * Created by nathan on 10/11/17.
 */

public class SharedReminderFragment extends Fragment {

    private RecyclerView recyclerView;

    protected static final Query sChatQuery =
            FirebaseDatabase.getInstance().getReference().child(UserDB.TABLE_NAME)
                    .child(FirebaseUtils.getCurrentUniqueUserId())
                    .child(ReminderDB.FIELD_REMINDER_FROM)
                    .limitToLast(50);

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reminder_from, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), linearLayoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        attachRecyclerViewAdapter();
    }

    private void attachRecyclerViewAdapter() {
        final RecyclerView.Adapter adapter = newAdapter();

        // Scroll to bottom on new messages
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                recyclerView.smoothScrollToPosition(adapter.getItemCount());
            }
        });

        recyclerView.setAdapter(adapter);
    }

    protected RecyclerView.Adapter newAdapter() {
        FirebaseRecyclerOptions<ReminderDB> options =
                new FirebaseRecyclerOptions.Builder<ReminderDB>()
                        .setQuery(sChatQuery, ReminderDB.class)
                        .setLifecycleOwner(this)
                        .build();

        return new FirebaseRecyclerAdapter<ReminderDB, ReminderHolder>(options) {
            @Override
            public ReminderHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new ReminderHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_reminder, parent, false), getActivity());
            }

            @Override
            protected void onBindViewHolder(ReminderHolder holder, int position, final ReminderDB reminderDB) {
                holder.bind(reminderDB);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), DetailActivity.class);
                        intent.putExtra(DetailReminderFragment.EXTRA_REMINDER, reminderDB);
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onDataChanged() {
                // If there are no chat messages, show a view that invites the user to add a message.
//                mEmptyListMessage.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
            }
        };
    }
}
