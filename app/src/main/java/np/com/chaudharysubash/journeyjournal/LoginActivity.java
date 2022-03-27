package np.com.chaudharysubash.journeyjournal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private EditText emailEdt,passwordEdt;
    private ProgressBar loadingBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEdt = findViewById(R.id.email);
        passwordEdt = findViewById(R.id.password);
        Button login = findViewById(R.id.login);
        TextView registerHere = findViewById(R.id.registerTextView);
        loadingBar = findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();


//        Register button click
        registerHere.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

//        Login button click
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateUser();
            }

            private void validateUser() {
//                User validation
                String email = emailEdt.getText().toString().trim();
                String pwd = passwordEdt.getText().toString().trim();
                if(TextUtils.isEmpty(email) || TextUtils.isEmpty(pwd)){
                    Toast.makeText(LoginActivity.this, "All credentials should be entered to login!", Toast.LENGTH_SHORT).show();

                } else {
                    loadingBar.bringToFront();
                    loadingBar.setVisibility(View.VISIBLE);
//                    user authentication
                    mAuth.signInWithEmailAndPassword(email,pwd).addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            loadingBar.setVisibility(View.GONE);
                            Toast.makeText(LoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            loadingBar.setVisibility(View.GONE);
                            Toast.makeText(LoginActivity.this, "Invalid Credentials!", Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
//        checking if user is already authenticated
        FirebaseUser user = mAuth.getCurrentUser();
        if(user !=null){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            this.finish();
        }
    }
}