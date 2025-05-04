package com.example.socialapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialapp.CommentActivity;
import com.example.socialapp.FollowersActivity;
import com.example.socialapp.R;
import com.example.socialapp.fragments.PostDetailFragment;
import com.example.socialapp.fragments.ProfileFragment;
import com.example.socialapp.model.Post;
import com.example.socialapp.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hendraanggrian.appcompat.socialview.widget.SocialTextView;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    private Context context;
    private List<Post> postList;

    private FirebaseUser firebaseUser;

    public PostAdapter(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.post_item, parent, false);
        return new PostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = postList.get(position);

        Picasso.get().load(post.getImageUrl()).into(holder.postImage);
        holder.description.setText(post.getDescription());

        FirebaseDatabase.getInstance().getReference().child("Users").child(post.getPublisher())
                .addValueEventListener(new ValueEventListener() {
                    @Override

                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            return;
                        }
                        User user = snapshot.getValue(User.class);
                        if (user != null) {
                            String imageUrl = user.getImageUrl();
                            if (imageUrl != null && !imageUrl.equals("default")) {
                                Picasso.get().load(imageUrl).into(holder.userImagePost);
                            } else {
                                holder.userImagePost.setImageResource(R.mipmap.ic_launcher);
                            }

                            holder.username.setText(user.getUsername());
                            holder.author.setText(user.getName());
                        } else {
                            Log.e("UserError", "User data is null");
                        }
                        return;
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        isLiked(post.getPostId(), holder.likeImage);
        noOfLikes(post.getPostId(), holder.noOfLikes);
        getComments(post.getPostId(), holder.noOfComments);
        isSaved(post.getPostId(), holder.saveImage);
        holder.likeImage.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if (holder.likeImage.getTag().equals("like")) {
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostId()).child(firebaseUser.getUid()).setValue(true);
                    if (!post.getPublisher().equals(firebaseUser.getUid()))
                    {
                        addNotification(post.getPostId(), post.getPublisher());
                    }

                } else {
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostId()).child(firebaseUser.getUid()).removeValue();
                }
            }
        });
        holder.commentImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CommentActivity.class);
                intent.putExtra("postId", post.getPostId());
                intent.putExtra("authorId", post.getPublisher());
                context.startActivity(intent);
            }
        });

        holder.noOfComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CommentActivity.class);
                intent.putExtra("postId", post.getPostId());
                intent.putExtra("authorId", post.getPublisher());
                context.startActivity(intent);

            }
        });
        holder.saveImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.saveImage.getTag().equals("save")) {
                    FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid()).child(post.getPostId()).setValue(true);
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid()).child(post.getPostId()).removeValue();
                }
            }
        });

        holder.author.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                context.getSharedPreferences("PROFILE", Context.MODE_PRIVATE).edit().putString("profileId", post.getPublisher()).apply();

                ((FragmentActivity) context).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new ProfileFragment()).commit();
            }
        });

        holder.userImagePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                context.getSharedPreferences("PROFILE", Context.MODE_PRIVATE).edit().putString("profileId", post.getPublisher()).apply();

                ((FragmentActivity) context).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new ProfileFragment()).commit();
            }
        });

        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                context.getSharedPreferences("PROFILE", Context.MODE_PRIVATE).edit().putString("profileId", post.getPublisher()).apply();

                ((FragmentActivity) context).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new ProfileFragment()).commit();
            }
        });

        holder.postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit().putString("postId", post.getPostId()).apply();

                ((FragmentActivity) context).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new PostDetailFragment()).commit();
            }
        });

        holder.noOfLikes.setOnClickListener(v->{
            Intent intent = new Intent(context, FollowersActivity.class);
            intent.putExtra("id", post.getPostId());
            intent.putExtra("title", "likes");
            context.startActivity(intent);
        });

        long currentTimestamp = System.currentTimeMillis();
        Long timestamp = post.getTimestamp();
        long timeDiff = currentTimestamp - timestamp;


        long munites = TimeUnit.MILLISECONDS.toMinutes(timeDiff);
        long hours = munites/60;
        long days = hours/24;
        long months = days / 30;
        long years = days / 365;

        String time;

        if (years > 0) {
            time =  years + " years ago ";
        } else if (months > 0) {
            time= months + " months ago";
        } else if (days > 0) {
           time = days + " days ago";
        } else if (hours > 0) {
            time =  hours + " hours ago";
        } else if (munites > 0){
            time = munites + " minutes ago";
        }
        else {
            time = "Just now";
        }

        holder.post_time.setText(time);
    }



    private void addNotification(String postId, String publisher) {
        Long  timestamp = System.currentTimeMillis();
        HashMap<String, Object> map = new HashMap<>();
        map.put("userId", firebaseUser.getUid());
        map.put("text", "liked your post");
        map.put("postId", postId);
        map.put("isPost", true);
        map.put("timestamp", timestamp);

        FirebaseDatabase.getInstance().getReference().child("Notifications").child(publisher).push().setValue(map);

    }


    private void isSaved(String postId, ImageView saveImage) {
        FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child(postId).exists()) {
                            saveImage.setImageResource(R.drawable.ic_save_black);
                            saveImage.setTag("saved");
                        } else {
                            saveImage.setImageResource(R.drawable.ic_save);
                            saveImage.setTag("save");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView userImagePost;
        public ImageView postImage;
        public ImageView likeImage;
        public ImageView commentImage;
        public ImageView moreImage;
        public ImageView saveImage;

        public TextView username;
        public TextView noOfLikes;
        public TextView noOfComments;
        public TextView author;
        SocialTextView description;

        public TextView post_time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userImagePost = itemView.findViewById(R.id.user_image);
            postImage = itemView.findViewById(R.id.post_image);
            likeImage = itemView.findViewById(R.id.like);
            commentImage = itemView.findViewById(R.id.comment);
            moreImage = itemView.findViewById(R.id.more);

            saveImage = itemView.findViewById(R.id.save);
            username = itemView.findViewById(R.id.username);
            noOfLikes = itemView.findViewById(R.id.no_of_likes);
            noOfComments = itemView.findViewById(R.id.no_of_comments);
            author = itemView.findViewById(R.id.author);
            description = itemView.findViewById(R.id.description);
            post_time = itemView.findViewById(R.id.post_time);

            System.out.println(username.getText() + " " + author.getText() + " " + description.getText());

        }
    }

    private void isLiked(String postId, ImageView imageView ) {
        FirebaseDatabase.getInstance().getReference().child("Likes").child(postId).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(firebaseUser.getUid()).exists()) {
                    imageView.setImageResource(R.drawable.ic_liked);
                    imageView.setTag("liked");
                } else {
                    imageView.setImageResource(R.drawable.ic_like);
                    imageView.setTag("like");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void noOfLikes (String postId, TextView textView) {
        FirebaseDatabase.getInstance().getReference().child("Likes").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                textView.setText(snapshot.getChildrenCount() + " likes");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void getComments(String postId, TextView text ) {
        FirebaseDatabase.getInstance().getReference().child("Comments").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                text.setText("View all " + snapshot.getChildrenCount() + " comments");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
