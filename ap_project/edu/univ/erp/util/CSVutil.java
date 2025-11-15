package edu.univ.erp.util;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;



public class CSVutil {

    // filepath - path of file to save the csv
    // List<String>rows - rows list of rows to write

    public static void writecsv(String filepath, List<String[]> rows) throws IOException{
        try(FileWriter writer=new FileWriter(filepath)){
            for(String[] row:rows){
                writer.write(String.join(",",row));
                writer.write("\n");
            }
        }
    }
}


