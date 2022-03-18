package np.com.chaudharysubash.journeyjournal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class EditJournalActivity extends AppCompatActivity {
    private ImageView imageView;
    private ImageButton imageButton;
    private EditText titleEdt, descriptionEdt, locationEdt;
    private ProgressBar loadingBar;
    private Button update, delete;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private String journalId;

    private JournalRVModal journalRVModal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_journal);

        firebaseDatabase = FirebaseDatabase.getInstance();

        update = findViewById(R.id.update);
        delete = findViewById(R.id.delete);
        imageView = findViewById(R.id.imageView);
        imageButton = findViewById(R.id.imageButton);

        titleEdt = findViewById(R.id.title);
        descriptionEdt = findViewById(R.id.description);
        locationEdt = findViewById(R.id.location);
        loadingBar = findViewById(R.id.progressBar);

        journalRVModal = getIntent().getParcelableExtra("journal");
        if(journalRVModal!=null){
            titleEdt.setText(journalRVModal.getTitle());
            descriptionEdt.setText(journalRVModal.getDescription());
            locationEdt.setText(journalRVModal.getLocation());

//            Loading image into imageView
            String imageUrl = null;
            imageUrl = journalRVModal.getImageURL();
            Picasso.get().load(imageUrl).fit().into(imageView);

            journalId = journalRVModal.getJournalId();
        }

        databaseReference = firebaseDatabase.getReference("Journals").child(journalId);

//        Update button clicked
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingBar.setVisibility(View.VISIBLE);
                String title = titleEdt.getText().toString().trim();
                String description = descriptionEdt.getText().toString().trim();
                String location = locationEdt.getText().toString().trim();

                Map<String,Object> map = new HashMap<>();
                map.put("title",title);
                map.put("description",description);
                map.put("location",location);
                map.put("journalId",journalId);


                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        loadingBar.setVisibility(View.GONE);
                        databaseReference.updateChildren(map);
                        Toast.makeText(EditJournalActivity.this, "Journal updated successfully!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(EditJournalActivity.this, MainActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        loadingBar.setVisibility(View.GONE);
                        Toast.makeText(EditJournalActivity.this, "Failed to update journal!", Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });

//        Delete button clicked
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteJournal();
            }
        });
    }

//    Creating deleteJournal method
    private void deleteJournal() {
        databaseReference.removeValue();
        Toast.makeText(this, "Journal deleted successfully!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(EditJournalActivity.this, MainActivity.class);
        startActivity(intent);
    }
}