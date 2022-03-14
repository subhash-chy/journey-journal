package np.com.chaudharysubash.journeyjournal;

import androidx.annotation.NonNull;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddJournalActivity extends AppCompatActivity {
    ImageView imageView;
    ImageButton imageButton;
    EditText titleEdt, descriptionEdt, locationEdt;
    ProgressBar loadingBar;
    Button save;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

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
                loadingBar.setVisibility(View.VISIBLE);
                String title = titleEdt.getText().toString();
                String description = descriptionEdt.getText().toString();
                String location = locationEdt.getText().toString();
//                TODO: Image

                journalId = title;
                JournalRVModal journalRVModal = new JournalRVModal(journalId,title,description,location);

                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        loadingBar.setVisibility(View.GONE);
                        databaseReference.child(journalId).setValue(journalRVModal);
                        Toast.makeText(AddJournalActivity.this, "Journal added successfully!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AddJournalActivity.this, MainActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        loadingBar.setVisibility(View.GONE);
                        Toast.makeText(AddJournalActivity.this, "Error: "+ error, Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        selected image path will be stored in uri variable
        Uri uri = data.getData();
        imageView.setImageURI(uri);

    }
}