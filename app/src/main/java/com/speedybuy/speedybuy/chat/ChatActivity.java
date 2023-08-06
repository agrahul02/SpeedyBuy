package com.speedybuy.speedybuy.chat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.speedybuy.speedybuy.Notification.APIService;
import com.speedybuy.speedybuy.Notification.Client;
import com.speedybuy.speedybuy.Notification.Data;
import com.speedybuy.speedybuy.Notification.MyResponse;
import com.speedybuy.speedybuy.Notification.Sender;
import com.speedybuy.speedybuy.Notification.Token;
import com.speedybuy.speedybuy.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView recyclerView;
    ImageView profile, block;
    TextView name, userstatus;
    EditText msg;
    ImageButton send, attach;
    FirebaseAuth firebaseAuth;
    String uid, myuid, image;
    ValueEventListener valueEventListener;
    List<ModelChat> chatList;
    AdapterChat adapterChat;
    Button download;

    private Button soundButton;

    private MediaPlayer notificationSound;

    private static final int IMAGEPICK_GALLERY_REQUEST = 300;
    private static final int IMAGE_PICKCAMERA_REQUEST = 400;
    private static final int CAMERA_REQUEST = 100;
    private static final int STORAGE_REQUEST = 200;
    String[] cameraPermission;
    String[] storagePermission;
    Uri imageuri = null;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference users;
    boolean notify = false;
    boolean isBlocked = false;
    private MediaPlayer mediaPlayer;
    DatabaseReference dbref;
    APIService apiService;
    FirebaseUser firebaseUser;
    ValueEventListener seenListener;
    Intent intent;
    String fuid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        firebaseAuth = FirebaseAuth.getInstance();

        profile = findViewById(R.id.profiletv);
        name = findViewById(R.id.nameptv);
        userstatus = findViewById(R.id.onlinetv);
        msg = findViewById(R.id.messaget);
        send = findViewById(R.id.sendmsg);
        attach = findViewById(R.id.attachbtn);
        block = findViewById(R.id.block);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView=findViewById(R.id.chatrecycle);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        checkUserStatus();
        firebaseDatabase= FirebaseDatabase.getInstance();
        users=firebaseDatabase.getReference("Users");
        uid=getIntent().getStringExtra("uid");
        firebaseDatabase= FirebaseDatabase.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePicDialog();
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify=true;
                String message = msg.getText().toString();
                if (!message.isEmpty()) {
                    sendmessage(firebaseUser.getUid(), uid, message);
                    msg.setText(""); // Clear the input field after sending the message
                } else {
                    Toast.makeText(ChatActivity.this, "Message cannot be empty", Toast.LENGTH_SHORT).show();
                }
                msg.setText("");
            }
        });
        Query userquery=users.orderByChild("uid").equalTo(uid);
        userquery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    String nameh = "" + dataSnapshot1.child("name").getValue();
                    image = "" + dataSnapshot1.child("image").getValue();
                    String onlinestatus = "" + dataSnapshot1.child("onlineStatus").getValue();
                    String typingto = "" + dataSnapshot1.child("typingTo").getValue();
                    if (typingto.equals(myuid)) {
                        userstatus.setText("Typing....");
                    } else {
                        if (onlinestatus.equals("online")) {
                            userstatus.setText(onlinestatus);
                        } else {
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTimeInMillis(Long.parseLong(onlinestatus));
                            String timedate = DateFormat.format("hh:mm aa", calendar).toString();
                            userstatus.setText("Last Seen:" + timedate);
                        }
                    }
                    name.setText(nameh);
                    try {
                        Glide.with(ChatActivity.this).load(image).placeholder(R.drawable.profile_image).into(profile);
                    } catch (Exception e) {

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //seenMessage(uid);
        readMessages();
        getTokenAndUpdate();

    }

    private void getTokenAndUpdate() {
    FirebaseMessaging.getInstance().getToken()
            .addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Log.w("ChatFragment", "Fetching FCM registration token failed", task.getException());
                    return;
                }
                // Get the token from the task result
                String token = task.getResult();
                // Call the updateToken method with the obtained token
                updateToken(token);
            });
}
    private void updateToken(String token) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            Token token1 = new Token(token);
            reference.child(firebaseUser.getUid()).setValue(token1);
        }
    }


    private void sendNotification(String receiver, String username, String message) {

        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query   query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(firebaseUser.getUid(),R.drawable.logo,username+": "+message,"New Message",
                            uid);
                    Sender sender = new Sender(data, token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200) {
                                        if (response.body().success != 1) {
                                            Toast.makeText(ChatActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
        String timestamp= String.valueOf(System.currentTimeMillis());
        checkOnlineStatus(timestamp);
        checkTypingStatus("noOne");
    }
    @Override
    protected void onResume() {
        checkOnlineStatus("online");
        super.onResume();
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
    private void checkOnlineStatus(String status){

        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Users").child(myuid);
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("onlineStatus", status);
        dbref.updateChildren(hashMap);
        // Hello guys
    }
    private void checkTypingStatus(String typing){

        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Users").child(myuid);
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("typingTo", typing);
        dbref.updateChildren(hashMap);
    }
    @Override
    protected void onStart() {
        checkUserStatus();
        checkOnlineStatus("online");
        super.onStart();
    }

//    private void seenMessage(String userid){
//         dbref= FirebaseDatabase.getInstance().getReference().child("Chats");
//        seenListener = dbref.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    ModelChat chat = snapshot.getValue(ModelChat.class);
//                    if (chat.getReceiver().equals(myuid)&& chat.getSender().equals(uid)){
//                        HashMap<String,Object> hashMap = new HashMap<>();
//                        hashMap.put("isseen",true);
//                        snapshot.getRef().updateChildren(hashMap);
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }


    private void readMessages() {
        chatList = new ArrayList<>();
        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference().child("Chats");
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                chatList.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    ModelChat modelChat = dataSnapshot1.getValue(ModelChat.class);
                    if (modelChat != null) {
                        if ((modelChat.getReceiver() != null && modelChat.getReceiver().equals(myuid) &&
                                modelChat.getSender() != null && modelChat.getSender().equals(uid))
                                || (modelChat.getReceiver() != null && modelChat.getReceiver().equals(uid) &&
                                modelChat.getSender() != null && modelChat.getSender().equals(myuid))) {
                            chatList.add(modelChat);
                        }
                    }
                }

                adapterChat = new AdapterChat(ChatActivity.this, chatList, image);
                adapterChat.notifyDataSetChanged();
                recyclerView.setAdapter(adapterChat);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database errors here if needed.
            }
        });
    }

   /* FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference().child("images").child("imageId"); // Replace "imageId" with the unique name of the desired image

    storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
        @Override
        public void onSuccess(Uri uri) {
            // Download the image using the download URL
            downloadImage(uri.toString());
        }
    }).addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            // Handle the failure case
        }
    });*/

    private void showImagePicDialog() {
        String[] options = {"Camera", "Gallery"};

        AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
        builder.setTitle("Pick Image From");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        if (checkCameraPermission()) {
                            pickFromCamera();
                        } else {
                            requestCameraPermission();
                        }
                        break;
                    case 1:
                        if (checkStoragePermission()) {
                            pickFromGallery();
                        } else {
                            requestStoragePermission();
                        }
                        break;
                }
            }
        });
        builder.setNegativeButton("Cancel", null); // Adding a cancel button
        builder.create().show();
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_REQUEST: {
                if (grantResults.length > 0) {
                    boolean camera_accepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageaccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (camera_accepted && writeStorageaccepted) {
                        pickFromCamera();
                    } else {
                        Toast.makeText(this, "Please Enable Camera and Storage Permissions", Toast.LENGTH_LONG).show();
                    }
                }
            }
            break;
            case STORAGE_REQUEST: {
                if (grantResults.length > 0) {
                    boolean writeStorageaccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (writeStorageaccepted) {
                        pickFromGallery();
                    } else {
                        Toast.makeText(this, "Please Enable Storage Permissions", Toast.LENGTH_LONG).show();
                    }
                }
            }
            break;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK){
            if(requestCode==IMAGEPICK_GALLERY_REQUEST){
                imageuri=data.getData();
                try {
                    sendImageMessage(imageuri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(requestCode==IMAGE_PICKCAMERA_REQUEST){
                try {
                    sendImageMessage(imageuri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void sendImageMessage(Uri imageuri) throws IOException {
        notify = true;
        final ProgressBar progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        progressBar.setIndeterminate(false);
        progressBar.setMax(100);
        progressBar.setProgress(0);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(progressBar);
        builder.setMessage("Sending Image");
        builder.setCancelable(false);

        final AlertDialog dialog = builder.create();
        dialog.show();

// Simulate progress update
        new Thread(new Runnable() {
            public void run() {
                try {
                    // Simulate a long-running task
                    for (int progress = 0; progress <= 100; progress += 10) {
                        Thread.sleep(500); // Simulate time-consuming task
                        progressBar.setProgress(progress);
                    }

                    // Task completed
                    dialog.dismiss();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        final String timestamp=""+System.currentTimeMillis();
        String filepathandname="ChatImages/"+"post"+timestamp;
        Bitmap bitmap=MediaStore.Images.Media.getBitmap(this.getContentResolver(),imageuri);
        ByteArrayOutputStream arrayOutputStream=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,arrayOutputStream);
        final byte[] data=arrayOutputStream.toByteArray();
        StorageReference ref= FirebaseStorage.getInstance().getReference().child(filepathandname);
        ref.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                dialog.dismiss();
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful()) ;
                String downloadUri = uriTask.getResult().toString();

                if(uriTask.isSuccessful()){
                    DatabaseReference re= FirebaseDatabase.getInstance().getReference();
                    HashMap<String,Object> hashMap=new HashMap<>();
                    hashMap.put("sender",myuid);
                    hashMap.put("receiver",uid);
                    hashMap.put("message",downloadUri);
                    hashMap.put("timestamp",timestamp);
                    hashMap.put("dilihat",false);
                    hashMap.put("type","images");
                    re.child("Chats").push().setValue(hashMap);
                    final DatabaseReference ref1= FirebaseDatabase.getInstance().getReference("ChatList").child(uid).child(myuid);
                    ref1.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if(!dataSnapshot.exists()){
                                ref1.child("id").setValue(myuid);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    final DatabaseReference ref2= FirebaseDatabase.getInstance().getReference("ChatList").child(myuid).child(uid);
                    ref2.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if(!dataSnapshot.exists()){
                                ref2.child("id").setValue(uid);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private Boolean checkCameraPermission(){
        boolean result= ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA)
                ==(PackageManager.PERMISSION_GRANTED);
        boolean result1= ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                ==(PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }
    private void requestCameraPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(cameraPermission,CAMERA_REQUEST);
        }
    }
    private void pickFromCamera(){
        ContentValues contentValues=new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE,"Temp_pic");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION,"Temp Description");
        imageuri=this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);
        Intent camerIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        camerIntent.putExtra(MediaStore.EXTRA_OUTPUT,imageuri);
        startActivityForResult(camerIntent,IMAGE_PICKCAMERA_REQUEST);
    }
    private void pickFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");
        if (galleryIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(galleryIntent, IMAGEPICK_GALLERY_REQUEST);
        } else {
            Toast.makeText(this, "No app available to handle this action.", Toast.LENGTH_SHORT).show();
        }
    }
    private Boolean checkStoragePermission(){
        boolean result= ContextCompat.checkSelfPermission(ChatActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                ==(PackageManager.PERMISSION_GRANTED);
        return result;
    }
    private void requestStoragePermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(storagePermission,STORAGE_REQUEST);
        }
    }

    private void sendmessage(String sender, String receiver, String message) {
        if (sender == null || receiver == null || message == null || firebaseUser == null) {
            // Handle the null values appropriately, e.g., show an error or return early.
            return;
        }
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference();
        String timestamp=String.valueOf(System.currentTimeMillis());
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("sender",myuid);
        hashMap.put("receiver",uid);
        hashMap.put("message",message);
        hashMap.put("timestamp",timestamp);
        hashMap.put("dilihat",false);
        hashMap.put("type","text");
        databaseReference.child("Chats").push().setValue(hashMap);
        final DatabaseReference ref1= FirebaseDatabase.getInstance().getReference("ChatList").child(uid).child(myuid);
        ref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(!dataSnapshot.exists()){
                    ref1.child("id").setValue(myuid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final DatabaseReference ref2= FirebaseDatabase.getInstance().getReference("ChatList").child(myuid).child(uid);
        ref2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(!dataSnapshot.exists()){
                    ref2.child("id").setValue(uid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        String msg = message;

        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ModelUsers modelUsers = dataSnapshot.getValue(ModelUsers.class);
                if (notify) {
                    sendNotification(receiver, modelUsers != null ? modelUsers.getName() : "Unknown User", msg);
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error if needed.
            }
        });
    }



  /*  @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.logout){
            firebaseAuth.signOut();
            checkUserStatus();
        }
        return super.onOptionsItemSelected(item);
    }
*/
    private void checkUserStatus(){
        FirebaseUser user=firebaseAuth.getCurrentUser();
        if(user!=null){
            myuid=user.getUid();
        }

    }

    private void downloadImage(String downloadUrl) {
        Picasso.get()
                .load(downloadUrl)
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        // Save the downloaded image to the gallery
                        saveImageToGallery(bitmap);
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                        // Handle the failure case
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                        // Optional: handle the case when the image is being prepared
                    }
                });
    }
    private void saveImageToGallery(Bitmap bitmap) {
        String fileName = "photo.jpg"; // Replace with your desired filename
        String mimeType = "image/jpeg";

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
        values.put(MediaStore.Images.Media.MIME_TYPE, mimeType);

        ContentResolver resolver = getContentResolver();
        Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        try {
            OutputStream outputStream = resolver.openOutputStream(imageUri);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.close();

            Toast.makeText(getApplicationContext(), "Image saved to gallery", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            // Handle the error case
        }
    }


}