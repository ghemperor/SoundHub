package tdtufinalproject.soundhub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUp extends AppCompatActivity {
    public static final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    public static final String TAG_EMAIL_SIGNUP = "Email SignUp";
    private EditText etxtEmail;
    private EditText etxtPass;
    private Button btn_SignUp;
    private Boolean isValidEmail = false;
    private TextView txtValidate;

    //Facebook login and Firebase Login
    private FirebaseAuth fbAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        getViewID();

        btn_SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isValidEmail){
                    Toast.makeText(getApplicationContext(),"Validate email failed",Toast.LENGTH_LONG).show();
                    return;
                }
                signUpEmail();
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.signin_bottom);
        bottomNavigationView.setOnItemReselectedListener(new NavigationBarView.OnItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.user:
                        toUser();
                        break;
                    case R.id.home:
                        toHome();
                        break;
                }
            }
        });

        etxtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //validate here
                txtValidate.setText("");
                String email = etxtEmail.getText().toString().trim();
                isValidEmail = (email.matches(emailPattern) && charSequence.length() > 0);
                if(!isValidEmail){
                    txtValidate.setTextColor(Color.rgb(228, 134, 148));
                    txtValidate.setText("Invalid email address");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void getViewID(){
        etxtEmail = (EditText) findViewById(R.id.etxt_Email);
        etxtPass = (EditText) findViewById(R.id.etxt_Password);
        btn_SignUp = (Button) findViewById(R.id.btn_Signup);
        txtValidate = (TextView) findViewById(R.id.txt_Validate2);
    }

    private void navigateToMainInterface() {
        Intent intent = new Intent(SignUp.this, MainInterface.class);
        startActivity(intent);
    }

    private void signUpEmail() {
        String uEmail = etxtEmail.getText().toString().trim();
        String uPass = etxtPass.getText().toString().trim();
        fbAuth = FirebaseAuth.getInstance();
        fbAuth.createUserWithEmailAndPassword(uEmail, uPass)
                .addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(SignUp.this, "Sign up successful!.",
                                    Toast.LENGTH_SHORT).show();
                            navigateToMainInterface();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG_EMAIL_SIGNUP, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUp.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    public void toUser() {
        Intent intent = new Intent(this, User.class);
        startActivity(intent);
    }
    public void toHome() {
        Intent intent = new Intent(this, MainInterface.class);
        startActivity(intent);
    }
}