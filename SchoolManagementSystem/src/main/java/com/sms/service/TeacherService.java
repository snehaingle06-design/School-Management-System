package com.sms.service;

import com.sms.dao.TeacherDAO;
import com.sms.model.Teacher;
import com.sms.model.Subject;
import com.sms.model.SchoolClass;

import java.util.List;

/**
 * Service class for teacher management operations.
 */
public class TeacherService {

    private final TeacherDAO teacherDAO = new TeacherDAO();

    /**
     * Adds a new teacher. Generates a teacher ID if one is not already set.
     *
     * @param teacher the teacher to add
     * @return true if the teacher was added successfully
     */
    public boolean addTeacher(Teacher teacher) {
        if (teacher == null) {
            return false;
        }
        if (teacher.getTeacherId() == null || teacher.getTeacherId().trim().isEmpty()) {
            teacher.setTeacherId(generateTeacherId());
        }
        return teacherDAO.insert(teacher);
    }

    /**
     * Updates an existing teacher record.
     *
     * @param teacher the teacher with updated data
     * @return true if the update was successful
     */
    public boolean updateTeacher(Teacher teacher) {
        if (teacher == null) {
            return false;
        }
        return teacherDAO.update(teacher);
    }

    /**
     * Deletes a teacher by their internal ID.
     *
     * @param id the internal database ID
     * @return true if the teacher was deleted successfully
     */
    public boolean deleteTeacher(int id) {
        return teacherDAO.delete(id);
    }

    /**
     * Retrieves a teacher by internal database ID.
     *
     * @param id the internal ID
     * @return the Teacher object, or null if not found
     */
    public Teacher getTeacherById(int id) {
        return teacherDAO.findById(id);
    }

    /**
     * Retrieves a teacher by their formatted teacher ID (e.g., "TCH-0001").
     *
     * @param teacherId the formatted teacher ID
     * @return the Teacher object, or null if not found
     */
    public Teacher getTeacherByTeacherId(String teacherId) {
        if (teacherId == null || teacherId.trim().isEmpty()) {
            return null;
        }
        return teacherDAO.findByTeacherId(teacherId);
    }

    /**
     * Retrieves all teachers.
     *
     * @return a list of all teachers
     */
    public List<Teacher> getAllTeachers() {
        return teacherDAO.findAll();
    }

    /**
     * Searches teachers by keyword (matches name, teacher ID, etc.).
     *
     * @param keyword the search keyword
     * @return a list of matching teachers
     */
    public List<Teacher> searchTeachers(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return teacherDAO.findAll();
        }
        return teacherDAO.search(keyword);
    }

    /**
     * Gets the total number of teachers in the system.
     *
     * @return the total teacher count
     */
    public int getTotalTeachers() {
        return teacherDAO.getTotalCount();
    }

    /**
     * Assigns a subject to a teacher.
     *
     * @param teacherId the teacher's internal ID
     * @param subjectId the subject's ID
     * @return true if the assignment was successful
     */
    public boolean assignSubject(int teacherId, int subjectId) {
        return teacherDAO.assignSubject(teacherId, subjectId);
    }

    /**
     * Assigns a class to a teacher.
     *
     * @param teacherId the teacher's internal ID
     * @param classId   the class ID
     * @return true if the assignment was successful
     */
    public boolean assignClass(int teacherId, int classId) {
        return teacherDAO.assignClass(teacherId, classId);
    }

    /**
     * Retrieves all subjects assigned to a teacher.
     *
     * @param teacherId the teacher's internal ID
     * @return a list of assigned subjects
     */
    public List<Subject> getAssignedSubjects(int teacherId) {
        return teacherDAO.getAssignedSubjects(teacherId);
    }

    /**
     * Retrieves all classes assigned to a teacher.
     *
     * @param teacherId the teacher's internal ID
     * @return a list of assigned classes
     */
    public List<SchoolClass> getAssignedClasses(int teacherId) {
        return teacherDAO.getAssignedClasses(teacherId);
    }

    /**
     * Replaces all subject assignments for a teacher with the given list.
     *
     * @param teacherId  the teacher's internal ID
     * @param subjectIds the new list of subject IDs to assign
     */
    public void updateSubjectAssignments(int teacherId, List<Integer> subjectIds) {
        teacherDAO.removeAllSubjectAssignments(teacherId);
        if (subjectIds != null) {
            for (int subjectId : subjectIds) {
                teacherDAO.assignSubject(teacherId, subjectId);
            }
        }
    }

    /**
     * Replaces all class assignments for a teacher with the given list.
     *
     * @param teacherId the teacher's internal ID
     * @param classIds  the new list of class IDs to assign
     */
    public void updateClassAssignments(int teacherId, List<Integer> classIds) {
        teacherDAO.removeAllClassAssignments(teacherId);
        if (classIds != null) {
            for (int classId : classIds) {
                teacherDAO.assignClass(teacherId, classId);
            }
        }
    }

    /**
     * Generates a new unique teacher ID.
     *
     * @return the generated teacher ID string
     */
    public String generateTeacherId() {
        return teacherDAO.generateTeacherId();
    }
}
