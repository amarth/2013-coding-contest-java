package ca.kijiji.contest;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class ParkingTicketRecordParser {

    private static final String EMPTY_STRING = "";
    private static final String SPACE_STRING = " ";

    public static final int ADDRESS_INDEX = 1;
    public static final int FINE_INDEX = 0;

    private static final int MIN_STREET_NAME_LENGTH = 3;
    // first 19 symbols of each record doesn't contain information related to the task
    private static final int RECORD_OFFSET = 19;

    private static final Set<String> streetTypes = new HashSet<String>(
            Arrays.asList("AVE", "CRT", "CIR", "TER", "WAY", "BLVD")
    );

    private static final char SEPARATOR = ',';

    private static ParkingTicketRecordParser ourInstance = new ParkingTicketRecordParser();

    public static ParkingTicketRecordParser getInstance() {
        return ourInstance;
    }

    private ParkingTicketRecordParser() {
    }

    public String[] extractFineAddressPair(String record) {

        int fineStartIndex = afterNthOccurrence(record, SEPARATOR, RECORD_OFFSET, 1);
        int fineEndIndex = record.indexOf(SEPARATOR, fineStartIndex);

        int addressStartIndex = afterNthOccurrence(record, SEPARATOR, fineEndIndex, 2);
        int addressEndIndex = record.indexOf(SEPARATOR, addressStartIndex);

        String fine = record.substring(fineStartIndex, fineEndIndex);
        String address = extractStreetName(record.substring(addressStartIndex, addressEndIndex));

        return new String[]{fine, address};
    }

    private int afterNthOccurrence(String str, char charToFind, int start, int occurrence) {
        int pos = str.indexOf(charToFind, start) + 1;
        for (int i = occurrence; i > 0; --i) {
            pos = str.indexOf(charToFind, pos) + 1;
        }
        return pos;
    }

    public String extractStreetName(String address) {

        String streetName = EMPTY_STRING;
        if(!address.isEmpty() && address.charAt(0) >= '0' && address.charAt(0) <= '9') {
            address = address.substring(address.indexOf(' ') + 1).trim();
        }

        if (address.length() > MIN_STREET_NAME_LENGTH) {
            String[] addressParts = address.split(SPACE_STRING);
            StringBuilder streetNameBuilder = new StringBuilder(addressParts[0]);

            for (int i = 1; i < addressParts.length; i++) {
                if (addressParts[i].length() < MIN_STREET_NAME_LENGTH ||
                        streetTypes.contains(addressParts[i])) {

                    break;
                }
                streetNameBuilder.append(SPACE_STRING).append(addressParts[i]);
            }
            streetName = streetNameBuilder.length() >= MIN_STREET_NAME_LENGTH ?
                    streetNameBuilder.toString() :
                    EMPTY_STRING;
        }

        return streetName;
    }
}
