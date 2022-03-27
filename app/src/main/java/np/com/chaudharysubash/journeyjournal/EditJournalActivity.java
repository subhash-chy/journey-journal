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
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EditJournalActivity extends AppCompatActivity {
    private ImageView imageView;
    private EditText titleEdt, descriptionEdt, locationEdt;
    private ProgressBar loadingBar;

    private DatabaseReference databaseReference;

    //        Newly added
    private StorageReference storageReference;
    String Storage_Path = "images/";
    private Uri filePath;

    private String journalId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_journal);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
//        Newly added
        // get the Firebase  storage reference
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        databaseReference = firebaseDatabase.getReference("Journals");

        Button update = findViewById(R.id.update);
        imageView = findViewById(R.id.imageView);
        ImageButton imageButton = findViewById(R.id.imageButton);

        titleEdt = findViewById(R.id.title);
        descriptionEdt = findViewById(R.id.description);
        locationEdt = findViewById(R.id.location);
        loadingBar = findViewById(R.id.progressBar);


        JournalRVModal journalRVModal = getIntent().getParcelableExtra("journal");
        if(journalRVModal !=null){
            titleEdt.setText(journalRVModal.getTitle());
            descriptionEdt.setText(journalRVModal.getDescription());
            locationEdt.setText(journalRVModal.getLocation());

//            Loading image into imageView
            String imageUrl;
            imageUrl = journalRVModal.getImageURL();
            Picasso.get().load(imageUrl).fit().into(imageView);

            journalId = journalRVModal.getJournalId();
        }

        imageButton.setOnClickListener(view -> {
            //                Using the image picker dependency
            ImagePicker.Companion.with(EditJournalActivity.this)
                    .crop()	    			//Crop image(Optional), Check Customization for more option
                    .compress(1024)			//Final image size will be less than 1 MB(Optional)
                    .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                    .start();
        });

        databaseReference = firebaseDatabase.getReference("Journals").child(journalId);
//        Update button clicked
        update.setOnClickListener(view -> {
            loadingBar.setVisibility(View.VISIBLE);
            String title = titleEdt.getText().toString().trim();
            String description = descriptionEdt.getText().toString().trim();
            String location = locationEdt.getText().toString().trim();



//                Creating second storage reference
            StorageReference storageReference2 = storageReference.child(Storage_Path+System.currentTimeMillis());

            storageReference2.putFile(filePath).addOnSuccessListener(taskSnapshot -> {
//                        Making progressbar invisible
                loadingBar.setVisibility(View.GONE);

//                        Saving data on firebase and getting image download url
                storageReference2.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

                    final Date d = Calendar.getInstance().getTime();
                    final SimpleDateFormat sdf= new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                    final String formattedDate = sdf.format(d);
                    @Override
                    public void onSuccess(Uri uri) {
                        Map<String,Object> map = new HashMap<>();
                        map.put("title",title);
                        map.put("description",description);
                        map.put("location",location);
                        map.put("journalId",journalId);
                        String url = uri.toString();
                        map.put("imageURL", url);
                        map.put("date",formattedDate);

//                                JournalRVModal journalRVModal = new JournalRVModal(journalId,title,description,location, url, formattedDate);
                        databaseReference.updateChildren(map);

                        Toast.makeText(EditJournalActivity.this, "Journal updated successfully!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(EditJournalActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                });
            });
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