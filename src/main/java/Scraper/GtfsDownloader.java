package Scraper;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Gestore del download e della sincronizzazione dei dati GTFS (General Transit Feed Specification).
 * La classe implementa la logica di aggiornamento automatico per i dati orari statici
 * di Roma Mobilità. Utilizza un confronto tra hash MD5 remoto e locale per determinare
 * se è necessario scaricare una nuova versione del dataset, ottimizzando l'uso della banda.
 * * <p>I dati vengono scaricati in formato ZIP, estratti in una directory locale
 * e resi disponibili per il parsing nel database.</p>
 */
public class GtfsDownloader {

    /** URL di base del portale Open Data di Roma Mobilità */
    private static final String BASE_URL = "https://romamobilita.it/sites/default/files/";

    /** Nome dell'archivio compresso contenente i dati statici */
    private static final String ZIP_FILENAME = "rome_static_gtfs.zip";

    /** Nome del file contenente l'impronta (hash) per la verifica della versione */
    private static final String MD5_FILENAME = "rome_static_gtfs.zip.md5";

    /** Percorso della cartella locale destinata allo storage dei dati GTFS */
    public static final String DOWNLOAD_DIR = "./gtfs_data/";

    /** File locale dove viene memorizzata l'ultima versione MD5 scaricata con successo */
    private static final String LOCAL_MD5_FILE = "current_version.md5";

    /**
     * Esegue un controllo comparativo tra la versione del file presente sul server
     * e quella memorizzata localmente.
     * * @return {@code true} se l'MD5 remoto differisce da quello locale (necessario aggiornamento),
     * {@code false} se i dati sono già aggiornati o in caso di errore di connessione.
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

    /**
     * Avvia il processo di aggiornamento se viene rilevata una nuova versione.
     * Coordina la creazione delle directory, il download dello ZIP, l'estrazione
     * e l'aggiornamento del file MD5 locale.
     * * @return {@code true} se il dataset è stato aggiornato correttamente,
     * {@code false} se non era necessario o se si è verificato un errore.
     */
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

    /** Crea la directory di download se non presente nel file system */
    private static void createDirIfNotExists() throws IOException {
        Path path = Path.of(DOWNLOAD_DIR);
        if (!Files.exists(path)) Files.createDirectories(path);
    }

    /** Scarica una risorsa web e la restituisce sotto forma di stringa (metodo di utilità per MD5) */
    private static String downloadString(String urlStr) throws IOException {
        try (Scanner scanner = new Scanner(new URL(urlStr).openStream(), StandardCharsets.UTF_8.toString())) {
            scanner.useDelimiter("\\A");
            return scanner.hasNext() ? scanner.next() : "";
        }
    }

    /** Legge l'ultimo hash MD5 memorizzato nel file di sistema locale */
    private static String readLocalMD5() {
        Path path = Path.of(DOWNLOAD_DIR, LOCAL_MD5_FILE);
        if (!Files.exists(path)) return "";
        try {
            return Files.readString(path).trim();
        } catch (IOException e) {
            return "";
        }
    }

    /** Sovrascrive il file MD5 locale con la nuova firma scaricata */
    private static void writeLocalMD5(String md5) throws IOException {
        Files.writeString(Path.of(DOWNLOAD_DIR, LOCAL_MD5_FILE), md5);
    }

    /** Gestisce il download fisico del file ZIP e la successiva pulizia del file temporaneo */
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

    /**
     * Decomprime l'archivio GTFS nella cartella di destinazione.
     * Implementa un controllo di sicurezza per prevenire attacchi di tipo "Zip Slip".
     * * @param zipFilePath Percorso del file ZIP
     * @param destDir Directory di destinazione
     * @throws IOException In caso di errori di lettura o scrittura
     */
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

    /** Protezione contro l'estrazione di file fuori dalla directory target (Zip Slip Vulnerability) */
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