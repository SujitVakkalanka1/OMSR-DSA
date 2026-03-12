
class DLLNode {
    Song song;
    DLLNode prev, next;

    DLLNode(Song s) {
        this.song = s;
    }
}




public class Playlist {
    DLLNode head, tail, current;
    int size = 0;
    String name;

    Playlist(String name) {
        this.name = name;
    }

    void add(Song s) {
        DLLNode n = new DLLNode(s);
        if (head == null) {
            head = tail = n;
        } else {
            tail.next = n;
            n.prev = tail;
            tail = n;
        }
        if (current == null) current = head;
        size++;
        System.out.println("Added: " + s);
    }

    boolean remove(String title) {
        DLLNode c = head;
        while (c != null) {
            if (c.song.title.equalsIgnoreCase(title)) {
                if (c == current) current = c.next != null ? c.next : c.prev;
                if (c.prev != null) c.prev.next = c.next;
                else head = c.next;
                if (c.next != null) c.next.prev = c.prev;
                else tail = c.prev;
                size--;
                System.out.println("Removed: \"" + title + "\"");
                return true;
            }
            c = c.next;
        }
        System.out.println("Song not found: \"" + title + "\"");
        return false;
    }

    void display() {
        if (head == null) {
            System.out.println("Playlist is empty.");
            return;
        }
        System.out.println("\n=== Playlist: " + name + " (" + size + " songs) ===");
        printTableHeader();
        DLLNode c = head;
        int i = 1;
        while (c != null) {
            c.song.print(i++, c == current);
            c = c.next;
        }
        System.out.println("(>>) = Now Playing");
    }

    void printTableHeader() {
        System.out.printf(
            "      %-4s %-28s %-18s %-10s %-6s %-10s %s%n",
            "No.",
            "Title",
            "Artist",
            "Genre",
            "Dur",
            "Rating",
            "Plays"
        );
        System.out.println("      " + "-".repeat(80));
    }

    Song[] toArray() {
        Song[] arr = new Song[size];
        DLLNode c = head;
        int i = 0;
        while (c != null) {
            arr[i++] = c.song;
            c = c.next;
        }
        return arr;
    }

    void fromArray(Song[] arr) {
        head = tail = current = null;
        size = 0;
        for (Song s : arr) {
            DLLNode n = new DLLNode(s);
            if (head == null) {
                head = tail = n;
            } else {
                tail.next = n;
                n.prev = tail;
                tail = n;
            }
            size++;
        }
        current = head;
    }

    Song playNext() {
        if (current == null || current.next == null) {
            System.out.println("Already at the last song.");
            return null;
        }
        current = current.next;
        current.song.playCount++;
        return current.song;
    }

    Song playPrev() {
        if (current == null || current.prev == null) {
            System.out.println("Already at the first song.");
            return null;
        }
        current = current.prev;
        current.song.playCount++;
        return current.song;
    }

    Song playCurrent() {
        if (current == null) {
            System.out.println("Playlist is empty.");
            return null;
        }
        current.song.playCount++;
        return current.song;
    }
}

