package np.com.chaudharysubash.journeyjournal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements JournalAdapter.JournalClickInterface {
    private RecyclerView recyclerView;
    private ProgressBar loadingBar;
    private FloatingActionButton addNewJournal;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseStorage firebaseStorage;
    private ArrayList<JournalRVModal> journalRVModalArrayList;

    private ScrollView bottomSheetRL;
    private JournalAdapter journalAdapter;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.rvJournal);
//        recyclerView.setHasFixedSize(true);
        loadingBar = findViewById(R.id.progressBar);

//        Showing progressbar as soon as user comes to MainActivity
        loadingBar.setVisibility(View.VISIBLE);

        addNewJournal = findViewById(R.id.addNewJournal);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Journals");
        firebaseStorage = FirebaseStorage.getInstance();
        journalRVModalArrayList = new ArrayList<>();

        bottomSheetRL = findViewById(R.id.bottomSheet);
        mAuth = FirebaseAuth.getInstance();
        journalAdapter = new JournalAdapter(journalRVModalArrayList, this, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(journalAdapter);


        getAllJournals();
    }




//    addNewJournal.setOnClickListener(new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//            Intent intent = new Intent(MainActivity.this, AddJournalActivity.class);
//            startActivity(intent);
//        }
//    });

//    Creating method for reading all journals coming from firebase database
    private void getAllJournals(){
        journalRVModalArrayList.clear();
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                loadingBar.setVisibility(View.GONE);
                journalRVModalArrayList.add(snapshot.getValue(JournalRVModal.class));
                journalAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                loadingBar.setVisibility(View.GONE);
                journalAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                loadingBar.setVisibility(View.GONE);
                journalAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                loadingBar.setVisibility(View.GONE);
                journalAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onJournalClick(int position) {
        displayBottomSheet(journalRVModalArrayList.get(position));

    }

    private void displayBottomSheet(JournalRVModal journalRVModal){
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View layout = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_dialog, bottomSheetRL);
        bottomSheetDialog.setContentView(layout);
        bottomSheetDialog.setCancelable(false);
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        bottomSheetDialog.show();

        Button editButton = layout.findViewById(R.id.edit);
        Button deleteButton = layout.findViewById(R.id.deleteButton);

        TextView journalTitleTV = layout.findViewById(R.id.journalTitle);
        TextView journalDescriptionTV = layout.findViewById(R.id.journalDescription);
        TextView journalLocationTV = layout.findViewById(R.id.journalLocation);
        ImageView imageView = layout.findViewById(R.id.journalImage);

        journalTitleTV.setText(journalRVModal.getTitle());
        journalDescriptionTV.setText(journalRVModal.getDescription());
        journalLocationTV.setText(journalRVModal.getLocation());

//        Loading image
        String imageUrl = null;
        imageUrl = journalRVModal.getImageURL();
        Picasso.get().load(imageUrl).fit().into(imageView);

//        Delete button pressed
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReference.removeValue();
                Toast.makeText(MainActivity.this, "Journal deleted successfully!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });


//        Edit button pressed
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditJournalActivity.class);
                intent.putExtra("journal", journalRVModal);
                startActivity(intent);
            }
        });
    }

//    On create option menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.logOut:
                Toast.makeText(this, "Logged out successfully!", Toast.LENGTH_SHORT).show();
                mAuth.signOut();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public void onClickAddNewJournal(View view) {
        Intent intent = new Intent(MainActivity.this, AddJournalActivity.class);
            startActivity(intent);
    }
}