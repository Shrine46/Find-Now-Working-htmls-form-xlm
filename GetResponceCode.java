import java.io.BufferedWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetResponceCode extends Thread {
    private String url;
    private BufferedWriter writer;
    
    public void GetResponceCode(String url, BufferedWriter writer) {
        this.url = url;
        this.writer = writer;
    }

    public void run(String url, BufferedWriter writer) throws IOException {
        try {
            URL obj = new URL(this.url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");


            int responseCode = con.getResponseCode();
            
            if (responseCode != 200) {
                this.writer.write(url);
                this.writer.newLine(); // Add a newline after each URL in the output file
                System.out.println(url);
            }
        } catch (Exception e) {
            System.err.println("Error checking URL: " + e.getMessage());
        }
    }
}
