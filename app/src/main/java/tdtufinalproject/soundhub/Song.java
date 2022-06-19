package tdtufinalproject.soundhub;

import com.google.firebase.firestore.Exclude;

public class Song {
    @Exclude String id;
    private String songname,artist,type;
    private long listened;

    public Song(){}

    public Song(String songname, String artist, String type, long listened) {
        this.songname = songname;
        this.artist = artist;
        this.type = type;
        this.listened = listened;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSongname() {
        return songname;
    }

    public void setSongname(String songname) {
        this.songname = songname;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getListened() {
        return listened;
    }

    public void setListened(long listened) {
        this.listened = listened;
    }
}
