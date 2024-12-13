import java.io.BufferedWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetResponseCode implements Runnable {
    private final String url;
    private final BufferedWriter writer;
    private final String date;
    private final String siteMap;

    public GetResponseCode(String url, BufferedWriter writer, String date, String siteMap) {
        this.url = url;
        this.writer = writer;
        this.date = date;
        this.siteMap = siteMap;
    }

    private String escapeCsvField(String field) {
        if (field.contains(",") || field.contains("\n")) {
            return "\"" + field.replace("\"", "\"\"") + "\""; // Escape quotes and wrap in quotes
        }
        return field;
    }

    @Override
    public void run() {
        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");

            int responseCode = con.getResponseCode();

            if (true) {
                // wants sitemap name, last mod, url, error code
                synchronized (writer) {
                    writer.write(
                        escapeCsvField(siteMap) + "," +
                        escapeCsvField(date) + "," +
                        escapeCsvField(url) + "," +
                        responseCode
                    );
                    writer.newLine(); // Add a newline after each URL in the output file
                }
            }
        } catch (IOException e) {
            System.err.println("Error checking URL: " + e.getMessage());
        }
    }
}
