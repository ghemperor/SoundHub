package category;

import genre.MusicGenreAdapter;
import tdtufinalproject.soundhub.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private Context mContext;
    private List<Category> mListCategory;

    public CategoryAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setData(List<Category> list) {
        this.mListCategory = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.music_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = mListCategory.get(position);
        if(category == null) {
            return;
        }
        holder.txtviewNameCategory.setText(category.getNameCategory());
        holder.rcvMusic.setLayoutManager(new GridLayoutManager(mContext,1, GridLayoutManager.HORIZONTAL, false));
        MusicGenreAdapter musicGenreAdapter = new MusicGenreAdapter();
        musicGenreAdapter.setData(category.getGenres());
        holder.rcvMusic.setAdapter(musicGenreAdapter);
    }

    @Override
    public int getItemCount() {
        if(mListCategory != null) {
            return mListCategory.size();
        }
        return 0;
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {
        private TextView txtviewNameCategory;
        private RecyclerView rcvMusic;
        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            txtviewNameCategory  = itemView.findViewById(R.id.txtview_music_category);
            rcvMusic= itemView.findViewById(R.id.rcv_musicgenre);
        }
    }
}
