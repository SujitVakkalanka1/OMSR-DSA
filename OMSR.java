import java.util.*;

/*
 OMSR - Offline Music Search and Retrieval Studio
 Academic DSA Project | Java Console Application
*/

// =============================================================
//  SONG
// =============================================================
class Song {
    static int counter = 1;
    int    id;
    String title, artist, genre;
    int    duration;
    float  rating;
    int    playCount;

    Song(String title, String artist, String genre, int duration, float rating) {
        this.id        = counter++;
        this.title     = title;
        this.artist    = artist;
        this.genre     = genre;
        this.duration  = duration;
        this.rating    = rating;
        this.playCount = 0;
    }

    String dur() {
        return duration / 60 + ":" + String.format("%02d", duration % 60);
    }

    void print(int pos, boolean nowPlaying) {
        String prefix = nowPlaying ? " >> " : "     ";
        String posStr = (pos > 0) ? String.format("%3d.", pos) : "    ";
        System.out.printf("%s%s %-28s %-18s %-10s %-6s Rating:%.1f  Plays:%d%n",
            prefix, posStr, title, artist, genre, dur(), rating, playCount);
    }

    public String toString() {
        return "\"" + title + "\" by " + artist;
    }
}

// =============================================================
//  DOUBLY LINKED LIST NODE
// =============================================================
class DLLNode {
    Song    song;
    DLLNode prev, next;
    DLLNode(Song s) { this.song = s; }
}

// =============================================================
//  PLAYLIST (Doubly Linked List)
// =============================================================
class Playlist {
    DLLNode head, tail, current;
    int     size = 0;
    String  name;

    Playlist(String name) { this.name = name; }

    void add(Song s) {
        DLLNode n = new DLLNode(s);
        if (head == null) { head = tail = n; }
        else { tail.next = n; n.prev = tail; tail = n; }
        if (current == null) current = head;
        size++;
        System.out.println("Added: " + s);
    }

