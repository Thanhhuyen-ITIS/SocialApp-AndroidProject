package com.example.socialapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialapp.MessageActivity;
import com.example.socialapp.R;
import com.example.socialapp.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder>{
    private Context context;
    private List<User> userList;

    FirebaseUser firebaseUser;

    public ChatAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_item, parent, false);
        return new ChatAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        User user = userList.get(position);

        holder.user.setText(user.getUsername());
        if (user.getImageUrl().equals("default")) {
            holder.userPic.setImageResource(R.mipmap.ic_launcher);
        } else {
            Picasso.get().load(user.getImageUrl()).into(holder.userPic);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, MessageActivity.class);
            intent.putExtra("userId", user.getId());
            context.startActivity(intent);
        });

        holder.itemView.setOnLongClickListener(v -> {
            deleteChat(user.getId());
            return true;
        });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, MessageActivity.class);
            intent.putExtra("userId", user.getId());
            context.startActivity(intent);
        });
    }

    private void deleteChat(String id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete Chat");
        builder.setMessage("Are you sure you want to delete this chat?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            FirebaseDatabase.getInstance().getReference().child("Messages").child(firebaseUser.getUid()).child(id).removeValue();
        });
        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView userPic;
        public TextView user;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userPic = itemView.findViewById(R.id.userPic);
            user = itemView.findViewById(R.id.user);
        }
    }
}
