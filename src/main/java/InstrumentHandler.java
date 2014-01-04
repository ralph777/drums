import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by rkoch on 29.12.13.
 */
public class InstrumentHandler {

    int instrumentCount = 4; //TODO
    int taktLength = 16; //TODO
    int instrumentNamesCol = 2; //TODO // 2 oder 38
    int taktRow = 6; //TODO
    int rowOffset = 8; //TODO
    int colOffset = 4; //TODO

    public ArrayList<String> getInstrumentNames(boolean isDefault) {
        ArrayList<String> instrumentNames = new ArrayList<String>();
        if (isDefault) {
            PropertyFinder propertyFinder = new PropertyFinder();
            ArrayList<String> propertyKeys = new ArrayList<String>();
            propertyKeys.add("instrumentNames");

            HashMap propertyValues = propertyFinder.getProperty("src/main/resources/default.properties", propertyKeys);

            String defaultValueNames = (String) propertyValues.get("instrumentNames");
            String[] splitResultNames = defaultValueNames.split(";");
            for(String value : splitResultNames) {

                instrumentNames.add(value);
            }
        }else {
            SpreadsheetExample spreadsheetExample = new SpreadsheetExample();

            spreadsheetExample.connect();

            for (int i = 0; i < instrumentCount+1; i++) {
                if (i == 0) {
                    instrumentNames.add("Takt");
                }else {
                    instrumentNames.add(spreadsheetExample.loadExampleDataCell(instrumentNamesCol,i+rowOffset-1));
                }
            }
        }
        return instrumentNames;
    }

    public ArrayList<Integer> getInstrumentNumbers(ArrayList<String> instrumentNames) {
        HashMap<String, Integer> instrumentNamesNumbersMap = new HashMap<String, Integer>();
        ArrayList<Integer> instrumentNumbers = new ArrayList<Integer>();

        instrumentNamesNumbersMap.put("Bass",35);
        instrumentNamesNumbersMap.put("Hihat",42);
        instrumentNamesNumbersMap.put("Hihat-open",46);
        instrumentNamesNumbersMap.put("Snare",38);
        instrumentNamesNumbersMap.put("Crash",49);
        instrumentNamesNumbersMap.put("Takt",49);

        for (int i = 0; i < instrumentNames.size()-1; i++) { // i = 1, because 0 is 'Takt' and this is not an instrument.
            instrumentNumbers.add(instrumentNamesNumbersMap.get(instrumentNames.get(i + 1)));
        }
        return instrumentNumbers;
    }

    public ArrayList<String> getTakt(boolean isDefault) {
        ArrayList<String> takt = new ArrayList<String>();
        if(isDefault) {
            PropertyFinder propertyFinder = new PropertyFinder();
            ArrayList<String> propertyKeys = new ArrayList<String>();
            propertyKeys.add("takt");
            HashMap propertyValues = propertyFinder.getProperty("src/main/resources/default.properties", propertyKeys);

            String defaultValueTakt = (String) propertyValues.get("takt");
            String[] splitResultTakt = defaultValueTakt.split(";");
            for(String value : splitResultTakt) {
                takt.add(value);
            }
        }else {
            SpreadsheetExample spreadsheetExample = new SpreadsheetExample();

            spreadsheetExample.connect();

            for(int i = 0; i < taktLength; i++) {
                takt.add(spreadsheetExample.loadExampleDataCell(colOffset+i,taktRow));
            }
        }
        return takt;
    }

    public static String[][] getPlaySequence(ArrayList<Integer> instrumentNumbers, int taktLength, boolean isDefault) {
        int rowOffset = 8; //TODO
        int colOffset = 4; //TODO
        String[][] playSequence = new String[instrumentNumbers.size()][taktLength];
        if(isDefault) {
            for (int i = 0; i < instrumentNumbers.size(); i++) {
                for (int j = 0; j < taktLength; j++) {
                    playSequence[i][j] = null;
                }
            }
        }else {
            SpreadsheetExample spreadsheetExample = new SpreadsheetExample();

            spreadsheetExample.connect();

            for (int i = 0; i < instrumentNumbers.size(); i++) {
                for (int j = 0; j < taktLength; j++) {
                    playSequence[i][j] = spreadsheetExample.loadExampleDataCell(j+colOffset,i+rowOffset);
                }
            }
        }
        return playSequence;
    }

}