    boolean remove(String title) {
        DLLNode c = head;
        while (c != null) {
            if (c.song.title.equalsIgnoreCase(title)) {
                if (c == current) current = c.next != null ? c.next : c.prev;
                if (c.prev != null) c.prev.next = c.next; else head = c.next;
                if (c.next != null) c.next.prev = c.prev; else tail = c.prev;
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
        System.out.printf("      %-4s %-28s %-18s %-10s %-6s %-10s %s%n",
            "No.", "Title", "Artist", "Genre", "Dur", "Rating", "Plays");
        System.out.println("      " + "-".repeat(80));
    }

    Song[] toArray() {
        Song[] arr = new Song[size];
        DLLNode c = head;
        int i = 0;
        while (c != null) { arr[i++] = c.song; c = c.next; }
        return arr;
    }

    void fromArray(Song[] arr) {
        head = tail = current = null;
        size = 0;
        for (Song s : arr) {
            DLLNode n = new DLLNode(s);
            if (head == null) { head = tail = n; }
            else { tail.next = n; n.prev = tail; tail = n; }
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

// =============================================================
//  SEARCH (Linear + Binary)
// =============================================================
class SearchEngine {

    // Linear Search - works on unsorted list
    static List<Song> linearSearch(Playlist pl, String query, String field) {
        List<Song> results = new ArrayList<>();
        DLLNode c = pl.head;
        int comparisons = 0;
        while (c != null) {
            comparisons++;
            String target = switch (field) {
                case "artist" -> c.song.artist;
                case "genre"  -> c.song.genre;
                default       -> c.song.title;
            };
            if (target.toLowerCase().contains(query.toLowerCase()))
                results.add(c.song);
            c = c.next;
        }
        System.out.println("Comparisons made: " + comparisons + " | Results found: " + results.size());
        return results;
    }

    // Binary Search - requires sorted array
    static Song binarySearch(Song[] sorted, String title) {
        int lo = 0, hi = sorted.length - 1, comparisons = 0;
        while (lo <= hi) {
            comparisons++;
            int mid = (lo + hi) / 2;
            int cmp = sorted[mid].title.compareToIgnoreCase(title);
            if (cmp == 0) {
                System.out.println("Found at position " + (mid + 1) + " in " + comparisons + " comparison(s).");
                return sorted[mid];
            } else if (cmp < 0) lo = mid + 1;
            else                hi = mid - 1;
        }
        System.out.println("Not found after " + comparisons + " comparison(s).");
        return null;
    }
}

// =============================================================
//  SORTING ALGORITHMS
// =============================================================
class Sorter {

    // Bubble Sort - by title A to Z
    static void bubbleSort(Song[] arr) {
        int n = arr.length, swaps = 0;
        for (int i = 0; i < n - 1; i++)
            for (int j = 0; j < n - i - 1; j++)
                if (arr[j].title.compareToIgnoreCase(arr[j+1].title) > 0) {
                    Song t = arr[j]; arr[j] = arr[j+1]; arr[j+1] = t;
                    swaps++;
                }
        System.out.println("Bubble Sort complete. Swaps: " + swaps);
    }

    // Insertion Sort - by artist A to Z
    static void insertionSort(Song[] arr) {
        int shifts = 0;
        for (int i = 1; i < arr.length; i++) {
            Song key = arr[i];
            int j = i - 1;
            while (j >= 0 && arr[j].artist.compareToIgnoreCase(key.artist) > 0) {
                arr[j+1] = arr[j]; j--; shifts++;
            }
            arr[j+1] = key;
        }
        System.out.println("Insertion Sort complete. Shifts: " + shifts);
    }

    // Merge Sort - by rating high to low
    static void mergeSort(Song[] arr, int l, int r) {
        if (l < r) {
            int m = (l + r) / 2;
            mergeSort(arr, l, m);
            mergeSort(arr, m + 1, r);
            merge(arr, l, m, r);
        }
    }

    private static void merge(Song[] arr, int l, int m, int r) {
        int n1 = m - l + 1, n2 = r - m;
        Song[] L = new Song[n1], R = new Song[n2];
        System.arraycopy(arr, l, L, 0, n1);
        System.arraycopy(arr, m + 1, R, 0, n2);
        int i = 0, j = 0, k = l;
        while (i < n1 && j < n2)
            arr[k++] = (L[i].rating >= R[j].rating) ? L[i++] : R[j++];
        while (i < n1) arr[k++] = L[i++];
        while (j < n2) arr[k++] = R[j++];
    }

    // Quick Sort - by play count high to low
    static void quickSort(Song[] arr, int low, int high) {
        if (low < high) {
            int pi = partition(arr, low, high);
            quickSort(arr, low, pi - 1);
            quickSort(arr, pi + 1, high);
        }
    }

    private static int partition(Song[] arr, int low, int high) {
        Song pivot = arr[high];
        int i = low - 1;
        for (int j = low; j < high; j++)
            if (arr[j].playCount >= pivot.playCount) {
                Song t = arr[++i]; arr[i] = arr[j]; arr[j] = t;
            }
        Song t = arr[i+1]; arr[i+1] = arr[high]; arr[high] = t;
        return i + 1;
    }
}

// =============================================================
//  STACK - Playback History
// =============================================================
class HistoryStack {
    private static class Node {
        Song song;
        Node next;
        Node(Song s, Node n) { song = s; next = n; }
    }

    private Node top = null;
    private int  size = 0;

    void push(Song s) {
        top = new Node(s, top);
        size++;
    }

    Song pop() {
        if (isEmpty()) {
            System.out.println("Playback history is empty.");
            return null;
        }
        Song s = top.song;
        top = top.next;
        size--;
        return s;
    }

    boolean isEmpty() { return top == null; }

    void display() {
        System.out.println("\n=== Playback History (" + size + " entries) ===");
        if (isEmpty()) { System.out.println("No history yet."); return; }
        Node c = top;
        int i = 1;
        while (c != null) {
            System.out.println("  " + i++ + ". " + c.song + "  (plays: " + c.song.playCount + ")");
            c = c.next;
        }
        System.out.println("(1 = most recently played)");
    }
}

// =============================================================
//  CIRCULAR QUEUE - Upcoming Songs
// =============================================================
class CircularQueue {
    private final Song[] data;
    private int front, rear, count;
    private final int cap;

    CircularQueue(int cap) {
        this.cap = cap;
        data = new Song[cap];
        front = 0; rear = -1; count = 0;
    }

    boolean enqueue(Song s) {
        if (isFull()) {
            System.out.println("Queue is full. Capacity: " + cap);
            return false;
        }
        rear = (rear + 1) % cap;
        data[rear] = s;
        count++;
        System.out.println("Added to upcoming: " + s);
        return true;
    }

    Song dequeue() {
        if (isEmpty()) {
            System.out.println("Upcoming queue is empty.");
            return null;
        }
        Song s = data[front];
        front = (front + 1) % cap;
        count--;
        return s;
    }

    boolean isEmpty() { return count == 0; }
    boolean isFull()  { return count == cap; }

    void display() {
        System.out.println("\n=== Upcoming Songs (" + count + "/" + cap + " slots used) ===");
        if (isEmpty()) { System.out.println("No upcoming songs."); return; }
        for (int i = 0; i < count; i++) {
            int idx = (front + i) % cap;
            System.out.println("  " + (i + 1) + ". " + data[idx]);
        }
    }
}

// =============================================================
//  HASH TABLE - Separate Chaining
// =============================================================
class HashTable {
    private static final int SIZE = 13;

    private static class HNode {
        String key; Song song; HNode next;
        HNode(String k, Song s) { key = k; song = s; }
    }

    private final HNode[] table = new HNode[SIZE];

    private int hash(String key) {
        int h = 0;
        for (char ch : key.toLowerCase().toCharArray())
            h = (h * 31 + ch) % SIZE;
        return h;
    }

    void insert(Song s) {
        String key = s.title.toLowerCase();
        int idx = hash(key);
        for (HNode c = table[idx]; c != null; c = c.next)
            if (c.key.equals(key)) { c.song = s; return; }
        HNode n = new HNode(key, s);
        n.next = table[idx];
        table[idx] = n;
    }

    Song lookup(String title) {
        String key = title.toLowerCase();
        int idx = hash(key);
        int steps = 0;
        for (HNode c = table[idx]; c != null; c = c.next) {
            steps++;
            if (c.key.equals(key)) {
                System.out.println("Found in " + steps + " step(s).");
                return c.song;
            }
        }
        System.out.println("Not found after " + steps + " step(s).");
        return null;
    }

    void remove(String title) {
        String key = title.toLowerCase();
        int idx = hash(key);
        HNode c = table[idx], prev = null;
        while (c != null) {
            if (c.key.equals(key)) {
                if (prev == null) table[idx] = c.next;
                else              prev.next  = c.next;
                return;
            }
            prev = c; c = c.next;
        }
    }

    void displayTable() {
        System.out.println("\n=== Hash Table (Size: " + SIZE + ") ===");
        for (int i = 0; i < SIZE; i++) {
            System.out.printf("  Bucket[%2d] -> ", i);
            HNode c = table[i];
            if (c == null) { System.out.println("empty"); continue; }
            StringBuilder sb = new StringBuilder();
            while (c != null) {
                sb.append("[").append(c.song.title).append("]");
                if (c.next != null) sb.append(" -> ");
                c = c.next;
            }
            System.out.println(sb);
        }
    }
}

// =============================================================
//  MAX-HEAP - Song Recommendations
// =============================================================
class MaxHeap {
    private final List<Song> heap = new ArrayList<>();

    private float score(Song s) { return s.rating * 2f + s.playCount; }
    private int parent(int i)   { return (i - 1) / 2; }
    private int left  (int i)   { return 2 * i + 1;   }
    private int right (int i)   { return 2 * i + 2;   }

    private void swap(int a, int b) {
        Song t = heap.get(a); heap.set(a, heap.get(b)); heap.set(b, t);
    }

    void insert(Song s) {
        heap.add(s);
        int i = heap.size() - 1;
        while (i > 0 && score(heap.get(i)) > score(heap.get(parent(i)))) {
            swap(i, parent(i));
            i = parent(i);
        }
    }

    Song extractMax() {
        if (heap.isEmpty()) return null;
        Song max  = heap.get(0);
        Song last = heap.remove(heap.size() - 1);
        if (!heap.isEmpty()) { heap.set(0, last); heapifyDown(0); }
        return max;
    }

    private void heapifyDown(int i) {
        int best = i, l = left(i), r = right(i), n = heap.size();
        if (l < n && score(heap.get(l)) > score(heap.get(best))) best = l;
        if (r < n && score(heap.get(r)) > score(heap.get(best))) best = r;
        if (best != i) { swap(i, best); heapifyDown(best); }
    }

    void rebuildFrom(Playlist pl) {
        heap.clear();
        DLLNode c = pl.head;
        while (c != null) { insert(c.song); c = c.next; }
        System.out.println("Recommendations updated. Total songs indexed: " + heap.size());
    }

    void showTop(int top) {
        if (heap.isEmpty()) {
            System.out.println("No recommendations. Please update recommendations first (option 1).");
            return;
        }
        System.out.println("\n=== Top " + top + " Recommended Songs ===");
        System.out.println("  (Score = rating x 2 + play count)");
        System.out.println("  " + "-".repeat(60));

        MaxHeap tmp = new MaxHeap();
        tmp.heap.addAll(new ArrayList<>(heap));

        for (int i = 1; i <= Math.min(top, tmp.heap.size()); i++) {
            Song s = tmp.extractMax();
            if (s == null) break;
            System.out.printf("  %2d. %-28s %-18s Score: %.2f  (Rating:%.1f, Plays:%d)%n",
                i, s.title, s.artist, score(s), s.rating, s.playCount);
        }
    }
}

// =============================================================
//  STACK APPLICATIONS
// =============================================================
class StackApps {

    private static int prec(char op) {
        return switch (op) {
            case '+', '-' -> 1;
            case '*', '/' -> 2;
            case '^'      -> 3;
            default       -> 0;
        };
    }

    private static boolean isOp(char c) { return "+-*/^".indexOf(c) >= 0; }

    // a) Infix to Postfix
    static String infixToPostfix(String expr) {
        StringBuilder out = new StringBuilder();
        Deque<Character> stk = new ArrayDeque<>();

        System.out.println("\n  Step-by-step trace:");
        System.out.printf("  %-10s %-24s %-28s%n", "Token", "Stack", "Output so far");
        System.out.println("  " + "-".repeat(62));

        for (char ch : expr.toCharArray()) {
            if (ch == ' ') continue;
            if (Character.isLetterOrDigit(ch)) {
                out.append(ch).append(' ');
            } else if (ch == '(') {
                stk.push(ch);
            } else if (ch == ')') {
                while (!stk.isEmpty() && stk.peek() != '(')
                    out.append(stk.pop()).append(' ');
                if (!stk.isEmpty()) stk.pop();
            } else if (isOp(ch)) {
                while (!stk.isEmpty() && stk.peek() != '(' && prec(stk.peek()) >= prec(ch))
                    out.append(stk.pop()).append(' ');
                stk.push(ch);
            }
            System.out.printf("  %-10s %-24s %-28s%n", ch, stk, out.toString().trim());
        }
        while (!stk.isEmpty()) out.append(stk.pop()).append(' ');

        String result = out.toString().trim();
        System.out.println("\n  Result (Postfix): " + result);
        return result;
    }

    // b) Postfix Evaluation
    static double evalPostfix(String postfix) {
        Deque<Double> stk = new ArrayDeque<>();

        System.out.println("\n  Step-by-step trace:");
        System.out.printf("  %-16s %-20s%n", "Operation", "Stack");
        System.out.println("  " + "-".repeat(36));

        for (String token : postfix.trim().split("\\s+")) {
            if (token.isEmpty()) continue;
            if (token.length() == 1 && isOp(token.charAt(0))) {
                if (stk.size() < 2) { System.out.println("  Error: invalid expression."); return Double.NaN; }
                char op = token.charAt(0);
                double b = stk.pop(), a = stk.pop();
                double res = switch (op) {
                    case '+' -> a + b;
                    case '-' -> a - b;
                    case '*' -> a * b;
                    case '/' -> b == 0 ? Double.NaN : a / b;
                    case '^' -> Math.pow(a, b);
                    default  -> 0;
                };
                stk.push(res);
                System.out.printf("  %-16s %-20s%n",
                    String.format("%.2f %c %.2f = %.2f", a, op, b, res), stk);
            } else {
                double v = Double.parseDouble(token);
                stk.push(v);
                System.out.printf("  %-16s %-20s%n", "push " + token, stk);
            }
        }

        double result = stk.isEmpty() ? Double.NaN : stk.pop();
        System.out.println("\n  Result: " + result);
        return result;
    }

    // c) Bracket Balancing
    static boolean isBalanced(String expr) {
        Deque<Character> stk = new ArrayDeque<>();

        System.out.println("\n  Step-by-step trace:");
        System.out.printf("  %-8s %-22s %-12s%n", "Char", "Stack", "Action");
        System.out.println("  " + "-".repeat(44));

        for (char ch : expr.toCharArray()) {
            if ("([{".indexOf(ch) >= 0) {
                stk.push(ch);
                System.out.printf("  %-8s %-22s %s%n", ch, stk, "PUSH");
            } else if (")]}".indexOf(ch) >= 0) {
                if (stk.isEmpty()) {
                    System.out.printf("  %-8s %-22s %s%n", ch, "[]", "MISMATCH");
                    System.out.println("\n  Result: NOT balanced - extra closing '" + ch + "'");
                    return false;
                }
                char top = stk.pop();
                boolean match = (ch == ')' && top == '(') ||
                                (ch == ']' && top == '[') ||
                                (ch == '}' && top == '{');
                System.out.printf("  %-8s %-22s %s%n", ch, stk,
                    match ? "POP (" + top + " matched)" : "MISMATCH");
                if (!match) {
                    System.out.println("\n  Result: NOT balanced - '" + top + "' vs '" + ch + "'");
                    return false;
                }
            }
        }

        if (stk.isEmpty()) {
            System.out.println("\n  Result: BALANCED - all symbols matched.");
            return true;
        }
        System.out.println("\n  Result: NOT balanced - unclosed '" + stk.peek() + "'");
        return false;
    }
}

// =============================================================
//  MAIN - Menu-Driven Console Application
// =============================================================
public class OMSR {

    static final Scanner sc = new Scanner(System.in);

    static String input(String prompt) {
        System.out.print(prompt + " ");
        return sc.nextLine().trim();
    }

    static int inputInt(String prompt) {
        while (true) {
            try { return Integer.parseInt(input(prompt)); }
            catch (NumberFormatException e) { System.out.println("Please enter a valid number."); }
        }
    }

    static float inputFloat(String prompt) {
        while (true) {
            try { return Float.parseFloat(input(prompt)); }
            catch (NumberFormatException e) { System.out.println("Please enter a valid decimal number."); }
        }
    }

    // Load sample songs on startup
    static void loadSampleData(Playlist pl, HashTable ht, MaxHeap mh) {
        Object[][] data = {
            {"Blinding Lights",   "The Weeknd",       "Pop",       200, 4.9f, 12},
            {"Shape of You",      "Ed Sheeran",       "Pop",       234, 4.7f,  7},
            {"Bohemian Rhapsody", "Queen",            "Rock",      354, 5.0f, 25},
            {"Levitating",        "Dua Lipa",         "Dance-Pop", 203, 4.5f,  9},
            {"Stay",              "Kid LAROI",        "Pop",       141, 4.3f,  5},
            {"drivers license",   "Olivia Rodrigo",   "Indie Pop", 242, 4.6f, 15},
            {"Peaches",           "Justin Bieber",    "R&B",       198, 4.1f,  3},
            {"good 4 u",          "Olivia Rodrigo",   "Alt Pop",   178, 4.4f, 11},
        };
        for (Object[] d : data) {
            Song s = new Song((String)d[0], (String)d[1], (String)d[2], (int)d[3], (float)d[4]);
            s.playCount = (int) d[5];
            pl.add(s);
            ht.insert(s);
            mh.insert(s);
        }
    }

    // ----------------------------------------------------------
    //  MENU: Playlist
    // ----------------------------------------------------------
    static void menuPlaylist(Playlist pl, HashTable ht, MaxHeap mh) {
        while (true) {
            System.out.println("\n=== Playlist Management ===");
            System.out.println("1. View playlist");
            System.out.println("2. Add song");
            System.out.println("3. Remove song");
            System.out.println("4. Show now-playing");
            System.out.println("0. Back");
            System.out.print("Choose option: ");
            switch (inputInt("")) {
                case 1 -> pl.display();
                case 2 -> {
                    String t = input("Title:");
                    if (t.isEmpty()) { System.out.println("Title cannot be empty."); break; }
                    String a = input("Artist:");
                    String g = input("Genre:");
                    int    d = inputInt("Duration in seconds:");
                    float  r = inputFloat("Rating (1.0 to 5.0):");
                    r = Math.max(1f, Math.min(5f, r));
                    Song s = new Song(t, a, g, d, r);
                    pl.add(s);
                    ht.insert(s);
                    mh.insert(s);
                }
                case 3 -> {
                    String t = input("Enter title to remove:");
                    pl.remove(t);
                    ht.remove(t);
                }
                case 4 -> {
                    if (pl.current != null) {
                        System.out.println("\n--- Now Playing ---");
                        pl.current.song.print(-1, false);
                    } else {
                        System.out.println("Nothing is selected.");
                    }
                }
                case 0 -> { return; }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    // ----------------------------------------------------------
    //  MENU: Search
    // ----------------------------------------------------------
    static void menuSearch(Playlist pl, HashTable ht) {
        while (true) {
            System.out.println("\n=== Search Songs ===");
            System.out.println("1. Search by title");
            System.out.println("2. Search by artist");
            System.out.println("3. Search by genre");
            System.out.println("4. Search by exact title (fast lookup)");
            System.out.println("0. Back");
            System.out.print("Choose option: ");
            int ch = inputInt("");
            if (ch == 0) return;

            if (ch >= 1 && ch <= 3) {
                String[] fields = {"title", "artist", "genre"};
                String q = input("Enter search query:");
                List<Song> res = SearchEngine.linearSearch(pl, q, fields[ch - 1]);
                if (res.isEmpty()) {
                    System.out.println("No results found.");
                } else {
                    System.out.println("\n--- Search Results ---");
                    pl.printTableHeader();
                    for (int i = 0; i < res.size(); i++)
                        res.get(i).print(i + 1, false);
                }
            } else if (ch == 4) {
                String t = input("Enter exact song title:");
                Song found = ht.lookup(t);
                if (found != null) {
                    System.out.println("\n--- Result ---");
                    pl.printTableHeader();
                    found.print(1, false);
                } else {
                    System.out.println("Song not found.");
                }
            } else {
                System.out.println("Invalid option.");
            }
        }
    }

    // ----------------------------------------------------------
    //  MENU: Sort
    // ----------------------------------------------------------
    static void menuSort(Playlist pl) {
        while (true) {
            System.out.println("\n=== Sort Playlist ===");
            System.out.println("1. Sort by title     (A to Z)");
            System.out.println("2. Sort by artist    (A to Z)");
            System.out.println("3. Sort by rating    (high to low)");
            System.out.println("4. Sort by play count (high to low)");
            System.out.println("0. Back");
            System.out.print("Choose option: ");
            int ch = inputInt("");
            if (ch == 0) return;
            if (ch < 1 || ch > 4) { System.out.println("Invalid option."); continue; }

            Song[] arr = pl.toArray();
            long t0 = System.nanoTime();

            switch (ch) {
                case 1 -> Sorter.bubbleSort(arr);
                case 2 -> Sorter.insertionSort(arr);
                case 3 -> { Sorter.mergeSort(arr, 0, arr.length - 1);
                            System.out.println("Merge Sort complete."); }
                case 4 -> { Sorter.quickSort(arr, 0, arr.length - 1);
                            System.out.println("Quick Sort complete."); }
            }

            long elapsed = System.nanoTime() - t0;
            pl.fromArray(arr);
            System.out.println("Time taken: " + elapsed + " ns  |  Songs sorted: " + arr.length);
            pl.display();
        }
    }

    // ----------------------------------------------------------
    //  MENU: Playback
    // ----------------------------------------------------------
    static void menuPlayback(Playlist pl, HistoryStack hist, CircularQueue queue) {
        while (true) {
            System.out.println("\n=== Playback Controls ===");
            System.out.println("1. Play current song");
            System.out.println("2. Next song");
            System.out.println("3. Previous song");
            System.out.println("4. Add song to upcoming queue");
            System.out.println("5. Play next from queue");
            System.out.println("6. View upcoming queue");
            System.out.println("7. View playback history");
            System.out.println("8. Go back in history");
            System.out.println("0. Back");
            System.out.print("Choose option: ");
            switch (inputInt("")) {
                case 1 -> {
                    Song s = pl.playCurrent();
                    if (s != null) { hist.push(s); System.out.println("Playing: " + s); }
                }
                case 2 -> {
                    Song s = pl.playNext();
                    if (s != null) { hist.push(s); System.out.println("Playing: " + s); }
                }
                case 3 -> {
                    Song s = pl.playPrev();
                    if (s != null) { hist.push(s); System.out.println("Playing: " + s); }
                }
                case 4 -> {
                    String t = input("Enter song title to add to queue:");
                    boolean found = false;
                    for (DLLNode c = pl.head; c != null; c = c.next)
                        if (c.song.title.equalsIgnoreCase(t)) { queue.enqueue(c.song); found = true; break; }
                    if (!found) System.out.println("Song not found in playlist.");
                }
                case 5 -> {
                    Song s = queue.dequeue();
                    if (s != null) { s.playCount++; hist.push(s); System.out.println("Playing from queue: " + s); }
                }
                case 6 -> queue.display();
                case 7 -> hist.display();
                case 8 -> {
                    Song s = hist.pop();
                    if (s != null) System.out.println("Returned to: " + s);
                }
                case 0 -> { return; }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    // ==========================================================
    //  MAIN ENTRY POINT
    // ==========================================================
    public static void main(String[] args) {

        // List of all playlists
        List<Playlist> playlists = new ArrayList<>();
        Playlist defaultPl = new Playlist("My Library");
        playlists.add(defaultPl);

        HistoryStack  hist = new HistoryStack();
        CircularQueue que  = new CircularQueue(10);
        HashTable     ht   = new HashTable();
        MaxHeap       mh   = new MaxHeap();

        System.out.println("==============================================");
        System.out.println("  OMSR - Offline Music Search and Retrieval  ");
        System.out.println("==============================================");
        System.out.println("Loading sample songs into My Library...");
        loadSampleData(defaultPl, ht, mh);
        System.out.println("Done. " + defaultPl.size + " songs loaded.");

        // Active playlist index
        int[] activeIdx = {0};

        while (true) {
            Playlist active = playlists.get(activeIdx[0]);
            System.out.println("\n=== Main Menu ===");
            System.out.println("Active playlist: " + active.name + " (" + active.size + " songs)");
            System.out.println("------------------------------------------");
            System.out.println("1. Playlist management");
            System.out.println("2. Search songs");
            System.out.println("3. Sort playlist");
            System.out.println("4. Playback controls");
            System.out.println("5. My playlists (create / delete / switch)");
            System.out.println("0. Exit");
            System.out.print("Choose option: ");
            switch (inputInt("")) {
                case 1 -> menuPlaylist(active, ht, mh);
                case 2 -> menuSearch(active, ht);
                case 3 -> menuSort(active);
                case 4 -> menuPlayback(active, hist, que);
                case 5 -> {
                    // Playlist manager loop
                    while (true) {
                        System.out.println("\n=== My Playlists ===");
                        System.out.println("Available playlists:");
                        for (int i = 0; i < playlists.size(); i++) {
                            String marker = (i == activeIdx[0]) ? " <<" : "";
                            System.out.println("  " + (i + 1) + ". " + playlists.get(i).name
                                + " (" + playlists.get(i).size + " songs)" + marker);
                        }
                        System.out.println("------------------------------------------");
                        System.out.println("1. Create new playlist");
                        System.out.println("2. Delete a playlist");
                        System.out.println("3. Switch active playlist");
                        System.out.println("0. Back");
                        System.out.print("Choose option: ");
                        int ch = inputInt("");

                        if (ch == 0) break;

                        else if (ch == 1) {
                            // Create
                            String name = input("Enter new playlist name:");
                            if (name.isEmpty()) {
                                System.out.println("Name cannot be empty.");
                            } else {
                                boolean exists = false;
                                for (Playlist p : playlists)
                                    if (p.name.equalsIgnoreCase(name)) { exists = true; break; }
                                if (exists) {
                                    System.out.println("A playlist with that name already exists.");
                                } else {
                                    playlists.add(new Playlist(name));
                                    System.out.println("Playlist \"" + name + "\" created.");
                                }
                            }
                        }

                        else if (ch == 2) {
                            // Delete
                            if (playlists.size() == 1) {
                                System.out.println("Cannot delete the only playlist.");
                            } else {
                                System.out.print("Enter playlist number to delete: ");
                                int del = inputInt("") - 1;
                                if (del < 0 || del >= playlists.size()) {
                                    System.out.println("Invalid number.");
                                } else {
                                    String confirm = input("Delete \"" + playlists.get(del).name + "\"? (yes/no):");
                                    if (confirm.equalsIgnoreCase("yes")) {
                                        System.out.println("Deleted: " + playlists.get(del).name);
                                        playlists.remove(del);
                                        // Adjust active index if needed
                                        if (activeIdx[0] >= playlists.size())
                                            activeIdx[0] = playlists.size() - 1;
                                        System.out.println("Active playlist is now: " + playlists.get(activeIdx[0]).name);
                                    } else {
                                        System.out.println("Cancelled.");
                                    }
                                }
                            }
                        }

                        else if (ch == 3) {
                            // Switch
                            System.out.print("Enter playlist number to switch to: ");
                            int sw = inputInt("") - 1;
                            if (sw < 0 || sw >= playlists.size()) {
                                System.out.println("Invalid number.");
                            } else {
                                activeIdx[0] = sw;
                                System.out.println("Switched to: " + playlists.get(activeIdx[0]).name);
                            }
                        }

                        else {
                            System.out.println("Invalid option.");
                        }
                    }
                }
                case 0 -> {
                    System.out.println("Goodbye!");
                    sc.close();
                    System.exit(0);
                }
                default -> System.out.println("Invalid option. Try again.");
            }
        }
    }
}
