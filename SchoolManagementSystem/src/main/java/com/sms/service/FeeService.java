package com.sms.service;

import com.sms.dao.FeeStructureDAO;
import com.sms.dao.FeePaymentDAO;
import com.sms.model.FeeStructure;
import com.sms.model.FeePayment;

import java.sql.Date;
import java.util.List;

/**
 * Service class for fee structure and fee payment management operations.
 */
public class FeeService {

    private final FeeStructureDAO feeStructureDAO = new FeeStructureDAO();
    private final FeePaymentDAO feePaymentDAO = new FeePaymentDAO();

    // ==================== Fee Structure CRUD ====================

    /**
     * Adds a new fee structure.
     *
     * @param feeStructure the fee structure to add
     * @return true if the fee structure was added successfully
     */
    public boolean addFeeStructure(FeeStructure feeStructure) {
        if (feeStructure == null) {
            return false;
        }
        return feeStructureDAO.insert(feeStructure);
    }

    /**
     * Updates an existing fee structure.
     *
     * @param feeStructure the fee structure with updated data
     * @return true if the update was successful
     */
    public boolean updateFeeStructure(FeeStructure feeStructure) {
        if (feeStructure == null) {
            return false;
        }
        return feeStructureDAO.update(feeStructure);
    }

    /**
     * Deletes a fee structure by ID.
     *
     * @param id the fee structure ID
     * @return true if the fee structure was deleted successfully
     */
    public boolean deleteFeeStructure(int id) {
        return feeStructureDAO.delete(id);
    }

    /**
     * Retrieves a fee structure by its ID.
     *
     * @param id the fee structure ID
     * @return the FeeStructure object, or null if not found
     */
    public FeeStructure getFeeStructureById(int id) {
        return feeStructureDAO.findById(id);
    }

    /**
     * Retrieves all fee structures.
     *
     * @return a list of all fee structures
     */
    public List<FeeStructure> getAllFeeStructures() {
        return feeStructureDAO.findAll();
    }

    /**
     * Retrieves fee structures for a specific class.
     *
     * @param classId the class ID
     * @return a list of fee structures for the given class
     */
    public List<FeeStructure> getFeeStructuresByClassId(int classId) {
        return feeStructureDAO.findByClassId(classId);
    }

    // ==================== Fee Payments ====================

    /**
     * Records a fee payment. Generates a receipt number if not already set,
     * and sets the payment date to today if not provided.
     *
     * @param payment the fee payment to record
     * @return true if the payment was recorded successfully
     */
    public boolean recordPayment(FeePayment payment) {
        if (payment == null) {
            return false;
        }

        if (payment.getReceiptNumber() == null || payment.getReceiptNumber().trim().isEmpty()) {
            payment.setReceiptNumber(generateReceiptNumber());
        }

        if (payment.getPaymentDate() == null) {
            payment.setPaymentDate(new Date(System.currentTimeMillis()));
        }

        return feePaymentDAO.insert(payment);
    }

    /**
     * Retrieves all fee payments made by a specific student.
     *
     * @param studentId the student's internal ID
     * @return a list of fee payments
     */
    public List<FeePayment> getPaymentsByStudent(int studentId) {
        return feePaymentDAO.findByStudentId(studentId);
    }

    /**
     * Retrieves all fee payments in the system.
     *
     * @return a list of all fee payments
     */
    public List<FeePayment> getAllPayments() {
        return feePaymentDAO.findAll();
    }

    /**
     * Gets the total amount of fees collected across all payments.
     *
     * @return the total collected amount
     */
    public double getTotalFeeCollected() {
        return feePaymentDAO.getTotalFeeCollected();
    }

    /**
     * Calculates the outstanding fee balance for a student in a given class.
     * This is the total fee for the class minus the total amount the student
     * has already paid.
     *
     * @param studentId the student's internal ID
     * @param classId   the class ID
     * @return the outstanding balance amount
     */
    public double getOutstandingBalance(int studentId, int classId) {
        double totalFee = 0;
        List<FeeStructure> feeStructures = feeStructureDAO.findByClassId(classId);
        for (FeeStructure fs : feeStructures) {
            totalFee += fs.getAmount();
        }

        double totalPaid = 0;
        List<FeePayment> payments = feePaymentDAO.findByStudentId(studentId);
        for (FeePayment payment : payments) {
            totalPaid += payment.getAmount();
        }

        return totalFee - totalPaid;
    }

    /**
     * Generates a new unique receipt number.
     *
     * @return the generated receipt number string
     */
    public String generateReceiptNumber() {
        return feePaymentDAO.generateReceiptNumber();
    }
}
