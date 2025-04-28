//import javax.swing.*;
//import java.awt.*;
//import java.awt.event.*;
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.PrintWriter;
//import java.net.Socket;
//
//public class SyncAppGUI {
//    private final MongoSystem mongo = new MongoSystem();
//    private final SQLSystem sql = new SQLSystem();
//    private final PigSystem pig = new PigSystem();
//
//    public void createAndShowGUI() {
//        JFrame frame = new JFrame("🗃️ NoSQL Sync GUI");
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setSize(650, 500);
//
//        JPanel panel = new JPanel();
//        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
//        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
//
//        String[] systems = { "MONGO", "SQL", "PIG" };
//        JComboBox<String> systemDropdown = new JComboBox<>(systems);
//        JComboBox<String> operationDropdown = new JComboBox<>(new String[]{"SET", "GET", "MERGE"});
//        JTextField studentIdField = new JTextField(15);
//        JTextField courseIdField = new JTextField(15);
//        JTextField gradeField = new JTextField(15);
//        JComboBox<String> mergeTargetDropdown = new JComboBox<>(systems);
//
//        JTextArea outputArea = new JTextArea(10, 50);
//        outputArea.setEditable(false);
//        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
//        outputArea.setBackground(Color.BLACK);
//        outputArea.setForeground(Color.GREEN);
//
//        JScrollPane scrollPane = new JScrollPane(outputArea);
//
//        JButton executeButton = new JButton("Execute");
//        JButton clearButton = new JButton("Clear");
//        JButton runPigButton = new JButton("🟣 LOAD PIG Data");
//
//
//        // Wrap buttons in a panel
//        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
//        buttonPanel.add(executeButton);
//        buttonPanel.add(clearButton);
//        buttonPanel.add(runPigButton);
//
//        panel.add(new JLabel("Select System:"));
//        panel.add(systemDropdown);
//        panel.add(new JLabel("Select Operation:"));
//        panel.add(operationDropdown);
//        panel.add(new JLabel("Student ID:"));
//        panel.add(studentIdField);
//        panel.add(new JLabel("Course ID:"));
//        panel.add(courseIdField);
//        panel.add(new JLabel("Grade (for SET):"));
//        panel.add(gradeField);
//        panel.add(new JLabel("Merge With (for MERGE):"));
//        panel.add(mergeTargetDropdown);
//        panel.add(buttonPanel);
//
//        frame.add(panel, BorderLayout.NORTH);
//        frame.add(scrollPane, BorderLayout.CENTER);
//
//        runPigButton.addActionListener(e -> {
//            PigRunner.runPigScript("load_data.pig");
//            JOptionPane.showMessageDialog(null, "Pig script executed — check terminal for output.");
//        });
//
//
//        executeButton.addActionListener(e -> {
//            String system = (String) systemDropdown.getSelectedItem();
//            String operation = (String) operationDropdown.getSelectedItem();
//            String sid = studentIdField.getText().trim();
//            String cid = courseIdField.getText().trim();
//            String grade = gradeField.getText().trim();
//            String mergeWith = (String) mergeTargetDropdown.getSelectedItem();
//
//            SystemInterface sys = getSystem(system);
//
//            try {
//                switch (operation) {
//                    case "SET" -> {
//                        if (!sid.isEmpty() && !cid.isEmpty() && !grade.isEmpty()) {
//                            int confirm = JOptionPane.showConfirmDialog(frame,
//                                    "Are you sure you want to SET (" + sid + "," + cid + ") = " + grade + " in " + system + "?",
//                                    "Confirm Update",
//                                    JOptionPane.YES_NO_OPTION);
//                            if (confirm == JOptionPane.YES_OPTION) {
//                                sys.set(sid, cid, grade);
//                                log(outputArea, "✅ SET done on " + system + ": (" + sid + "," + cid + ") = " + grade, true);
//                            }
//                        } else {
//                            log(outputArea, "❌ Please fill all fields for SET.", false);
//                        }
//                    }
//                    case "GET" -> {
//                        if (!sid.isEmpty() && !cid.isEmpty()) {
//                            String result = sys.get(sid, cid);
//                            simulateLog(outputArea, "📦 GET from " + system + ": (" + sid + "," + cid + ") → " + result, true);
//                        } else {
//                            log(outputArea, "❌ Please enter Student ID and Course ID for GET.", false);
//                        }
//                    }
//                    case "MERGE" -> {
//                        if (!system.equalsIgnoreCase(mergeWith)) {
//                            int confirm = JOptionPane.showConfirmDialog(frame,
//                                    "Do you really want to merge " + mergeWith + " into " + system + "?",
//                                    "Confirm Merge",
//                                    JOptionPane.YES_NO_OPTION);
//                            if (confirm == JOptionPane.YES_OPTION) {
//                                sys.merge(mergeWith);
//                                simulateLog(outputArea, "🔄 " + system + ".MERGE(" + mergeWith + ") completed.", true);
//                            }
//                        } else {
//                            log(outputArea, "❌ Cannot merge a system into itself.", false);
//                        }
//                    }
//                }
//            } catch (Exception ex) {
//                log(outputArea, "❌ Error: " + ex.getMessage(), false);
//                ex.printStackTrace();
//            }
//        });
//
//        clearButton.addActionListener(e -> {
//            studentIdField.setText("");
//            courseIdField.setText("");
//            gradeField.setText("");
//            outputArea.setText("");
//        });
//
//        frame.setVisible(true);
//    }
//
//    private SystemInterface getSystem(String name) {
//        return switch (name.toUpperCase()) {
//            case "MONGO" -> mongo;
//            case "SQL" -> sql;
//            case "PIG" -> pig;
//            default -> throw new IllegalArgumentException("Unknown system: " + name);
//        };
//    }
//
//    private void log(JTextArea output, String message, boolean success) {
//        output.setForeground(success ? Color.GREEN : Color.RED);
//        output.append(message + "\n");
//    }
//
//    private void simulateLog(JTextArea output, String message, boolean success) {
//        output.setForeground(success ? Color.GREEN : Color.RED);
//        for (char ch : message.toCharArray()) {
//            output.append(String.valueOf(ch));
//            try {
//                Thread.sleep(10);  // typing animation effect
//            } catch (InterruptedException ignored) {}
//        }
//        output.append("\n");
//    }
//
//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> new SyncAppGUI().createAndShowGUI());
//    }
//}

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;

