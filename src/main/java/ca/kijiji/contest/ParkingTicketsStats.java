package ca.kijiji.contest;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.*;

import static com.google.common.collect.Multimaps.synchronizedListMultimap;

public class ParkingTicketsStats {

    private static final Logger LOG = LoggerFactory.getLogger(ParkingTicketsStats.class);
    private static final int PARSER_THREADS_COUNT = Math.max(1, Runtime.getRuntime().availableProcessors()-1);

    public static SortedMap<String, Integer> sortStreetsByProfitability(InputStream parkingTicketsStream) {
        //Multimap to collect all fines related to street
        ListMultimap<String, Integer> fineMultimap = ArrayListMultimap.create();
        ListMultimap<String, Integer> fineAccumulator = synchronizedListMultimap(fineMultimap);

        ExecutorService executorService = Executors.newFixedThreadPool(PARSER_THREADS_COUNT);

        //Parse tickets info and accumulate to fineAccumulator
        try {
            ParkingDataProcessor.processParkingTagsData(fineAccumulator, parkingTicketsStream, executorService);
        } catch (IOException e) {
            LOG.error(e.getMessage());
        } catch (InterruptedException e) {
            LOG.error(e.getMessage());
        }

        //Calculate fine for each street and put into map, sorted by value
        MapValueComparator<String, Integer> valueComparator = new MapValueComparator<String, Integer>();
        SortedMap<String, Integer> sortedResult = new ValueSortedMap<String, Integer>(valueComparator);

        for (String key : fineAccumulator.keySet()) {
            Iterable<Integer> values = fineAccumulator.get(key);
            int sum = 0;
            for (Integer val : values) {
                sum += val;
            }
            sortedResult.put(key, sum);
        }

        return sortedResult;
    }
}