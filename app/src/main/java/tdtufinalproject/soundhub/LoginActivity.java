package tdtufinalproject.soundhub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {
    public static final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private static final String TAG_FACEBOOK_LOGIN = "Facebook Login";
    private static final String TAG_EMAIL_LOGIN = "Email Login";
    private EditText etxtEmail;
    private EditText etxtPass;
    private Button btnLogin;
    private TextView txtSignUp;
    private TextView txtValidate;
    private Boolean isValidEmail = false;

    //Facebook login and Firebase Login
    private CallbackManager fbCallbackManager;
    private LoginButton btnLoginFacebook;
    private FirebaseAuth fbAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        AppEventsLogger.activateApp(getApplication());

        getViewID();
        setupLoginFacebook();

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

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isValidEmail){
                    Toast.makeText(getApplicationContext(),"Validate email failed",Toast.LENGTH_LONG).show();
                    return;
                }
                LoginEmail();
            }
        });

        txtSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignUp.class);
                startActivity(intent);
            }
        });

        etxtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                txtValidate.setText("");
                String uEmail = etxtEmail.getText().toString().trim();
                isValidEmail = (uEmail.matches(emailPattern) && charSequence.length() > 0);
                if(!isValidEmail){
                    txtValidate.setTextColor(Color.rgb(200, 0, 50));
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
        btnLogin = (Button) findViewById(R.id.btn_Signin);
        txtSignUp = (TextView) findViewById(R.id.txt_signup);
        txtValidate = (TextView) findViewById(R.id.txt_Validate);
        btnLoginFacebook = (LoginButton) findViewById(R.id.login_button_Facebook);
    }

    private void navigateToMainInterface() {
        Intent intent = new Intent(LoginActivity.this, MainInterface.class);
        startActivity(intent);

    }

    private void checkLogin() {
        SharedPreferences sharedPreferences = this.getPreferences(Context.MODE_PRIVATE);
        if(sharedPreferences.getInt("isLoggedIn", 0) == 1) {
            navigateToMainInterface();
        }
    }

    private void LoginEmail(){
        String uEmail = etxtEmail.getText().toString().trim();
        String uPass = etxtPass.getText().toString().trim();
        FirebaseAuth fbAuth = FirebaseAuth.getInstance();
        fbAuth.signInWithEmailAndPassword(uEmail, uPass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG_EMAIL_LOGIN, "signInWithEmail:success");
                            FirebaseUser user = fbAuth.getCurrentUser();
                            navigateToMainInterface();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG_EMAIL_LOGIN, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        fbCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void handleFacebookAccessToken(AccessToken accessToken) {
        AuthCredential authCredential = FacebookAuthProvider.getCredential(accessToken.getToken());
        fbAuth.signInWithCredential(authCredential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            FirebaseUser firebaseUser = fbAuth.getCurrentUser();
                            Log.d(TAG_FACEBOOK_LOGIN, "signInWithCredential success");
                            saveUserInfor(firebaseUser);
                            navigateToMainInterface();

                        } else {
                            Log.d(TAG_FACEBOOK_LOGIN, "signInWithCredential failed");
                            saveUserInfor(null);
                        }
                    }
                });

    }

    private void saveUserInfor(FirebaseUser facebookUser) {
        SharedPreferences sharedPreferences = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if(facebookUser != null) {
            String userId = facebookUser.getUid();
            String email = facebookUser.getEmail();
            editor.putString("userId", userId);
            editor.putString("email", email);
            editor.putString("userType", "facebook");
            editor.putInt("isLoggedIn", 1);
        } else {
            editor.putString("userId", "");
            editor.putString("email", "");
            editor.putString("userType", "");
            editor.putInt("isLoggedIn", 0);
        }
        editor.commit();
    }

    private void setupLoginFacebook() {
        fbCallbackManager = CallbackManager.Factory.create();
        btnLoginFacebook.setPermissions(Arrays.asList("email", "public_profile"));
        btnLoginFacebook.registerCallback(fbCallbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    Log.d(TAG_FACEBOOK_LOGIN, "Login success. Result = "+loginResult);
                    handleFacebookAccessToken(loginResult.getAccessToken());
                }

                @Override
                public void onCancel() {
                    Log.d(TAG_FACEBOOK_LOGIN, "Login cancelled");
                    saveUserInfor(null);
                }

                @Override
                public void onError(FacebookException error) {
                    Log.d(TAG_FACEBOOK_LOGIN, "Login failed. Result = "+error);
                    saveUserInfor(null);
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