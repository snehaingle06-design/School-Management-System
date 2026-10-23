package com.sms.service;

import com.sms.dao.ExaminationDAO;
import com.sms.dao.MarkDAO;
import com.sms.model.Examination;
import com.sms.model.Mark;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service class for examination and marks management operations.
 */
public class ExaminationService {

    private final ExaminationDAO examDAO = new ExaminationDAO();
    private final MarkDAO markDAO = new MarkDAO();

    // ==================== Examination CRUD ====================

    /**
     * Adds a new examination.
     *
     * @param examination the examination to add
     * @return true if the examination was added successfully
     */
    public boolean addExamination(Examination examination) {
        if (examination == null) {
            return false;
        }
        return examDAO.insert(examination);
    }

    /**
     * Updates an existing examination.
     *
     * @param examination the examination with updated data
     * @return true if the update was successful
     */
    public boolean updateExamination(Examination examination) {
        if (examination == null) {
            return false;
        }
        return examDAO.update(examination);
    }

    /**
     * Deletes an examination by ID.
     *
     * @param id the examination ID
     * @return true if the examination was deleted successfully
     */
    public boolean deleteExamination(int id) {
        return examDAO.delete(id);
    }

    /**
     * Retrieves an examination by its ID.
     *
     * @param id the examination ID
     * @return the Examination object, or null if not found
     */
    public Examination getExaminationById(int id) {
        return examDAO.findById(id);
    }

    /**
     * Retrieves all examinations.
     *
     * @return a list of all examinations
     */
    public List<Examination> getAllExaminations() {
        return examDAO.findAll();
    }

    /**
     * Retrieves all examinations for a specific class.
     *
     * @param classId the class ID
     * @return a list of examinations for the given class
     */
    public List<Examination> getExaminationsByClassId(int classId) {
        return examDAO.findByClassId(classId);
    }

    // ==================== Marks Management ====================

    /**
     * Adds a mark for a student. The grade is automatically calculated from the
     * percentage. If a mark already exists for the same exam, student, and subject
     * combination, the existing record is updated instead.
     *
     * @param mark the mark to add
     * @return true if the operation was successful
     */
    public boolean addMark(Mark mark) {
        if (mark == null) {
            return false;
        }

        calculateAndSetGrade(mark);

        Mark existing = markDAO.findByExamStudentSubject(
                mark.getExamId(), mark.getStudentId(), mark.getSubjectId());

        if (existing != null) {
            mark.setId(existing.getId());
            return markDAO.update(mark);
        } else {
            return markDAO.insert(mark);
        }
    }

    /**
     * Updates an existing mark. The grade is recalculated from the percentage.
     *
     * @param mark the mark with updated data
     * @return true if the update was successful
     */
    public boolean updateMark(Mark mark) {
        if (mark == null) {
            return false;
        }
        calculateAndSetGrade(mark);
        return markDAO.update(mark);
    }

    /**
     * Retrieves marks for a specific exam and student.
     *
     * @param examId    the examination ID
     * @param studentId the student's internal ID
     * @return a list of marks
     */
    public List<Mark> getMarksByExamAndStudent(int examId, int studentId) {
        return markDAO.findByExamAndStudent(examId, studentId);
    }

    /**
     * Retrieves all marks for a specific examination.
     *
     * @param examId the examination ID
     * @return a list of marks
     */
    public List<Mark> getMarksByExam(int examId) {
        return markDAO.findByExamId(examId);
    }

    /**
     * Retrieves all marks for a specific student.
     *
     * @param studentId the student's internal ID
     * @return a list of marks
     */
    public List<Mark> getMarksByStudent(int studentId) {
        return markDAO.findByStudentId(studentId);
    }

    /**
     * Generates a comprehensive report card for a student for a specific exam.
     * The returned map contains:
     * <ul>
     *   <li>"marks" - List&lt;Mark&gt; of all marks</li>
     *   <li>"totalMarks" - Double total marks obtained</li>
     *   <li>"totalMaxMarks" - Integer total maximum marks</li>
     *   <li>"percentage" - Double overall percentage</li>
     *   <li>"overallGrade" - String overall grade</li>
     *   <li>"passed" - Boolean pass/fail status</li>
     * </ul>
     *
     * @param studentId the student's internal ID
     * @param examId    the examination ID
     * @return a map containing report card data
     */
    public Map<String, Object> getStudentReportCard(int studentId, int examId) {
        Map<String, Object> reportCard = new HashMap<>();

        List<Mark> marks = markDAO.findByExamAndStudent(examId, studentId);
        reportCard.put("marks", marks);

        double totalMarksObtained = 0;
        int totalMaxMarks = 0;
        boolean passed = true;

        for (Mark mark : marks) {
            totalMarksObtained += mark.getMarksObtained();
            totalMaxMarks += mark.getMaxMarks();

            // A student fails if any subject score is below 33%
            if (mark.getMaxMarks() > 0) {
                double subjectPercentage = (mark.getMarksObtained() / (double) mark.getMaxMarks()) * 100.0;
                if (subjectPercentage < 33.0) {
                    passed = false;
                }
            }
        }

        double percentage = 0;
        if (totalMaxMarks > 0) {
            percentage = (totalMarksObtained / totalMaxMarks) * 100.0;
        }

        String overallGrade = calculateGrade(percentage);

        reportCard.put("totalMarks", totalMarksObtained);
        reportCard.put("totalMaxMarks", totalMaxMarks);
        reportCard.put("percentage", percentage);
        reportCard.put("overallGrade", overallGrade);
        reportCard.put("passed", passed);

        return reportCard;
    }

    // ==================== Private Helpers ====================

    /**
     * Calculates and sets the grade on a Mark based on its percentage
     * (marksObtained / maxMarks * 100).
     */
    private void calculateAndSetGrade(Mark mark) {
        double percentage = 0;
        if (mark.getMaxMarks() > 0) {
            percentage = (mark.getMarksObtained() / (double) mark.getMaxMarks()) * 100.0;
        }
        mark.setGrade(calculateGrade(percentage));
    }

    /**
     * Converts a percentage to a letter grade.
     *
     * @param percentage the percentage value
     * @return the letter grade string
     */
    private String calculateGrade(double percentage) {
        if (percentage >= 90) {
            return "A+";
        } else if (percentage >= 80) {
            return "A";
        } else if (percentage >= 70) {
            return "B+";
        } else if (percentage >= 60) {
            return "B";
        } else if (percentage >= 50) {
            return "C";
        } else if (percentage >= 40) {
            return "D";
        } else if (percentage >= 33) {
            return "E";
        } else {
            return "F";
        }
    }
}
