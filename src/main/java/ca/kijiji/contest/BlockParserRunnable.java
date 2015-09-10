package ca.kijiji.contest;

import com.google.common.collect.ListMultimap;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class BlockParserRunnable implements Runnable {

    //Parser for individual records
    private static ParkingTicketRecordParser recordParser = ParkingTicketRecordParser.getInstance();

    //Class to use in map to avoid creation of Integer instances for each int value
    static private class MutableInt {
        public int value;

        public MutableInt(int val) {
            this.value = val;
        }
    }

    private final char[] fineInfoBlock;
    private final ListMultimap<String, Integer> fineAccumulator;

    public BlockParserRunnable(char[] fineInfoBlock, ListMultimap<String, Integer> fineAccumulator) {
        this.fineInfoBlock = fineInfoBlock;
        this.fineAccumulator = fineAccumulator;
    }

    private int skipToEndOfLine(int startIndex) {
        int endIndex = startIndex;
        while (endIndex < fineInfoBlock.length && fineInfoBlock[endIndex] != '\n')
            endIndex++;

        return endIndex;
    }

    @Override
    public void run() {
        String[] fineInfo;
        int fine;
        String street;
        Map<String, MutableInt> taskMap;

        if(fineInfoBlock != null) {
            //Map to accumulate fines for each street in a data block
            taskMap = new HashMap<String, MutableInt>();
            int startIndex = 0;
            int endIndex = skipToEndOfLine(startIndex);

            while (endIndex < fineInfoBlock.length) {

                //Get one record and parse it
                String fineRecord = new String(Arrays.copyOfRange(fineInfoBlock, startIndex, endIndex));
                startIndex = endIndex + 1;
                endIndex = skipToEndOfLine(startIndex);

                fineInfo = recordParser.extractFineAddressPair(fineRecord);

                street = fineInfo[ParkingTicketRecordParser.ADDRESS_INDEX];
                try {
                    fine = Integer.parseInt(fineInfo[ParkingTicketRecordParser.FINE_INDEX]);
                } catch (Exception e) {
                    fine = 0;
                }

                //Add new fine to the map
                if(taskMap.containsKey(street)) {
                    taskMap.get(street).value += fine;
                } else {
                    taskMap.put(street, new MutableInt(fine));
                }
            }

            //When whole block is parsed, add result to accumulator
            for (String key : taskMap.keySet()) {
                fineAccumulator.put(key, taskMap.get(key).value);
            }
        }
    }
}
