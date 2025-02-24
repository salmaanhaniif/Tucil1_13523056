import java.util.HashMap;
import java.util.Map;

public class Board {

    private int baris;
    private int kolom;
    private char[][] papan;

    // Constructor
    public Board(int kolom, int baris) {
        this.kolom = kolom;
        this.baris = baris;
        this.papan = new char[baris][kolom];
        for (int i = 0; i < baris; i++) {
            for (int j = 0; j < kolom; j++) {
                papan[i][j] = '.';
            }
        }
    }

    public int getBaris() {
        return baris;
    }
    public int getKolom() {
        return kolom;
    }

    public char[][] getUkuran() {
        return papan;
    }

    public char getCell(int row, int col) {
        return papan[row][col];
    }

    // Color
    private static final String RESET = "\u001B[0m";
    public static final Map<Character, String> COLOR_MAP = new HashMap<>();

    static {
        String[] colors = {
            "\u001B[31m", "\u001B[32m", "\u001B[33m", "\u001B[34m", "\u001B[35m",
            "\u001B[36m", "\u001B[91m", "\u001B[92m", "\u001B[93m", "\u001B[94m",
            "\u001B[95m", "\u001B[96m", "\u001B[97m", "\u001B[90m", "\u001B[41m",
            "\u001B[42m", "\u001B[43m", "\u001B[44m", "\u001B[45m", "\u001B[46m",
            "\u001B[100m", "\u001B[101m", "\u001B[102m", "\u001B[103m", "\u001B[104m",
            "\u001B[105m"
        };
        
        char letter = 'A';
        for (int i = 0; i < 26; i++) {
            COLOR_MAP.put(letter, colors[i]);
            COLOR_MAP.put(Character.toLowerCase(letter), colors[i]);
            letter++;
        }
    }

    public void displayBoard() {
        for (int i = 0; i < baris; i++) {
            for (int j = 0; j < kolom; j++) {
                char c = papan[i][j];
                String color = COLOR_MAP.getOrDefault(c, "\u001B[0m");
                System.out.print(color + c + RESET);
            }
            System.out.println();
        }
        System.out.println();
    }

    public boolean canPlaced(Board board, Piece piece, int x, int y) {
        char[][] bentuk = piece.getBentuk();
        int tinggiPiece = piece.getTinggi();
        int lebarPiece = piece.getLebar();

        if (x < 0 || y < 0 || x + lebarPiece > board.getKolom() 
        || y + tinggiPiece > board.getBaris()) {
            return false;
        }

        for (int i = 0; i < tinggiPiece; i++) {
            for (int j = 0; j < lebarPiece; j++) {
                if (bentuk[i][j] != '.' && board.papan[y + i][x + j] != '.') {
                    return false;
                }
            }
        }

        return true;
    }

    public void placePiece(Board board, Piece piece, int x, int y) {
        if (!canPlaced(board, piece, x, y)) {
            return; // Tidak bisa ditempatkan
        }

        char[][] bentuk = piece.getBentuk();
        int tinggi = piece.getTinggi();
        int lebar = piece.getLebar();

        // Menempatkan karakter di papan
        for (int i = 0; i < tinggi; i++) {
            for (int j = 0; j < lebar; j++) {
                if (bentuk[i][j] != '.') {
                    int newY = y + i; // Baris
                    int newX = x + j; // Kolom
                    if (newY >= 0 && newY < board.getBaris() && 
                    newX >= 0 && newX < board.getKolom()) {
                        board.papan[newY][newX] = bentuk[i][j];
                    }
                }
            }
        }
    }

    public boolean isEndGame (Board board) {
        boolean endGame = true;
        for (int i = 0; i < baris; i++) {
            for (int j = 0; j < kolom; j++) {
                if (papan[i][j] == '.') {
                    return false;
                }
            }
        }
        return true;
    }

    public void removePiece(Board board, Piece piece, int x, int y) {
        char[][] bentuk = piece.getBentuk();
        int tinggi = piece.getTinggi();
        int lebar = piece.getLebar();

        // Menghapus karakter dari papan
        for (int i = 0; i < tinggi; i++) {
            for (int j = 0; j < lebar; j++) {
                if (bentuk[i][j] != '.') {
                    int newY = y + i; // Baris
                    int newX = x + j; // Kolom
                    if (newY >= 0 && newY < board.getBaris() && newX >= 0 
                    && newX < board.getKolom()) {
                        board.papan[newY][newX] = '.'; // Kosongkan posisi
                    }
                }
            }
        }
    }
}