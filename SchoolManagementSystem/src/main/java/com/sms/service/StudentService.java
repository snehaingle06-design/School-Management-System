package com.sms.service;

import com.sms.dao.StudentDAO;
import com.sms.dao.ClassDAO;
import com.sms.model.Student;

import java.util.List;

/**
 * Service class for student management operations.
 */
public class StudentService {

    private final StudentDAO studentDAO = new StudentDAO();
    private final ClassDAO classDAO = new ClassDAO();

    /**
     * Adds a new student. Generates a student ID if one is not already set.
     *
     * @param student the student to add
     * @return true if the student was added successfully
     */
    public boolean addStudent(Student student) {
        if (student == null) {
            return false;
        }
        if (student.getStudentId() == null || student.getStudentId().trim().isEmpty()) {
            student.setStudentId(generateStudentId());
        }
        return studentDAO.insert(student);
    }

    /**
     * Updates an existing student record.
     *
     * @param student the student with updated data
     * @return true if the update was successful
     */
    public boolean updateStudent(Student student) {
        if (student == null) {
            return false;
        }
        return studentDAO.update(student);
    }

    /**
     * Deletes a student by their internal ID.
     *
     * @param id the internal database ID
     * @return true if the student was deleted successfully
     */
    public boolean deleteStudent(int id) {
        return studentDAO.delete(id);
    }

    /**
     * Retrieves a student by internal database ID.
     *
     * @param id the internal ID
     * @return the Student object, or null if not found
     */
    public Student getStudentById(int id) {
        return studentDAO.findById(id);
    }

    /**
     * Retrieves a student by their formatted student ID (e.g., "STU-0001").
     *
     * @param studentId the formatted student ID
     * @return the Student object, or null if not found
     */
    public Student getStudentByStudentId(String studentId) {
        if (studentId == null || studentId.trim().isEmpty()) {
            return null;
        }
        return studentDAO.findByStudentId(studentId);
    }

    /**
     * Retrieves all students.
     *
     * @return a list of all students
     */
    public List<Student> getAllStudents() {
        return studentDAO.findAll();
    }

    /**
     * Retrieves all students enrolled in a specific class.
     *
     * @param classId the class ID
     * @return a list of students in the given class
     */
    public List<Student> getStudentsByClass(int classId) {
        return studentDAO.findByClassId(classId);
    }

    /**
     * Searches students by keyword (matches name, student ID, etc.).
     *
     * @param keyword the search keyword
     * @return a list of matching students
     */
    public List<Student> searchStudents(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return studentDAO.findAll();
        }
        return studentDAO.search(keyword);
    }

    /**
     * Gets the total number of students in the system.
     *
     * @return the total student count
     */
    public int getTotalStudents() {
        return studentDAO.getTotalCount();
    }

    /**
     * Generates a new unique student ID.
     *
     * @return the generated student ID string
     */
    public String generateStudentId() {
        return studentDAO.generateStudentId();
    }
}
