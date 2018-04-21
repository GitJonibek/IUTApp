package com.example.jonib.iutchat;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {

    private ImageView mProfileImage;
    private TextView mProfileName, mProfileStatus, mProfileFriendsCount;
    private Button mProfileSendReqBtn, mProfileDeclineReqBtn;

    private DatabaseReference mUsersDatabase;
    private DatabaseReference mFriendsReqDatabase;
    private DatabaseReference mFriendDatabase;
    private DatabaseReference mNotificationDatabase;
    private DatabaseReference mRootRef;

    private ProgressDialog mProgressDialog;

    private FirebaseUser mCurrentUser;

    private String current_state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final String user_id = Objects.requireNonNull(getIntent().getExtras()).getString("user_id");

        mRootRef = FirebaseDatabase.getInstance().getReference();

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        mFriendsReqDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req");
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        mNotificationDatabase = FirebaseDatabase.getInstance().getReference().child("Notifications");
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();


        mProfileImage = findViewById(R.id.profile_image);
        mProfileName = findViewById(R.id.profile_displayName);
        mProfileStatus = findViewById(R.id.profile_status);
        mProfileFriendsCount = findViewById(R.id.profile_totalFriends);
        mProfileSendReqBtn = findViewById(R.id.profile_send_req_btn);
        mProfileDeclineReqBtn = findViewById(R.id.profile_decline_req_btn);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Loading User Data!");
        mProgressDialog.setMessage("Please, wait while loading the data.");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

        mProfileDeclineReqBtn.setVisibility(View.INVISIBLE);
        mProfileDeclineReqBtn.setEnabled(false);

        mUsersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String displayName = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();

                current_state = "not_friends";

                mProfileName.setText(displayName);
                mProfileStatus.setText(status);
                Picasso.get().load(image).placeholder(R.drawable.default_avatar).into(mProfileImage);

                // _______________ FRIENDS LIST / REQUEST FEATURE _________________

                mFriendsReqDatabase.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.hasChild(user_id)){

                            String req_type = dataSnapshot.child(user_id).child("request_type").getValue().toString();

                            if(req_type.equals("received")){

                                current_state = "req_received";
                                mProfileSendReqBtn.setText("Accept friend request");

                                mProfileDeclineReqBtn.setVisibility(View.VISIBLE);
                                mProfileDeclineReqBtn.setEnabled(true);

                            }else if(req_type.equals("sent")){

                                current_state = "req_sent";
                                mProfileSendReqBtn.setText("Cancel friend request");

                                mProfileDeclineReqBtn.setVisibility(View.INVISIBLE);
                                mProfileDeclineReqBtn.setEnabled(false);

                            }

                            mProgressDialog.dismiss();

                        } else {

                            mFriendDatabase.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if(dataSnapshot.hasChild(user_id)){

                                        current_state = "friends";
                                        mProfileSendReqBtn.setText("unfriend " + mProfileName.getText());

                                        mProfileDeclineReqBtn.setVisibility(View.INVISIBLE);
                                        mProfileDeclineReqBtn.setEnabled(false);

                                    }

                                    mProgressDialog.dismiss();

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    mProgressDialog.dismiss();
                                }
                            });

                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        mProgressDialog.dismiss();
                    }
                });


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mProfileSendReqBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {

                mProfileSendReqBtn.setEnabled(false);

                // -------------- NOT FRIEND STATE -------------------

                if(current_state.equals("not_friends")){

                    DatabaseReference newNotificationsRef = mRootRef.child("Notifications").child(user_id).push();
                    String newNotificationsId = newNotificationsRef.getKey();

                    HashMap<String, String> notificationData = new HashMap<>();
                    notificationData.put("from", mCurrentUser.getUid());
                    notificationData.put("type", "request");

                    Map requestMap = new HashMap();
                    requestMap.put("Friend_req/" + mCurrentUser.getUid() + "/" + user_id + "/request_type", "sent");
                    requestMap.put("Friend_req/" + user_id + "/" + mCurrentUser.getUid() + "/request_type", "received");
                    requestMap.put("Notifications/" + user_id + "/" + newNotificationsId, notificationData);

                    mRootRef.updateChildren(requestMap, new DatabaseReference.CompletionListener(){

                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if(databaseError != null){
                                Toast.makeText(ProfileActivity.this, "There was some error in sending request!", Toast.LENGTH_LONG).show();
                            }
                            mProfileSendReqBtn.setEnabled(true);
                            current_state = "req_sent";
                            mProfileSendReqBtn.setText("Cancel friend request");
                        }

                    });

                }

                // -------------- CANCEL REQUEST STATE -------------------

                if(current_state.equals("req_sent")){

                    Map cancelMap = new HashMap();
                    cancelMap.put("Friend_req/" + mCurrentUser.getUid() + "/" + user_id, null);
                    cancelMap.put("Friend_req/" + user_id + "/" + mCurrentUser.getUid(), null);

                    mRootRef.updateChildren(cancelMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if(databaseError == null){
                                mProfileSendReqBtn.setEnabled(true);
                                current_state = "not_friends";
                                mProfileSendReqBtn.setText("Send friend request");

                                mProfileDeclineReqBtn.setVisibility(View.VISIBLE);
                                mProfileDeclineReqBtn.setEnabled(true);
                            }
                        }
                    });
                }

                // -------------- REQUEST RECEIVED STATE ----------------------

                if(current_state.equals("req_received")){

                    final String currentDate = SimpleDateFormat.getDateTimeInstance().format(new java.util.Date());

                    Map friendsMap = new HashMap();
                    friendsMap.put("Friends/" + mCurrentUser.getUid() + "/" + user_id + "/date", currentDate);
                    friendsMap.put("Friends/" + user_id + "/" + mCurrentUser.getUid() + "/date", currentDate);

                    friendsMap.put("Friend_req/" + mCurrentUser.getUid() + "/" + user_id, null);
                    friendsMap.put("Friend_req/" + user_id + "/" + mCurrentUser.getUid(), null);

                    mRootRef.updateChildren(friendsMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if(databaseError == null){

                                mProfileSendReqBtn.setEnabled(true);
                                current_state = "friends";
                                mProfileSendReqBtn.setText("unfriend " + mProfileName.getText());

                                mProfileDeclineReqBtn.setVisibility(View.INVISIBLE);
                                mProfileDeclineReqBtn.setEnabled(false);

                            }else {

                                String error = databaseError.getMessage();
                                Toast.makeText(ProfileActivity.this, error, Toast.LENGTH_SHORT).show();

                            }

                        }

                    });

                }

                // -------------- UNFRIENDING YOUR FRIEND ----------------------

                if(current_state.equals("friends")) {

                    Map unfriendMap = new HashMap();
                    unfriendMap.put("Friends/" + mCurrentUser.getUid() + "/" + user_id, null);
                    unfriendMap.put("Friends/" + user_id + "/" + mCurrentUser.getUid(), null);

                    mRootRef.updateChildren(unfriendMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if(databaseError == null){

                                current_state = "not_friends";
                                mProfileSendReqBtn.setText("Send Friend Request");

                                mProfileDeclineReqBtn.setVisibility(View.INVISIBLE);
                                mProfileDeclineReqBtn.setEnabled(false);

                            }else {

                                String error = databaseError.getMessage();
                                Toast.makeText(ProfileActivity.this, error, Toast.LENGTH_SHORT).show();

                            }
                            mProfileSendReqBtn.setEnabled(true);

                        }

                    });
                }
            }
        });
    }
}
