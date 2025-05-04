package com.example.socialapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;

import android.widget.Toast;

import com.example.socialapp.adapter.MessageAdapter;
import com.example.socialapp.model.Message;
import com.example.socialapp.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class MessageActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;
    private List<Message> messageList;

    private User currentUser;
    private User chatUser;

    private EditText messageSend;
    private ImageButton send;

    private String userId;

    private Toolbar toolbar;

    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        currentUser = new User();
        chatUser = new User();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Intent intent = getIntent();
        userId = (String) intent.getStringExtra("userId");

        System.out.println("MessageActivivty" + userId);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());


        recyclerView = findViewById(R.id.recycler_view_message);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(this, messageList, userId);
        recyclerView.setAdapter(messageAdapter);

        messageSend = findViewById(R.id.message_send);
        send = findViewById(R.id.send);

        send.setOnClickListener(v -> {
            String msg = messageSend.getText().toString();
            if (!msg.equals("")) {
                sendMessage(firebaseUser.getUid(), userId, msg);
            } else {
                Toast.makeText(MessageActivity.this, "You can't send empty message", Toast.LENGTH_SHORT).show();
            }
            messageSend.setText("");
        });

        getUser();
        getMessage(firebaseUser.getUid(), userId);


    }

    private void getUser() {
        FirebaseDatabase.getInstance().getReference().child("Users")
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                User user = dataSnapshot.getValue(User.class);
                                if (user.getId().equals(userId)) {
                                    chatUser = user;
                                    getSupportActionBar().setTitle(user.getName());
                                }
                            }

                        }

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void getMessage(String uid, String userId) {
        FirebaseDatabase.getInstance().getReference().child("Messages").child(uid).child(userId)
                .addValueEventListener(new ValueEventListener() {

                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messageList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            if (dataSnapshot.exists()) {
                                System.out.println(dataSnapshot.getValue());
                                Message message = dataSnapshot.getValue(Message.class);
                                messageList.add(message);
                            }
                            else {
                                System.out.println("No message");
                            }
                        }
                        Collections.sort(messageList, Comparator.comparingLong(Message::getTimestamp));
                        Collections.reverse(messageList);
                        messageAdapter.notifyDataSetChanged();

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void sendMessage(String uid, String userId, String msg) {
        Date date = new Date();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Messages").child(uid).child(userId);
        String messageId = ref.push().getKey();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", messageId);
        hashMap.put("sendId", uid);
        hashMap.put("receiveId", userId);
        hashMap.put("message", msg);
        hashMap.put("timestamp", date.getTime());

        assert messageId != null;
        FirebaseDatabase.getInstance().getReference().child("Messages").child(uid).child(userId).child(messageId).setValue(hashMap);
        FirebaseDatabase.getInstance().getReference().child("Messages").child(userId).child(uid).child(messageId).setValue(hashMap);
    }

}