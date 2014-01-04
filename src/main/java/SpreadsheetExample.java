/**
 * Created by rkoch on 19.12.13.
 */
import com.google.gdata.client.spreadsheet.FeedURLFactory;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.*;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class SpreadsheetExample {

    private static final FeedURLFactory urlFactory = FeedURLFactory.getDefault();

    private SpreadsheetService service;


    // ==========================================================================
    // CONNECT to Google Spreadsheet API

    /**
     * Opens a connection to Google spreadsheet API.
     */
    public void connect() {
        SpreadsheetService newService = new SpreadsheetService("application_name");
        PropertyFinder propertyFinder = new PropertyFinder();
        ArrayList<String> propertyKeys = new ArrayList<String>();
        propertyKeys.add("user");
        propertyKeys.add("password");
        HashMap propertyValues = propertyFinder.getProperty("src/main/resources/login.properties", propertyKeys);

        // Google account login and passwords to access your spreadsheet.
        // Password is in plain text. For sure there are better ways to do that
        // so the passwords does not have to be in the code, but the simplest.
        String email = (String) propertyValues.get("user"); //TODO
        String password = (String) propertyValues.get("password"); //TODO
        try {
            newService.setUserCredentials(email, password);
        } catch (AuthenticationException e) {
            throw new RuntimeException("Cannot authenticate, invalid user/password", e);
        }

        this.service = newService;
    }

    private WorksheetEntry findWorksheet() throws IOException, ServiceException {

        // Spreadsheet KEY is an unique identifier of a spreadsheet document.
        // The KEY is part of spreadsheet document URL. To find it, just open the
        // spreadsheet document in your browser and take the 'key' param value.
        // For example:
        // URL=https://docs.google.com/spreadsheet/ccc?key=0AuhY-asdcrtr123bc#gid=1
        // => KEY=0AuhY-asdcrtr123bc
        // (the real keys are usually much longer)
        //
        // Other approach to find spreadsheetKey could be to list all spreadsheets
        // documents that belong the user and the select one by name. It would require
        // more code and in particular one more call to API. Then it would stop working
        // as soon as you name another spreadsheet documents with the same name.
        // Thus, I think it is not worth the effort.
        //
        String spreadsheetKey = "0AtAEdaHyIUe3dGpNUjRvdW1DTmZwV3FJUS1QT2tvX2c";

        // Worksheet name is the name of a sheet inside the spreadsheet document.
        // It is a name given by the user as seen in the spreadsheet. In particular
        // it does not have to be unique, but this is the simplest way I found to
        // identify a sheet.
        // Note that this work only if your worksheet has unique name (within
        // the spreadsheet document, not globally).
        //
        // Other approach to find a worksheet is to use worksheet position (1,2, ...)
        // but I find it less convenient.
        //
        String worksheetName = "drumExample";

        SpreadsheetFeed feed = service.getFeed(urlFactory.getSpreadsheetsFeedUrl(), SpreadsheetFeed.class);
        for (SpreadsheetEntry se : feed.getEntries()) {
            if (se.getSpreadsheetLink().getHref().endsWith(spreadsheetKey)) {
                for (WorksheetEntry we : se.getWorksheets()) {
                    if (we.getTitle().getPlainText().equalsIgnoreCase(worksheetName)) {
                        return we;
                    }
                }
            }
        }

        throw new RuntimeException("Cannot find worksheet=" + worksheetName);
    }

    // ==========================================================================
    // Load data

    /**
     * Loads data from the sheet.
     *
     * @return  List of all non empty values in the first sheet column as 'row_number:value', where row_number=1,2,...
     */
    public List<String> loadExampleData(int colStart, int colEnd, int rowStart, int rowEnd) {
        CellFeed cellFeed = getCellFeed();
        List<String> result = new ArrayList<String>();
        for (CellEntry entry : cellFeed.getEntries()) {
            Cell cell = entry.getCell();
            if (cell.getCol() >= colStart && cell.getCol() <= colEnd && cell.getRow() >= rowStart && cell.getRow() <= rowEnd) { // in column D starts the 1st tact and in column S it will end
                String value = cell.getRow() + "-" + cell.getCol() + "-" + cell.getValue();
                result.add(value);
            }
        }

        return result;
    }

    public String loadExampleDataCell(int col, int row) {
        CellFeed cellFeed = getCellFeed();
        String result = null;
        for (CellEntry entry : cellFeed.getEntries()) {
            Cell cell = entry.getCell();
            if (cell.getCol() == col && cell.getRow() == row) { // in column D starts the 1st tact and in column S it will end
                result = cell.getValue();
            }
        }
        return result;
    }

    // ==========================================================================
    // Access a worksheet in CELL mode

    /**
     * Returns CellFeed connected to predefined, existing spreadsheet and a sheet inside it. Provided CellFeed will
     * include only non empty cells. This method is convenient for reading, but not always so good for updating (you do
     * not see all cells).
     */
    private CellFeed getCellFeed() {
        try {
            return service.getFeed(findWorksheet().getCellFeedUrl(), CellFeed.class);
        } catch (ServiceException e) {
            throw new RuntimeException("Service error when loading data", e);
        } catch (IOException e) {
            throw new RuntimeException("Connection with server broken", e);
        }
    }



}

