package tdtufinalproject.soundhub;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class SongViewHolder {
    ImageView itemImage;
    TextView songName;
    TextView artist;
    SongViewHolder(View v) {
        itemImage = v.findViewById(R.id.imgView_song);
        songName = v.findViewById(R.id.song_name);
        artist = v.findViewById(R.id.artist);
    }
}
