import java.io.BufferedWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;

public class GetResponseCode implements Runnable {
    private final String url;
    private final BufferedWriter writer;
    private final String date;
    private final String siteMap;
    private final URLCheckerGUI gui;
    private final int totalUrls;

    public GetResponseCode(String url, BufferedWriter writer, String date, String siteMap, URLCheckerGUI gui, int totalUrls) {
        this.url = url;
        this.writer = writer;
        this.date = date;
        this.siteMap = siteMap;
        this.gui = gui;
        this.totalUrls = totalUrls;
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

            // Update progress and GUI
            int processed = gui.getProcessedCount().incrementAndGet();
            double progress = (processed / (double) totalUrls) * 100;
            long elapsedTime = System.currentTimeMillis() - gui.getStartTime();
            long estimatedTotalTime = (elapsedTime / processed) * totalUrls;
            long estimatedTimeLeft = estimatedTotalTime - elapsedTime;
            String timeLeft = formatTime(estimatedTimeLeft);

            gui.updateProgress(url, (int) progress, timeLeft);

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

    private String formatTime(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        minutes = minutes % 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}