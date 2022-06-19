package tdtufinalproject.soundhub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

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

import category.Category;
import category.CategoryAdapter;
import genre.MusicGenre;

public class MainInterface extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CategoryAdapter categoryAdapter;

    private EditText find;
    RecyclerView songList;
    RecyclerView searchRecycler;


    private Song song;
    private List<Song> listSong;
    private SongListAdapter songListAdapter;
    public FirebaseFirestore firebaseFirestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_interface);
//
//        Intent intent = getIntent();
//        if(intent.hasExtra("linkURL")){
//            String url = intent.getStringExtra("linkURL");
//            Toast.makeText(this,url,Toast.LENGTH_SHORT).show();
//        }


        firebaseFirestore = FirebaseFirestore.getInstance();

        ConstraintLayout constraintLayout = findViewById(R.id.constraint_main_inter);
        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2500);
        animationDrawable.setExitFadeDuration(5000);
        animationDrawable.start();

        find = findViewById(R.id.maininter_ptxt_search);
        searchRecycler = findViewById(R.id.recycler_search);
        searchRecycler.setVisibility(View.INVISIBLE);

        listSong = new ArrayList<>();
        songListAdapter = new SongListAdapter(getApplicationContext(),listSong);


        recyclerView = findViewById(R.id.rcv_category);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this,1, GridLayoutManager.VERTICAL, true));
        categoryAdapter = new CategoryAdapter(this);
        categoryAdapter.setData(getListCategory());
        recyclerView.setAdapter(categoryAdapter);


        songList = findViewById(R.id.song_list);
        songList.setHasFixedSize(true);
        songList.setLayoutManager(new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, true));
        songList.setAdapter(songListAdapter);

        BottomNavigationView bottomNavigationView = findViewById(R.id.signin_bottom);
        bottomNavigationView.setOnItemReselectedListener(new NavigationBarView.OnItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.user:
                        toUser();
                        break;
                }
            }
        });

        firebaseFirestore.collection("SongList")
                .orderBy("listened", Query.Direction.ASCENDING)
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

        find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchRecycler.setVisibility(View.INVISIBLE);
                findProcess();
            }
        });

        constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchRecycler.setVisibility(View.INVISIBLE);
            }
        });
    }


    public void findProcess(){
        find.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keycode, KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)
                {
                    searchRecycler.setHasFixedSize(true);
                    searchRecycler.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1, GridLayoutManager.VERTICAL, true));
                    List<Song> tmplist = new ArrayList<>();
                    SongListAdapter tmpAdapter = new SongListAdapter(getApplicationContext(),tmplist);
                    searchRecycler.setAdapter(tmpAdapter);


                    firebaseFirestore
                            .collection("SongList")
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    if(!queryDocumentSnapshots.isEmpty()){
                                        // Get all documents in collections
                                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                                        for(DocumentSnapshot d:list){
                                            Song tmpSong = d.toObject(Song.class);
                                            String tmp1 = tmpSong.getSongname().toLowerCase().replaceAll("\\s","");
                                            String tmp2 = find.getText().toString().toLowerCase().replaceAll("\\s","");

                                            String tmp3 = tmpSong.getArtist().toLowerCase().replaceAll("\\s","");

                                            if(tmp1.equals(tmp2) | tmp3.contains(tmp2)){
                                                tmplist.add(tmpSong);
                                            }
                                            Log.d("TEST",tmpSong.getSongname());
                                        }
                                        if(tmplist.size()==0){
                                            searchRecycler.setVisibility(View.INVISIBLE);
                                            Toast.makeText(getApplicationContext(),"Cannot find this value. Enter correct song's name",Toast.LENGTH_SHORT).show();
                                        }else{
                                            searchRecycler.setVisibility(View.VISIBLE);
                                        }
                                    }
                                }
                            }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    tmpAdapter.notifyDataSetChanged();
                                }
                            });
                }else{
                    searchRecycler.setVisibility(View.INVISIBLE);
                }
                return false;
            }
        });


    }


    private SongListAdapter getSongListAdapter() {
        return songListAdapter;
    }

    private List<Category> getListCategory() {
        List<Category> list = new ArrayList<>();
        List<MusicGenre> musicGenreList = new ArrayList<>();
        musicGenreList.add(new MusicGenre(R.drawable.pop, "Pop Music"));
        musicGenreList.add(new MusicGenre(R.drawable.rap, "Rap Music"));
        musicGenreList.add(new MusicGenre(R.drawable.jazz, "Jazz Music"));
        musicGenreList.add(new MusicGenre(R.drawable.country, "Country Music"));
        musicGenreList.add(new MusicGenre(R.drawable.rock, "Rock Music"));
        musicGenreList.add(new MusicGenre(R.drawable.soul, "Soul Music"));
        list.add(new Category("GENRE", musicGenreList));
        return list;

    }



    @Override
    public void onBackPressed() {
       return;
    }

    public void toUser() {
        Intent intent = new Intent(this, User.class);
        startActivity(intent);
    }
}