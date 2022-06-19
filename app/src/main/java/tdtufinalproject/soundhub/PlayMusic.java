package tdtufinalproject.soundhub;

import static tdtufinalproject.soundhub.R.id.btn_loop;
import static tdtufinalproject.soundhub.R.id.imageSong;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PlayMusic extends AppCompatActivity {
    public static final String EXTRA_NAME="song_name";
    private ImageButton btnPlay, btnPre, btnRandom, btnNext, btnLoop;
    private TextView txtName, txtStart, txtEnd;
    private SeekBar seekBarMusic;
    private String name;
    Boolean repeatOne = false;

    public MediaPlayer mdaPlayer;
    int pstion; //position
    ArrayList<File> mySong;
    Handler handler = new Handler();

    private String SongName;
    private List<String> listSong;
    private StorageReference songStoragePicture,songStorageMp3;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_music);

        setBackGround();
        getViewID();

        Intent intent = getIntent();

        if(intent.hasExtra("Song name")){
            SongName = intent.getStringExtra("Song name");
        }

        if(intent.hasExtra("List name")){
            listSong = (List<String>) intent.getSerializableExtra("List name");
        }

        if(!SongName.isEmpty()){
            play(SongName);
        }

        /*if (mdaPlayer != null){
            mdaPlayer.stop();
            mdaPlayer.release();
        }

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        mySong = (ArrayList) bundle.getParcelableArrayList("song");
        String songName = intent.getStringExtra("songname");
        pstion = bundle.getInt("pos", 0);
        txtName.setSelected(true);
        Uri uri = Uri.parse(mySong.get(pstion).toString());
        txtName.setText(mySong.get(pstion).getName());
        mdaPlayer = MediaPlayer.create(getApplicationContext(),uri);*/
    }

    public List<String> notDuplicated(List<String> list){
        List<String> tmpList = new ArrayList<>();
        for(int i = 0;i<list.size();i++){
            String tmpString = list.get(i);
            if(!tmpList.contains(tmpString)){
                tmpList.add(tmpString);
            }
        }

        return tmpList;
    }

    private void turnoffMusicOnPlaying(MediaPlayer tmpPlayer){
        if(tmpPlayer.isPlaying()){
            tmpPlayer.stop();
//            tmpPlayer.release();
                /* pstion =(pstion+1)%mySong.size());
                settingMusic() */
            setTime();
            UpdateSeekbar();
        }
    }

    // Play music End
    private void play(String tmpname){
        SongName = tmpname;
        String name = SongName.toLowerCase().replaceAll("\\s","");
        String pathImage = "music/"+name;
        String pathMp3 = "music-play/"+name;

        playThisMp3(pathImage,pathMp3);
    }

    private void playThisMp3(String img,String mp3){
        TextView tmpName = findViewById(R.id.txt_songName);
        tmpName.setText(SongName);

        btnPlay.setImageResource(R.drawable.ic_play_pause);

        settingImageMusic(img);
        settingMp3(mp3);
    }

    private void settingImageMusic(String img){
        songStoragePicture = FirebaseStorage.getInstance().getReference().child(img+".jpg");
        songStoragePicture.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        //Load image using Picasso
                        Picasso.with(getApplicationContext())
                                .load(uri)
                                .fit()
                                .into((ImageView) findViewById(imageSong));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(holder.song_Picture.getContext(),"Add "+name+" image: Failed",Toast.LENGTH_SHORT).show();
                        Log.d("Loading IMG","Add "+name+" image: Failed");
                    }
                });


    }
    private void settingMp3(String mp3){
        mdaPlayer = new MediaPlayer();
        songStorageMp3 = FirebaseStorage.getInstance().getReference().child(mp3+".mp3");
        songStorageMp3.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        playMusicWithUrl(uri);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Loading Mp3","Add "+name+" mp3: Failed");
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                    }
                });
    }

    private void playMusicWithUrl(Uri uri){
        try {
            mdaPlayer.setDataSource(String.valueOf(uri));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        mdaPlayer.prepareAsync();
        setTime();
        UpdateSeekbar();
        mdaPlayer.start();


        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mdaPlayer.isPlaying()){
                    btnPlay.setImageResource(R.drawable.ic_play_play);
                    mdaPlayer.pause();
                }else{
                    btnPlay.setImageResource(R.drawable.ic_play_pause);
                    mdaPlayer.start();
                    setTime();
                    UpdateSeekbar();
                }
            }
        });

        // Init play
        while (!mdaPlayer.isPlaying()){
            mdaPlayer.start();

            if(repeatOne){
                mdaPlayer.setLooping(false);
                mdaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                    @Override
                    public void onSeekComplete(MediaPlayer mediaPlayer) {
                        mdaPlayer.stop();
                        playNextMp3();
                    }
                });
            }
            else{
                mdaPlayer.setLooping(true);
                mdaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                    @Override
                    public void onSeekComplete(MediaPlayer mediaPlayer) {
                    }
                });
            }

            setTime();
            UpdateSeekbar();
        }

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                turnoffMusicOnPlaying(mdaPlayer);
                playNextMp3();
            }
        });

        btnPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                turnoffMusicOnPlaying(mdaPlayer);
                playPreviousMp3();
            }
        });

        btnLoop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                repeatOne = !repeatOne;
                if(repeatOne){
                    loopOne();
                }else{
                    loopAll();
                }
            }
        });

        seekBarMusic.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mdaPlayer.seekTo(seekBarMusic.getProgress());
            }
        });
    }

    // Play music End

    private void playNextMp3(){
        List<String> tmpList = notDuplicated(listSong);

        for(int i = 0; i < tmpList.size() ; i++){
            String tmpname_now = SongName.trim().toLowerCase();
            String tmpname_pos = tmpList.get(i).trim().toLowerCase();
            if(tmpname_now.equals(tmpname_pos)){
                if(i == tmpList.size()-1){
                    play(tmpList.get(0));
                    break;
                }else{
                    play(tmpList.get(i+1));
                    break;
                }
            }
        }
    }
    private void playPreviousMp3(){
        List<String> tmpList = notDuplicated(listSong);

        for(int i = 0; i < tmpList.size() ; i++){
            String tmpname_now = SongName.trim().toLowerCase();
            String tmpname_pos = tmpList.get(i).trim().toLowerCase();
            if(tmpname_now.equals(tmpname_pos)){
                if(i == 0){
                    play(tmpList.get(tmpList.size()-1));
                    break;
                }else{
                    play(tmpList.get(i-1));
                    break;
                }
            }
        }
    }



    private void setBackGround(){
        LinearLayout linearLayout = findViewById(R.id.root_layout);
        AnimationDrawable animationDrawable;
        animationDrawable = (AnimationDrawable) linearLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2500);
        animationDrawable.setExitFadeDuration(5000);
        animationDrawable.start();
    }

    private void getViewID(){
        btnPlay = (ImageButton) findViewById(R.id.btn_play);
        btnPre = (ImageButton) findViewById(R.id.btn_pre);
        btnRandom = (ImageButton) findViewById(R.id.btn_random);
        btnNext = (ImageButton) findViewById(R.id.btn_next);
        btnLoop = (ImageButton) findViewById(R.id.btn_loop);
        txtName = (TextView) findViewById(R.id.txt_songName);
        txtStart = (TextView) findViewById(R.id.txt_Start);
        txtEnd = (TextView) findViewById(R.id.txt_End);
        seekBarMusic = (SeekBar) findViewById(R.id.seekbar);
    }

    private void settingMusic(){
        Uri uri = Uri.parse(mySong.get(pstion).toString());
        mdaPlayer= MediaPlayer.create(getApplicationContext(),uri);
        txtName.setText(mySong.get(pstion).getName());
        mdaPlayer.start();
        btnPlay.setImageResource(R.drawable.ic_play_pause);
    }
    private void setTime(){
        SimpleDateFormat time_format = new SimpleDateFormat("mm:ss");
        txtEnd.setText(time_format.format(mdaPlayer.getDuration())+"");
        seekBarMusic.setMax(mdaPlayer.getDuration());
    }

    private void UpdateSeekbar() {
        final Handler handler1 = new Handler();
        handler1.postDelayed(new Runnable(){
        @Override
        public void run() {
            SimpleDateFormat time_format = new SimpleDateFormat("mm:ss");
            txtStart.setText(time_format.format(mdaPlayer.getCurrentPosition()));
            seekBarMusic.setProgress(mdaPlayer.getCurrentPosition());
            handler1.postDelayed(this,500);
            }
        },100);
    }

    private void loopOne(){
        btnLoop.setImageResource(R.drawable.ic_play_repeat_on);
        mdaPlayer.setLooping(true);
        mdaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mediaPlayer) {
            }
        });
    }

    private void loopAll(){
        btnLoop.setImageResource(R.drawable.ic_play_repeat_off);
        mdaPlayer.setLooping(false);
        mdaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mediaPlayer) {
                mdaPlayer.stop();
                playNextMp3();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(mdaPlayer.isPlaying()){
            mdaPlayer.stop();
        }
    }
}