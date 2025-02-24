import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;

public class Main {

    /* Baca Baris Panjang Board x Jumlah Pieces */
    public static int[] bacaLine (String barisInfo) {
        /* ex. barisInfo : "4 5 6" */
        Scanner scanner = new Scanner(barisInfo);
        int panjang = scanner.nextInt();
        int lebar = scanner.nextInt();
        int jumlahPiece = scanner.nextInt();
        scanner.close();
        int[] retval = {panjang, lebar, jumlahPiece};
        return retval;
    }

    // Konversi string di file menjadi Piece
    public static char[][] convertToPiece(List<String> lines) {
        int maxWidth = 0;
        
        // Menentukan lebar maksimum
        for (String line : lines) {
            maxWidth = Math.max(maxWidth, line.length());
        }
        
        int height = lines.size();
        char[][] pieceArray = new char[height][maxWidth]; 
        
        // Mengisi array dengan karakter yang mempertahankan spasi awal
        for (int i = 0; i < height; i++) {
            String line = lines.get(i);
            for (int j = 0; j < maxWidth; j++) {
                if (j < line.length()) {
                    char currentChar = line.charAt(j);
                // Ubah spasi menjadi titik
                    pieceArray[i][j] = (currentChar == ' ') ? '.' : currentChar;
                } else {
                    pieceArray[i][j] = '.'; // Isi dengan spasi jika kosong
                }
            }
        }
        
        return pieceArray;
    }

    public static void saveSolution(Board board, int ctr, long executeTime, String filename) {
        StringBuilder sb = new StringBuilder();
        sb.append("Solusi ditemukan:\n");
        
        for (int i = 0; i < board.getBaris(); i++) {
            for (int j = 0; j < board.getKolom(); j++) {
                sb.append(board.getUkuran()[i][j]);
            }
            sb.append("\n");
        }
        sb.append("\n");
        sb.append("Jumlah Percobaan = ").append(ctr).append("\n");
        sb.append("Waktu Eksekusi = ").append(executeTime).append(" ms\n");
        
        saveToFile(filename, sb.toString());
    }

    private static void saveToFile(String filename, String content) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Masukkan file path : ");
        String filePath = scanner.nextLine();
        List<Piece> pieces = new ArrayList<>();
        int tinggi = 0, lebar = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            int[] boardInfo = bacaLine(br.readLine());
            tinggi = boardInfo[0];
            lebar = boardInfo[1];

            String line;
            List<String> currentcalonPiece = new ArrayList<>();
            char currentChar = '\0';
            
            br.readLine();

