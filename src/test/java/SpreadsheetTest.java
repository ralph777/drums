
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static org.junit.Assert.assertTrue;

/**
 * Created by rkoch on 19.12.13.
 */
public class SpreadsheetTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpreadsheetTest.class);

    @Test
    public void ConnectionTest() {
        boolean expected;

        SpreadsheetExample spreadsheetExample = new SpreadsheetExample();
        spreadsheetExample.connect();
        String test = spreadsheetExample.loadExampleDataCell(4,6);
        String test2 = "1";
        //LOGGER.debug("Variable test: " + test);
        System.out.println("Variable test: " + test);
        if (test == test2) {
            expected = true;
        }else{
            expected = false;
            System.out.println("das");
        }
        assertTrue(expected);
    }
}
