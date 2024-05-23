package com.example.socialapp.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialapp.R;
import com.example.socialapp.StartActivity;
import com.example.socialapp.model.Comment;
import com.example.socialapp.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder>{
    private Context context;
    private List<Comment> commentList;

    private String postId;

    private FirebaseUser firebaseUser;

    public CommentAdapter(Context context, List<Comment> commentList, String postId) {
        this.context = context;
        this.commentList = commentList;
        this.postId = postId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.comment_item, parent, false);
        return new CommentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Comment comment = commentList.get(position);

        holder.comment.setText(comment.getComment());
        FirebaseDatabase.getInstance().getReference().child("Users")
                .child(comment.getPublisher())
                .addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            User user = snapshot.getValue(User.class);
                            holder.username.setText(user.getUsername());
                            if (user.getImageUrl().equals("default")) {
                                holder.userImage.setImageResource(R.mipmap.ic_launcher);
                            } else {
                                Picasso.get().load(user.getImageUrl()).into(holder.userImage);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, StartActivity.class);
                intent.putExtra("publisherId", comment.getPublisher());
                context.startActivity(intent);
            }
        });

        holder.userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, StartActivity.class);
                intent.putExtra("publisherId", comment.getPublisher());
                context.startActivity(intent);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (comment.getPublisher().endsWith(firebaseUser.getUid())) {
                    AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                    alertDialog.setTitle("Do you want to delete this comment?");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "No",
                            (dialog, which) -> dialog.dismiss());
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                            (dialog, which) -> FirebaseDatabase.getInstance().getReference().child("Comments")
                                    .child(postId)
                                    .child(comment.getId())
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                           @Override
                                           public void onComplete(@NonNull Task<Void> task) {
                                                 if (task.isSuccessful()) {
                                                      Toast.makeText(context, "Comment deleted", Toast.LENGTH_SHORT).show();
                                                 } else {
                                                      Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                 }
                                           }
                                       }
                                    ));
                    alertDialog.show();
                }
                return true;
            }
        });
    }
    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView userImage;
        public TextView username;
        public TextView comment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            userImage = itemView.findViewById(R.id.user_image);
            username = itemView.findViewById(R.id.username);
            comment = itemView.findViewById(R.id.comment);
        }
    }
}
