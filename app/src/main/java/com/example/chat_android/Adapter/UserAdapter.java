package com.example.chat_android.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chat_android.Activities.MessageActivity;
import com.example.chat_android.Models.Chat;
import com.example.chat_android.Models.User;
import com.example.chat_android.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context context;
    private List<User> users;
    private boolean is_chat;

    String theLastMsg;
    boolean is_user;

    public UserAdapter(Context context, List<User> users, boolean is_chat) {
        this.context = context;
        this.users = users;
        this.is_chat = is_chat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = users.get(position);
        holder.username.setText(user.getUsername());
        if (user.getImageURL().equals("default")) {
            holder.profile_image.setImageResource(R.drawable.ic_name);
        } else {
            Glide.with(context).load(user.getImageURL()).into(holder.profile_image);
        }


        if (is_chat) {
            lastMessage(user.getId(), holder.last_msg,holder.last_user);
        } else {
            holder.last_msg.setVisibility(View.GONE);
            holder.last_user.setVisibility(View.GONE);
        }

        if (is_chat) {
            if (user.getStatus().equals("online")) {
                holder.img_on.setVisibility(View.VISIBLE);
                holder.img_off.setVisibility(View.GONE);
            } else
            if (user.getStatus().equals("offline")) {
                holder.img_off.setVisibility(View.VISIBLE);
                holder.img_on.setVisibility(View.GONE);
            }
        } else {
            holder.img_on.setVisibility(View.GONE);
            holder.img_off.setVisibility(View.VISIBLE);
        }



        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, MessageActivity.class);
            intent.putExtra("user_id", user.getId());
            context.startActivity(intent);
        });

    }


    @Override
    public int getItemCount() {
        return users.size();
    }



    public class ViewHolder extends  RecyclerView.ViewHolder{

        public TextView username;
        public ImageView profile_image;
        public Button img_on;
        public Button img_off;
        public TextView last_msg;
        public TextView last_user;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            profile_image = itemView.findViewById(R.id.profile_image);
            img_on = itemView.findViewById(R.id.img_on);
            img_off = itemView.findViewById(R.id.img_off);
            last_msg = itemView.findViewById(R.id.last_msg);
            last_user = itemView.findViewById(R.id.last_user);
        }
    }

    // hien tn cuoi cung
    private void lastMessage(String userId, TextView last_msg, TextView last_user) {
        theLastMsg = "default";
        is_user = false;
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Chat chat = dataSnapshot.getValue(Chat.class);

//                    assert chat != null;
//
//                    assert firebaseUser != null;

                    if (firebaseUser != null && chat != null) {
                        if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userId)){
                            theLastMsg = chat.getMessage();
                        } else if (chat.getReceiver().equals(userId) && chat.getSender().equals(firebaseUser.getUid())) {
                            theLastMsg = chat.getMessage();
                            is_user = true;
                        }
                    }

//                    if ("default".equals(theLastMsg)) {
//                        last_msg.setText("No Message");
//                    } else {

                    if (is_user) {
                        last_user.setText(R.string.you);
                    } else {
                        last_user.setText("");
                    }
                    last_msg.setText(theLastMsg);
//                    }
//                    theLastMsg = "default";
                }
            }


            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
}
