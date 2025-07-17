import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Comparator;
import javax.swing.table.DefaultTableModel;

public class ProcessSchedulingGUI {
    private JFrame mainFrame;
    private CardLayout cardLayout;
    private JPanel mainPanel;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ProcessSchedulingGUI().showUI());
    }

    public void showUI() {
        mainFrame = new JFrame("Process Scheduling Algorithms");
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        JPanel mainMenuPanel = new JPanel(new GridLayout(5, 1, 10, 10));
        mainMenuPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainMenuPanel.setBackground(new Color(135, 206, 250)); // Sky blue

        // Button styling
        Font buttonFont = new Font("Times New Roman", Font.BOLD, 16);
        Color buttonColor = new Color(30, 144, 255); // Dodger blue
        Color textColor = Color.WHITE;

        // Create main menu
        JButton fcfsButton = new JButton("First-Come, First-Served (FCFS)");
        JButton sjfButton = new JButton("Shortest Job First (SJF)");
        JButton rrButton = new JButton("Round Robin (RR)");
        JButton priorityButton = new JButton("Priority Scheduling");
        JButton backButton = new JButton("Back");

        JButton[] buttons = { fcfsButton, sjfButton, rrButton, priorityButton, backButton };
        for (JButton button : buttons) {
            button.setFont(buttonFont);
            button.setBackground(buttonColor);
            button.setForeground(textColor);
            button.setFocusPainted(false);
            mainMenuPanel.add(button);//naima
        }

        fcfsButton.addActionListener(e -> showFCFSPage());
        sjfButton.addActionListener(e -> showSJFPage());
        rrButton.addActionListener(e -> showRoundRobinPage());
        priorityButton.addActionListener(e -> showPrioritySchedulingPage());
        backButton.addActionListener(e -> mainFrame.dispose());

        mainMenuPanel.add(fcfsButton);
        mainMenuPanel.add(sjfButton);
        mainMenuPanel.add(rrButton);
        mainMenuPanel.add(priorityButton);
        mainMenuPanel.add(backButton);

        // Add the main menu panel to the card layout
        mainPanel.add(mainMenuPanel, "Main Menu");//naima

        // Set up the main frame
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(500, 400);
        mainFrame.add(mainPanel);
        mainFrame.setVisible(true);
    }

    // FCFS Page
    private void showFCFSPage() {
        JPanel fcfsPanel = new JPanel();
        fcfsPanel.setLayout(new BorderLayout());

        // Input form for FCFS
        JPanel inputPanel = new JPanel(new GridLayout(6, 2, 10, 10));  // Increased row count to 6 for labels and buttons
        JLabel numProcessesLabel = new JLabel("Enter number of processes:");
        JTextField numProcessesField = new JTextField();
        JLabel arrivalLabel = new JLabel("Arrival Times (comma separated):");
        JTextField arrivalField = new JTextField();
        JLabel burstLabel = new JLabel("Burst Times (comma separated):");
        JTextField burstField = new JTextField();
        //naimatznit22001

        // Add labels for averages
        JLabel avgTATLabel = new JLabel("Average Turnaround Time: ");
        JLabel avgWTLabel = new JLabel("Average Waiting Time: ");

        JButton calculateButton = new JButton("Calculate");
        JButton backButton = new JButton("Back");

        inputPanel.add(numProcessesLabel);
        inputPanel.add(numProcessesField);
        inputPanel.add(arrivalLabel);
        inputPanel.add(arrivalField);
        inputPanel.add(burstLabel);
        inputPanel.add(burstField);
        inputPanel.add(calculateButton);
        inputPanel.add(backButton);
        inputPanel.add(avgTATLabel);  // Added to show the average Turnaround Time
        inputPanel.add(avgWTLabel);   // Added to show the average Waiting Time

        // Table to show results
        JTable resultTable = new JTable(new DefaultTableModel(new Object[]{"Process", "AT", "BT", "CT", "TAT", "WT"}, 0));
        JScrollPane scrollPane = new JScrollPane(resultTable);
        fcfsPanel.add(inputPanel, BorderLayout.NORTH);
        fcfsPanel.add(scrollPane, BorderLayout.CENTER);

        // Action for calculate button
        calculateButton.addActionListener(e -> {
            try {
                int n = Integer.parseInt(numProcessesField.getText()); // Getting the number of processes
                String[] arrivalTimes = arrivalField.getText().split(",");
                String[] burstTimes = burstField.getText().split(",");

                // Ensure that the number of arrival and burst times match the number of processes
                if (arrivalTimes.length != n || burstTimes.length != n) {
                    JOptionPane.showMessageDialog(fcfsPanel, "Number of arrival times or burst times doesn't match the number of processes.");
                } else {
                    calculateFCFS(n, arrivalTimes, burstTimes, resultTable, avgTATLabel, avgWTLabel);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(fcfsPanel, "Please enter valid numbers for all fields.");
            }
        });
        backButton.addActionListener(e -> mainFrame.dispose());
        inputPanel.add(backButton);
        mainFrame.setVisible(true);
        // Add the FCFS panel to the card layout
        mainPanel.add(fcfsPanel, "FCFS Page");
        cardLayout.show(mainPanel, "FCFS Page");
    }

    // Updated FCFS calculation method
    private void calculateFCFS(int n, String[] arrivalTimes, String[] burstTimes, JTable resultTable, JLabel avgTATLabel, JLabel avgWTLabel) {
        int[] at = new int[n], bt = new int[n], processId = new int[n];
        for (int i = 0; i < n; i++) {
            at[i] = Integer.parseInt(arrivalTimes[i].trim());
            bt[i] = Integer.parseInt(burstTimes[i].trim());
            processId[i] = i + 1; // Assigning process IDs
        }

        // Sort processes based on Arrival Time
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (at[j] > at[j + 1]) {
                    // Swap Arrival Times
                    int temp = at[j];
                    at[j] = at[j + 1];
                    at[j + 1] = temp;

                    // Swap Burst Times
                    temp = bt[j];
                    bt[j] = bt[j + 1];
                    bt[j + 1] = temp;

                    // Swap Process IDs
                    temp = processId[j];
                    processId[j] = processId[j + 1];
                    processId[j + 1] = temp;
                }
            }
        }

        // Arrays to store Completion Time, Turnaround Time, and Waiting Time
        int[] ct = new int[n], tat = new int[n], wt = new int[n];
        int totalTAT = 0, totalWT = 0;
        int currentTime = 0;

        // Clear previous results in the table
        DefaultTableModel model = (DefaultTableModel) resultTable.getModel();
        model.setRowCount(0);

        // Calculate Completion Time, Turnaround Time, and Waiting Time
        for (int i = 0; i < n; i++) {
            // If current time is less than the arrival time, set current time to arrival time
            if (currentTime < at[i]) {
                currentTime = at[i];
            }

            // Calculate Completion Time (CT), Turnaround Time (TAT), and Waiting Time (WT)
            ct[i] = currentTime + bt[i];
            tat[i] = ct[i] - at[i];  // TAT = CT - AT
            wt[i] = tat[i] - bt[i];  // WT = TAT - BT

            // Update current time
            currentTime = ct[i];

            // Update total TAT and WT
            totalTAT += tat[i];
            totalWT += wt[i];

            // Update the table with the results
            model.addRow(new Object[]{processId[i], at[i], bt[i], ct[i], tat[i], wt[i]});
        }

        // Calculate the averages
        double avgTAT = (double) totalTAT / n;
        double avgWT = (double) totalWT / n;

        // Display averages in the labels
        avgTATLabel.setText("Average Turnaround Time: " + String.format("%.2f", avgTAT));
        avgWTLabel.setText("Average Waiting Time: " + String.format("%.2f", avgWT));
    }


    // Round Robin Scheduling Page (Preemptive)
    private void showRoundRobinPage() {
        JFrame frame = new JFrame("Round Robin Scheduling");
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Input panel
        JPanel inputPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        JLabel numProcessesLabel = new JLabel("Enter number of processes:");
        JTextField numProcessesField = new JTextField();
        JLabel arrivalTimeLabel = new JLabel("Arrival Times (comma-separated):");
        JTextField arrivalTimeField = new JTextField();
        JLabel burstTimeLabel = new JLabel("Burst Times (comma-separated):");
        JTextField burstTimeField = new JTextField();
        JLabel timeQuantumLabel = new JLabel("Enter Time Quantum:");
        JTextField timeQuantumField = new JTextField();
        JButton calculateButton = new JButton("Calculate");
        JButton backButton = new JButton("Back");

        inputPanel.add(numProcessesLabel);
        inputPanel.add(numProcessesField);
        inputPanel.add(arrivalTimeLabel);
        inputPanel.add(arrivalTimeField);
        inputPanel.add(burstTimeLabel);
        inputPanel.add(burstTimeField);
        inputPanel.add(timeQuantumLabel);
        inputPanel.add(timeQuantumField);
        inputPanel.add(new JLabel()); // Spacer
        inputPanel.add(calculateButton);
        inputPanel.add(backButton);

        // Table for results
        JTable resultTable = new JTable(new DefaultTableModel(
                new Object[]{"Process", "AT", "BT", "CT", "TAT", "WT"}, 0
        ));
        JScrollPane scrollPane = new JScrollPane(resultTable);

        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        calculateButton.addActionListener(e -> {
            try {
                int n = Integer.parseInt(numProcessesField.getText().trim());
                int[] at = Arrays.stream(arrivalTimeField.getText().split(","))
                        .mapToInt(Integer::parseInt).toArray();
                int[] bt = Arrays.stream(burstTimeField.getText().split(","))
                        .mapToInt(Integer::parseInt).toArray();
                int timeQuantum = Integer.parseInt(timeQuantumField.getText().trim());

                if (at.length != n || bt.length != n) {
                    JOptionPane.showMessageDialog(frame, "Number of inputs does not match the number of processes.");
                    return;
                }

                calculateRoundRobin(n, at, bt, timeQuantum, resultTable);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Invalid input. Please try again.");
            }
        });
        backButton.addActionListener(e -> mainFrame.dispose());
        inputPanel.add(backButton);
        mainFrame.setVisible(true);

        frame.add(mainPanel);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void calculateRoundRobin(int n, int[] at, int[] bt, int timeQuantum, JTable resultTable) {
        int[] processId = new int[n];
        int[] remainingTime = Arrays.copyOf(bt, n);
        int[] completionTime = new int[n];
        int[] turnaroundTime = new int[n];
        int[] waitingTime = new int[n];
        boolean[] completed = new boolean[n];
        int currentTime = 0;
        int completedCount = 0;

        for (int i = 0; i < n; i++) {
            processId[i] = i + 1;
        }

        while (completedCount < n) {
            boolean allProcessesIdle = true;

            for (int i = 0; i < n; i++) {
                if (at[i] <= currentTime && !completed[i]) {
                    allProcessesIdle = false;

                    if (remainingTime[i] > 0) {
                        int timeToExecute = Math.min(remainingTime[i], timeQuantum);
                        remainingTime[i] -= timeToExecute;
                        currentTime += timeToExecute;

                        if (remainingTime[i] == 0) {
                            completed[i] = true;
                            completionTime[i] = currentTime;
                            completedCount++;
                        }
                    }
                }
            }

            if (allProcessesIdle) {
                currentTime++;
            }
        }

        // Calculate Turnaround Time and Waiting Time
        for (int i = 0; i < n; i++) {
            turnaroundTime[i] = completionTime[i] - at[i];
            waitingTime[i] = turnaroundTime[i] - bt[i];
        }

        displayRoundRobinResults(n, processId, at, bt, completionTime, turnaroundTime, waitingTime, resultTable);
    }

    private static void displayRoundRobinResults(int n, int[] processId, int[] at, int[] bt, int[] ct, int[] tat, int[] wt, JTable resultTable) {
        DefaultTableModel model = (DefaultTableModel) resultTable.getModel();
        model.setRowCount(0);

        for (int i = 0; i < n; i++) {
            model.addRow(new Object[]{processId[i], at[i], bt[i], ct[i], tat[i], wt[i]});
        }

        double avgTAT = Arrays.stream(tat).average().orElse(0);
        double avgWT = Arrays.stream(wt).average().orElse(0);
        JOptionPane.showMessageDialog(null, String.format("Average TAT: %.2f\nAverage WT: %.2f", avgTAT, avgWT));
    }

    // Priority Scheduling Page (Non-Preemptive and Preemptive)
    private void showPrioritySchedulingPage() {
        JFrame frame = new JFrame("Priority Scheduling");
        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel priorityPanel = new JPanel();
        priorityPanel.setLayout(new BorderLayout());

        // Input panel
        JPanel inputPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        JLabel numProcessesLabel = new JLabel("Enter number of processes:");
        JTextField numProcessesField = new JTextField();
        JLabel arrivalTimeLabel = new JLabel("Arrival Times (comma-separated):");
        JTextField arrivalTimeField = new JTextField();
        JLabel burstTimeLabel = new JLabel("Burst Times (comma-separated):");
        JTextField burstTimeField = new JTextField();
        JLabel priorityLabel = new JLabel("Priorities (comma-separated):");
        JTextField priorityField = new JTextField();
        JLabel schedulingTypeLabel = new JLabel("Select Scheduling Type:");
        JComboBox<String> schedulingTypeComboBox = new JComboBox<>(new String[]{"Non-Preemptive", "Preemptive"});
        JButton calculateButton = new JButton("Calculate");
        JButton backButton = new JButton("Back");

        inputPanel.add(numProcessesLabel);
        inputPanel.add(numProcessesField);
        inputPanel.add(arrivalTimeLabel);
        inputPanel.add(arrivalTimeField);
        inputPanel.add(burstTimeLabel);
        inputPanel.add(burstTimeField);
        inputPanel.add(priorityLabel);
        inputPanel.add(priorityField);
        inputPanel.add(schedulingTypeLabel);
        inputPanel.add(schedulingTypeComboBox);
        inputPanel.add(new JLabel()); // Placeholder for spacing
        inputPanel.add(calculateButton);
        inputPanel.add(backButton);

        // Table for results
        JTable resultTable = new JTable(new DefaultTableModel(
                new Object[]{"Process", "AT", "BT", "Priority", "CT", "TAT", "WT"}, 0
        ));
        JScrollPane scrollPane = new JScrollPane(resultTable);

        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        calculateButton.addActionListener(e -> {
            try {
                int n = Integer.parseInt(numProcessesField.getText().trim());
                int[] at = Arrays.stream(arrivalTimeField.getText().split(","))
                        .mapToInt(Integer::parseInt).toArray();
                int[] bt = Arrays.stream(burstTimeField.getText().split(","))
                        .mapToInt(Integer::parseInt).toArray();
                int[] priority = Arrays.stream(priorityField.getText().split(","))
                        .mapToInt(Integer::parseInt).toArray();

                if (at.length != n || bt.length != n || priority.length != n) {
                    JOptionPane.showMessageDialog(frame, "Number of inputs does not match the number of processes.");
                    return;
                }

                String type = (String) schedulingTypeComboBox.getSelectedItem();
                if ("Non-Preemptive".equals(type)) {
                    calculateNonPreemptivePriority(n, at, bt, priority, resultTable);
                } else {
                    calculatePreemptivePriority(n, at, bt, priority, resultTable);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Invalid input. Please try again.");
            }
        });
        backButton.addActionListener(e -> mainFrame.dispose());
        inputPanel.add(backButton);
        mainFrame.setVisible(true);

        frame.add(mainPanel);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void calculateNonPreemptivePriority(int n, int[] at, int[] bt, int[] priority, JTable resultTable) {
        int[][] processes = new int[n][5]; // processId, arrivalTime, burstTime, priority, completionTime
        for (int i = 0; i < n; i++) {
            processes[i][0] = i + 1;
            processes[i][1] = at[i];
            processes[i][2] = bt[i];
            processes[i][3] = priority[i];
        }

        Arrays.sort(processes, Comparator.comparingInt(p -> p[1]));

        int currentTime = 0;
        boolean[] completed = new boolean[n];
        int completedCount = 0;

        while (completedCount < n) {
            int idx = -1;
            int highestPriority = Integer.MAX_VALUE;

            for (int i = 0; i < n; i++) {
                if (!completed[i] && processes[i][1] <= currentTime && processes[i][3] < highestPriority) {
                    highestPriority = processes[i][3];
                    idx = i;
                }
            }

            if (idx == -1) {
                currentTime++;
            } else {
                completed[idx] = true;
                currentTime += processes[idx][2];
                processes[idx][4] = currentTime;
                completedCount++;
            }
        }

        displayResults(n, processes, resultTable);
    }

    private static void calculatePreemptivePriority(int n, int[] at, int[] bt, int[] priority, JTable resultTable) {
        int[] remainingTime = Arrays.copyOf(bt, n);
        int[] completionTime = new int[n];
        boolean[] completed = new boolean[n];
        int currentTime = 0;
        int completedCount = 0;

        while (completedCount < n) {
            int idx = -1;
            int highestPriority = Integer.MAX_VALUE;

            for (int i = 0; i < n; i++) {
                if (!completed[i] && at[i] <= currentTime && priority[i] < highestPriority) {
                    highestPriority = priority[i];
                    idx = i;
                }
            }

            if (idx == -1) {
                currentTime++;
            } else {
                remainingTime[idx]--;
                currentTime++;

                if (remainingTime[idx] == 0) {
                    completed[idx] = true;
                    completedCount++;
                    completionTime[idx] = currentTime;
                }
            }
        }

        int[][] processes = new int[n][5];
        for (int i = 0; i < n; i++) {
            processes[i][0] = i + 1;
            processes[i][1] = at[i];
            processes[i][2] = bt[i];
            processes[i][3] = priority[i];
            processes[i][4] = completionTime[i];
        }

        displayResults(n, processes, resultTable);
    }

    private static void displayResults(int n, int[][] processes, JTable resultTable) {
        int[] turnaroundTime = new int[n];
        int[] waitingTime = new int[n];

        for (int i = 0; i < n; i++) {
            turnaroundTime[i] = processes[i][4] - processes[i][1];
            waitingTime[i] = turnaroundTime[i] - processes[i][2];
        }

        DefaultTableModel model = (DefaultTableModel) resultTable.getModel();
        model.setRowCount(0);

        for (int i = 0; i < n; i++) {
            model.addRow(new Object[]{
                    processes[i][0], processes[i][1], processes[i][2], processes[i][3],
                    processes[i][4], turnaroundTime[i], waitingTime[i]
            });
        }

        double avgTAT = Arrays.stream(turnaroundTime).average().orElse(0);
        double avgWT = Arrays.stream(waitingTime).average().orElse(0);
        JOptionPane.showMessageDialog(null, String.format("Average TAT: %.2f\nAverage WT: %.2f", avgTAT, avgWT));
    }

    // SJF Page (Non-Preemptive and Preemptive)
    private void showSJFPage() {
        JFrame frame = new JFrame("SJF Scheduling");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        JPanel sjfPanel = new JPanel();
        sjfPanel.setLayout(new BorderLayout());

        // Input form
        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        JLabel numProcessesLabel = new JLabel("Enter number of processes:");
        JTextField numProcessesField = new JTextField();
        JLabel arrivalLabel = new JLabel("Arrival Times (comma separated):");
        JTextField arrivalField = new JTextField();
        JLabel burstLabel = new JLabel("Burst Times (comma separated):");
        JTextField burstField = new JTextField();
        JLabel schedulingTypeLabel = new JLabel("Select Scheduling Type:");
        JComboBox<String> schedulingTypeComboBox = new JComboBox<>(new String[]{"Non-Preemptive", "Preemptive"});
        JButton calculateButton = new JButton("Calculate");
        JButton backButton = new JButton("Back");

        inputPanel.add(numProcessesLabel);
        inputPanel.add(numProcessesField);
        inputPanel.add(arrivalLabel);
        inputPanel.add(arrivalField);
        inputPanel.add(burstLabel);
        inputPanel.add(burstField);
        inputPanel.add(schedulingTypeLabel);
        inputPanel.add(schedulingTypeComboBox);
        inputPanel.add(new JLabel());
        inputPanel.add(calculateButton);
        inputPanel.add(backButton);

        // Table for displaying results
        JTable resultTable = new JTable(new DefaultTableModel(new Object[]{"Process", "AT", "BT", "CT", "TAT", "WT"}, 0));
        JScrollPane scrollPane = new JScrollPane(resultTable);

        sjfPanel.add(inputPanel, BorderLayout.NORTH);
        sjfPanel.add(scrollPane, BorderLayout.CENTER);

        calculateButton.addActionListener(e -> {
            try {
                int n = Integer.parseInt(numProcessesField.getText().trim());
                String[] arrivalTimes = arrivalField.getText().split(",");
                String[] burstTimes = burstField.getText().split(",");

                if (arrivalTimes.length != n || burstTimes.length != n) {
                    JOptionPane.showMessageDialog(frame, "Mismatch in number of processes and inputs.");
                    return;
                }

                int[] at = Arrays.stream(arrivalTimes).mapToInt(Integer::parseInt).toArray();
                int[] bt = Arrays.stream(burstTimes).mapToInt(Integer::parseInt).toArray();

                String type = (String) schedulingTypeComboBox.getSelectedItem();
                if ("Non-Preemptive".equals(type)) {
                    calculateNonPreemptiveSJF(n, at, bt, resultTable);
                } else {
                    calculatePreemptiveSJF(n, at, bt, resultTable);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Invalid input. Please try again.");
            }
        });
        backButton.addActionListener(e -> mainFrame.dispose());
        inputPanel.add(backButton);
        mainFrame.setVisible(true);

        frame.add(sjfPanel);
        frame.setVisible(true);
    }

    private static void calculateNonPreemptiveSJF(int n, int[] at, int[] bt, JTable resultTable) {
        int[][] processes = new int[n][5]; // processId, arrivalTime, burstTime, completionTime, turnaroundTime
        for (int i = 0; i < n; i++) {
            processes[i][0] = i + 1;
            processes[i][1] = at[i];
            processes[i][2] = bt[i];
        }

        // Sort by arrival time
        Arrays.sort(processes, Comparator.comparingInt(p -> p[1]));

        int currentTime = 0, completedCount = 0;
        boolean[] completed = new boolean[n];

        while (completedCount < n) {
            int idx = -1;
            int minBurstTime = Integer.MAX_VALUE;

            for (int i = 0; i < n; i++) {
                if (!completed[i] && processes[i][1] <= currentTime && processes[i][2] < minBurstTime) {
                    minBurstTime = processes[i][2];
                    idx = i;
                }
            }

            if (idx == -1) {
                currentTime++;
            } else {
                completed[idx] = true;
                currentTime += processes[idx][2];
                processes[idx][3] = currentTime; // Completion time
                processes[idx][4] = processes[idx][3] - processes[idx][1]; // Turnaround time
                completedCount++;
            }
        }

        // Calculate waiting time
        int[][] finalResults = new int[n][6]; // PID, AT, BT, CT, TAT, WT
        for (int i = 0; i < n; i++) {
            int tat = processes[i][4];
            int wt = tat - processes[i][2];
            finalResults[i][0] = processes[i][0];
            finalResults[i][1] = processes[i][1];
            finalResults[i][2] = processes[i][2];
            finalResults[i][3] = processes[i][3];
            finalResults[i][4] = tat;
            finalResults[i][5] = wt;
        }

        // Show in JTable
        String[] columnNames = {"Process ID", "Arrival Time", "Burst Time", "Completion Time", "Turnaround Time", "Waiting Time"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        for (int i = 0; i < n; i++) {
            model.addRow(new Object[]{
                    finalResults[i][0],
                    finalResults[i][1],
                    finalResults[i][2],
                    finalResults[i][3],
                    finalResults[i][4],
                    finalResults[i][5]
            });
        }

        resultTable.setModel(model);
        displayResultsForSjf(n, processes, resultTable);
    }


    private static void calculatePreemptiveSJF(int n, int[] at, int[] bt, JTable resultTable) {
        int[] remainingTime = Arrays.copyOf(bt, n);
        int[] completionTime = new int[n];
        boolean[] completed = new boolean[n];
        int currentTime = 0, completedCount = 0;

        while (completedCount < n) {
            int idx = -1, minTime = Integer.MAX_VALUE;

            for (int i = 0; i < n; i++) {
                if (!completed[i] && at[i] <= currentTime && remainingTime[i] < minTime) {
                    minTime = remainingTime[i];
                    idx = i;
                }
            }

            if (idx == -1) {
                currentTime++;
            } else {
                remainingTime[idx]--;
                currentTime++;

                if (remainingTime[idx] == 0) {
                    completed[idx] = true;
                    completedCount++;
                    completionTime[idx] = currentTime;
                }
            }
        }

        int[][] processes = new int[n][4];
        for (int i = 0; i < n; i++) {
            processes[i][0] = i + 1;
            processes[i][1] = at[i];
            processes[i][2] = bt[i];
            processes[i][3] = completionTime[i];
        }

        displayResultsForSjf(n, processes, resultTable);
    }

    private static void displayResultsForSjf(int n, int[][] processes, JTable resultTable) {
        int[] turnaroundTime = new int[n];
        int[] waitingTime = new int[n];

        for (int i = 0; i < n; i++) {
            turnaroundTime[i] = processes[i][3] - processes[i][1];
            waitingTime[i] = turnaroundTime[i] - processes[i][2];
        }

        DefaultTableModel model = (DefaultTableModel) resultTable.getModel();
        model.setRowCount(0);

        for (int i = 0; i < n; i++) {
            model.addRow(new Object[]{
                    processes[i][0], processes[i][1], processes[i][2],
                    processes[i][3], turnaroundTime[i], waitingTime[i]
            });
        }

        double avgTAT = Arrays.stream(turnaroundTime).average().orElse(0);
        double avgWT = Arrays.stream(waitingTime).average().orElse(0);
        JOptionPane.showMessageDialog(null, String.format("Average TAT: %.2f\nAverage WT: %.2f", avgTAT, avgWT));
    }

}