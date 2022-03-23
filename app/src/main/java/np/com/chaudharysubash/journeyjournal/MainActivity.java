package np.com.chaudharysubash.journeyjournal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements JournalAdapter.JournalClickInterface {
    private ProgressBar loadingBar;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ArrayList<JournalRVModal> journalRVModalArrayList;

    private ScrollView bottomSheetRL;
    private JournalAdapter journalAdapter;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.rvJournal);
        loadingBar = findViewById(R.id.progressBar);

//        Showing progressbar as soon as user comes to MainActivity
        loadingBar.setVisibility(View.VISIBLE);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Journals");
        journalRVModalArrayList = new ArrayList<>();

        bottomSheetRL = findViewById(R.id.bottomSheet);
        mAuth = FirebaseAuth.getInstance();
        journalAdapter = new JournalAdapter(journalRVModalArrayList, this, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(journalAdapter);

//      Calling getAllJournal method
        getAllJournals();
    }


//    Creating method for reading all journals coming from firebase database
    private void getAllJournals(){
        journalRVModalArrayList.clear();
        databaseReference.addChildEventListener(new ChildEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                loadingBar.setVisibility(View.GONE);
                journalRVModalArrayList.add(snapshot.getValue(JournalRVModal.class));
                journalAdapter.notifyDataSetChanged();
            }


            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                loadingBar.setVisibility(View.GONE);
                journalAdapter.notifyDataSetChanged();
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                loadingBar.setVisibility(View.GONE);
                journalAdapter.notifyDataSetChanged();

            }

            @SuppressLint("NotifyDataSetChanged")
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

//    clicking individual item will open bottom sheet navigation
    @Override
    public void onJournalClick(int position) {
        displayBottomSheet(journalRVModalArrayList.get(position));
    }

//    Creating displayBottomSheet method
    private void displayBottomSheet(JournalRVModal journalRVModal){
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
//        Inflating bottom sheet dialog
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
        TextView dateTV = layout.findViewById(R.id.journalDate);

        journalTitleTV.setText(journalRVModal.getTitle());
        journalDescriptionTV.setText(journalRVModal.getDescription());
        journalLocationTV.setText(journalRVModal.getLocation());
        dateTV.setText(journalRVModal.getDate());

//        Loading image by using Picasso library
        String imageUrl = journalRVModal.getImageURL();
        Picasso.get().load(imageUrl).fit().into(imageView);

        String journalId = journalRVModal.getJournalId();
        DatabaseReference databaseReference2 = firebaseDatabase.getReference("Journals").child(journalId);
        //        Delete the item when delete button is pressed
        deleteButton.setOnClickListener(view -> {
            databaseReference2.removeValue();
            Toast.makeText(MainActivity.this, "Journal deleted successfully!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });


//        Edit button pressed
        editButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, EditJournalActivity.class);
            intent.putExtra("journal", journalRVModal);
            startActivity(intent);
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
        if (id == R.id.logOut) {
            Toast.makeText(this, "Logged out successfully!", Toast.LENGTH_SHORT).show();
//                Signing out and opening login activity
            mAuth.signOut();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

//    clicking floating action button will open AddJournalActivity
    public void onClickAddNewJournal(View view) {
        Intent intent = new Intent(MainActivity.this, AddJournalActivity.class);
            startActivity(intent);
    }
}