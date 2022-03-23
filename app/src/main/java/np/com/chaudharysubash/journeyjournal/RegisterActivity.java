package np.com.chaudharysubash.journeyjournal;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private EditText userNameEdt, emailEdt, passwordEdt, confirmPasswordEdt;
    private ProgressBar loadingBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

//        Finding all the variables by their id's
        userNameEdt = findViewById(R.id.fullName);
        emailEdt = findViewById(R.id.email);
        passwordEdt = findViewById(R.id.password);
        confirmPasswordEdt = findViewById(R.id.confirmPassword);
        Button registerBtn = findViewById(R.id.idBtnRegister);

        TextView loginTV = findViewById(R.id.loginTextView);
        loadingBar = findViewById(R.id.progressBar);
        mAuth = FirebaseAuth.getInstance();

        // setting on click method if already registered text view is clicked
        loginTV.setOnClickListener(view -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        // On clicking Register button
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }

            // Creating a register user method
            private void registerUser() {
                String userName = userNameEdt.getText().toString().trim();
                String pwd = passwordEdt.getText().toString().trim();
                String email = emailEdt.getText().toString().trim();
                String confirmPwd = confirmPasswordEdt.getText().toString().trim();

                // Validating all the input fields
                if(!pwd.equals(confirmPwd)){
                    Toast.makeText(RegisterActivity.this, "Password doesn't match!", Toast.LENGTH_SHORT).show();
                    confirmPasswordEdt.requestFocus();

                } else if(TextUtils.isEmpty(userName) || userName.length()<3){
                    userNameEdt.setError("Username should contain at least 3 characters!");
                    userNameEdt.requestFocus();

                } else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches() || TextUtils.isEmpty(email)){
                    emailEdt.setError("Please enter valid email address!");
                    emailEdt.requestFocus();

                } else if(TextUtils.isEmpty(pwd) || pwd.length()<4){
                    passwordEdt.setError("Password should be greater than 4 characters!");
                    passwordEdt.requestFocus();

                }

//              Do this if everything goes right
                else {
                    loadingBar.setVisibility(View.VISIBLE);
//                  Creating user with email and password in firebase
                    mAuth.createUserWithEmailAndPassword(email,pwd).addOnCompleteListener(task -> {
//                            If task completes successfully
                        if(task.isSuccessful()){
                            loadingBar.setVisibility(View.GONE);
                            Toast.makeText(RegisterActivity.this, "Registered successfully!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
//                            If task is not successful
                        else{
                            loadingBar.setVisibility(View.GONE);
                            Toast.makeText(RegisterActivity.this, "Failed user registration!", Toast.LENGTH_SHORT).show();
                        }
                    });

                }

            }
        });
    }
}