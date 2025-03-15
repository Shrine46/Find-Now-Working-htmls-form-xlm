import javax.swing.*;
import java.awt.*;
import java.util.concurrent.atomic.AtomicInteger;

public class URLCheckerGUI extends JFrame {
    private JTextArea urlTextArea;
    private JProgressBar progressBar;
    private JLabel progressLabel;
    private JLabel urlsCheckedLabel;
    private JLabel urlsLeftLabel;
    private AtomicInteger processedCount;
    private int totalUrls;
    private long startTime;

    public URLCheckerGUI(int totalUrls) {
        this.totalUrls = totalUrls;
        this.processedCount = new AtomicInteger(0);
        this.startTime = System.currentTimeMillis();

        setTitle("URL Checker");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        urlTextArea = new JTextArea();
        urlTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(urlTextArea);

        progressBar = new JProgressBar(0, 100);
        progressLabel = new JLabel("Progress: 0%");
        urlsCheckedLabel = new JLabel("URLs checked: 0");
        urlsLeftLabel = new JLabel("URLs left: " + totalUrls);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(progressBar, BorderLayout.SOUTH);
        panel.add(progressLabel, BorderLayout.NORTH);
        panel.add(urlsCheckedLabel, BorderLayout.WEST);
        panel.add(urlsLeftLabel, BorderLayout.EAST);

        add(panel);
    }

    public void updateProgress(String url, int progress, int urlsChecked, int urlsLeft) {
        SwingUtilities.invokeLater(() -> {
            urlTextArea.append(url + "\n");
            progressBar.setValue(progress);
            progressLabel.setText("Progress: " + progress + "%");
            urlsCheckedLabel.setText("URLs checked: " + urlsChecked);
            urlsLeftLabel.setText("URLs left: " + urlsLeft);
        });
    }

    public long getStartTime() {
        return startTime;
    }

    public AtomicInteger getProcessedCount() {
        return processedCount;
    }

    public int getTotalUrls() {
        return totalUrls;
    }
}