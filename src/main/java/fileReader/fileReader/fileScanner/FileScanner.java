package fileReader.fileReader.fileScanner;

import fileReader.fileReader.utils.DataStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


public class FileScanner implements Runnable {
    private final char end;
    private final char start;
    private final Path path;
    private StringBuilder fileName = new StringBuilder();
    private File folder = new File("src/results/");
    private File[] listOfFiles = folder.listFiles();
    private BufferedReader bufferReader = null;
    private HashMap<String, Integer> countWords = new HashMap<String, Integer>();
    private Logger log = LoggerFactory.getLogger(this.getClass().getName());

    @Autowired
    DataStorage dataStorage;

    public FileScanner(Path pathToFolder, char start1, char end1) {
        this.path = pathToFolder;
        this.start = start1;
        this.end = end1;
        fileName.append("src/results/filterResults/");
        fileName.append(start).append("-").append(end).append(".txt");
    }
    @Override
    public void run() {
        for (File file : listOfFiles) {
            if (file.isFile()) {
                try {
                    InputStream inputStream = new FileInputStream(file.getPath());
                    InputStreamReader streamReader = new InputStreamReader(inputStream);
                    bufferReader = new BufferedReader(streamReader);
                    String line;
                    while ((line = bufferReader.readLine()) != null) {
                        line = line.replaceAll("[^a-zA-Z'\\s+]", " ");
                        Scanner filInp = new Scanner(line);
                        while (filInp.hasNext()) {
                            try {
                                String nextWord = filInp.next();
                                nextWord = nextWord.toLowerCase();
                                for (char alpha = start; alpha <= end; alpha++) {
                                    if (nextWord.startsWith(String.valueOf(alpha))) {
                                        if (countWords.containsKey(nextWord)) {
                                            countWords.put(nextWord, countWords.get(nextWord) + 1);
                                            DataStorage.setWordMap(nextWord, countWords.get(nextWord));
                                        } else {
                                            countWords.put(nextWord, 1);
                                            DataStorage.setWordMap(nextWord, 1);
                                        }
                                    } else {
                                    }
                                }
                            } catch (Exception e) {
                                log.error(String.valueOf(e));
                            }
                        }
                        filInp.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    DataStorage.setFiles(fileName);
                    try {
                        Files.write(Paths.get(fileName.toString()), () -> countWords.entrySet().stream()
                                .<CharSequence>map(e -> e.getKey() + " : " + e.getValue())
                                .iterator());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        bufferReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
