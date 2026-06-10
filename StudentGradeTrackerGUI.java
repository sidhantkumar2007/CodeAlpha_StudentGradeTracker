import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

/**
 * CodeAlpha — Task 1: Student Grade Tracker (GUI Version)
 * Built with Java Swing
 * Updated: Added Save/Load feature
 */
public class StudentGradeTrackerGUI extends JFrame {

    // ── Colors & Fonts ────────────────────────────────────────────────────
    static final Color BG        = new Color(15, 23, 42);
    static final Color CARD      = new Color(30, 41, 59);
    static final Color ACCENT    = new Color(99, 102, 241);
    static final Color ACCENT2   = new Color(16, 185, 129);
    static final Color DANGER    = new Color(239, 68, 68);
    static final Color YELLOW    = new Color(234, 179, 8);
    static final Color TEXT      = new Color(248, 250, 252);
    static final Color SUBTEXT   = new Color(148, 163, 184);
    static final Color BORDER    = new Color(51, 65, 85);
    static final Font  TITLE_F   = new Font("Segoe UI", Font.BOLD, 22);
    static final Font  HEADER_F  = new Font("Segoe UI", Font.BOLD, 13);
    static final Font  BODY_F    = new Font("Segoe UI", Font.PLAIN, 13);
    static final Font  SMALL_F   = new Font("Segoe UI", Font.PLAIN, 11);

    // ── Data ──────────────────────────────────────────────────────────────
    static class Student {
        String name;
        ArrayList<Double> grades = new ArrayList<>();
        Student(String n) { this.name = n; }
        double avg() { return grades.isEmpty() ? 0 : grades.stream().mapToDouble(x->x).average().orElse(0); }
        double high() { return grades.isEmpty() ? 0 : Collections.max(grades); }
        double low()  { return grades.isEmpty() ? 0 : Collections.min(grades); }
        String letter() {
            double a = avg();
            if (a>=90) return "A"; if (a>=80) return "B";
            if (a>=70) return "C"; if (a>=60) return "D"; return "F";
        }
        String status() { return avg() >= 50 ? "PASS" : "FAIL"; }
    }

    ArrayList<Student> students = new ArrayList<>();
    DefaultTableModel tableModel;
    JLabel statAvg, statPass, statTop, statCount;
    JTextArea gradesArea;

    // ── Constructor ───────────────────────────────────────────────────────
    public StudentGradeTrackerGUI() {
        setTitle("Student Grade Tracker — CodeAlpha");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setBackground(BG);

        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(BG);

        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildMain(), BorderLayout.CENTER);

