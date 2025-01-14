import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import java.io.InputStream;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.swing.*;

import java.util.concurrent.*;

public class XMLURLChecker {

    public static void main(String[] args) throws Exception {
        // Define a fixed thread pool with a bounded blocking queue
        int maxThreads = 50; // Maximum number of threads
        int queueCapacity = 10000; // Maximum number of tasks waiting in the queue

        ThreadPoolExecutor executor = new ThreadPoolExecutor(
            maxThreads, // Core pool size
            maxThreads, // Maximum pool size
            0L, TimeUnit.MILLISECONDS, // Keep-alive time for extra threads
            new ArrayBlockingQueue<>(queueCapacity), // Blocking queue with capacity
            new ThreadPoolExecutor.CallerRunsPolicy() // Handle rejected tasks
        );

        // JFrame frame = new JFrame();
		// frame.setSize(800,800);
		// frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// frame.setLocationRelativeTo(null);
		// frame.add(new Display());
		// frame.setVisible(true);


        Document mainMap = loadXMLDoc("https://soldout.com/sitemap.xml");
        NodeList inputs = mainMap.getElementsByTagName("loc");
        NodeList dates = mainMap.getElementsByTagName("lastmod");
        String outputFileName = "urls.csv";

        int urlCount = 0;
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFileName))) {
            for (int j = 0; j < inputs.getLength(); j++) {
                Node dateNode = dates.item(j);
                String date = dateNode.getTextContent();

                Node inputNode = inputs.item(j);
                String inputFileName = inputNode.getTextContent();

                try {
                    Document doc = loadXMLDoc(inputFileName);
                    NodeList nodeList = doc.getElementsByTagName("loc");

                    for (int i = 0; i < nodeList.getLength(); i++) {
                        Node node = nodeList.item(i);
                        String urlText = node.getTextContent();

                        urlCount++;
                        
                        // Submit tasks to the executor
                        executor.submit(new GetResponseCode(urlText, writer, date, inputFileName));
                    }

                } catch (ParserConfigurationException | SAXException | IOException e) {
                    System.err.println("Error parsing XML or writing file: " + e.getMessage());
                }
            }
        }
        System.out.println(urlCount);

        // Shut down the executor and wait for tasks to finish
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.MINUTES);
    }

    private static Document loadXMLDoc(String fileName) throws Exception {
        URL url = new URL(fileName);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        con.setConnectTimeout(10000); // Set timeout
        con.setReadTimeout(10000);
        try (InputStream inputStream = con.getInputStream()) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(inputStream);
        } finally {
            con.disconnect(); // Close the connection
        }
    }
    
}