package com.sms.service;

import com.sms.dao.AttendanceDAO;
import com.sms.model.Attendance;

import java.sql.Date;
import java.util.List;

/**
 * Service class for attendance management operations.
 */
public class AttendanceService {

    private final AttendanceDAO attendanceDAO = new AttendanceDAO();

    /**
     * Marks attendance for a student. If an attendance record already exists
     * for the same student on the same date, it is updated; otherwise a new
     * record is inserted.
     *
     * @param attendance the attendance record
     * @return true if the operation was successful
     */
    public boolean markAttendance(Attendance attendance) {
        if (attendance == null) {
            return false;
        }

        Attendance existing = attendanceDAO.findByStudentAndDate(
                attendance.getStudentId(), attendance.getDate());

        if (existing != null) {
            attendance.setId(existing.getId());
            return attendanceDAO.update(attendance);
        } else {
            return attendanceDAO.insert(attendance);
        }
    }

    /**
     * Retrieves all attendance records for a specific student.
     *
     * @param studentId the student's internal ID
     * @return a list of attendance records
     */
    public List<Attendance> getAttendanceByStudent(int studentId) {
        return attendanceDAO.findByStudentId(studentId);
    }

    /**
     * Retrieves attendance records for a specific class on a given date.
     *
     * @param classId the class ID
     * @param date    the date
     * @return a list of attendance records
     */
    public List<Attendance> getAttendanceByClassAndDate(int classId, Date date) {
        return attendanceDAO.findByClassAndDate(classId, date);
    }

    /**
     * Calculates the attendance percentage for a student.
     *
     * @param studentId the student's internal ID
     * @return the attendance percentage (0.0 to 100.0)
     */
    public double getAttendancePercentage(int studentId) {
        return attendanceDAO.getAttendancePercentage(studentId);
    }

    /**
     * Retrieves a monthly attendance report for a specific class.
     *
     * @param classId the class ID
     * @param month   the month (1-12)
     * @param year    the year
     * @return a list of attendance records for the specified month
     */
    public List<Attendance> getMonthlyReport(int classId, int month, int year) {
        return attendanceDAO.getMonthlyReport(classId, month, year);
    }

    /**
     * Marks attendance for multiple students at once. Each record is processed
     * individually using {@link #markAttendance(Attendance)}.
     *
     * @param attendanceList the list of attendance records
     * @return true if all records were processed successfully
     */
    public boolean markBulkAttendance(List<Attendance> attendanceList) {
        if (attendanceList == null || attendanceList.isEmpty()) {
            return false;
        }

        boolean allSuccess = true;
        for (Attendance attendance : attendanceList) {
            if (!markAttendance(attendance)) {
                allSuccess = false;
            }
        }
        return allSuccess;
    }
}
