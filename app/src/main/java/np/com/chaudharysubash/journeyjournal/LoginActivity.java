package np.com.chaudharysubash.journeyjournal;

import androidx.annotation.NonNull;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private EditText emailEdt,passwordEdt;
    private Button login;
    private TextView registerHere;
    private ProgressBar loadingBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEdt = findViewById(R.id.email);
        passwordEdt = findViewById(R.id.password);
        login = findViewById(R.id.login);
        registerHere = findViewById(R.id.registerTextView);
        loadingBar = findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();


        registerHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

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
                    return;
                } else {
                    loadingBar.bringToFront();
                    loadingBar.setVisibility(View.VISIBLE);
                    mAuth.signInWithEmailAndPassword(email,pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
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