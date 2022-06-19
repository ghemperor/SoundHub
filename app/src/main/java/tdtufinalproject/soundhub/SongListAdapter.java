package tdtufinalproject.soundhub;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class SongListAdapter extends RecyclerView.Adapter<SongListAdapter.Song_Holder>{
    private List<Song> songList;
    private StorageReference songStoragePicture,songStorageMp3;

    private Context context;
    private List<String> listName = new ArrayList<>();

    public SongListAdapter(Context applicationContext, List<Song> songList) {
        this.context = applicationContext;
        this.songList = songList;
    }

    @NonNull
    @Override
    public Song_Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_info, parent, false);
        return new Song_Holder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull Song_Holder holder, @SuppressLint("RecyclerView") int position) {
        Song song = this.songList.get(position);

        holder.songName.setText(song.getSongname());
        holder.Artist.setText(song.getArtist());

        listName.add(song.getSongname());

        String name = song.getSongname().toLowerCase().replaceAll("\\s","");
        String pathImage = "music/"+name;
        String pathMp3 = "music-play/"+name;
        songStoragePicture = FirebaseStorage.getInstance().getReference().child(pathImage+".jpg");

        songStoragePicture.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        //Load image using Picasso
                        Picasso.with(holder.song_Picture.getContext())
                                .load(uri)
                                .fit()
                                .into(holder.song_Picture);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(holder.song_Picture.getContext(),"Add "+name+" image: Failed",Toast.LENGTH_SHORT).show();
                        Log.d("Loading IMG","Add "+name+" image: Failed");
                    }
                });


        songStorageMp3 = FirebaseStorage.getInstance().getReference().child(pathMp3+".mp3");

        songStorageMp3.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Loading Mp3","Add "+name+" mp3: Failed");
                    }
                });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(),PlayMusic.class);
                intent.putExtra("Song name",holder.songName.getText());
                intent.putExtra("List name",(Serializable) listName);
                view.getContext().startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    public List<Song> getSearchList(String searchString) {
        List<Song> tmpList = new ArrayList<>();
        for(int i = 0;i<getItemCount();i++){
            Song tmpSong = songList.get(i);
            if(tmpSong.getSongname().equals(searchString) | tmpSong.getArtist().equals(searchString)){
                tmpList.add(tmpSong);
            }
        }

        return tmpList;
    }

    class Song_Holder extends RecyclerView.ViewHolder{
        private TextView songName,Artist;
        private ImageView song_Picture;

        public Song_Holder(View itemView) {
            super(itemView);
            song_Picture = itemView.findViewById(R.id.imgView_song);
            songName = itemView.findViewById(R.id.song_name);
            Artist = itemView.findViewById(R.id.artist);
        }
    }
}
