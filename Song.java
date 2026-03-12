public class Song {
    static int counter = 1;
    int id;
    String title, artist, genre;
    int duration;
    float rating;
    int playCount;

    Song(String title, String artist, String genre, int duration, float rating) {
        this.id = counter++;
        this.title = title;
        this.artist = artist;
        this.genre = genre;
        this.duration = duration;
        this.rating = rating;
        this.playCount = 0;
    }

    String dur() {
        return duration / 60 + ":" + String.format("%02d", duration % 60);
    }

    void print(int pos, boolean nowPlaying) {
        String prefix = nowPlaying ? " >> " : "     ";
        String posStr = (pos > 0) ? String.format("%3d.", pos) : "    ";
        System.out.printf(
            "%s%s %-28s %-18s %-10s %-6s Rating:%.1f  Plays:%d%n",
            prefix,
            posStr,
            title,
            artist,
            genre,
            dur(),
            rating,
            playCount
        );
    }

    public String toString() {
        return "\"" + title + "\" by " + artist;
    }
}

