package com.bankit.loan.dao;

/**
 * Factory class for creating DAO instances
 * Implements the Singleton pattern to ensure only one instance of each DAO exists
 */
public class DAOFactory {
    private static DAOFactory instance;
    private final LoanDAO loanDAO;
    private final OfficerDAO officerDAO;

    private DAOFactory() {
        // Initialize DAOs
        this.officerDAO = new OfficerDAOImpl();
        this.loanDAO = new LoanDAOImpl(officerDAO);
    }

    /**
     * Gets the singleton instance of DAOFactory
     *
     * @return the DAOFactory instance
     */
    public static synchronized DAOFactory getInstance() {
        if (instance == null) {
            instance = new DAOFactory();
        }
        return instance;
    }

    /**
     * Gets the LoanDAO instance
     *
     * @return the LoanDAO instance
     */
    public LoanDAO getLoanDAO() {
        return loanDAO;
    }

    /**
     * Gets the OfficerDAO instance
     *
     * @return the OfficerDAO instance
     */
    public OfficerDAO getOfficerDAO() {
        return officerDAO;
    }
}