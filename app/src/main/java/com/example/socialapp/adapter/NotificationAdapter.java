package com.example.socialapp.adapter;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialapp.R;
import com.example.socialapp.fragments.PostDetailFragment;
import com.example.socialapp.fragments.ProfileFragment;
import com.example.socialapp.model.Notification;
import com.example.socialapp.model.Post;
import com.example.socialapp.model.User;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private Context mContext;

    private List<Notification> mNotification;

    private FirebaseUser firebaseUser;

    public NotificationAdapter(Context mContext, List<Notification> mNotification) {
        this.mContext = mContext;
        this.mNotification = mNotification;

    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.notification_item, parent, false);

        return new NotificationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notification notification = mNotification.get(position);

        getUser(holder.username, holder.imageProfile, notification.getUserId());

        if (notification.isPost()) {
            holder.postImage.setVisibility(View.VISIBLE);
            getPostImage(holder.postImage, notification.getPostId());
        } else {
            holder.postImage.setVisibility(View.GONE);
        }

        holder.comment.setText(notification.getText());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (notification.isPost()) {
                    mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit().putString("postId", notification.getPostId()).apply();
                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PostDetailFragment()).commit();
                } else {
                    mContext.getSharedPreferences("PROFILE", Context.MODE_PRIVATE).edit().putString("profileId", notification.getUserId()).apply();
                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
                }
            }
        });

    }

    private void getPostImage(ImageView postImage, String postId) {
        FirebaseDatabase.getInstance().getReference().child("Posts")
                .child(postId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Post post = snapshot.getValue(Post.class);
                        Picasso.get().load(post.getImageUrl()).placeholder(R.mipmap.ic_launcher).into(postImage);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void getUser(TextView username, ImageView imageProfile, String userId) {

        FirebaseDatabase.getInstance().getReference().child("Users")
                .child(userId)
                .addValueEventListener(new ValueEventListener() {
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
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return mNotification.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageProfile;
        public ImageView postImage;
        public TextView username;
        public TextView comment;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageProfile = itemView.findViewById(R.id.image_profile);
            postImage = itemView.findViewById(R.id.post_image);
            username = itemView.findViewById(R.id.username);
            comment = itemView.findViewById(R.id.comment);

        }
    }
}
