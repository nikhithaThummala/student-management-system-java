import java.io.*;
import java.util.*;

/**
 * ============================================================
 *  Student Management System (Console-based, Java + OOP)
 * ============================================================
 *  Demonstrates:
 *   - Encapsulation  (Student: private fields, public getters/setters)
 *   - Abstraction    (StudentManager hides storage/file details)
 *   - Single Responsibility (Student = data, StudentManager = logic,
 *                             StudentManagementSystem = UI/menu)
 *   - Collections (ArrayList) for in-memory data management
 *   - File I/O for simple data persistence between runs
 *   - Exception handling for robust user input
 * ============================================================
 */

/**
 * StudentManagementSystem - the console UI / entry point.
 * Talks only to StudentManager; never manipulates raw data structures.
 */
public class StudentManagementSystem {

    private static final StudentManager manager = new StudentManager();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        int choice;
        do {
            printMenu();
            choice = readInt("Enter your choice: ");
            switch (choice) {
                case 1: addStudent(); break;
                case 2: searchStudent(); break;
                case 3: displayAllStudents(); break;
                case 4: updateStudent(); break;
                case 5: deleteStudent(); break;
                case 6: System.out.println("Exiting... Goodbye!"); break;
                default: System.out.println("Invalid choice. Please select 1-6.");
            }
        } while (choice != 6);

        scanner.close();
    }

    private static void printMenu() {
        System.out.println("\n===== STUDENT MANAGEMENT SYSTEM =====");
        System.out.println("1. Add Student");
        System.out.println("2. Search Student");
        System.out.println("3. Display All Students");
        System.out.println("4. Update Student");
        System.out.println("5. Delete Student");
        System.out.println("6. Exit");
        System.out.println("======================================");
    }

    private static void addStudent() {
        System.out.println("\n--- Add New Student ---");
        int id = readInt("Enter ID: ");
        if (manager.findById(id) != null) {
            System.out.println("A student with ID " + id + " already exists.");
            return;
        }
        String name = readNonEmptyString("Enter Name: ");
        int age = readInt("Enter Age: ");
        String course = readNonEmptyString("Enter Course: ");
        double marks = readDouble("Enter Marks (0-100): ");

        Student s = new Student(id, name, age, course, marks);
        manager.addStudent(s);
        System.out.println("Student added successfully!");
    }

    private static void searchStudent() {
        System.out.println("\n--- Search Student ---");
        System.out.println("1. Search by ID");
        System.out.println("2. Search by Name");
        int opt = readInt("Choose option: ");

        if (opt == 1) {
            int id = readInt("Enter ID: ");
            Student s = manager.findById(id);
            if (s != null) {
                printTableHeader();
                System.out.println(s);
            } else {
                System.out.println("No student found with ID " + id);
            }
        } else if (opt == 2) {
            String name = readNonEmptyString("Enter Name (or part of it): ");
            List<Student> result = manager.findByName(name);
            if (result.isEmpty()) {
                System.out.println("No matching students found.");
            } else {
                printTableHeader();
                for (Student s : result) System.out.println(s);
            }
        } else {
            System.out.println("Invalid option.");
        }
    }

    private static void displayAllStudents() {
        System.out.println("\n--- All Students ---");
        List<Student> list = manager.getAllStudents();
        if (list.isEmpty()) {
            System.out.println("No student records found.");
            return;
        }
        printTableHeader();
        for (Student s : list) {
            System.out.println(s);
        }
        System.out.println("Total Students: " + manager.count());
    }

    private static void updateStudent() {
        System.out.println("\n--- Update Student ---");
        int id = readInt("Enter ID of student to update: ");
        Student existing = manager.findById(id);
        if (existing == null) {
            System.out.println("No student found with ID " + id);
            return;
        }

        System.out.println("Current details:");
        printTableHeader();
        System.out.println(existing);
        System.out.println("(Press Enter on any field to keep its current value)");

        String name = readString("New Name [" + existing.getName() + "]: ");
        String ageStr = readString("New Age [" + existing.getAge() + "]: ");
        String course = readString("New Course [" + existing.getCourse() + "]: ");
        String marksStr = readString("New Marks [" + existing.getMarks() + "]: ");

        Integer age = null;
        if (!ageStr.isEmpty()) {
            try {
                age = Integer.parseInt(ageStr);
            } catch (NumberFormatException e) {
                System.out.println("Invalid age entered - keeping old value.");
            }
        }

        Double marks = null;
        if (!marksStr.isEmpty()) {
            try {
                marks = Double.parseDouble(marksStr);
            } catch (NumberFormatException e) {
                System.out.println("Invalid marks entered - keeping old value.");
            }
        }

        manager.updateStudent(id, name, age, course, marks);
        System.out.println("Student updated successfully!");
    }

    private static void deleteStudent() {
        System.out.println("\n--- Delete Student ---");
        int id = readInt("Enter ID of student to delete: ");
        if (manager.deleteStudent(id)) {
            System.out.println("Student deleted successfully!");
        } else {
            System.out.println("No student found with ID " + id);
        }
    }

    private static void printTableHeader() {
        System.out.printf("%-6s %-20s %-5s %-15s %-8s %-3s%n",
                "ID", "Name", "Age", "Course", "Marks", "Grade");
        System.out.println("---------------------------------------------------------------");
    }

    // ===================== Input helpers (with validation) =====================

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Please try again.");
            }
        }
    }

    private static double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                return Double.parseDouble(line);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Please try again.");
            }
        }
    }

    private static String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private static String readNonEmptyString(String prompt) {
        String value;
        do {
            System.out.print(prompt);
            value = scanner.nextLine().trim();
            if (value.isEmpty()) {
                System.out.println("This field cannot be empty.");
            }
        } while (value.isEmpty());
        return value;
    }
}

