package com.bankit.loan.service;

/**
 * Factory class for creating service instances
 * Implements the Singleton pattern to ensure only one instance of each service exists
 */
public class ServiceFactory {
    private static ServiceFactory instance;
    private final LoanService loanService;
    private final OfficerService officerService;

    private ServiceFactory() {
        this.loanService = new LoanServiceImpl();
        this.officerService = new OfficerServiceImpl();
    }

    /**
     * Gets the singleton instance of ServiceFactory
     *
     * @return the ServiceFactory instance
     */
    public static synchronized ServiceFactory getInstance() {
        if (instance == null) {
            instance = new ServiceFactory();
        }
        return instance;
    }

    /**
     * Gets the LoanService instance
     *
     * @return the LoanService instance
     */
    public LoanService getLoanService() {
        return loanService;
    }

    /**
     * Gets the OfficerService instance
     *
     * @return the OfficerService instance
     */
    public OfficerService getOfficerService() {
        return officerService;
    }
}