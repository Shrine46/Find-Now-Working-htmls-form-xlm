import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import java.io.InputStream;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class XMLURLChecker {

    public static void main(String[] args) {
        String[] inputs = {
            "https://soldout.com/sitemap-base.xml",
            "https://soldout.com/sitemap-category-1.xml",
            "https://soldout.com/sitemap-city-1.xml",
            "https://soldout.com/sitemap-performer-1.xml",
            "https://soldout.com/sitemap-performer-2.xml",
            "https://soldout.com/sitemap-performer-3.xml",
            "https://soldout.com/sitemap-performer-4.xml",
            "https://soldout.com/sitemap-performer-5.xml",
            "https://soldout.com/sitemap-region-1.xml",
            "https://soldout.com/sitemap-venue-1.xml"
        };
        String[] outputs = {
            "sitemap-base.txt",
            "sitemap-category-1.txt",
            "sitemap-city-1.txt",
            "sitemap-performer-1.txt",
            "sitemap-performer-2.txt",
            "sitemap-performer-3.txt",
            "sitemap-performer-4.txt",
            "sitemap-performer-5.txt",
            "sitemap-region-1.txt",
            "sitemap-venue-1.txt"
        };
        for(int j = 0; j<10; j++) {
            String inputFileName = inputs[j]; // Input XML file with URLs
            String outputFileName = outputs[j]; // Output file with only 404 URLs
            int[] urlCountErrorCount = new int[2];
            System.out.println(inputs[j] + " " + outputs[j]);
            try {
                Document doc = loadXMLDoc(inputFileName);
                NodeList nodeList = doc.getElementsByTagName("loc");
                System.out.println("reading" + inputFileName);
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFileName))) {
                    for (int i = 0; i < nodeList.getLength(); i++) {
                        Node node = nodeList.item(i);
                        Element element = (Element) node;
                        
                        String urlText = element.getTextContent();
                        urlCountErrorCount = checkURL(urlText, writer, urlCountErrorCount);
                        System.out.print(urlCountErrorCount[0] + "\r");
                        
                        // System.out.println(urlText);
                    }
                }
                System.out.println("Errors " + inputs[j] + " " + urlCountErrorCount[1] );
            } catch (ParserConfigurationException | SAXException | IOException e) {
                System.err.println("Error parsing XML or writing file: " + e.getMessage());
            }
        }
    }

    private static Document loadXMLDoc(String fileName) throws ParserConfigurationException, SAXException, IOException {
    // Assume it's a URL if it's not a local file
    URL url = new URL(fileName);
    HttpURLConnection con = (HttpURLConnection) url.openConnection();
    con.setRequestMethod("GET");
    con.setRequestProperty("User-Agent", "Mozilla/5.0"); // This helps avoid being blocked as a bot
    try (InputStream inputStream = con.getInputStream()) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(inputStream);
    }
}


    private static int[] checkURL(String url, BufferedWriter writer, int[] urlCountErrorCount) throws IOException {
        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            urlCountErrorCount[0]++;

            
            int responseCode = con.getResponseCode();
            
            if (responseCode != 200) {
                writer.write(url);
                writer.newLine(); // Add a newline after each URL in the output file
                urlCountErrorCount[1]++;
                System.out.println(url);
            }
        } catch (Exception e) {
            System.err.println("Error checking URL: " + e.getMessage());
        }
        return urlCountErrorCount;
    }
}