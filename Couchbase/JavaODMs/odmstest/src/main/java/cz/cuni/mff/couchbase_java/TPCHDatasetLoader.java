package cz.cuni.mff.couchbase_java;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.data.couchbase.core.CouchbaseTemplate;
import org.springframework.data.couchbase.core.ReactiveCouchbaseTemplate;
import reactor.core.publisher.Flux;

import java.io.File;
import java.io.FileReader;
import java.util.List;

public class TPCHDatasetLoader {

    // Java code to illustrate
// Reading CSV File with different separator
    public static List<String[]> readDataFromCustomSeparator(String filePath)
    {
        try {
            // Create an object of file reader class with CSV file as a parameter.
            FileReader filereader = new FileReader(filePath);

            // create csvParser object with
            // custom separator semi-colon
            CSVParser parser = new CSVParserBuilder().withSeparator('|').build();

            // create csvReader object with parameter
            // filereader and parser
            CSVReader csvReader = new CSVReaderBuilder(filereader)
                    .withCSVParser(parser)
                    .build();

            // Read all data at once
            List<String[]> allData = csvReader.readAll();



            return allData;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void printCSV(List<String[]> allData)
    {
        // Print Data.
        for (String[] row : allData) {
            for (String cell : row) {
                System.out.print(cell + "\t");
            }
            System.out.println();
        }
    }

    public static void listFilesForFolder(final File folder) {
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isFile()) {
                System.out.println(fileEntry.getName());
            }
        }
    }

    private static <T> void saveManyDocuments(List<T> documents, CouchbaseTemplate reactiveCouchbaseTemplate) {
        throw new NotImplementedException();
    }

    protected static <T> void saveManyDocuments(List<T> documents, ReactiveCouchbaseTemplate reactiveCouchbaseTemplate){
        // not fast enough
        /*for (T document : documents) {
            reactiveCouchbaseTemplate.save(document);
        }*/

        // parallel
        Flux.fromIterable(documents)
                // add a 0-based index to each element
                .index()
                .flatMap(tuple2 -> {
                    long idx = tuple2.getT1();        // index
                    T document = tuple2.getT2();  // document
                    // log every 10_000 documents
                    if ((idx + 1) % 10_000 == 0) {
                        System.out.println("Inserted " + (idx + 1) + "/" + documents.size() +" documents so far");
                    }
                    return reactiveCouchbaseTemplate.save(document);
                }, 500) // concurrency of 500
                .blockLast(); // wait until all are saved

        System.out.println("Flux saved!");

    }
}
