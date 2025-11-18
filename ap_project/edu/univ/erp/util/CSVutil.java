package edu.univ.erp.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CSVutil {

    public static boolean writecsv(File file, List<String[]> rows, String[] header) {
        try (FileWriter writer = new FileWriter(file)) {

            // write header
            writer.write(String.join(",", header));
            writer.write("\n");

            // write rows
            for (String[] row : rows) {
                writer.write(String.join(",", row));
                writer.write("\n");
            }

            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
