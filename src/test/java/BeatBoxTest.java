import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static org.junit.Assert.assertTrue;

/**
 * Created by rkoch on 19.12.13.
 */
public class BeatBoxTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(BeatBoxTest.class);

    @Test
    public void Test() {

        //LOGGER.info("set up test environment");
        System.out.println(new File("src/main/resources/login.properties").getAbsolutePath());
        assertTrue(true);
    }
}
