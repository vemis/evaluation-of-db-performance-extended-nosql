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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;

public class TPCHDatasetLoader {

    public static Object[] createLineitemsTags(String[] lineitemsRow) {
        return new Object[]{
                Integer.parseInt(lineitemsRow[0]),
                Integer.parseInt(lineitemsRow[1]),
                Integer.parseInt(lineitemsRow[2]),
                Integer.parseInt(lineitemsRow[3]),
                Integer.parseInt(lineitemsRow[4]),
                Double.parseDouble(lineitemsRow[5]),
                Double.parseDouble(lineitemsRow[6]),
                Double.parseDouble(lineitemsRow[7]),
                lineitemsRow[8],
                lineitemsRow[9],
                LocalDate.parse(lineitemsRow[10]),
                LocalDate.parse(lineitemsRow[11]),
                LocalDate.parse(lineitemsRow[12]),
                lineitemsRow[13],
                lineitemsRow[14],
                lineitemsRow[15]
        };
    }

    /**
     * The Random() instance is created every time, because it can be used to generate the same
     * output across every dataset.
     */
    public static Object[] shuffleArrayItemsAndLenght(Object[] tags, long SHUFFLE_SEED) {
        Random RANDOM = new Random(SHUFFLE_SEED);
        List<Object> list = new ArrayList<>(Arrays.asList(tags));
        Collections.shuffle(list, RANDOM);
        int size = 1 + RANDOM.nextInt(list.size()); // 1 to 16
        return list.subList(0, size).toArray();
    }

    public static Object[] getShuffledLineitemsTagsFromRow(String[] lineitemsRow, long SHUFFLE_SEED) {
        return shuffleArrayItemsAndLenght(createLineitemsTags(lineitemsRow), SHUFFLE_SEED);
    }

    public static <T> Map<Integer, List<T>> groupListsByKey(List<T> items, Function<T, Integer> keyExtractor) {
        Map<Integer, List<T>> map = new HashMap<>();
        for (T item : items) {
            map.computeIfAbsent(keyExtractor.apply(item), k -> new ArrayList<>()).add(item);
        }
        return map;
    }

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

    @SuppressWarnings("unchecked")
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
                    return reactiveCouchbaseTemplate.upsertById((Class<T>) document.getClass()).one(document);
                }, 200) // concurrency of 500
                .blockLast(); // wait until all are saved

        System.out.println("Flux saved!");

    }

    protected static <T> List<List<T>> partition(List<T> list, int batchSize) {
        List<List<T>> batches = new ArrayList<>();

        for (int i = 0; i < list.size(); i += batchSize) {
            batches.add(list.subList(i, Math.min(i + batchSize, list.size())));
        }

        return batches;
    }
}
