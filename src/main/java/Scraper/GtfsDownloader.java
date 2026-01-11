package Scraper;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class GtfsDownloader {

    private static final String BASE_URL = "https://romamobilita.it/sites/default/files/";
    private static final String ZIP_FILENAME = "rome_static_gtfs.zip";
    private static final String MD5_FILENAME = "rome_static_gtfs.zip.md5";

    // Cartella locale dove salvare i dati
    public static final String DOWNLOAD_DIR = "./gtfs_data/";
    // Nome del file dove salviamo l'ultimo MD5 controllato
    private static final String LOCAL_MD5_FILE = "current_version.md5";

    /**
     * Controlla se ci sono aggiornamenti.
     * @return true se sono stati scaricati nuovi dati, false se siamo già aggiornati.
     */



    public static boolean checkForUpdates() {

        try{


        System.out.println("=== VERIFICA AGGIORNAMENTI GTFS ===");

        // 1. Scarica l'MD5 remoto (la firma del file online)
        String remoteMD5 = downloadString(BASE_URL + MD5_FILENAME).trim();
        System.out.println(">> MD5 Remoto: " + remoteMD5);

        // 2. Leggi l'MD5 locale (la firma dell'ultimo file scaricato)
        String localMD5 = readLocalMD5();
        System.out.println(">> MD5 Locale: " + (localMD5.isEmpty() ? "Nessuno (Primo avvio)" : localMD5));

        // 3. Confronta
        if (remoteMD5.equals(localMD5)) {
            System.out.println(">> I dati sono già aggiornati. Nessun download necessario.");
            return false; // Nessun aggiornamento
        }
        return true;
        }
        catch (Exception e){
            return false;
        }
    }


    public static boolean downloadIfNew() {
        try {
            createDirIfNotExists();

            boolean newUpdate = checkForUpdates();
            if(!newUpdate) return false;

            // 4. Se diverso, scarica il file ZIP
            System.out.println(">> Trovata nuova versione! Avvio download ZIP...");
            downloadZipAndExtract();

            // 5. Salva il nuovo MD5 come riferimento per la prossima volta
            String remoteMD5 = downloadString(BASE_URL + MD5_FILENAME).trim();
            writeLocalMD5(remoteMD5);

            System.out.println("=== DOWNLOAD E ESTRAZIONE COMPLETATI ===");
            return true; // Abbiamo aggiornato

        } catch (IOException e) {
            System.err.println("Errore durante il controllo aggiornamenti: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private static void createDirIfNotExists() throws IOException {
        Path path = Path.of(DOWNLOAD_DIR);
        if (!Files.exists(path)) Files.createDirectories(path);
    }

    // Scarica il contenuto di un URL come stringa (per il file .md5)
    private static String downloadString(String urlStr) throws IOException {
        try (Scanner scanner = new Scanner(new URL(urlStr).openStream(), StandardCharsets.UTF_8.toString())) {
            scanner.useDelimiter("\\A");
            return scanner.hasNext() ? scanner.next() : "";
        }
    }

    private static String readLocalMD5() {
        Path path = Path.of(DOWNLOAD_DIR, LOCAL_MD5_FILE);
        if (!Files.exists(path)) return "";
        try {
            return Files.readString(path).trim();
        } catch (IOException e) {
            return "";
        }
    }

    private static void writeLocalMD5(String md5) throws IOException {
        Files.writeString(Path.of(DOWNLOAD_DIR, LOCAL_MD5_FILE), md5);
    }

    private static void downloadZipAndExtract() throws IOException {
        Path zipPath = Path.of(DOWNLOAD_DIR, ZIP_FILENAME);

        // Scarica ZIP
        try (InputStream in = new URL(BASE_URL + ZIP_FILENAME).openStream()) {
            Files.copy(in, zipPath, StandardCopyOption.REPLACE_EXISTING);
        }

        // Estrai
        unzip(zipPath.toString(), DOWNLOAD_DIR);

        Files.delete(zipPath);
    }

    private static void unzip(String zipFilePath, String destDir) throws IOException {
        File dir = new File(destDir);
        byte[] buffer = new byte[1024];
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFilePath))) {
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                File newFile = newFile(dir, zipEntry);
                if (zipEntry.isDirectory()) {
                    if (!newFile.isDirectory() && !newFile.mkdirs()) throw new IOException("Failed to create dir " + newFile);
                } else {
                    File parent = newFile.getParentFile();
                    if (!parent.isDirectory() && !parent.mkdirs()) throw new IOException("Failed to create dir " + parent);
                    try (FileOutputStream fos = new FileOutputStream(newFile)) {
                        int len;
                        while ((len = zis.read(buffer)) > 0) fos.write(buffer, 0, len);
                    }
                }
                zipEntry = zis.getNextEntry();
            }
        }
    }

    private static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());
        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();
        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }
        return destFile;
    }
}