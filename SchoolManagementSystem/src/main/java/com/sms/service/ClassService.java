package com.sms.service;

import com.sms.dao.ClassDAO;
import com.sms.model.SchoolClass;

import java.util.List;

/**
 * Service class for school class management operations.
 */
public class ClassService {

    private final ClassDAO classDAO = new ClassDAO();

    /**
     * Adds a new school class.
     *
     * @param schoolClass the class to add
     * @return true if the class was added successfully
     */
    public boolean addClass(SchoolClass schoolClass) {
        if (schoolClass == null) {
            return false;
        }
        return classDAO.insert(schoolClass);
    }

    /**
     * Updates an existing school class.
     *
     * @param schoolClass the class with updated data
     * @return true if the update was successful
     */
    public boolean updateClass(SchoolClass schoolClass) {
        if (schoolClass == null) {
            return false;
        }
        return classDAO.update(schoolClass);
    }

    /**
     * Deletes a school class by ID.
     *
     * @param id the class ID
     * @return true if the class was deleted successfully
     */
    public boolean deleteClass(int id) {
        return classDAO.delete(id);
    }

    /**
     * Retrieves a school class by its ID.
     *
     * @param id the class ID
     * @return the SchoolClass object, or null if not found
     */
    public SchoolClass getClassById(int id) {
        return classDAO.findById(id);
    }

    /**
     * Retrieves all school classes.
     *
     * @return a list of all classes
     */
    public List<SchoolClass> getAllClasses() {
        return classDAO.findAll();
    }

    /**
     * Gets the total number of classes in the system.
     *
     * @return the total class count
     */
    public int getTotalClasses() {
        return classDAO.getTotalCount();
    }
}
