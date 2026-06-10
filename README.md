# 📊 Student Grade Tracker

![Screenshot](screenshot.png)
A Java Swing desktop application for tracking student grades.

---

## Features

- **Add / Remove Students** — Manage a dynamic list of students by name.
- **Add Grades** — Assign multiple grades (0–100) to any student via a dropdown selector.
- **Live Grade Table** — Displays each student's full grade list, average, highest score, lowest score, letter grade, and pass/fail status — updated in real time.
- **Class Statistics Bar** — At-a-glance stats: total students, class average, pass rate, and top student.
- **Grade Detail View** — Click any row in the table to see that student's individual grades listed below.
- **Summary Report** — One-click report showing each student's average, letter grade, pass/fail status, grade distribution (A/B/C/D/F counts), and overall class average.
- **Dark UI Theme** — Custom dark color scheme built with Java Swing.

---

## Grade Scale

| Average Score | Letter Grade |
|---------------|--------------|
| 90 – 100      | A            |
| 80 – 89       | B            |
| 70 – 79       | C            |
| 60 – 69       | D            |
| Below 60      | F            |

Pass threshold: **50 and above**.

---

## Project Structure

```
StudentGradeTrackerGUI.java       # Main source file (single-file project)
StudentGradeTrackerGUI.class      # Compiled main class
StudentGradeTrackerGUI$1.class    # Anonymous inner class (table renderer)
StudentGradeTrackerGUI$2.class    # Anonymous inner class (list selection listener)
StudentGradeTrackerGUI$3.class    # Anonymous inner class (combo box timer)
StudentGradeTrackerGUI$4.class    # Anonymous inner class (button mouse listener)
StudentGradeTrackerGUI$Student.class  # Static inner Student class
```

---

## Requirements

- **Java 17 or higher** (uses switch expressions with arrow syntax)
- No external libraries — standard Java SE only (`javax.swing`, `java.awt`, `java.util`)

---

## How to Run

### Option 1 — Run from pre-compiled `.class` files

Place all `.class` files in the same directory and run:

```bash
java StudentGradeTrackerGUI
```

### Option 2 — Compile and run from source

```bash
javac StudentGradeTrackerGUI.java
java StudentGradeTrackerGUI
```

---

## Usage

1. **Add a student** — Type a name in the *Add Student* panel and click **Add Student**.
2. **Add a grade** — Select the student from the dropdown in the *Add Grades* panel, enter a grade (0–100), and click **Add Grade**.
3. **View stats** — The stats bar at the top of the table updates automatically after every change.
4. **View individual grades** — Click any row in the table to see that student's grades in the detail panel below.
5. **Generate a report** — Click **Generate Report** in the right panel for a full class summary.
6. **Remove a student** — Select the student from the dropdown and click **Remove Selected**.

---
## Author

**Sidhant Kumar**
🎓 Java Programming Intern @ CodeAlpha
🐙 GitHub: [sidhantkumar2007](https://github.com/sidhantkumar2007)
📌 Project: CodeAlpha Internship — Task 1