public class SyncAppGUI {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public void createAndShowGUI() {
        JFrame frame = new JFrame("🗃️ NoSQL Sync GUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(650, 500);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        String[] systems = { "MONGO", "SQL", "PIG" };
        JComboBox<String> systemDropdown = new JComboBox<>(systems);
        JComboBox<String> operationDropdown = new JComboBox<>(new String[]{"SET", "GET", "MERGE"});
        JTextField studentIdField = new JTextField(15);
        JTextField courseIdField = new JTextField(15);
        JTextField gradeField = new JTextField(15);
        JComboBox<String> mergeTargetDropdown = new JComboBox<>(systems);

        JTextArea outputArea = new JTextArea(10, 50);
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        outputArea.setBackground(Color.BLACK);
        outputArea.setForeground(Color.GREEN);

        JScrollPane scrollPane = new JScrollPane(outputArea);

        JButton executeButton = new JButton("Execute");
        JButton clearButton = new JButton("Clear");
        JButton runPigButton = new JButton("🟣 LOAD PIG Data");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        buttonPanel.add(executeButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(runPigButton);

        panel.add(new JLabel("Select System:"));
        panel.add(systemDropdown);
        panel.add(new JLabel("Select Operation:"));
        panel.add(operationDropdown);
        panel.add(new JLabel("Student ID:"));
        panel.add(studentIdField);
        panel.add(new JLabel("Course ID:"));
        panel.add(courseIdField);
        panel.add(new JLabel("Grade (for SET):"));
        panel.add(gradeField);
        panel.add(new JLabel("Merge With (for MERGE):"));
        panel.add(mergeTargetDropdown);
        panel.add(buttonPanel);

        frame.add(panel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Connect once on startup
        try {
            socket = new Socket("127.0.0.1", 9999);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // Discard the welcome message
            String welcome = in.readLine();
            outputArea.append(welcome + "\n");
        } catch (IOException e) {
            outputArea.setForeground(Color.RED);
            outputArea.setText("❌ Could not connect to server: " + e.getMessage());
        }

        runPigButton.addActionListener(e -> {
            PigRunner.runPigScript("load_data.pig");
            JOptionPane.showMessageDialog(null, "Pig script executed — check terminal for output.");
        });

        executeButton.addActionListener(e -> {
            String system = (String) systemDropdown.getSelectedItem();
            String operation = (String) operationDropdown.getSelectedItem();
            String sid = studentIdField.getText().trim();
            String cid = courseIdField.getText().trim();
            String grade = gradeField.getText().trim();
            String mergeWith = (String) mergeTargetDropdown.getSelectedItem();

            String response = "";
            switch (operation) {
                case "SET" -> {
                    if (!sid.isEmpty() && !cid.isEmpty() && !grade.isEmpty()) {
                        int confirm = JOptionPane.showConfirmDialog(frame,
                                "Are you sure you want to SET (" + sid + "," + cid + ") = " + grade + " in " + system + "?",
                                "Confirm Update", JOptionPane.YES_NO_OPTION);
                        if (confirm == JOptionPane.YES_OPTION) {
                            response = sendToServer("SET," + system + "," + sid + "," + cid + "," + grade);
                        }
                    } else {
                        response = "❌ Please fill all fields for SET.";
                    }
                }
                case "GET" -> {
                    if (!sid.isEmpty() && !cid.isEmpty()) {
                        response = sendToServer("GET," + system + "," + sid + "," + cid);
                    } else {
                        response = "❌ Please enter Student ID and Course ID for GET.";
                    }
                }
                case "MERGE" -> {
                    if (!system.equalsIgnoreCase(mergeWith)) {
                        int confirm = JOptionPane.showConfirmDialog(frame,
                                "Do you really want to merge " + mergeWith + " into " + system + "?",
                                "Confirm Merge", JOptionPane.YES_NO_OPTION);
                        if (confirm == JOptionPane.YES_OPTION) {
                            response = sendToServer("MERGE," + system + "," + mergeWith);
                        }
                    } else {
                        response = "❌ Cannot merge a system into itself.";
                    }
                }
            }

            outputArea.append(response + "\n");
        });

        clearButton.addActionListener(e -> {
            studentIdField.setText("");
            courseIdField.setText("");
            gradeField.setText("");
            outputArea.setText("");
        });

        // Gracefully close socket on exit
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                try {
                    if (out != null) out.close();
                    if (in != null) in.close();
                    if (socket != null) socket.close();
                } catch (IOException ignored) {}
            }
        });

        frame.setVisible(true);
    }

    private String sendToServer(String message) {
        try {
            out.println(message);
            return in.readLine();
        } catch (IOException e) {
            return "❌ Communication error: " + e.getMessage();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SyncAppGUI().createAndShowGUI());
    }
}

