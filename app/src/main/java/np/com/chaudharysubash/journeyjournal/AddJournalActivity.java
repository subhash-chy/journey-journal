package np.com.chaudharysubash.journeyjournal;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddJournalActivity extends AppCompatActivity {
    private ImageView imageView;
    private EditText titleEdt, descriptionEdt, locationEdt;
    private ProgressBar loadingBar;

    private StorageReference storageReference;
    private DatabaseReference databaseReference;

    // Folder path for Firebase Storage.
    String Storage_Path = "images/";

    private Uri filePath;

    private String journalId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_journal);


        Button save = findViewById(R.id.save);
        imageView = findViewById(R.id.imageView);
        ImageButton imageButton = findViewById(R.id.imageButton);

        titleEdt = findViewById(R.id.title);
        descriptionEdt = findViewById(R.id.description);
        locationEdt = findViewById(R.id.location);
        loadingBar = findViewById(R.id.progressBar);


        // get the Firebase  storage reference
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Journals");

//        Image button clicked to pick an image
        imageButton.setOnClickListener(view -> {

//                Using the image picker dependency
            ImagePicker.Companion.with(AddJournalActivity.this)
                    .crop()	    			//Crop image(Optional), Check Customization for more option
                    .compress(1024)			//Final image size will be less than 1 MB(Optional)
                    .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                    .start();

        });

//        Save button clicked to save data into the firebase
        save.setOnClickListener(view -> {
            String title = titleEdt.getText().toString();
            String description = descriptionEdt.getText().toString();
            String location = locationEdt.getText().toString();

            journalId = title;

//                Creating second storage reference
            StorageReference storageReference2 = storageReference.child(Storage_Path+System.currentTimeMillis());

//                Adding on success listener
            storageReference2.putFile(filePath).addOnSuccessListener(taskSnapshot -> {
//                        Making progressbar invisible
                loadingBar.setVisibility(View.GONE);

//                        Saving data on firebase and getting image download url
                storageReference2.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

                    final Date d = Calendar.getInstance().getTime();
                    final SimpleDateFormat  sdf= new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                    final String formattedDate = sdf.format(d);
                    @Override
                    public void onSuccess(Uri uri) {
                        String url = uri.toString();
                        JournalRVModal journalRVModal = new JournalRVModal(journalId,title,description,location, url, formattedDate);
                        databaseReference.child(journalId).setValue(journalRVModal);

                        Toast.makeText(AddJournalActivity.this, "Journal added successfully!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AddJournalActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                });
            })
//                        If task fails, we will add on failure listener
                    .addOnFailureListener(e -> {
    //                        Hiding loading bar
                        loadingBar.setVisibility(View.GONE);
    //                        Showing error as a toast message
                        Toast.makeText(AddJournalActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();


                    })
//                        Data saving or uploading on progress
                .addOnProgressListener(snapshot -> loadingBar.setVisibility(View.VISIBLE));
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        selected image path will be stored in uri variable
        assert data != null;
        filePath = data.getData();
        imageView.setImageURI(filePath);

    }

}