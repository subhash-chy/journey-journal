package np.com.chaudharysubash.journeyjournal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class AddJournalActivity extends AppCompatActivity {
    private ImageView imageView;
    private ImageButton imageButton;
    private EditText titleEdt, descriptionEdt, locationEdt;
    private ProgressBar loadingBar;
    private Button save;

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    // Folder path for Firebase Storage.
    String Storage_Path = "images/";

    private Uri filePath;

    private String journalId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_journal);


        save = findViewById(R.id.save);
        imageView = findViewById(R.id.imageView);
        imageButton = findViewById(R.id.imageButton);

        titleEdt = findViewById(R.id.title);
        descriptionEdt = findViewById(R.id.description);
        locationEdt = findViewById(R.id.location);
        loadingBar = findViewById(R.id.progressBar);


        // get the Firebase  storage reference
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Journals");

//        Image button clicked to pick an image
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                Using the image picker dependency
                ImagePicker.Companion.with(AddJournalActivity.this)
                        .crop()	    			//Crop image(Optional), Check Customization for more option
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start();

            }
        });

//        Save button clicked to save data into the firebase
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = titleEdt.getText().toString();
                String description = descriptionEdt.getText().toString();
                String location = locationEdt.getText().toString();

                journalId = title;

//                Creating second storage reference
                StorageReference storageReference2 = storageReference.child(Storage_Path+System.currentTimeMillis());

//                Adding on success listener
                storageReference2.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                        Making progressbar invisible
                        loadingBar.setVisibility(View.GONE);

//                        Saving data on firebase and getting image download url
                        storageReference2.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String url = uri.toString();
                                JournalRVModal journalRVModal = new JournalRVModal(journalId,title,description,location, url);
                                databaseReference.child(journalId).setValue(journalRVModal);

                                Toast.makeText(AddJournalActivity.this, "Journal added successfully!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(AddJournalActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                        });
                    }
                })
//                        If task fails, we will add on failure listener
                        .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
//                        Hiding loading bar
                        loadingBar.setVisibility(View.GONE);
//                        Showing error as a toast message
                        Toast.makeText(AddJournalActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();


                    }
                })
//                        Data saving or uploading on progress
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                        loadingBar.setVisibility(View.VISIBLE);

                    }
                });

//                databaseReference.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        loadingBar.setVisibility(View.GONE);
//                        databaseReference.child(journalId).setValue(journalRVModal);
//                        Toast.makeText(AddJournalActivity.this, "Journal added successfully!", Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent(AddJournalActivity.this, MainActivity.class);
//                        startActivity(intent);
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                        loadingBar.setVisibility(View.GONE);
//                        Toast.makeText(AddJournalActivity.this, "Error: "+ error, Toast.LENGTH_SHORT).show();
//
//                    }
//                });
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        selected image path will be stored in uri variable
        filePath = data.getData();
        imageView.setImageURI(filePath);

    }

}