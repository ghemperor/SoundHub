package category;

import java.util.List;

import genre.MusicGenre;

public class Category {
    private String nameCategory;
    private List<MusicGenre> genres;

    public Category(String nameCategory, List<MusicGenre> genres) {
        this.nameCategory = nameCategory;
        this.genres = genres;
    }

    public String getNameCategory() {
        return nameCategory;
    }

    public void setNameCategory(String nameCategory) {
        this.nameCategory = nameCategory;
    }

    public List<MusicGenre> getGenres() {
        return genres;
    }

    public void setGenres(List<MusicGenre> genres) {
        this.genres = genres;
    }
}