        setContentPane(root);
        setVisible(true);
    }

    // ── Header ────────────────────────────────────────────────────────────
    JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(CARD);
        p.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));
        p.setPreferredSize(new Dimension(0, 64));

        JLabel title = new JLabel("  📊 Student Grade Tracker");
        title.setFont(TITLE_F);
        title.setForeground(TEXT);

        JLabel sub = new JLabel("CodeAlpha Internship Project  ");
        sub.setFont(SMALL_F);
        sub.setForeground(SUBTEXT);

        p.add(title, BorderLayout.WEST);
        p.add(sub, BorderLayout.EAST);
        return p;
    }

    // ── Main Layout ───────────────────────────────────────────────────────
    JPanel buildMain() {
        JPanel p = new JPanel(new BorderLayout(12, 12));
        p.setBackground(BG);
        p.setBorder(new EmptyBorder(16, 16, 16, 16));

        p.add(buildLeftPanel(), BorderLayout.WEST);
        p.add(buildCenterPanel(), BorderLayout.CENTER);
        p.add(buildRightPanel(), BorderLayout.EAST);

        return p;
    }

    // ── Left: Add Student & Grades ────────────────────────────────────────
    JPanel buildLeftPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(BG);
        p.setPreferredSize(new Dimension(260, 0));

        // Add Student card
        JPanel addCard = card("➕ Add Student");
        JTextField nameField = styledField("Student name...");
        JButton addBtn = accentBtn("Add Student", ACCENT);
        addBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty() || name.equals("Student name...")) {
                showError("Please enter a student name."); return;
            }
            for (Student s : students) if (s.name.equalsIgnoreCase(name)) {
                showError("Student already exists."); return;
            }
            students.add(new Student(name));
            nameField.setText("");
            refreshTable();
            updateStats();
            showSuccess("Student '" + name + "' added!");
        });
        addCard.add(label("Full Name")); addCard.add(Box.createVerticalStrut(4));
        addCard.add(nameField); addCard.add(Box.createVerticalStrut(8));
        addCard.add(addBtn);

        // Add Grades card
        JPanel gradeCard = card("📝 Add Grades");
        JComboBox<String> studentCombo = new JComboBox<>();
        studentCombo.setBackground(CARD); studentCombo.setForeground(TEXT);
        studentCombo.setFont(BODY_F);
        JTextField gradeField = styledField("Grade (0-100)...");
        JButton addGradeBtn = accentBtn("Add Grade", ACCENT2);
        addGradeBtn.addActionListener(e -> {
            String sel = (String) studentCombo.getSelectedItem();
            if (sel == null) { showError("No student selected."); return; }
            try {
                double g = Double.parseDouble(gradeField.getText().trim());
                if (g < 0 || g > 100) { showError("Grade must be 0–100."); return; }
                Student s = findStudent(sel);
                if (s != null) { s.grades.add(g); gradeField.setText(""); refreshTable(); updateStats(); showSuccess("Grade added!"); }
            } catch (NumberFormatException ex) { showError("Enter a valid number."); }
        });
        gradeCard.add(label("Select Student")); gradeCard.add(Box.createVerticalStrut(4));
        gradeCard.add(studentCombo); gradeCard.add(Box.createVerticalStrut(8));
        gradeCard.add(label("Grade Value")); gradeCard.add(Box.createVerticalStrut(4));
        gradeCard.add(gradeField); gradeCard.add(Box.createVerticalStrut(8));
        gradeCard.add(addGradeBtn);

        // Remove Student card
        JPanel removeCard = card("🗑 Remove Student");
        JButton removeBtn = accentBtn("Remove Selected", DANGER);
        removeBtn.addActionListener(e -> {
            String sel = (String) studentCombo.getSelectedItem();
            if (sel == null) { showError("No student selected."); return; }
            students.removeIf(s -> s.name.equals(sel));
            refreshTable(); updateStats();
            showSuccess("Student removed.");
        });
        removeCard.add(label("Select from dropdown above"));
        removeCard.add(Box.createVerticalStrut(8));
        removeCard.add(removeBtn);

        // ── Save / Load card ─────────────────────────────────────────────
        JPanel fileCard = card("💾 Save / Load");
        JButton saveBtn = accentBtn("💾 Save Data", ACCENT);
        JButton loadBtn = accentBtn("📂 Load Data", YELLOW);
        saveBtn.addActionListener(e -> saveData());
        loadBtn.addActionListener(e -> loadData());
        fileCard.add(saveBtn);
        fileCard.add(Box.createVerticalStrut(6));
        fileCard.add(loadBtn);

        // Timer to update combo
        javax.swing.Timer t = new javax.swing.Timer(500, e -> {
            String prev = (String) studentCombo.getSelectedItem();
            studentCombo.removeAllItems();
            for (Student s : students) studentCombo.addItem(s.name);
            if (prev != null) studentCombo.setSelectedItem(prev);
        });
        t.start();

        p.add(addCard); p.add(Box.createVerticalStrut(12));
        p.add(gradeCard); p.add(Box.createVerticalStrut(12));
        p.add(removeCard); p.add(Box.createVerticalStrut(12));
        p.add(fileCard);
        return p;
    }

    // ── Center: Table ─────────────────────────────────────────────────────
    JPanel buildCenterPanel() {
        JPanel p = new JPanel(new BorderLayout(0, 12));
        p.setBackground(BG);

        // Stats row
        JPanel stats = new JPanel(new GridLayout(1, 4, 10, 0));
        stats.setBackground(BG);
        statCount = statCard("Total Students", "0");
        statAvg   = statCard("Class Average", "0.00");
        statPass  = statCard("Pass Rate", "0%");
        statTop   = statCard("Top Student", "—");
        stats.add(statCount); stats.add(statAvg);
        stats.add(statPass);  stats.add(statTop);

        // Table
        String[] cols = {"Name", "Grades", "Average", "Highest", "Lowest", "Letter", "Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(tableModel);
        table.setBackground(CARD);
        table.setForeground(TEXT);
        table.setFont(BODY_F);
        table.setRowHeight(36);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.getTableHeader().setBackground(new Color(15, 23, 42));
        table.getTableHeader().setForeground(SUBTEXT);
        table.getTableHeader().setFont(HEADER_F);
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0,0,1,0,BORDER));
        table.setSelectionBackground(new Color(99, 102, 241, 80));
        table.setSelectionForeground(TEXT);

        // Status column renderer
        table.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                l.setHorizontalAlignment(JLabel.CENTER);
                l.setForeground("PASS".equals(v) ? ACCENT2 : DANGER);
                l.setFont(new Font("Segoe UI", Font.BOLD, 12));
                return l;
            }
        });

        // Grade detail panel
        gradesArea = new JTextArea(4, 0);
        gradesArea.setBackground(new Color(15, 23, 42));
        gradesArea.setForeground(SUBTEXT);
        gradesArea.setFont(SMALL_F);
        gradesArea.setEditable(false);
        gradesArea.setBorder(new EmptyBorder(8,8,8,8));
        gradesArea.setText("  Select a student to view their individual grades...");
        table.getSelectionModel().addListSelectionListener(ev -> {
            int row = table.getSelectedRow();
            if (row >= 0 && row < students.size()) {
                Student s = students.get(row);
                StringBuilder sb = new StringBuilder("  Grades for " + s.name + ": ");
                if (s.grades.isEmpty()) sb.append("No grades yet.");
                else for (int i = 0; i < s.grades.size(); i++)
                    sb.append(String.format("%.0f", s.grades.get(i))).append(i < s.grades.size()-1 ? ", " : "");
                gradesArea.setText(sb.toString());
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBackground(CARD);
        scroll.getViewport().setBackground(CARD);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER));

        JScrollPane gradeScroll = new JScrollPane(gradesArea);
        gradeScroll.setBackground(new Color(15,23,42));
        gradeScroll.getViewport().setBackground(new Color(15,23,42));
        gradeScroll.setBorder(BorderFactory.createLineBorder(BORDER));
        gradeScroll.setPreferredSize(new Dimension(0, 70));

        p.add(stats, BorderLayout.NORTH);
        p.add(scroll, BorderLayout.CENTER);
        p.add(gradeScroll, BorderLayout.SOUTH);
        return p;
    }

    // ── Right: Summary Report ─────────────────────────────────────────────
    JPanel buildRightPanel() {
        JPanel p = card("📋 Summary Report");
        p.setPreferredSize(new Dimension(200, 0));

        JTextArea report = new JTextArea();
        report.setBackground(CARD);
        report.setForeground(TEXT);
        report.setFont(new Font("Monospaced", Font.PLAIN, 11));
        report.setEditable(false);
        report.setBorder(new EmptyBorder(4,4,4,4));

        JButton genBtn = accentBtn("Generate Report", ACCENT);
        genBtn.addActionListener(e -> {
            if (students.isEmpty()) { report.setText("No students yet."); return; }
            StringBuilder sb = new StringBuilder();
            sb.append("=== GRADE REPORT ===\n\n");
            long a=0,b=0,c=0,d=0,f=0, pass=0;
            for (Student s : students) {
                sb.append(s.name).append("\n");
                sb.append("  Avg: ").append(String.format("%.1f", s.avg()));
                sb.append("  ").append(s.letter()).append(" [").append(s.status()).append("]\n\n");
                switch(s.letter()){case"A"->a++;case"B"->b++;case"C"->c++;case"D"->d++;default->f++;}
                if(s.status().equals("PASS")) pass++;
            }
            sb.append("=== DISTRIBUTION ===\n");
            sb.append("A: ").append(a).append("  B: ").append(b).append("\n");
            sb.append("C: ").append(c).append("  D: ").append(d).append("  F: ").append(f).append("\n\n");
            sb.append("Pass: ").append(pass).append("/").append(students.size()).append("\n");
            double classAvg = students.stream().mapToDouble(Student::avg).average().orElse(0);
            sb.append("Class Avg: ").append(String.format("%.2f", classAvg));
            report.setText(sb.toString());
        });

        p.add(genBtn); p.add(Box.createVerticalStrut(8));
        JScrollPane sc = new JScrollPane(report);
        sc.setBackground(CARD); sc.getViewport().setBackground(CARD);
        sc.setBorder(BorderFactory.createLineBorder(BORDER));
        p.add(sc);
        return p;
    }

    // ── Save / Load ───────────────────────────────────────────────────────
    void saveData() {
        try (PrintWriter pw = new PrintWriter(new FileWriter("grades.txt"))) {
            for (Student s : students) {
                pw.print(s.name);
                for (double g : s.grades) pw.print("," + g);
                pw.println();
            }
            showSuccess("Data saved to grades.txt ✅");
        } catch (IOException ex) {
            showError("Failed to save: " + ex.getMessage());
        }
    }

    void loadData() {
        File f = new File("grades.txt");
        if (!f.exists()) { showError("No saved data found (grades.txt missing)."); return; }
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            students.clear();
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 0 || parts[0].isBlank()) continue;
                Student s = new Student(parts[0]);
                for (int i = 1; i < parts.length; i++) {
                    try { s.grades.add(Double.parseDouble(parts[i])); } catch (NumberFormatException ignored) {}
                }
                students.add(s);
            }
            refreshTable();
            updateStats();
            showSuccess("Data loaded successfully! ✅");
        } catch (IOException ex) {
            showError("Failed to load: " + ex.getMessage());
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────
    void refreshTable() {
        tableModel.setRowCount(0);
        for (Student s : students) {
            String gradesStr = s.grades.isEmpty() ? "—" :
                s.grades.stream().map(g -> String.format("%.0f", g))
                    .reduce((a,b) -> a+", "+b).orElse("");
            tableModel.addRow(new Object[]{
                s.name, gradesStr,
                s.grades.isEmpty() ? "—" : String.format("%.2f", s.avg()),
                s.grades.isEmpty() ? "—" : String.format("%.0f", s.high()),
                s.grades.isEmpty() ? "—" : String.format("%.0f", s.low()),
                s.grades.isEmpty() ? "—" : s.letter(),
                s.grades.isEmpty() ? "—" : s.status()
            });
        }
    }

    void updateStats() {
        statCount.setText("<html><center><span style='font-size:22px;color:#f8fafc'>" + students.size() + "</span><br><span style='color:#94a3b8;font-size:10px'>Total Students</span></center></html>");
        if (students.isEmpty()) return;
        double avg = students.stream().mapToDouble(Student::avg).average().orElse(0);
        long pass = students.stream().filter(s -> s.status().equals("PASS")).count();
        Student top = students.stream().max(Comparator.comparingDouble(Student::avg)).orElse(null);
        statAvg.setText("<html><center><span style='font-size:22px;color:#f8fafc'>" + String.format("%.1f", avg) + "</span><br><span style='color:#94a3b8;font-size:10px'>Class Average</span></center></html>");
        statPass.setText("<html><center><span style='font-size:22px;color:#10b981'>" + (students.isEmpty() ? 0 : (int)(pass*100/students.size())) + "%</span><br><span style='color:#94a3b8;font-size:10px'>Pass Rate</span></center></html>");
        statTop.setText("<html><center><span style='font-size:13px;color:#f8fafc'>" + (top != null ? top.name : "—") + "</span><br><span style='color:#94a3b8;font-size:10px'>Top Student</span></center></html>");
    }

    JLabel statCard(String label, String val) {
        JLabel l = new JLabel("<html><center><span style='font-size:22px;color:#f8fafc'>" + val + "</span><br><span style='color:#94a3b8;font-size:10px'>" + label + "</span></center></html>", JLabel.CENTER);
        l.setOpaque(true); l.setBackground(CARD);
        l.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER), new EmptyBorder(12,8,12,8)));
        return l;
    }

    JPanel card(String title) {
        JPanel p = new JPanel(); p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(CARD); p.setAlignmentX(0);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER),
            new EmptyBorder(14, 14, 14, 14)));
        JLabel t = new JLabel(title); t.setFont(HEADER_F); t.setForeground(TEXT);
        t.setAlignmentX(0); p.add(t); p.add(Box.createVerticalStrut(10));
        return p;
    }

    JTextField styledField(String placeholder) {
        JTextField f = new JTextField(placeholder);
        f.setBackground(new Color(15, 23, 42)); f.setForeground(SUBTEXT);
        f.setCaretColor(TEXT); f.setFont(BODY_F);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER), new EmptyBorder(6,8,6,8)));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        f.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) { if (f.getText().equals(placeholder)) { f.setText(""); f.setForeground(TEXT); } }
            public void focusLost(FocusEvent e)   { if (f.getText().isEmpty()) { f.setText(placeholder); f.setForeground(SUBTEXT); } }
        });
        return f;
    }

    JButton accentBtn(String text, Color color) {
        JButton b = new JButton(text); b.setFont(HEADER_F);
        b.setBackground(color); b.setForeground(Color.WHITE);
        b.setBorder(new EmptyBorder(9, 14, 9, 14));
        b.setFocusPainted(false); b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setAlignmentX(0); b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(color.darker()); }
            public void mouseExited(MouseEvent e)  { b.setBackground(color); }
        });
        return b;
    }

    JLabel label(String text) {
        JLabel l = new JLabel(text); l.setFont(SMALL_F); l.setForeground(SUBTEXT); l.setAlignmentX(0); return l;
    }

    Student findStudent(String name) { for (Student s : students) if (s.name.equals(name)) return s; return null; }

    void showError(String msg)   { JOptionPane.showMessageDialog(this, msg, "Error",   JOptionPane.ERROR_MESSAGE); }
    void showSuccess(String msg) { JOptionPane.showMessageDialog(this, msg, "Success", JOptionPane.INFORMATION_MESSAGE); }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
        SwingUtilities.invokeLater(StudentGradeTrackerGUI::new);
    }
}
