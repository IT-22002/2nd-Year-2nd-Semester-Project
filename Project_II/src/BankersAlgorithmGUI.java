import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BankersAlgorithmGUI extends JFrame {
    private int n, m;
    private int[][] allocation, max, need;
    private int[] available;
    private JLabel outputLabel;
    private JTable allocationTable, maxTable, needTable;
    private JTextField[] availableFields;
    private JTextField nField, mField;

    public void showUI() {
        setTitle("Banker's Algorithm");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(true);

        Color skyBlue = new Color(135, 206, 250);
        Color buttonBlue = new Color(30, 144, 255);
        Font labelFont = new Font("Times New Roman", Font.BOLD, 24);

        JPanel inputPanel = new JPanel(new GridLayout(2, 2));
        inputPanel.add(new JLabel("Enter number of processes:"));
        inputPanel.setBackground(skyBlue);
        nField = new JTextField();
        inputPanel.add(nField);
        inputPanel.add(new JLabel("Enter number of resource types:"));
        mField = new JTextField();
        inputPanel.add(mField);

        JButton submitButton = new JButton("Submit");
        submitButton.setFont(labelFont);
        submitButton.setBackground(buttonBlue);
        submitButton.setForeground(Color.WHITE);
        submitButton.setFocusPainted(false);
        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                n = Integer.parseInt(nField.getText());
                m = Integer.parseInt(mField.getText());
                initializeTables();
                setupInputSection();
                pack();
                setLocationRelativeTo(null);
            }
        });

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(inputPanel, BorderLayout.CENTER);
        topPanel.add(submitButton, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        outputLabel = new JLabel("Results will be displayed here.");
        outputLabel.setPreferredSize(new Dimension(600, 50));
        outputLabel.setVerticalAlignment(SwingConstants.TOP);
        setVisible(true);
    }

    private void initializeTables() {
        allocation = new int[n][m];
        max = new int[n][m];
        need = new int[n][m];
        available = new int[m];

        allocationTable = createTable("Allocation Matrix");
        maxTable = createTable("Maximum Matrix");
        needTable = createTable("Need Matrix");
    }

    private JTable createTable(String title) {
        String[] columns = new String[m];
        for (int i = 0; i < m; i++) {
            columns[i] = "R" + i;
        }//naimatznit22001
        Object[][] data = new Object[n][m];
        JTable table = new JTable(data, columns);
        table.setBorder(BorderFactory.createTitledBorder(title));
        table.setFillsViewportHeight(true);
        return table;
    }

    private void setupInputSection() {
        JPanel tablePanel = new JPanel(new GridLayout(3, 1, 10, 10));

        allocationTable.setPreferredScrollableViewportSize(new Dimension(600, 100));
        maxTable.setPreferredScrollableViewportSize(new Dimension(600, 100));
        needTable.setPreferredScrollableViewportSize(new Dimension(600, 100));

        tablePanel.add(new JScrollPane(allocationTable));
        tablePanel.add(new JScrollPane(maxTable));
        tablePanel.add(new JScrollPane(needTable));

        JScrollPane tableScrollPane = new JScrollPane(tablePanel);
        add(tableScrollPane, BorderLayout.CENTER);

        JPanel availablePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        availablePanel.add(new JLabel("Enter Available Resources: "));
        availableFields = new JTextField[m];
        for (int i = 0; i < m; i++) {
            availableFields[i] = new JTextField(3);
            availablePanel.add(availableFields[i]);
        }
        add(availablePanel, BorderLayout.WEST);

        JButton checkButton = new JButton("Check Safety");
        checkButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                readInput();
                checkSafety();
            }
        });

        JButton backButton = new JButton("Back");  // ← Back button added
        backButton.addActionListener(e -> this.dispose());  // Closes the window

        JPanel bottomPanel = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel();  // Sub-panel to hold both buttons

        buttonPanel.add(checkButton);
        buttonPanel.add(backButton);

        bottomPanel.add(buttonPanel, BorderLayout.NORTH);

        bottomPanel.add(outputLabel, BorderLayout.SOUTH);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void readInput() {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                allocation[i][j] = Integer.parseInt(allocationTable.getValueAt(i, j).toString());
                max[i][j] = Integer.parseInt(maxTable.getValueAt(i, j).toString());
                need[i][j] = max[i][j] - allocation[i][j];
                needTable.setValueAt(need[i][j], i, j);
            }
        }
        for (int i = 0; i < m; i++) {
            available[i] = Integer.parseInt(availableFields[i].getText());
        }
    }

    private void checkSafety() {
        boolean[] finish = new boolean[n];
        int[] work = available.clone();
        int[] safeSequence = new int[n];
        int count = 0;

        while (count < n) {
            boolean found = false;
            for (int i = 0; i < n; i++) {
                if (!finish[i]) {
                    int j;
                    for (j = 0; j < m; j++) {
                        if (need[i][j] > work[j]) break;
                    }
                    if (j == m) {
                        for (int k = 0; k < m; k++) {
                            work[k] += allocation[i][k];
                        }
                        safeSequence[count++] = i;
                        finish[i] = true;
                        found = true;
                    }
                }
            }
            if (!found) {
                outputLabel.setText("<html><b>The system is not in a safe state.</b></html>");
                return;
            }
        }

        StringBuilder result = new StringBuilder("<html><b>The system is in a safe state.<br>Safe sequence is:</b><br>");
        for (int i = 0; i < n; i++) {
            result.append("P").append(safeSequence[i]);
            if (i < n - 1) result.append(" → ");
        }
        result.append("</html>");
        outputLabel.setText(result.toString());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BankersAlgorithmGUI gui = new BankersAlgorithmGUI();
            gui.showUI();
        });
    }
}
