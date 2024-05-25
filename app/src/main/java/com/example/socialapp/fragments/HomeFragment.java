package com.example.socialapp.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.example.socialapp.ChatActivity;
import com.example.socialapp.MessageActivity;
import com.example.socialapp.R;
import com.example.socialapp.adapter.PostAdapter;
import com.example.socialapp.model.Post;
import com.example.socialapp.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerViewPost;
    private PostAdapter postAdapter;
    private List<Post> postList;

    private List<String> followingList;

    private ImageView chat;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerViewPost = view.findViewById(R.id.recycler_view_post);
        recyclerViewPost.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerViewPost.setLayoutManager(linearLayoutManager);

        postList = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(), postList);

        recyclerViewPost.setAdapter(postAdapter);

        followingList = new ArrayList<>();

        chat = view.findViewById(R.id.chat);

        checkFollowingUsers();
        return view;
    }

    private void checkFollowingUsers() {
        FirebaseDatabase.getInstance().getReference().child("Follow").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("following")
                .addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        followingList.clear();
                        for(DataSnapshot data: snapshot.getChildren()) {
                            followingList.add(data.getKey());
                        }
                        readPosts();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void readPosts() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        FirebaseDatabase.getInstance().getReference().child("Posts")
                .orderByChild("timestamp") // Sắp xếp theo trường timestamp
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        postList.clear();
                        for (DataSnapshot data : snapshot.getChildren()) {
                            Post post = data.getValue(Post.class);
                            if (post != null) {
                                if (post.getPublisher().equals(user.getUid())) {
                                    postList.add(post);
                                } else {
                                    for (String id : followingList) {
                                        if (post.getPublisher().equals(id)) {
                                            postList.add(post);
                                        }
                                    }
                                }
                            }
                        }
                        // Sắp xếp postList theo thời gian giảm dần
                        Collections.sort(postList, new Comparator<Post>() {
                            @Override
                            public int compare(Post post1, Post post2) {
                                return Long.compare( post1.getTimestamp(), post2.getTimestamp());
                            }
                        });
                        postAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Xử lý khi có lỗi
                    }
                });
    }
}