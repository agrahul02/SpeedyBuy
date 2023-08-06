package com.speedybuy.speedybuy.chat;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.speedybuy.speedybuy.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatListFragment extends Fragment {

    String uid;
    FirebaseAuth firebaseAuth;
    RecyclerView recyclerView;
    List<ModelChatList> chatListList;
    List<ModelUsers> usersList;
    DatabaseReference reference;
    FirebaseUser firebaseUser;
    AdapterChatList adapterChatList;
    List<ModelChat> chatList;
    public ChatListFragment() {
        // Required empty public constructor
    }
    public void performAction() {
        // Implement the desired functionality here
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View  view=inflater.inflate(R.layout.fragment_chat_list, container, false);

        firebaseAuth= FirebaseAuth.getInstance();
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();


        recyclerView=view.findViewById(R.id.chatlistrecycle);
        chatListList=new ArrayList<>();
        chatList=new ArrayList<>();
        reference= FirebaseDatabase.getInstance().getReference("ChatList").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatListList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    ModelChatList modelChatList = ds.getValue(ModelChatList.class);
                    if(!modelChatList.getId().equals(firebaseUser.getUid())) {
                        chatListList.add(modelChatList);
                    }

                }
                loadChats();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return view;

    }
    private void loadChats() {
        usersList=new ArrayList<>();
        reference=FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersList.clear();
                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    ModelUsers user=dataSnapshot1.getValue(ModelUsers.class);
                    for (ModelChatList chatList:chatListList){
                        if(user.getUid()!=null && user.getUid().equals(chatList.getId())){
                            usersList.add(user);
                            break;
                        }
                    }
                    adapterChatList=new AdapterChatList(getActivity(),usersList);
                    recyclerView.setAdapter(adapterChatList);
                    for (int i=0; i<usersList.size(); i++){
                        lastMessage(usersList.get(i).getUid());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void lastMessage(final String uid) {

        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Chats");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String lastmess = "default";
                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    ModelChat chat=dataSnapshot1.getValue(ModelChat.class);
                    if(chat==null){
                        continue;
                    }
                    String sender=chat.getSender();
                    String receiver=chat.getReceiver();
                    if(sender == null || receiver == null){
                        continue;
                    }
                    if(chat.getReceiver().equals(firebaseUser.getUid())&&
                            chat.getSender().equals(uid)||
                            chat.getReceiver().equals(uid)&&
                                    chat.getSender().equals(firebaseUser.getUid())){
                        if(chat.getType().equals("images")){
                            lastmess="Sent a Photo";
                        }
                        else {
                            lastmess = chat.getMessage();
                        }
                    }

                }
                adapterChatList.setlastMessageMap(uid,lastmess);
                adapterChatList.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void searchChatList(final String search) {

        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("ChatList");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatList.clear();
                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    ModelChatList modelChatList=dataSnapshot1.getValue(ModelChatList.class);
                    if(modelChatList.getTitle().toLowerCase().contains(search.toLowerCase())||
                            modelChatList.getDescription().toLowerCase().contains(search.toLowerCase())) {
                        chatListList.add(modelChatList);
                    }
                    adapterChatList=new AdapterChatList(getActivity(),chatListList);
                    recyclerView.setAdapter(adapterChatList);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(getActivity(),databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

     @Override
       public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
           inflater.inflate(R.menu.main_menu,menu);
          MenuItem item=menu.findItem(R.id.search);

           super.onCreateOptionsMenu(menu,inflater);
       }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }


    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        String lastmess = "default";
        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
            // Retrieve the chat data from the dataSnapshot
            ModelChat chat = dataSnapshot1.getValue(ModelChat.class);
            if (chat == null) {
                continue;
            }

            // Retrieve the sender and receiver values from the chat data
            String sender = chat.getSender();
            String receiver = chat.getReceiver();

            // Perform your desired logic using the chat data
            if (receiver != null && sender != null) {
                if (receiver.equals(firebaseUser.getUid()) && sender.equals(uid) ||
                        receiver.equals(uid) && sender.equals(firebaseUser.getUid())) {
                    if (chat.getType().equals("images")) {
                        lastmess = "Sent a Photo";
                    } else {
                        lastmess = chat.getMessage();
                    }

                    // Call the method to play the notification sound
                    playNotificationSound();
                }
            }
        }
        // Your existing code...
    }

    private void playNotificationSound() {
        try {
            Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(getActivity(), notificationSound);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mediaPlayer.release();
                }
            });
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}