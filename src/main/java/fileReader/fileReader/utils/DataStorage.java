package fileReader.fileReader.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataStorage {
    static HashMap<String, Integer> people = new HashMap<String, Integer>();
    static List<String> files = new ArrayList<>();

    public static HashMap<String, Integer> getWordMap()
    {
        return people;
    }
    public static void setWordMap(String word, int count) {
        DataStorage.people.put(word,count);
    }
    public static List<String> getFiles() {
        return files;
    }
    public static void setFiles(StringBuilder files) {
        if (!getFiles().contains(String.valueOf(files)))
        DataStorage.files.add(String.valueOf(files));
    }
}
