package tdtufinalproject.soundhub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class User extends AppCompatActivity {
    private Button btnLogOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        btnLogOut=findViewById(R.id.btn_Logout);

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                toHome();
            }
        });
        BottomNavigationView bottomNavigationView = findViewById(R.id.user_bottom);
        bottomNavigationView.setOnItemReselectedListener(new NavigationBarView.OnItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home:
                        toHome();
                        break;
                }
            }
        });

    }
    @Override
    protected void onStart(){
        super.onStart();
        FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
        if (fbUser == null) {
            Intent intent = new Intent(User.this, LoginActivity.class);
            startActivity(intent);
        }
    }

    public void toHome() {
        Intent intent = new Intent(this, MainInterface.class);
        startActivity(intent);
    }



}