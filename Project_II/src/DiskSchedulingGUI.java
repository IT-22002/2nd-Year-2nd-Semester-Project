import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class DiskSchedulingGUI extends JFrame {
    private JTextField numRequestsField, diskRequestsField, headPositionField, diskSizeField;
    private JTextArea resultArea;

    public DiskSchedulingGUI() {
        // Frame setup
        setTitle("Disk Scheduling Algorithms");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
    }

    public void showUI() {
        Color lightPurple = new Color(230, 220, 250);

        // Main panel with background color
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(lightPurple);
        // Panel for input fields
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(5, 2));
        inputPanel.setBackground(lightPurple);

        inputPanel.add(new JLabel("Enter number of disk requests:"));
        numRequestsField = new JTextField();
        inputPanel.add(numRequestsField);

        inputPanel.add(new JLabel("Enter the disk requests (comma separated):"));
        diskRequestsField = new JTextField();
        inputPanel.add(diskRequestsField);

        inputPanel.add(new JLabel("Enter initial head position:"));
        headPositionField = new JTextField();
        inputPanel.add(headPositionField);

        inputPanel.add(new JLabel("Enter disk size (0 to size-1):"));
        diskSizeField = new JTextField();
        inputPanel.add(diskSizeField);

        // Add the input panel to the frame
        //naimatznIT22001
        add(inputPanel, BorderLayout.NORTH);

        // Text area for displaying results
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);
        add(scrollPane, BorderLayout.CENTER);

        // Button to calculate and display results
        JButton calculateButton = new JButton("Calculate Seek Times");
        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculateSeekTimes();
            }
        });
        // Back button to return to the main page
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            this.dispose();               // Close current window
        });

// Panel to hold both buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(calculateButton);
        buttonPanel.add(backButton);

// Add button panel to the frame
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void calculateSeekTimes() {
        try {
            // Get input values
            int n = Integer.parseInt(numRequestsField.getText().trim());
            String[] diskRequestsStr = diskRequestsField.getText().trim().split(",");
            int[] requests = new int[n];

            for (int i = 0; i < n; i++) {
                requests[i] = Integer.parseInt(diskRequestsStr[i].trim());
            }

            int head = Integer.parseInt(headPositionField.getText().trim());
            int diskSize = Integer.parseInt(diskSizeField.getText().trim());

            // Clear previous results
            resultArea.setText("");

            // Display results for each algorithm
            resultArea.append("FCFS Total Seek Time: " + fcfs(requests, head) + "\n");
            resultArea.append("SSTF Total Seek Time: " + sstf(requests, head) + "\n");
            resultArea.append("SCAN Total Seek Time: " + scan(requests, head, diskSize) + "\n");
            resultArea.append("C-SCAN Total Seek Time: " + cscan(requests, head, diskSize) + "\n");
            resultArea.append("LOOK Total Seek Time: " + look(requests, head) + "\n");
            resultArea.append("C-LOOK Total Seek Time: " + clook(requests, head) + "\n");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid input. Please check your values.");
        }
    }

    // FCFS - First Come First Serve
    public static int fcfs(int[] requests, int head) {
        int totalSeekTime = 0;
        for (int req : requests) {
            totalSeekTime += Math.abs(head - req);
            head = req;
        }
        return totalSeekTime;
    }

    // SSTF - Shortest Seek Time First
    public static int sstf(int[] requests, int head) {
        int totalSeekTime = 0;
        boolean[] visited = new boolean[requests.length];

        for (int i = 0; i < requests.length; i++) {
            int closest = -1;
            int minDistance = Integer.MAX_VALUE;

            for (int j = 0; j < requests.length; j++) {
                if (!visited[j] && Math.abs(head - requests[j]) < minDistance) {
                    closest = j;
                    minDistance = Math.abs(head - requests[j]);
                }
            }

            visited[closest] = true;
            totalSeekTime += minDistance;
            head = requests[closest];
        }
        return totalSeekTime;
    }

    // SCAN - Move in one direction, service requests, then reverse direction
    public static int scan(int[] requests, int head, int diskSize) {
        Arrays.sort(requests); // Sort the requests in ascending order
        int totalSeekTime = 0;

        ArrayList<Integer> left = new ArrayList<>();
        ArrayList<Integer> right = new ArrayList<>();

        for (int req : requests) {
            if (req < head) {
                left.add(req);
            } else {
                right.add(req);
            }
        }

        Collections.reverse(left);

        // Move towards the rightmost end first
        for (int req : right) {
            totalSeekTime += Math.abs(head - req);
            head = req;
        }

        if (!right.isEmpty()) {
            totalSeekTime += Math.abs(head - (diskSize - 1));  // Move to the rightmost end
            head = diskSize - 1;
        }

        for (int req : left) {
            totalSeekTime += Math.abs(head - req);
            head = req;
        }

        return totalSeekTime;
    }

    // C-SCAN - Circular SCAN
    public static int cscan(int[] requests, int head, int diskSize) {
        Arrays.sort(requests); // Sort the requests in ascending order
        int totalSeekTime = 0;

        ArrayList<Integer> left = new ArrayList<>();
        ArrayList<Integer> right = new ArrayList<>();

        for (int req : requests) {
            if (req < head) {
                left.add(req);
            } else {
                right.add(req);
            }
        }

        for (int req : right) {
            totalSeekTime += Math.abs(head - req);
            head = req;
        }

        totalSeekTime += Math.abs(head - (diskSize - 1)); // Move to the rightmost end
        head = diskSize - 1;

        totalSeekTime += Math.abs(head - 0); // Jump to the beginning
        head = 0;

        for (int req : left) {
            totalSeekTime += Math.abs(head - req);
            head = req;
        }

        return totalSeekTime;
    }

    // LOOK - Move towards the farthest request in one direction and reverse
    public static int look(int[] requests, int head) {
        Arrays.sort(requests);
        int totalSeekTime = 0;

        ArrayList<Integer> left = new ArrayList<>();
        ArrayList<Integer> right = new ArrayList<>();

        for (int req : requests) {
            if (req < head) {
                left.add(req);
            } else {
                right.add(req);
            }
        }

        Collections.reverse(left);

        for (int req : right) {
            totalSeekTime += Math.abs(head - req);
            head = req;
        }

        for (int req : left) {
            totalSeekTime += Math.abs(head - req);
            head = req;
        }

        return totalSeekTime;
    }

    // C-LOOK - Circular LOOK
    public static int clook(int[] requests, int head) {
        Arrays.sort(requests);
        int totalSeekTime = 0;

        ArrayList<Integer> left = new ArrayList<>();
        ArrayList<Integer> right = new ArrayList<>();

        for (int req : requests) {
            if (req < head) {
                left.add(req);
            } else {
                right.add(req);
            }
        }

        for (int req : right) {
            totalSeekTime += Math.abs(head - req);
            head = req;
        }

        for (int req : left) {
            totalSeekTime += Math.abs(head - req);
            head = req;
        }

        return totalSeekTime;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DiskSchedulingGUI frame = new DiskSchedulingGUI();
            frame.showUI();  // This line ensures the UI is shown
            frame.setVisible(true);  // Ensure the frame is visible
        });
    }
}