            while ((line = br.readLine()) != null) {
                line = line.replaceAll("\t", "    ");
                if (line.trim().isEmpty()) continue;

                char firstChar = line.trim().charAt(0);
                if (currentChar == '\0' || firstChar == currentChar) {
                    currentcalonPiece.add(line);
                    currentChar = firstChar;
                } else {
                    pieces.add(new Piece(convertToPiece(currentcalonPiece)));
                    currentcalonPiece.clear();
                    currentcalonPiece.add(line);
                    currentChar = firstChar;
                }
            }
            if (!currentcalonPiece.isEmpty()) {
                pieces.add(new Piece(convertToPiece(currentcalonPiece)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Board board = new Board(lebar, tinggi);
        // System.out.println("Ukuran Board: " + tinggi + " x " + lebar);
        // System.out.println("Jumlah Pieces: " + pieces.size());

        // for (int i = 0; i < pieces.size(); i++) {
        //     System.out.println("Piece " + (i + 1) + ":");
        //     pieces.get(i).displayPiece();
        // }

        // is Puzzle Solvable?
        int totalPieceSize = 0;
        System.out.println("tinggi & lebar = " + tinggi + lebar);
        for (Piece piece : pieces) {
            totalPieceSize += Piece.getPieceSize(piece);
            piece.displayPiece();
            if ( piece.getTinggi() > tinggi || piece.getLebar()>lebar) {
                System.out.println("\nNo solution\n");
                return;
            }
        }

        if (totalPieceSize > tinggi*lebar) {
            System.out.println("\ntotalPieceSize = \n" + totalPieceSize);
            System.out.println("\nNo solution\n");
            return;
        }

        // Generate  list of <every piece all combinations>
        List<List<Piece>> allPieceCombinations = new ArrayList<>();
        for (Piece piece : pieces) {
            allPieceCombinations.add(Piece.generateVariations(piece));
        }

        Stack<int[]> posisiPiece = new Stack<>(); // Untuk menyimpan posisi piece terakhir yang ditempatkan di board (berguna saat backtracking)
        int[] indices = new int[pieces.size()];
        int pieceIndex = 0;
        int x = 0, y = 0, rotationIndex = 0;
        int ctr = 0;
        long startTime = System.currentTimeMillis();

        while (pieceIndex >= 0) {
            if (pieceIndex >= pieces.size()) {
                // Jika semua pieces telah ditempatkan, cek apakah solusi valid
                if (board.isEndGame(board)) {
                    long finishTime = System.currentTimeMillis();
                    long executeTime = finishTime - startTime;
                    System.out.println("Solution Found:");
                    board.displayBoard();
                    System.out.println("Jumlah Percobaan = " + ctr);
                    System.out.println("Waktu Eksekusi = " + executeTime + " ms");
                    System.out.println();

                    System.out.println("Apakah anda ingin menyimpan solusi ke txt? (y/n)");

                    String respon = scanner.nextLine().trim().toLowerCase();
                    if (respon.equals("y")) {
                        String outputFilename = filePath.replace(".txt", "_solved.txt");
                        saveSolution(board, ctr, executeTime, outputFilename);
                        System.out.println("Berhasil menyimpan file : " + outputFilename);
                    }
                    scanner.close();
                    break;
                }
                pieceIndex--; // Kembali ke langkah sebelumnya dan lakukan perubahan jika solusi belum ditemukan
                continue;
            }

            boolean placed = false;
            List<Piece> pieceCombination = allPieceCombinations.get(pieceIndex);

            while (rotationIndex < pieceCombination.size()) {
                Piece piece = pieceCombination.get(rotationIndex);

                for (y = 0; y < board.getBaris(); y++) {
                    for (x = 0; x < board.getKolom(); x++) {
                        if (board.canPlaced(board, piece, x, y)) {
                            ctr += 1;
                            board.placePiece(board, piece, x, y);
                            posisiPiece.push(new int[]{pieceIndex, rotationIndex, x, y});
                            // System.out.println("Piece " + pieceIndex + " placed at (" + x + ", " + y + ")");
                            // board.displayBoard();

                            // Lanjut ke piece berikutnya
                            pieceIndex++;
                            rotationIndex = 0; // kembalikan ke state pertama rotasi
                            placed = true;
                            break;
                        } else {
                            ctr += 1;
                        }
                    }
                    if (placed) {
                        break;
                    }
                }
                if (placed) {
                    break;
                }

                rotationIndex++;
            }

            if (!placed) {
                if (!posisiPiece.isEmpty()) {
                    int[] lastPosition = posisiPiece.pop();
                    pieceIndex = lastPosition[0];
                    rotationIndex = lastPosition[1] + 1;
                    x = lastPosition[2];
                    y = lastPosition[3];

                    Piece lastPiece = allPieceCombinations.get(pieceIndex).get(lastPosition[1]);
                    board.removePiece(board, lastPiece, x, y);
                    // System.out.println("Menghapus piece " + pieceIndex + " dari (" + x + ", " + y + ")");

                } else {
                    break; // Keluar jika sudah kembali ke langkah awal
                }
            }
            // if (!board.isEndGame(board)) {
            //     System.out.println("No Solution");
            // }
        }
    } 
}