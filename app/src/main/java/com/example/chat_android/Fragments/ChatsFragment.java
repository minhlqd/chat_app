package com.example.chat_android.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chat_android.Adapter.UserAdapter;
import com.example.chat_android.Models.Chat;
import com.example.chat_android.Models.ChatList;
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

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;


public class ChatsFragment extends Fragment {

    private UserAdapter userAdapter;
    private List<User> users;

    FirebaseUser firebaseUser;
    DatabaseReference reference;

    RecyclerView recyclerView;

    private List<ChatList> chatLists;
    private List<String> users_list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chats, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        chatLists = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("ChatList").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                chatLists.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    ChatList chatList = dataSnapshot.getValue(ChatList.class);
                    chatLists.add(chatList);
                }
                chat();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

//        reference = FirebaseDatabase.getInstance().getReference("Chats");
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                users_list.clear();
//                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
//                    Chat chat = dataSnapshot.getValue(Chat.class);
//                    assert chat != null;
//                    if (chat.getSender().equals(firebaseUser.getUid())) {
//                        users_list.add(chat.getReceiver());
//                    }
//
//                    if (chat.getReceiver().equals(firebaseUser.getUid())) {
//                        users_list.add(chat.getSender());
//                    }
//                }
//
//                ReadChats();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

        return view;

    }

    private void chat() {
        users = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                users.clear();
                for (DataSnapshot dataSnapshot:snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    for(ChatList cList : chatLists) {
                        assert user != null;
                        if (user.getId().equals(cList.getId())) {
                            users.add(user);
                        }
                    }
                }
                userAdapter = new UserAdapter(getContext(), users, true);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }

//    private void ReadChats() {
//
//        users = new ArrayList<>();
//        ListIterator<User> listIteratorUser = users.listIterator();
//
//        reference = FirebaseDatabase.getInstance().getReference("Users");
//
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                users.clear();
//                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
//                    User user = dataSnapshot.getValue(User.class);
//
//                    for (String id : users_list) {
//                        if (user.getId().equals(id)) {
//                            if (users.size()!=0) {
//                                ListIterator<User> listIteratorUser = users.listIterator();
//                                while(listIteratorUser.hasNext()){
//                                    User user1 = listIteratorUser.next();
//                                    if (!user.getId().equals(user1.getId())){
//                                        listIteratorUser.add(user);
//                                    }
//                                }
//                            } else {
//                                ListIterator<User> listIteratorUser = users.listIterator();
//                                listIteratorUser.add(user);
//                            }
//                        }
//                    }
//                }
//                userAdapter = new UserAdapter(getContext(), users, true);
//                recyclerView.setAdapter(userAdapter);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
//    }
}