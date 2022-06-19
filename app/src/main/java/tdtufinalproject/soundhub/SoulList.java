package tdtufinalproject.soundhub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SoulList extends AppCompatActivity {
    RecyclerView songList;
    private Song song;
    private List<Song> listSong;
    private SongListAdapter songListAdapter;

    public FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soul_list);

        ConstraintLayout constraintLayout = findViewById(R.id.soul_view);
        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2500);
        animationDrawable.setExitFadeDuration(5000);
        animationDrawable.start();

        firebaseFirestore = FirebaseFirestore.getInstance();

        songList = findViewById(R.id.song_list);
        songList.setHasFixedSize(true);
        // Giới hạn item cho mỗi dòng
        songList.setLayoutManager(new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, true));

        listSong = new ArrayList<>();
        songListAdapter = new SongListAdapter(getApplicationContext(), listSong); // input = List-String

        songList.setAdapter(songListAdapter);
        firebaseFirestore.collection("SongList")
                .whereEqualTo("type","Soul")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty()){
                    // Get all documents in collections
                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                    for(DocumentSnapshot d:list){
                        Song tmpSong = d.toObject(Song.class);
                        song = d.toObject(Song.class);
                        song.setId(d.getId());
                        listSong.add(song);
                    }
                }
            }
        }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                songListAdapter.notifyDataSetChanged();
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.soul_bottom);
        bottomNavigationView.setOnItemReselectedListener(new NavigationBarView.OnItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home:
                        toHome();
                }
            }
        });

    }
    public void toHome() {
        Intent intent = new Intent(this, MainInterface.class);
        startActivity(intent);
    }

    public void toUser() {
        Intent intent = new Intent(this, User.class);
        startActivity(intent);
    }
}