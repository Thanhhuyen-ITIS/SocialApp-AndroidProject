package com.example.socialapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.hendraanggrian.appcompat.socialview.autocomplete.Hashtag;
import com.hendraanggrian.appcompat.socialview.widget.HashtagArrayAdapter;
import com.hendraanggrian.appcompat.socialview.widget.SocialAutoCompleteTextView;

import java.util.HashMap;
import java.util.List;

public class PostActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private String imageUrl;

    private ImageView close;
    private ImageView imagePost;
    private TextView post;

    SocialAutoCompleteTextView postDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        close = findViewById(R.id.close);
        imagePost = findViewById(R.id.post_image);
        post = findViewById(R.id.post);
        postDescription = findViewById(R.id.post_description);

        imagePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PostActivity.this, StartActivity.class));
                finish();
            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload();
            }
        });
    }

    private void upload() {
        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Uploading");
        pd.show();

        if (imageUri != null) {
            final StorageReference filePath = FirebaseStorage.getInstance().getReference("Posts").child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
            StorageTask uploadTask = filePath.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return filePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    Uri uri = (Uri) task.getResult();
                    imageUrl = uri.toString();

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
                    String postId = reference.push().getKey();

                    Long timestamp = System.currentTimeMillis();

                    HashMap<String, Object> map = new HashMap<>();
                    map.put("postId", postId);
                    map.put("imageUrl", imageUrl);
                    map.put("description", postDescription.getText().toString());
                    map.put("publisher", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    map.put("timestamp", timestamp);

                    reference.child(postId).setValue(map);

                    DatabaseReference hashTagRef = FirebaseDatabase.getInstance().getReference().child("HashTags");
                    List<String> hashTags = postDescription.getHashtags();
                    if (!hashTags.isEmpty()) {
                        for (String tag : hashTags) {
                            map.clear();
                            map.put("tag", tag.toLowerCase());
                            map.put("postId", postId);

                            hashTagRef.child(tag.toLowerCase()).child(postId).setValue(map);
                        }
                    }
                    pd.dismiss();
                    startActivity(new Intent(PostActivity.this, StartActivity.class));
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(PostActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            Toast.makeText(this, "No image was selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imagePost.setImageURI(imageUri);
        }
    }

    private String getFileExtension(Uri imageUri) {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(this.getContentResolver().getType(imageUri));
    }

    @Override
    protected void onStart() {
        super.onStart();
        final ArrayAdapter<Hashtag> hashtagAdapter = new HashtagArrayAdapter<>(getApplicationContext());
        FirebaseDatabase.getInstance().getReference().child("HashTags").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    hashtagAdapter.add(new Hashtag(dataSnapshot.getKey(), (int) dataSnapshot.getChildrenCount()));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        postDescription.setHashtagAdapter(hashtagAdapter);
    }
}