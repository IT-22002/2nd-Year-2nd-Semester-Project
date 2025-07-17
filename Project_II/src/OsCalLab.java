import javax.swing.*;
import java.awt.*;

public class OsCalLab {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(OsCalLab::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Operating System Algorithms Calculator Homepage");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 400);
        frame.setLocationRelativeTo(null);
        JPanel initialPanel = new JPanel(new BorderLayout());
        initialPanel.setBackground(new Color(135, 206, 250)); //Background is light purple
        JLabel titleLabel = new JLabel(
                "<html><div style='text-align: center;'>Operating System Algorithms Calculator<br>(Creator: Tasnim Zannat Naima(IT-22001) and MD.Atif Rahman Rudro(IT-22002))</div></html>",
                SwingConstants.CENTER
        );
        titleLabel.setFont(new Font("Times New Roman", Font.BOLD, 24));
        titleLabel.setForeground(new Color(75, 0, 130)); // Deep purple text

        JButton seeAllBtn = new JButton("See All Algorithms");
        seeAllBtn.setFont(new Font("Times New Roman", Font.BOLD, 16));
        seeAllBtn.setBackground(new Color(138, 43, 226)); // Blue violet
        seeAllBtn.setForeground(Color.WHITE);
        seeAllBtn.setFocusPainted(false);


        // Action Listener for "See All Algorithms" button
        seeAllBtn.addActionListener(e -> {
            // Replace the initial panel with the main algorithm buttons panel
            JPanel algorithmPanel = createAlgorithmPanel(frame,initialPanel);
            frame.setContentPane(algorithmPanel);
            frame.revalidate();
            frame.repaint();
        });

        initialPanel.add(titleLabel, BorderLayout.CENTER);
        initialPanel.add(seeAllBtn, BorderLayout.SOUTH);

        // Set the initial panel as the content pane
        frame.setContentPane(initialPanel);
        frame.setVisible(true);
    }
    private static JPanel createAlgorithmPanel(JFrame frame,JPanel initialPanel) {

        JPanel panel = new JPanel(new GridLayout(6, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(230, 220, 255)); // Same light purple background

        Font buttonFont = new Font("Arial", Font.PLAIN, 16);
        Color buttonColor = new Color(138, 43, 226); // Blue violet
        Color textColor = Color.WHITE;

        JButton processSchedulingBtn = new JButton("Process Scheduling");
        JButton bankersBtn = new JButton("Banker's Algorithm");
        JButton memoryBtn = new JButton("Memory Allocation");
        JButton pageBtn = new JButton("Page Replacement");
        JButton diskBtn = new JButton("Disk Scheduling");
        JButton backBtn = new JButton("Back");
        JButton exitBtn = new JButton("Exit");

        JButton[] buttons = {
                processSchedulingBtn, bankersBtn, memoryBtn, pageBtn, diskBtn, backBtn, exitBtn
        };

        for (JButton btn : buttons) {
            btn.setFont(buttonFont);
            btn.setBackground(buttonColor);
            btn.setForeground(textColor);
            btn.setFocusPainted(false);
            panel.add(btn);
        }

        // Action Listeners
        processSchedulingBtn.addActionListener(e -> new ProcessSchedulingGUI().showUI());
        bankersBtn.addActionListener(e -> new BankersAlgorithmGUI().showUI());
        memoryBtn.addActionListener(e -> new MemoryAllocationGUI());
        pageBtn.addActionListener(e -> new PageReplacementGUI().showUI());
        diskBtn.addActionListener(e -> new DiskSchedulingGUI().showUI());
        backBtn.addActionListener(e -> {
            frame.setContentPane(initialPanel);
            frame.revalidate();
            frame.repaint();
        });

        exitBtn.addActionListener(e -> System.exit(0));

        panel.add(processSchedulingBtn);
        panel.add(bankersBtn);
        panel.add(memoryBtn);
        panel.add(pageBtn);
        panel.add(diskBtn);
        panel.add(backBtn);
        panel.add(exitBtn);

        return panel;
    }
}