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
        int errors = 0;
    }

    private String escapeCsvField(String field) {
        if (field.contains(",") || field.contains("\n")) {
            return "\"" + field.replace("\"", "\"\"") + "\""; // Escape quotes and wrap in quotes
        }
        return field;
    }

    @Override
    public void run() {
        HttpURLConnection con = null;
        try {
            URL obj = new URL(url);
            con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Connection", "keep-alive");
            con.setConnectTimeout(20000); // Timeout for connection
            con.setReadTimeout(20000);    // Timeout for reading

            int responseCode = con.getResponseCode();

            synchronized (writer) {
                writer.write(
                    escapeCsvField(siteMap) + "," +
                    escapeCsvField(date) + "," +
                    escapeCsvField(url) + "," +
                    responseCode
                );
                writer.newLine();
            }
            // Optional delay to throttle requests
            Thread.sleep(100);

        } catch (IOException | InterruptedException e) {
            System.err.println("Error checking URL (" + url + "): " + e.getMessage());
            synchronized (writer) {
                try {
                    writer.write(
                        escapeCsvField(siteMap) + "," +
                        escapeCsvField(date) + "," +
                        escapeCsvField(url) + "," +
                        0 + "," +
                        e.getMessage()
                    );
                    writer.newLine();
                } catch (IOException e1) {}
            }
        } finally {
            if (con != null) {
                con.disconnect(); // Ensure the connection is closed
            }
        }
    }

}
