package com.sms.service;

import com.sms.dao.SubjectDAO;
import com.sms.model.Subject;

import java.util.List;

/**
 * Service class for subject management operations.
 */
public class SubjectService {

    private final SubjectDAO subjectDAO = new SubjectDAO();

    /**
     * Adds a new subject.
     *
     * @param subject the subject to add
     * @return true if the subject was added successfully
     */
    public boolean addSubject(Subject subject) {
        if (subject == null) {
            return false;
        }
        return subjectDAO.insert(subject);
    }

    /**
     * Updates an existing subject.
     *
     * @param subject the subject with updated data
     * @return true if the update was successful
     */
    public boolean updateSubject(Subject subject) {
        if (subject == null) {
            return false;
        }
        return subjectDAO.update(subject);
    }

    /**
     * Deletes a subject by ID.
     *
     * @param id the subject ID
     * @return true if the subject was deleted successfully
     */
    public boolean deleteSubject(int id) {
        return subjectDAO.delete(id);
    }

    /**
     * Retrieves a subject by its ID.
     *
     * @param id the subject ID
     * @return the Subject object, or null if not found
     */
    public Subject getSubjectById(int id) {
        return subjectDAO.findById(id);
    }

    /**
     * Retrieves a subject by its unique code.
     *
     * @param code the subject code
     * @return the Subject object, or null if not found
     */
    public Subject getSubjectByCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return null;
        }
        return subjectDAO.findByCode(code);
    }

    /**
     * Retrieves all subjects.
     *
     * @return a list of all subjects
     */
    public List<Subject> getAllSubjects() {
        return subjectDAO.findAll();
    }
}
