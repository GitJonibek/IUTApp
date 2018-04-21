package com.example.jonib.iutchat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.jonib.iutchat.model.Users;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private RecyclerView mUsersList;

    private DatabaseReference mUsersDatabase;

    private FirebaseRecyclerAdapter<Users, UsersViewHolder> mUsersRVAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        mToolbar = findViewById(R.id.users_appBar);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");


        mUsersList = findViewById(R.id.users_list);
        mUsersList.setHasFixedSize(true);
        mUsersList.setLayoutManager(new LinearLayoutManager(this));

        Query personsQuery = mUsersDatabase.orderByKey();

        FirebaseRecyclerOptions personsOptions = new FirebaseRecyclerOptions.Builder<Users>().setQuery(personsQuery, Users.class).build();

        mUsersRVAdapter = new FirebaseRecyclerAdapter<Users, UsersViewHolder>(personsOptions) {

            @Override
            protected void onBindViewHolder(UsersViewHolder holder, int position, Users model) {
                holder.setName(model.getName());
                holder.setImage(model.getThumb_image());
                holder.setStatus(model.getStatus());

                final String user_id = getRef(position).getKey();

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent profile = new Intent(UsersActivity.this, ProfileActivity.class);
                        profile.putExtra("user_id", user_id);
                        startActivity(profile);
                    }
                });

            }

            @Override
            public UsersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.users_single_layout, parent, false);

                return new UsersViewHolder(view);
            }
        };

        mUsersList.setAdapter(mUsersRVAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        mUsersRVAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mUsersRVAdapter.stopListening();
    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public UsersViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setName(String name){
            TextView displayName = mView.findViewById(R.id.user_single_name);
            displayName.setText(name);
        }

        public void setImage(String image){
            CircleImageView imageView = mView.findViewById(R.id.user_single_image);
            Picasso.get().load(image).placeholder(R.drawable.default_avatar).into(imageView);
        }

        public void setStatus(String status){
            TextView _status = mView.findViewById(R.id.user_single_status);
            _status.setText(status);
        }

    }

}
