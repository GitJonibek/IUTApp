package com.example.jonib.iutchat.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.jonib.iutchat.ProfileActivity;
import com.example.jonib.iutchat.R;
import com.example.jonib.iutchat.UsersActivity;
import com.example.jonib.iutchat.model.Friends;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsFragment extends Fragment {

    private RecyclerView mFriendsList;

    private DatabaseReference mFriendsDatabse;
    private DatabaseReference mUsersDatabase;

    private String mCurrentUserId;

    private FirebaseAuth mAuth;

    private View mMainView;

    private FirebaseRecyclerAdapter<Friends, FriendsViewHolder> friendsRecyclerAdapter;

    public FriendsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mMainView = inflater.inflate(R.layout.fragment_friends, container, false);

        mFriendsList = mMainView.findViewById(R.id.friends_list);
        mAuth = FirebaseAuth.getInstance();

        mCurrentUserId = mAuth.getCurrentUser().getUid();

        mFriendsDatabse = FirebaseDatabase.getInstance().getReference().child("Friends").child(mCurrentUserId);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        mFriendsList.setHasFixedSize(true);
        mFriendsList.setLayoutManager(new LinearLayoutManager(getContext()));

        return mMainView;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Query friendsQuery = mFriendsDatabse.orderByKey();

        FirebaseRecyclerOptions friendsOptions = new FirebaseRecyclerOptions.Builder<Friends>().setQuery(friendsQuery, Friends.class).build();

        friendsRecyclerAdapter = new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(friendsOptions) {
            @NonNull
            @Override
            public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.users_single_layout, parent, false);

                return new FriendsViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final FriendsViewHolder holder, int position, @NonNull Friends model) {

                holder.setDate(model.getDate());

                final String list_user_id = getRef(position).getKey();

                mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        String userName = dataSnapshot.child("name").getValue().toString();
                        String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();

                        holder.setName(userName);
                        holder.setImage(thumb_image);

                        holder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent profile = new Intent(getContext(), ProfileActivity.class);
                                profile.putExtra("user_id", list_user_id);
                                startActivity(profile);
                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        };

        mFriendsList.setAdapter(friendsRecyclerAdapter);

    }

    @Override
    public void onStart() {
        super.onStart();
        friendsRecyclerAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        friendsRecyclerAdapter.stopListening();
    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public FriendsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setDate(String date){
            TextView userNameView = mView.findViewById(R.id.user_single_status);
            userNameView.setText(date);
        }

        public void setName(String name){
            TextView displayName = mView.findViewById(R.id.user_single_name);
            displayName.setText(name);
        }

        public void setImage(String image){
            CircleImageView imageView = mView.findViewById(R.id.user_single_image);
            Picasso.get().load(image).placeholder(R.drawable.default_avatar).into(imageView);
        }

    }

}
