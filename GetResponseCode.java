import java.io.BufferedWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;

class GetResponseCode implements Runnable {
    private final String url;
    private final BufferedWriter writer;
    private final String date;
    private final String siteMap;
    private final URLCheckerGUI gui;
    private static final AtomicInteger processedCount = new AtomicInteger(0);
    private final int totalUrls;

    public GetResponseCode(String url, BufferedWriter writer, String date, String siteMap, URLCheckerGUI gui, int totalUrls) {
        this.url = url;
        this.writer = writer;
        this.date = date;
        this.siteMap = siteMap;
        this.gui = gui;
        this.totalUrls = totalUrls;
    }

    @Override
    public void run() {
        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Connection", "keep-alive");
            con.setConnectTimeout(20000);
            con.setReadTimeout(20000);

            int responseCode = con.getResponseCode();
            synchronized (writer) {
                writer.write(siteMap + "," + date + "," + url + "," + responseCode);
                writer.newLine();
            }

            int processed = processedCount.incrementAndGet();
            double progress = totalUrls > 0 ? (processed / (double) totalUrls) * 100 : 0;
            int urlsLeft = totalUrls - processed;
            gui.updateProgress(url, (int) progress, processed, urlsLeft);

            Thread.sleep(100);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}