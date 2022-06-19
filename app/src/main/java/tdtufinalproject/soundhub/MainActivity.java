package tdtufinalproject.soundhub;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.security.MessageDigest;

public class MainActivity extends AppCompatActivity {
    private Button btnStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);

        btnStart = findViewById(R.id.btn_started);


        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMainInterface();
            }
        });
    }
    public void openMainInterface() {
        Intent moveToMaininter = new Intent(this, MainInterface.class);
        startActivity(moveToMaininter);
    }
}