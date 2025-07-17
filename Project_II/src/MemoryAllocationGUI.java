import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class MemoryAllocationGUI extends JFrame {

    private JTextField blockInput, processInput;
    private JTextArea resultArea;
    private JComboBox<String> algorithmSelector;

    public MemoryAllocationGUI() {
        setTitle("Memory Allocation");
        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(230, 220, 255)); // Light purple

        // Top input panel
        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        inputPanel.setBackground(new Color(230, 220, 255));

        blockInput = new JTextField();
        processInput = new JTextField();

        algorithmSelector = new JComboBox<>(new String[]{"First Fit", "Best Fit", "Worst Fit"});

        JButton allocateButton = new JButton("Allocate");
        allocateButton.addActionListener(e -> allocateMemory());

        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> dispose());
        //naimatznIT22001
        inputPanel.add(new JLabel("Block Sizes (space-separated):"));
        inputPanel.add(blockInput);
        inputPanel.add(new JLabel("Process Sizes (space-separated):"));
        inputPanel.add(processInput);
        inputPanel.add(new JLabel("Select Algorithm:"));
        inputPanel.add(algorithmSelector);
        inputPanel.add(allocateButton);
        inputPanel.add(backButton);

        // Result area
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setBackground(new Color(173, 216, 230)); // Sky blue
        JScrollPane scrollPane = new JScrollPane(resultArea);

        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        add(panel);
        setVisible(true);
    }

    private void allocateMemory() {
        try {
            String[] blockStr = blockInput.getText().trim().split("\\s+");
            String[] processStr = processInput.getText().trim().split("\\s+");

            int[] blocks = Arrays.stream(blockStr).mapToInt(Integer::parseInt).toArray();
            int[] processes = Arrays.stream(processStr).mapToInt(Integer::parseInt).toArray();

            String algorithm = (String) algorithmSelector.getSelectedItem();

            resultArea.setText("");

            switch (algorithm) {
                case "First Fit" -> firstFit(Arrays.copyOf(blocks, blocks.length), processes);
                case "Best Fit" -> bestFit(Arrays.copyOf(blocks, blocks.length), processes);
                case "Worst Fit" -> worstFit(Arrays.copyOf(blocks, blocks.length), processes);
            }
        } catch (Exception e) {
            resultArea.setText("Invalid input! Please enter space-separated integers.");
        }
    }

    private void firstFit(int[] blockSize, int[] processSize) {
        int[] allocation = new int[processSize.length];
        boolean[] blockUsed = new boolean[blockSize.length];
        Arrays.fill(allocation, -1);
        int totalFragmentation = 0;

        for (int i = 0; i < processSize.length; i++) {
            for (int j = 0; j < blockSize.length; j++) {
                if (blockSize[j] >= processSize[i] && !blockUsed[j]) {
                    allocation[i] = j;
                    totalFragmentation += blockSize[j] - processSize[i];
                    blockSize[j] -= processSize[i];
                    blockUsed[j] = true;
                    break;
                }
            }
        }

        for (int i = 0; i < blockSize.length; i++) {
            if (!blockUsed[i]) {
                totalFragmentation += blockSize[i];
            }
        }

        printResult("First Fit Allocation:", allocation, processSize, totalFragmentation);
    }

    private void bestFit(int[] blockSize, int[] processSize) {
        int[] allocation = new int[processSize.length];
        boolean[] blockUsed = new boolean[blockSize.length];
        Arrays.fill(allocation, -1);
        int totalFragmentation = 0;

        for (int i = 0; i < processSize.length; i++) {
            int bestIdx = -1;
            for (int j = 0; j < blockSize.length; j++) {
                if (blockSize[j] >= processSize[i] && !blockUsed[j]) {
                    if (bestIdx == -1 || blockSize[j] < blockSize[bestIdx]) {
                        bestIdx = j;
                    }
                }
            }
            if (bestIdx != -1) {
                allocation[i] = bestIdx;
                totalFragmentation += blockSize[bestIdx] - processSize[i];
                blockSize[bestIdx] -= processSize[i];
                blockUsed[bestIdx] = true;
            }
        }

        for (int i = 0; i < blockSize.length; i++) {
            if (!blockUsed[i]) {
                totalFragmentation += blockSize[i];
            }
        }

        printResult("Best Fit Allocation:", allocation, processSize, totalFragmentation);
    }

    private void worstFit(int[] blockSize, int[] processSize) {
        int[] allocation = new int[processSize.length];
        boolean[] blockUsed = new boolean[blockSize.length];
        Arrays.fill(allocation, -1);
        int totalFragmentation = 0;

        for (int i = 0; i < processSize.length; i++) {
            int worstIdx = -1;
            for (int j = 0; j < blockSize.length; j++) {
                if (blockSize[j] >= processSize[i] && !blockUsed[j]) {
                    if (worstIdx == -1 || blockSize[j] > blockSize[worstIdx]) {
                        worstIdx = j;
                    }
                }
            }
            if (worstIdx != -1) {
                allocation[i] = worstIdx;
                totalFragmentation += blockSize[worstIdx] - processSize[i];
                blockSize[worstIdx] -= processSize[i];
                blockUsed[worstIdx] = true;
            }
        }

        for (int i = 0; i < blockSize.length; i++) {
            if (!blockUsed[i]) {
                totalFragmentation += blockSize[i];
            }
        }

        printResult("Worst Fit Allocation:", allocation, processSize, totalFragmentation);
    }

    private void printResult(String title, int[] allocation, int[] processSize, int fragmentation) {
        resultArea.append(title + "\n");
        resultArea.append("Process No.\tProcess Size\tBlock No.\n");
        for (int i = 0; i < processSize.length; i++) {
            resultArea.append((i + 1) + "\t\t" + processSize[i] + "\t\t");
            if (allocation[i] != -1) {
                resultArea.append((allocation[i] + 1) + "\n");
            } else {
                resultArea.append("Not Allocated\n");
            }
        }
        resultArea.append("Total Internal Fragmentation: " + fragmentation + "\n\n");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MemoryAllocationGUI::new);
    }
}