import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

/**
 * Created by rkoch on 18.12.13.
 */

public class PropertyFinder {

    HashMap<String, String> PropertyMap = new HashMap<String, String>();

    public HashMap getProperty(final String propertyPath, final ArrayList<String> propertyKeys) {

        Properties properties = new Properties();
        BufferedInputStream stream = null;
        try {
            stream = new BufferedInputStream(new FileInputStream(propertyPath));
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        }

        try {
            properties.load(stream);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        try {
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //propertyNames
        for (String value : propertyKeys) {
            String valueResult = properties.getProperty(value);
            PropertyMap.put(value, valueResult);
        }

        return PropertyMap;
    }
}
