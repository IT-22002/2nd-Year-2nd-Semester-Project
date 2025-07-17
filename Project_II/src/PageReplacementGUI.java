import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.*;

public class PageReplacementGUI {
    public void showUI() {
        JFrame frame = new JFrame("Page Replacement Algorithms");
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(135, 206, 250)); // Sky blue background

        JLabel pagesLabel = new JLabel("Page Reference String (comma-separated):");
        JTextField pagesField = new JTextField();

        JLabel capacityLabel = new JLabel("Frame Capacity:");
        JTextField capacityField = new JTextField();

        JLabel algorithmLabel = new JLabel("Select Algorithm:");
        String[] algorithms = {"FIFO", "LRU", "Optimal"};
        JComboBox<String> algorithmBox = new JComboBox<>(algorithms);

        JButton runButton = new JButton("Run");
        JButton backButton = new JButton("Back");  // ← Added Back button

        JTextArea outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(outputArea);

        panel.add(pagesLabel);
        panel.add(pagesField);
        panel.add(capacityLabel);
        panel.add(capacityField);
        panel.add(algorithmLabel);
        //naimatznit22001
        panel.add(algorithmBox);
        panel.add(new JLabel()); // empty label for spacing
        panel.add(runButton);
        panel.add(backButton);

        frame.setLayout(new BorderLayout());
        frame.add(panel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);

        runButton.addActionListener((ActionEvent e) -> {
            try {
                String[] parts = pagesField.getText().split(",");
                int[] pages = Arrays.stream(parts).map(String::trim).mapToInt(Integer::parseInt).toArray();
                int capacity = Integer.parseInt(capacityField.getText().trim());

                String selected = (String) algorithmBox.getSelectedItem();
                String result = "";

                if ("FIFO".equals(selected)) {
                    result = runFIFO(pages, capacity);
                } else if ("LRU".equals(selected)) {
                    result = runLRU(pages, capacity);
                } else {
                    result = runOptimal(pages, capacity);
                }

                outputArea.setText(result);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Invalid input! Please enter integers only.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        backButton.addActionListener(e -> frame.dispose());  // Close the current window
        frame.setVisible(true);
    }

    // Logic wrappers
    private String runFIFO(int[] pages, int capacity) {
        Set<Integer> set = new LinkedHashSet<>(capacity);
        Queue<Integer> queue = new LinkedList<>();
        int faults = 0, hits = 0;

        for (int page : pages) {
            if (!set.contains(page)) {
                if (set.size() == capacity) {
                    int removed = queue.poll();
                    set.remove(removed);
                }
                set.add(page);
                queue.add(page);
                faults++;
            } else {
                hits++;
            }
        }

        return "FIFO Algorithm Results:\n" +
                "Page Faults: " + faults + "\n" +
                "Page Hits: " + hits + "\n" +
                "Miss Ratio: " + String.format("%.2f", (double) faults / pages.length) + "\n" +
                "Hit Ratio: " + String.format("%.2f", (double) hits / pages.length);
    }

    private String runLRU(int[] pages, int capacity) {
        Set<Integer> set = new LinkedHashSet<>(capacity);
        Map<Integer, Integer> lruMap = new HashMap<>();
        int faults = 0, hits = 0;

        for (int i = 0; i < pages.length; i++) {
            int page = pages[i];

            if (!set.contains(page)) {
                if (set.size() == capacity) {
                    int lru = Collections.min(lruMap.entrySet(), Map.Entry.comparingByValue()).getKey();
                    set.remove(lru);
                    lruMap.remove(lru);
                }
                set.add(page);
                faults++;
            } else {
                hits++;
            }

            lruMap.put(page, i);
        }

        return "LRU Algorithm Results:\n" +
                "Page Faults: " + faults + "\n" +
                "Page Hits: " + hits + "\n" +
                "Miss Ratio: " + String.format("%.2f", (double) faults / pages.length) + "\n" +
                "Hit Ratio: " + String.format("%.2f", (double) hits / pages.length);
    }

    private String runOptimal(int[] pages, int capacity) {
        Set<Integer> set = new LinkedHashSet<>(capacity);
        int faults = 0, hits = 0;

        for (int i = 0; i < pages.length; i++) {
            int page = pages[i];

            if (!set.contains(page)) {
                if (set.size() == capacity) {
                    int farthest = i, pageToRemove = -1;

                    for (int p : set) {
                        int next = findNextIndex(pages, i + 1, p);
                        if (next == -1) {
                            pageToRemove = p;
                            break;
                        } else if (next > farthest) {
                            farthest = next;
                            pageToRemove = p;
                        }
                    }

                    set.remove(pageToRemove);
                }
                set.add(page);
                faults++;
            } else {
                hits++;
            }
        }

        return "Optimal Algorithm Results:\n" +
                "Page Faults: " + faults + "\n" +
                "Page Hits: " + hits + "\n" +
                "Miss Ratio: " + String.format("%.2f", (double) faults / pages.length) + "\n" +
                "Hit Ratio: " + String.format("%.2f", (double) hits / pages.length);
    }

    private int findNextIndex(int[] pages, int start, int target) {
        for (int i = start; i < pages.length; i++) {
            if (pages[i] == target) return i;
        }
        return -1;
    }
}