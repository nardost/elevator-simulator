package elevator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

public class SystemConfiguration {

    private static Hashtable<String, String> configurationTable = null;

    private SystemConfiguration() throws ElevatorSystemException {
        configurationTable = new Hashtable<>();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("configuration.xml")) {
            Document configuration = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream);
            NodeList properties = configuration.getElementsByTagName("property");
            for(int i = 0; i < properties.getLength(); i++) {
                Element property = (Element) properties.item(i);
                String config = property.getAttribute("name");
                String value = property.getAttribute("value");
                configurationTable.put(config, value);
            }
        } catch(IOException ioe) {
            throw new ElevatorSystemException("Cannot read configuration file.");
        } catch(ParserConfigurationException pe) {
            throw new ElevatorSystemException("Malformed configuration file.");
        } catch(SAXException se) {
            throw new ElevatorSystemException("Malformed configuration file.");
        }
    }

    public static void initializeSystemConfiguration() throws ElevatorSystemException {
        if(configurationTable == null) {
            synchronized (SystemConfiguration.class) {
                if(configurationTable == null) {
                    new SystemConfiguration();
                }
            }
        }
    }

    public static String getConfiguration(String config) {
        return configurationTable.get(config);
    }

}