/**
 * Student - represents one student record.
 * A plain data/model class with full encapsulation.
 */
class Student implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String name;
    private int age;
    private String course;
    private double marks;

    public Student(int id, String name, int age, String course, double marks) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.course = course;
        this.marks = marks;
    }

    // ---------- Getters ----------
    public int getId() { return id; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getCourse() { return course; }
    public double getMarks() { return marks; }

    // ---------- Setters ----------
    public void setName(String name) { this.name = name; }
    public void setAge(int age) { this.age = age; }
    public void setCourse(String course) { this.course = course; }
    public void setMarks(double marks) { this.marks = marks; }

    /** Derives a letter grade from marks - simple business logic kept inside the model. */
    public String getGrade() {
        if (marks >= 90) return "A+";
        if (marks >= 80) return "A";
        if (marks >= 70) return "B";
        if (marks >= 60) return "C";
        if (marks >= 50) return "D";
        return "F";
    }

    @Override
    public String toString() {
        return String.format("%-6d %-20s %-5d %-15s %-8.2f %-3s",
                id, name, age, course, marks, getGrade());
    }

    /** Serializes this student to a single CSV line for file storage. */
    public String toFileFormat() {
        return id + "," + name + "," + age + "," + course + "," + marks;
    }

    /** Rebuilds a Student object from a stored CSV line. */
    public static Student fromFileFormat(String line) {
        String[] parts = line.split(",");
        return new Student(
                Integer.parseInt(parts[0].trim()),
                parts[1].trim(),
                Integer.parseInt(parts[2].trim()),
                parts[3].trim(),
                Double.parseDouble(parts[4].trim())
        );
    }
}

/**
 * StudentManager - owns the collection of students and all
 * CRUD + persistence logic. The UI layer never touches the
 * List directly, which keeps the data management encapsulated.
 */
class StudentManager {

    private final List<Student> students;
    private static final String FILE_NAME = "students.txt";

    public StudentManager() {
        students = new ArrayList<>();
        loadFromFile();
    }

    /** Adds a student. Returns false if the ID is already taken. */
    public boolean addStudent(Student s) {
        if (findById(s.getId()) != null) {
            return false;
        }
        students.add(s);
        saveToFile();
        return true;
    }

    public Student findById(int id) {
        for (Student s : students) {
            if (s.getId() == id) return s;
        }
        return null;
    }

    /** Case-insensitive partial name search. */
    public List<Student> findByName(String name) {
        List<Student> result = new ArrayList<>();
        for (Student s : students) {
            if (s.getName().toLowerCase().contains(name.toLowerCase())) {
                result.add(s);
            }
        }
        return result;
    }

    public List<Student> getAllStudents() {
        return students;
    }

    /**
     * Updates an existing record. Pass null for any field that
     * should be left unchanged. Returns false if the ID is unknown.
     */
    public boolean updateStudent(int id, String name, Integer age, String course, Double marks) {
        Student s = findById(id);
        if (s == null) return false;
        if (name != null && !name.isEmpty()) s.setName(name);
        if (age != null) s.setAge(age);
        if (course != null && !course.isEmpty()) s.setCourse(course);
        if (marks != null) s.setMarks(marks);
        saveToFile();
        return true;
    }

    public boolean deleteStudent(int id) {
        Student s = findById(id);
        if (s == null) return false;
        students.remove(s);
        saveToFile();
        return true;
    }

    public int count() {
        return students.size();
    }

    /** Persists all records to a CSV text file. */
    private void saveToFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (Student s : students) {
                pw.println(s.toFileFormat());
            }
        } catch (IOException e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }

    /** Loads previously saved records on startup, if the file exists. */
    private void loadFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    students.add(Student.fromFileFormat(line));
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading data: " + e.getMessage());
        }
    }
}
