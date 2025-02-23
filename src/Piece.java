import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Piece {
    private char[][] bentuk;
    private int tinggi;
    private int lebar;

    // Constructor
    public Piece(char[][] bentuk) {
        int tinggi = bentuk.length;
        int lebar = bentuk[0].length;
        this.bentuk = new char[tinggi][lebar];

        for (int i = 0; i < tinggi; i++) {
            System.arraycopy(bentuk[i], 0, this.bentuk[i], 0, lebar);
        }
    }

    public int getTinggi() {
        return bentuk.length;
    }
    public int getLebar() {
        return bentuk[0].length;
    }
    public char[][] getBentuk() {
        return bentuk;
    }

    public static Piece mirror(Piece piece) {
        int jmlKolom = piece.bentuk[0].length;
        int jmlBaris = piece.bentuk.length;
        char[][] mirroredPiece = new char[jmlBaris][jmlKolom];
        for (int i=0; i < jmlKolom; i++) {
            for (int j=0; j < jmlBaris; j++) {
                mirroredPiece[j][jmlKolom-1-i] = piece.bentuk[j][i];
            };
        };
        return new Piece(mirroredPiece);
        }
    
    public static Piece rotate(Piece piece) {
        //rotasi 90 derajat searah jarum jam
        int jmlBaris = piece.bentuk.length;
        int jmlKolom = piece.bentuk[0].length;
        char[][] rotatedPiece = new char[jmlKolom][jmlBaris];
        for (int i = 0; i < jmlBaris; i++) {
            for (int j = 0; j < jmlKolom; j++) {
                rotatedPiece[j][jmlBaris -1 -i] = piece.bentuk[i][j];
            }; 
        };
        return new Piece(rotatedPiece);
    }

    public void displayPiece() {
        for (char[] baris : this.bentuk) {
            for (char karakter : baris) {
                System.out.print(karakter + "");
            }
            System.out.println();
        }
        System.out.println();
        }   

    public static List<Piece> generateVariations(Piece piece) {
        List<Piece> variations = new ArrayList<>();

        Piece current = piece;
        for (int i = 0; i < 4; i++) { // 4 Rotasi: 0°, 90°, 180°, 270°
            variations.add(current);
            current = Piece.rotate(current);
        }

        // Tambahkan versi mirrored (dan rotasi mirrored)
        Piece mirrored = Piece.mirror(piece);
        current = mirrored;
        for (int i = 0; i < 4; i++) {
            String hash = Arrays.deepToString(current.bentuk);
            variations.add(current);
            current = Piece.rotate(current);
        }
        return variations;
    }

    public static int getPieceSize(Piece piece) {
        int size = 0;
        for (int i = 0; i < piece.getTinggi(); i++) {
            for (int j = 0; j < piece.getLebar(); j++) {
                if (piece.bentuk[i][j] != '.') {
                    size++;
                }
            }
        }
        return size;
    }
    
    public static void printMatrixAsArray(char[][] matrix) {
        System.out.println("{");
        for (int i = 0; i < matrix.length; i++) {
            System.out.print("    {");
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.print("'" + matrix[i][j] + "'");
                if (j < matrix[i].length - 1) {
                    System.out.print(", ");
                }
            }
            System.out.print("}");
            if (i < matrix.length - 1) {
                System.out.print(",");
            }
            System.out.println();
        }
        System.out.println("}");
    }
    
    public static void main(String[] args) {
        char[][] bentukAsli = {
            {'A', ' ', 'A'},
            {'A', 'A', 'A'},
            {' ', 'A', ' '}
        };

        Piece piece = new Piece(bentukAsli);

        // Tampilkan bentuk asli
        System.out.println("Bentuk Asli:");
        piece.displayPiece();

        // Rotasi 90 derajat
        Piece rotatedPiece = Piece.rotate(piece);
        System.out.println("Bentuk Setelah Rotasi:");
        rotatedPiece.displayPiece();

        Piece mirroredPiece = Piece.mirror(rotatedPiece);
        System.err.println("Bentuk setelah mirror");
        mirroredPiece.displayPiece();
    }
}