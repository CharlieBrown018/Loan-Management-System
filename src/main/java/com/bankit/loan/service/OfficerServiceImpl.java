package com.bankit.loan.service;

import com.bankit.loan.dao.DAOFactory;
import com.bankit.loan.dao.OfficerDAO;
import com.bankit.loan.model.Officer;
import com.bankit.loan.util.ValidationUtils;

import java.sql.SQLException;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

/**
 * Implementation of OfficerService interface
 */
public class OfficerServiceImpl implements OfficerService {

    private final OfficerDAO officerDAO;
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@(.+)$"
    );
    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^09\\d{9}$"
    );

    public OfficerServiceImpl() {
        this.officerDAO = DAOFactory.getInstance().getOfficerDAO();
    }

    @Override
    public void createOfficer(Officer officer) throws SQLException {
        validateOfficer(officer);
        String officerId = generateOfficerId();
        officer.setOfficerId(officerId);
        officerDAO.save(officer);
    }

    @Override
    public void updateOfficer(Officer officer) throws SQLException {
        validateOfficer(officer);
        officerDAO.update(officer);
    }

    @Override
    public void deleteOfficer(String officerId) throws SQLException {
        if (officerId == null || officerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Officer ID cannot be empty");
        }
        officerDAO.delete(officerId);
    }

    @Override
    public Officer getOfficerById(String officerId) throws SQLException {
        if (officerId == null || officerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Officer ID cannot be empty");
        }
        return officerDAO.findById(officerId).orElse(null);
    }

    @Override
    public List<Officer> getAllOfficers() throws SQLException {
        return officerDAO.findAll();
    }

    @Override
    public void validateOfficer(Officer officer) {
        if (officer == null) {
            throw new IllegalArgumentException("Officer cannot be null");
        }

        ValidationUtils.validateNotEmpty(officer.getName(), "Officer name");

        if (!EMAIL_PATTERN.matcher(officer.getEmail()).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }

        if (!PHONE_PATTERN.matcher(officer.getContact()).matches()) {
            throw new IllegalArgumentException("Invalid phone number format");
        }

        // Additional business rule validations can be added here
        validateOfficerName(officer.getName());
    }

    @Override
    public String generateOfficerId() {
        Random random = new Random();
        int randomNum = 1000 + random.nextInt(9000); // 4-digit number
        return "OFF" + randomNum;
    }

    /**
     * Validates officer name format and rules
     *
     * @param name the officer name to validate
     * @throws IllegalArgumentException if the name is invalid
     */
    private void validateOfficerName(String name) {
        // Name should contain at least two parts (first and last name)
        String[] nameParts = name.trim().split("\\s+");
        if (nameParts.length < 2) {
            throw new IllegalArgumentException("Officer name must include both first and last name");
        }

        // Each part should be at least 2 characters long
        for (String part : nameParts) {
            if (part.length() < 2) {
                throw new IllegalArgumentException("Each part of the name must be at least 2 characters long");
            }

            // Name parts should only contain letters and hyphens
            if (!part.matches("^[a-zA-Z-]+$")) {
                throw new IllegalArgumentException("Name can only contain letters and hyphens");
            }
        }
    }
}