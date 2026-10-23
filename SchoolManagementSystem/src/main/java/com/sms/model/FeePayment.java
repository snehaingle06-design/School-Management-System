package com.sms.model;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Represents a fee payment made by a student.
 */
public class FeePayment {
    private int id;
    private int studentId;
    private int feeStructureId;
    private double amountPaid;
    private Date paymentDate;
    private String paymentMethod; // CASH, CARD, ONLINE, CHEQUE
    private String transactionRef;
    private String receiptNumber;
    private String remarks;
    private Timestamp createdAt;

    // Transient display fields
    private String studentName;
    private String feeType;
    private double totalFee;

    public FeePayment() {}

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public int getFeeStructureId() { return feeStructureId; }
    public void setFeeStructureId(int feeStructureId) { this.feeStructureId = feeStructureId; }

    public double getAmountPaid() { return amountPaid; }
    public void setAmountPaid(double amountPaid) { this.amountPaid = amountPaid; }

    public Date getPaymentDate() { return paymentDate; }
    public void setPaymentDate(Date paymentDate) { this.paymentDate = paymentDate; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getTransactionRef() { return transactionRef; }
    public void setTransactionRef(String transactionRef) { this.transactionRef = transactionRef; }

    public String getReceiptNumber() { return receiptNumber; }
    public void setReceiptNumber(String receiptNumber) { this.receiptNumber = receiptNumber; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getFeeType() { return feeType; }
    public void setFeeType(String feeType) { this.feeType = feeType; }

    public double getTotalFee() { return totalFee; }
    public void setTotalFee(double totalFee) { this.totalFee = totalFee; }

    @Override
    public String toString() {
        return "Payment{" + receiptNumber + ", ₹" + amountPaid + "}";
    }
}

