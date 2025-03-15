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
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class XMLURLChecker {

    private static final Queue<String> urlQueue = new ConcurrentLinkedQueue<>();
    private static final AtomicInteger processedCount = new AtomicInteger(0);
    private static int totalUrls = 0;
    private static URLCheckerGUI gui;

    public static void main(String[] args) throws Exception {
        int maxThreads = 50;
        int queueCapacity = 10000;

        ThreadPoolExecutor executor = new ThreadPoolExecutor(
            maxThreads,
            maxThreads,
            0L, TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(queueCapacity),
            new ThreadPoolExecutor.CallerRunsPolicy()
        );

        String oneMapString = "";

        if (!oneMapString.isEmpty()) {
            totalUrls = countUrls(oneMapString);
        } else {
            totalUrls = countUrls("https://soldout.com/sitemap.xml");
        }

        SwingUtilities.invokeLater(() -> {
            gui = new URLCheckerGUI(totalUrls);
            gui.setVisible(true);
        });

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("urls.csv", true))) {
            if (!oneMapString.isEmpty()) {
                processXML(oneMapString, executor, "No Date Available", writer);
            } else {
                processXML("https://soldout.com/sitemap.xml", executor, null, writer);
            }
            executor.shutdown();
            executor.awaitTermination(10, TimeUnit.MINUTES);
        }
    }

    private static void processXML(String xmlUrl, ThreadPoolExecutor executor, String parentDate, BufferedWriter writer) throws Exception {
        Document doc = loadXMLDoc(xmlUrl);
        NodeList urls = doc.getElementsByTagName("loc");
        NodeList dates = doc.getElementsByTagName("lastmod");

        for (int j = 0; j < urls.getLength(); j++) {
            String date = parentDate;
            if (dates.getLength() > j) {
                Node dateNode = dates.item(j);
                date = dateNode.getTextContent();
            }

            Node urlNode = urls.item(j);
            String urlsFileName = urlNode.getTextContent();

            if (urlsFileName.endsWith(".xml")) {
                processXML(urlsFileName, executor, date, writer);
            } else {
                executor.submit(new GetResponseCode(urlsFileName, writer, date, xmlUrl, gui, totalUrls));
            }
        }
    }

    private static Document loadXMLDoc(String fileName) throws Exception {
        URL url = new URL(fileName);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        con.setConnectTimeout(10000);
        con.setReadTimeout(10000);
        try (InputStream inputStream = con.getInputStream()) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(inputStream);
        } finally {
            con.disconnect();
        }
    }

    private static int countUrls(String xmlUrl) throws Exception {
        Document doc = loadXMLDoc(xmlUrl);
        NodeList urls = doc.getElementsByTagName("loc");
        int count = urls.getLength();

        for (int j = 0; j < urls.getLength(); j++) {
            Node urlNode = urls.item(j);
            String urlsFileName = urlNode.getTextContent();

            if (urlsFileName.endsWith(".xml")) {
                count += countUrls(urlsFileName);
            }
        }

        return count;
    }
}