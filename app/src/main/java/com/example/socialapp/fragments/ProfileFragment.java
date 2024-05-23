package com.example.socialapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialapp.EditProfileActivity;
import com.example.socialapp.FollowersActivity;
import com.example.socialapp.OptionsActivity;
import com.example.socialapp.R;
import com.example.socialapp.adapter.PhotoAdapter;
import com.example.socialapp.model.Post;
import com.example.socialapp.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private RecyclerView recyclerViewSave;

    private List<Post> saveList;
    private PhotoAdapter photoAdapterSave;

    private RecyclerView recyclerView;
    private List<Post> photoList;
    private PhotoAdapter photoAdapter;

    private CircleImageView imageProfile;
    private ImageView options;
    private TextView followers;
    private TextView following;
    private TextView posts;
    private TextView fullName;
    private TextView bio;
    private TextView username;

    private ImageView myPosts;
    private ImageView savedPosts;

    private FirebaseUser firebaseUser;

    private String profileId;

    private Button editProfile;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        String data = getContext().getSharedPreferences("PROFILE", getContext().MODE_PRIVATE).getString("profileId", "none");

        if (data.equals("none")) {
            profileId = firebaseUser.getUid();
        } else {
            profileId = data;
            getContext().getSharedPreferences("PROFILE", Context.MODE_PRIVATE).edit().clear().apply();
        }

        imageProfile = view.findViewById(R.id.profile_image);
        options = view.findViewById(R.id.options);
        followers = view.findViewById(R.id.followers);
        following = view.findViewById(R.id.following);
        posts = view.findViewById(R.id.posts);
        fullName = view.findViewById(R.id.fullname);
        bio = view.findViewById(R.id.bio);
        username = view.findViewById(R.id.username);
        editProfile = view.findViewById(R.id.edit_profile);

        myPosts = view.findViewById(R.id.my_pictures);
        savedPosts = view.findViewById(R.id.saved_pictures);

        recyclerView = view.findViewById(R.id.recycler_view_picturers);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));

        photoList = new ArrayList<>();

        photoAdapter = new PhotoAdapter(getContext(), photoList);

        recyclerView.setAdapter(photoAdapter);
        
        recyclerViewSave = view.findViewById(R.id.recycler_view_saved);
        recyclerViewSave.setHasFixedSize(true);
        recyclerViewSave.setLayoutManager(new GridLayoutManager(getContext(), 3));
        
        saveList = new ArrayList<>();
        
        photoAdapterSave = new PhotoAdapter(getContext(), saveList);
        
        recyclerViewSave.setAdapter(photoAdapterSave);

        userInfo();

        getFollowersAndFollowingCount();

        getPostsCount();

        getPhotoList();
        
        getSaved();

        if (profileId.equals(firebaseUser.getUid())) {
            editProfile.setText("Edit Profile");
        } else {
            checkFollow();
            savedPosts.setVisibility(View.GONE);
            options.setVisibility(View.GONE);
        }

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String btn = editProfile.getText().toString();
                if (btn.equals("Edit Profile")) {
                    startActivity(new Intent(getContext(), EditProfileActivity.class));
                }
                else {
                    if (btn.equals("follow")) {
                        FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                                .child("following").child(profileId).setValue(true);
                        FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId)
                                .child("followers").child(firebaseUser.getUid()).setValue(true);
                    } else {
                        FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                                .child("following").child(profileId).removeValue();
                        FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId)
                                .child("followers").child(firebaseUser.getUid()).removeValue();
                    }
                }
            }
        });

        recyclerView.setVisibility(View.VISIBLE);
        recyclerViewSave.setVisibility(View.GONE);

        myPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.setVisibility(View.VISIBLE);
                recyclerViewSave.setVisibility(View.GONE);
            }
        });

        savedPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.setVisibility(View.GONE);
                recyclerViewSave.setVisibility(View.VISIBLE);
            }
        });

        followers.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), FollowersActivity.class);
            intent.putExtra("id", profileId);
            intent.putExtra("title", "followers");
            startActivity(intent);
        });
        following.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), FollowersActivity.class);
            intent.putExtra("id", profileId);
            intent.putExtra("title", "following");
            startActivity(intent);
        });

        options.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), OptionsActivity.class));
        });
        return view;
    }

    private void getSaved() {
        final List<String> savedIds = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot snap: snapshot.getChildren()) {
                            savedIds.add(snap.getKey());
                        }
                        FirebaseDatabase.getInstance().getReference().child("Posts")
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        saveList.clear();
                                        for (DataSnapshot snap: snapshot.getChildren()) {
                                            Post post = snap.getValue(Post.class);
                                            for (String id: savedIds) {
                                                if (post.getPostId().equals(id)) {
                                                    saveList.add(post);
                                                }
                                            }
                                        }
                                        photoAdapterSave.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void getPhotoList() {
        FirebaseDatabase.getInstance().getReference().child("Posts")
                .addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        photoList.clear();
                        for (DataSnapshot snaap : snapshot.getChildren()) {
                            Post post = snaap.getValue(Post.class);
                            if (post.getPublisher().equals(profileId)) {
                                photoList.add(post);
                            }
                        }
                        Collections.reverse(photoList);

                        photoAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void checkFollow() {
        FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                .child("following").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child(profileId).exists()) {
                            editProfile.setText("following");
                        } else {
                            editProfile.setText("follow");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void getPostsCount() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Posts");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int counter = 0;
                    for (DataSnapshot snaap : snapshot.getChildren()) {
                        Post post = snaap.getValue(Post.class);
                        if (post.getPublisher().equals(profileId)) {
                            counter++;
                        }
                    }
                    posts.setText("" + counter);
                }
                else {
                    posts.setText("0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getFollowersAndFollowingCount() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId);

        ref.child("followers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followers.setText("" + snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        ref.child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                following.setText("" + snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void userInfo() {
        FirebaseDatabase.getInstance().getReference().child("Users")
                .child(profileId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            User user = snapshot.getValue(User.class);
                            if (user != null) {
                                String imageUrl = user.getImageUrl();
                                if (imageUrl != null && "default".equals(imageUrl)) {
                                    imageProfile.setImageResource(R.mipmap.ic_launcher);
                                } else {
                                    Picasso.get().load(imageUrl).placeholder(R.mipmap.ic_launcher).into(imageProfile);
                                }
                                username.setText(user.getUsername());
                                fullName.setText(user.getName());
                                bio.setText(user.getBio());
                            } else {
                                Log.e("UserError", "User data is null");
                            }
                        } else {
                            Log.e("DataSnapshotError", "DataSnapshot does not exist");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }
}