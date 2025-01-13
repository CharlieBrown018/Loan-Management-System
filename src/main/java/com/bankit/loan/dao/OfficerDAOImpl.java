package com.bankit.loan.dao;

import com.bankit.loan.config.DatabaseConfig;
import com.bankit.loan.model.Officer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of OfficerDAO interface
 */
public class OfficerDAOImpl implements OfficerDAO {

    @Override
    public void save(Officer officer) throws SQLException {
        String sql = """
            INSERT INTO officers (officer_id, name, email, contact) 
            VALUES (?, ?, ?, ?)
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            setOfficerParameters(pstmt, officer);
            pstmt.executeUpdate();
        }
    }

    @Override
    public void update(Officer officer) throws SQLException {
        String sql = """
            UPDATE officers 
            SET name = ?, email = ?, contact = ?
            WHERE officer_id = ?
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, officer.getName());
            pstmt.setString(2, officer.getEmail());
            pstmt.setString(3, officer.getContact());
            pstmt.setString(4, officer.getOfficerId());

            if (pstmt.executeUpdate() == 0) {
                throw new SQLException("Update failed, no rows affected.");
            }
        }
    }

    @Override
    public void delete(String officerId) throws SQLException {
        String sql = "DELETE FROM officers WHERE officer_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, officerId);

            if (pstmt.executeUpdate() == 0) {
                throw new SQLException("Delete failed, no rows affected.");
            }
        }
    }

    @Override
    public Optional<Officer> findById(String officerId) throws SQLException {
        String sql = "SELECT * FROM officers WHERE officer_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, officerId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(resultSetToOfficer(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Officer> findAll() throws SQLException {
        List<Officer> officers = new ArrayList<>();
        String sql = "SELECT * FROM officers";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                officers.add(resultSetToOfficer(rs));
            }
        }
        return officers;
    }

    /**
     * Helper method to set officer parameters in a PreparedStatement
     *
     * @param pstmt the PreparedStatement to set parameters for
     * @param officer the Officer object containing the data
     * @throws SQLException if a database access error occurs
     */
    private void setOfficerParameters(PreparedStatement pstmt, Officer officer) throws SQLException {
        pstmt.setString(1, officer.getOfficerId());
        pstmt.setString(2, officer.getName());
        pstmt.setString(3, officer.getEmail());
        pstmt.setString(4, officer.getContact());
    }

    /**
     * Helper method to convert a ResultSet row to an Officer object
     *
     * @param rs the ResultSet containing the officer data
     * @return an Officer object populated with the data
     * @throws SQLException if a database access error occurs
     */
    private Officer resultSetToOfficer(ResultSet rs) throws SQLException {
        Officer officer = new Officer();
        officer.setOfficerId(rs.getString("officer_id"));
        officer.setName(rs.getString("name"));
        officer.setEmail(rs.getString("email"));
        officer.setContact(rs.getString("contact"));
        return officer;
    }
}