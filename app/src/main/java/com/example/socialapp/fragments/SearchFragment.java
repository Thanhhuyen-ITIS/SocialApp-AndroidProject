package com.example.socialapp.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialapp.R;
import com.example.socialapp.adapter.TagAdapter;
import com.example.socialapp.adapter.UserAdapter;
import com.example.socialapp.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hendraanggrian.appcompat.socialview.widget.SocialAutoCompleteTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class SearchFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<User> mUsers;
    private UserAdapter userAdapter;

    private RecyclerView recyclerViewTags;

    private List<String> mHashTags;

    private List<String> mHashTagsCount;

    private TagAdapter tagAdapter;

    private SocialAutoCompleteTextView search_bar;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_search, container, false);
        recyclerView = view.findViewById(R.id.recycler_view_users);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mUsers = new ArrayList<>();
        userAdapter = new UserAdapter(getContext(), mUsers, true);
        recyclerView.setAdapter(userAdapter);

        recyclerViewTags = view.findViewById(R.id.recycler_view_tags);
        recyclerViewTags.setHasFixedSize(true);
        recyclerViewTags.setLayoutManager(new LinearLayoutManager(getContext()));
        mHashTags = new ArrayList<>();
        mHashTagsCount = new ArrayList<>();
        tagAdapter = new TagAdapter(getContext(), mHashTags, mHashTagsCount);
        recyclerViewTags.setAdapter(tagAdapter);

        search_bar = view.findViewById(R.id.search_bar);

        readUsers();
        readTags();
        search_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchUsers(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                searchTags(s.toString());
            }
        });
        return view;
    }

    private void readTags() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("HashTags");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (TextUtils.isEmpty(search_bar.getText().toString())) {
                    mHashTags.clear();
                    mHashTagsCount.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        mHashTags.add(dataSnapshot.getKey());
                        mHashTagsCount.add(dataSnapshot.getChildrenCount() + "");
                    }
                    tagAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readUsers() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
        reference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (TextUtils.isEmpty(search_bar.getText().toString())) {
                    mUsers.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        User user = dataSnapshot.getValue(User.class);
                        mUsers.add(user);
                    }
                    userAdapter.notifyDataSetChanged();
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void searchUsers(String s) {
        Query reference = FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("username").startAt(s).endAt(s + "\uf8ff");
        reference.addValueEventListener(new ValueEventListener() {

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user.getUsername().toLowerCase().contains(s.toLowerCase())) {
                        mUsers.add(user);
                    }
                }
                userAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void searchTags(String s) {
        Query reference = FirebaseDatabase.getInstance().getReference().child("HashTags").orderByKey().startAt(s).endAt(s + "\uf8ff");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mHashTags.clear();
                mHashTagsCount.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    mHashTags.add(dataSnapshot.getKey());
                    mHashTagsCount.add(dataSnapshot.getChildrenCount() + "");
                }
                tagAdapter.filter(mHashTags, mHashTagsCount);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}