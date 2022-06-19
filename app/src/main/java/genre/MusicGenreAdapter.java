package genre;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import tdtufinalproject.soundhub.CountryList;
import tdtufinalproject.soundhub.JazzList;
import tdtufinalproject.soundhub.MainActivity;
import tdtufinalproject.soundhub.PopList;
import tdtufinalproject.soundhub.R;
import tdtufinalproject.soundhub.RapList;
import tdtufinalproject.soundhub.RockList;
import tdtufinalproject.soundhub.SoulList;

public class MusicGenreAdapter extends RecyclerView.Adapter<MusicGenreAdapter.MusicGenreHolder> {
    private List<MusicGenre> mGenres;
    Context context;
    public void setData(List<MusicGenre> list) {
        this.mGenres = list;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public MusicGenreHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.music_genre, parent, false);
        return new MusicGenreHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicGenreHolder holder, int position) {
        MusicGenre musicGenre = mGenres.get(position);
        if(musicGenre == null) {
            return;
        }
        holder.imageView.setImageResource(musicGenre.getResourceId());
        holder.txtviewTitle.setText(musicGenre.getTitle());

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            // move to genre interface
            public void onClick(View view) {
                final Intent intent;
                switch (holder.txtviewTitle.getText().toString()){
                    case "Pop Music":
                        intent = new Intent(context, PopList.class);
                        break;
                    case "Rap Music":
                        intent = new Intent(context, RapList.class);
                        break;
                    case "Jazz Music":
                        intent = new Intent(context, JazzList.class);
                        break;
                    case "Country Music":
                        intent = new Intent(context, CountryList.class);
                        break;
                    case "Rock Music":
                        intent = new Intent(context, RockList.class);
                        break;
                    default:
                        intent = new Intent(context, SoulList.class);
                        break;
                }
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        if(mGenres != null) {
            return mGenres.size();
        }
        return 0;
    }

    public class MusicGenreHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView txtviewTitle;
        public MusicGenreHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imgview_genre);
            txtviewTitle = itemView.findViewById(R.id.txtview_genre);
            context=itemView.getContext();
        }

    }
}


