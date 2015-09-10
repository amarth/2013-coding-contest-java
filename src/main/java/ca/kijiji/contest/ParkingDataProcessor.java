package ca.kijiji.contest;

import com.google.common.collect.ListMultimap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class ParkingDataProcessor {

    private static final int BUFFER_SIZE = 10 * 1024 * 1024;
    private static final int READ_BUFFER_SIZE = 3 * 1024 * 1024;

    private static final int EXECUTOR_WAIT_TIME = 30;

    public static void processParkingTagsData(ListMultimap<String, Integer> fineAccumulator,
                                              InputStream parkingTicketsStream,
                                              ExecutorService executorService) throws IOException, InterruptedException {

        BufferedReader stringReader = new BufferedReader(new InputStreamReader(parkingTicketsStream), BUFFER_SIZE);
        char[] readBuffer = new char[READ_BUFFER_SIZE];

        //Skip first line
        stringReader.readLine();

        //Read block of records
        char[] tail = null;
        char[] prevTail = null;
        int bytesRead = 0;
        int offset = 0;
        while (stringReader.ready()) {
            offset = 0;
            bytesRead = stringReader.read(readBuffer, 0, READ_BUFFER_SIZE - 200);

            while (readBuffer[bytesRead - offset - 1] != '\n')
                offset++;

            if (offset > 0) {
                tail = new char[offset];
                System.arraycopy(readBuffer, bytesRead - offset, tail, 0, offset);
            }

            char[] workingBuffer = new char[READ_BUFFER_SIZE];
            if (prevTail != null && prevTail.length > 0) {
                System.arraycopy(prevTail, 0, workingBuffer, 0, prevTail.length);
                System.arraycopy(readBuffer, 0, workingBuffer, prevTail.length, READ_BUFFER_SIZE - 200);
            } else {
                System.arraycopy(readBuffer, 0, workingBuffer, 0, READ_BUFFER_SIZE - 200);
            }

            //Add task for parser
            executorService.execute(new BlockParserRunnable(workingBuffer, fineAccumulator));

            if (tail != null && tail.length > 0) {
                prevTail = tail;
            }
        }

        executorService.shutdown();
        executorService.awaitTermination(EXECUTOR_WAIT_TIME, TimeUnit.SECONDS);
    }
}