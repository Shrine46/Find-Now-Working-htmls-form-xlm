import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.atomic.AtomicInteger;
import java.io.File;
import java.io.IOException;

public class URLCheckerGUI extends JFrame {
    private JTextArea urlTextArea;
    private JProgressBar progressBar;
    private JLabel progressLabel;
    private JLabel timeLabel;
    private JLabel urlsCheckedLabel;
    private JLabel urlsLeftLabel;
    private JTextField sitemapUrlField;
    private JButton startButton;
    private JButton downloadButton;
    private AtomicInteger processedCount;
    private int totalUrls;
    private long startTime;
    private File resultFile;

    public URLCheckerGUI() {
        this.processedCount = new AtomicInteger(0);

        setTitle("URL Checker");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        sitemapUrlField = new JTextField("Enter sitemap URL here");
        startButton = new JButton("Start");
        downloadButton = new JButton("Download Results");
        downloadButton.setEnabled(false);

        urlTextArea = new JTextArea();
        urlTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(urlTextArea);
        scrollPane.setPreferredSize(new Dimension(600, 150)); // Set preferred size

        progressBar = new JProgressBar(0, 100);
        progressLabel = new JLabel("Progress: 0%");
        timeLabel = new JLabel("Estimated time left: calculating...");
        urlsCheckedLabel = new JLabel("URLs Checked: 0");
        urlsLeftLabel = new JLabel("URLs Left: 0");

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(sitemapUrlField, BorderLayout.CENTER);
        inputPanel.add(startButton, BorderLayout.EAST);

        JPanel progressPanel = new JPanel(new GridLayout(2, 1));
        JPanel progressSubPanel = new JPanel(new BorderLayout());
        progressSubPanel.add(progressLabel, BorderLayout.WEST);
        progressSubPanel.add(progressBar, BorderLayout.CENTER);
        progressSubPanel.add(timeLabel, BorderLayout.EAST);
        progressPanel.add(progressSubPanel);

        JPanel urlsPanel = new JPanel(new GridLayout(1, 2));
        urlsPanel.add(urlsCheckedLabel);
        urlsPanel.add(urlsLeftLabel);
        progressPanel.add(urlsPanel);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(downloadButton, BorderLayout.WEST);
        bottomPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(progressPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String sitemapUrl = sitemapUrlField.getText();
                startChecking(sitemapUrl);
            }
        });

        downloadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (resultFile != null && resultFile.exists()) {
                    try {
                        Desktop.getDesktop().open(resultFile);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
    }

    public void startChecking(String sitemapUrl) {
        this.startTime = System.currentTimeMillis();
        SwingUtilities.invokeLater(() -> {
            urlTextArea.setText("");
            progressBar.setValue(0);
            progressLabel.setText("Progress: 0%");
            timeLabel.setText("Estimated time left: calculating...");
            urlsCheckedLabel.setText("URLs Checked: 0");
            urlsLeftLabel.setText("URLs Left: " + totalUrls);
            downloadButton.setEnabled(false);
        });

        new Thread(() -> {
            try {
                resultFile = XMLURLChecker.startChecking(sitemapUrl, this);
                SwingUtilities.invokeLater(() -> downloadButton.setEnabled(true));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    public void updateProgress(String url, int progress, String timeLeft) {
        SwingUtilities.invokeLater(() -> {
            urlTextArea.append(url + "\n");
            progressBar.setValue(progress);
            progressLabel.setText("Progress: " + progress + "%");
            timeLabel.setText("Estimated time left: " + timeLeft);
            urlsCheckedLabel.setText("URLs Checked: " + processedCount.get());
            urlsLeftLabel.setText("URLs Left: " + (totalUrls - processedCount.get()));
        });
    }

    public long getStartTime() {
        return startTime;
    }

    public AtomicInteger getProcessedCount() {
        return processedCount;
    }

    public void setTotalUrls(int totalUrls) {
        this.totalUrls = totalUrls;
    }
}