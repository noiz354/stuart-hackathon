package com.stuart.hackatonproject.fragment;

import android.app.Activity;
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
import com.stuart.hackatonproject.adapter.FriendsHolder;
import com.stuart.hackatonproject.adapter.ReminderHolder;
import com.stuart.hackatonproject.model.ReminderDB;
import com.stuart.hackatonproject.model.UserDB;
import com.stuart.hackatonproject.util.FirebaseUtils;

/**
 * Created by zulfikarrahman on 10/16/17.
 */

public class ListFriendsFragment extends Fragment {

    public static final String EXTRA_USER_CHOOSEN = "extra_user_choosen";
    protected static final Query sChatQuery =
            FirebaseDatabase.getInstance().getReference().child(UserDB.TABLE_NAME)
                    .limitToLast(50);

    private RecyclerView recyclerView;

    public static ListFriendsFragment createInstance(){
        return new ListFriendsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_friends, container, false);
        recyclerView = view.findViewById(R.id.list_view_friends);
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
        FirebaseRecyclerOptions<UserDB> options =
                new FirebaseRecyclerOptions.Builder<UserDB>()
                        .setQuery(sChatQuery, UserDB.class)
                        .setLifecycleOwner(this)
                        .build();

        return new FirebaseRecyclerAdapter<UserDB, FriendsHolder>(options) {
            @Override
            public FriendsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new FriendsHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_reminder, parent, false));
            }

            @Override
            protected void onBindViewHolder(FriendsHolder holder, int position, final UserDB userDB) {
                holder.bind(userDB);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.putExtra(ListFriendsFragment.EXTRA_USER_CHOOSEN, userDB);
                        getActivity().setResult(Activity.RESULT_OK, intent);
                        getActivity().finish();
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
