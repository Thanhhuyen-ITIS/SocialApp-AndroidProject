package com.example.socialapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialapp.R;
import com.example.socialapp.model.Message;
import com.example.socialapp.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private Context context;
    private List<Message> messageList;


    private FirebaseUser firebaseUser;
    private String currentImageUrl;
    private String chatImageUrl;


    private String currentUserId;


    public MessageAdapter(Context context, List<Message> messageList, String currentUserId){
        this.context = context;
        this.messageList = messageList;

        this.currentUserId = currentUserId;
        currentImageUrl = "default";
        chatImageUrl = "default";

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageList.get(position);

        if (message.getSendId().equals(firebaseUser.getUid())) {
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            View view = LayoutInflater.from(context)
                    .inflate(R.layout.message_item_send, parent, false);
            return new SentMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(context)
                    .inflate(R.layout.message_item_receive, parent, false);
            return new ReceivedMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        Message message = messageList.get(position);


        if (holder.getItemViewType() == VIEW_TYPE_MESSAGE_RECEIVED) {
            FirebaseDatabase.getInstance().getReference().child("Users").child(message.getSendId()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        User user = snapshot.getValue(User.class);
                        chatImageUrl = user.getImageUrl();
                        if (holder.getItemViewType() == VIEW_TYPE_MESSAGE_RECEIVED) {
                            ((ReceivedMessageViewHolder) holder).message.setText(message.getMessage());
                            if (chatImageUrl.equals("default") || chatImageUrl.equals("")) {
                                ((ReceivedMessageViewHolder) holder).userPic.setImageResource(R.mipmap.ic_launcher);
                            } else {
                                System.out.println("chatImageUrl" + chatImageUrl);
                                Glide.with(context).load(chatImageUrl).into(((ReceivedMessageViewHolder) holder).userPic);

//                                Picasso.get().load(chatImageUrl).placeholder(R.mipmap.ic_launcher).into(((ReceivedMessageViewHolder) holder).userPic);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        User user = snapshot.getValue(User.class);
                        currentImageUrl = user.getImageUrl();
                        if (holder.getItemViewType() == VIEW_TYPE_MESSAGE_SENT) {

                            ((SentMessageViewHolder) holder).message.setText(message.getMessage());
                            if (currentImageUrl.equals("default") || currentImageUrl.equals("")) {
                                ((SentMessageViewHolder) holder).userPic.setImageResource(R.mipmap.ic_launcher);
                            } else {
                                Picasso.get().load(currentImageUrl).placeholder(R.mipmap.ic_launcher).into(((SentMessageViewHolder) holder).userPic);
                            }
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }



    }


    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class SentMessageViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView userPic;
        public TextView message;
        public SentMessageViewHolder(View itemView) {
            super(itemView);
            userPic = itemView.findViewById(R.id.userPic);
            message = itemView.findViewById(R.id.message);
        }
    }

    public class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        public TextView message;
        public CircleImageView userPic;

        public ReceivedMessageViewHolder(View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.message);
            userPic = itemView.findViewById(R.id.userPic);
        }
    }
}
