package com.bankit.loan.util;

import com.bankit.loan.model.Loan;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.util.Matrix;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Utility class for generating professional PDF documents for loans
 * Handles both customer-facing and internal bank documents with proper formatting
 */
public class DocumentGenerator {
    // Formatting constants
    private static final NumberFormat CURRENCY = NumberFormat.getCurrencyInstance();
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMMM dd, yyyy");

    // Document layout constants for A4 paper
    private static final float PAGE_WIDTH = PDRectangle.A4.getWidth();
    private static final float PAGE_HEIGHT = PDRectangle.A4.getHeight();
    private static final float MARGIN_LEFT = 50;
    private static final float MARGIN_RIGHT = 50;
    private static final float MARGIN_TOP = 810;
    private static final float MARGIN_BOTTOM = 30;

    // Spacing constants
    private static final float LINE_SPACING = 10;
    private static final float SECTION_SPACING = 10;
    private static final float FIELD_INDENT = 120;
    private static final float CONTENT_START = MARGIN_TOP - 30;

    // Font sizes for different elements
    private static final float HEADER_FONT_SIZE = 14;
    private static final float SECTION_FONT_SIZE = 11;
    private static final float CONTENT_FONT_SIZE = 9;
    private static final float FOOTER_FONT_SIZE = 8;

    // Document security and control
    private static final String WATERMARK_TEXT = "CONFIDENTIAL";
    private static final float WATERMARK_OPACITY = 0.1f;
    private static final String VERSION = "v1.0";

    /**
     * Generates a customer-facing loan agreement document
     */
    public static void generateCustomerLoanDocument(Loan loan, File outputFile) throws IOException {
        try (PDDocument document = new PDDocument()) {
            addContent(document, loan, "customer");
            document.save(outputFile);
        }
    }

    /**
     * Generates a detailed internal bank document
     */
    public static void generateBankLoanDocument(Loan loan, File outputFile) throws IOException {
        try (PDDocument document = new PDDocument()) {
            addContent(document, loan, "internal");
            document.save(outputFile);
        }
    }

    /**
     * Adds the document header with improved layout and branding
     */
    private static float addDocumentHeader(PDDocument document, PDPageContentStream contentStream,
                                           String title, float y) throws IOException {
        // Add logo with proper scaling
        PDImageXObject logo = PDImageXObject.createFromFile("src/main/resources/images/Logo.png", document);
        float logoWidth = 60;
        float logoHeight = 30;
        contentStream.drawImage(logo, MARGIN_LEFT, y - logoHeight, logoWidth, logoHeight);

        // Add professional header with proper spacing
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, HEADER_FONT_SIZE);
        contentStream.newLineAtOffset(MARGIN_LEFT + logoWidth + 20, y - (logoHeight/2) + 5);
        contentStream.showText("BankIT " + title);
        contentStream.endText();

        // Add horizontal line under header
        contentStream.setLineWidth(0.5f);
        contentStream.moveTo(MARGIN_LEFT, y - logoHeight - 10);
        contentStream.lineTo(PAGE_WIDTH - MARGIN_RIGHT, y - logoHeight - 10);
        contentStream.stroke();

        return y - logoHeight - 20;
    }

    /**
     * Adds bank information section with proper formatting
     */
    private static float addBankInformation(PDPageContentStream contentStream, float y) throws IOException {
        addSectionHeader(contentStream, "Bank Information", y);
        y -= 12;

        addField(contentStream, "Institution:", "BankIT Financial Services", y);
        y -= LINE_SPACING;
        addField(contentStream, "Branch:", "Main Branch - Financial District", y);
        y -= LINE_SPACING;
        addField(contentStream, "Address:", "123 Banking Avenue, Business City, BC 12345", y);
        y -= LINE_SPACING;
        addField(contentStream, "Contact:", "+1 (555) 123-4567", y);
        y -= LINE_SPACING;
        addField(contentStream, "SWIFT Code:", "BNKIT1234", y);
        y -= LINE_SPACING;
        addField(contentStream, "License No:", "FED-12345-BA", y);

        return y - SECTION_SPACING;
    }

    /**
     * Adds customer information section with improved layout
     */
    private static float addCustomerSection(PDPageContentStream contentStream, Loan loan, float y)
            throws IOException {
        addSectionHeader(contentStream, "Customer Information", y);
        y -= 12;

        // Add customer fields with proper spacing and formatting
        float fieldY = y;
        float col2X = PAGE_WIDTH / 2 + 50;

        // Column 1
        addField(contentStream, "Customer ID:", loan.getCustomerId(), fieldY);
        fieldY -= LINE_SPACING;
        addField(contentStream, "Full Name:", loan.getCustomerName(), fieldY);
        fieldY -= LINE_SPACING;
        addField(contentStream, "Contact:", formatPhoneNumber(loan.getContact()), fieldY);

        // Reset Y for column 2
        fieldY = y;
        // Column 2
        addField(contentStream, "Email:", loan.getEmail(), fieldY, col2X);
        fieldY -= LINE_SPACING;
        addField(contentStream, "Address:", loan.getAddress(), fieldY, col2X);

        return y -= 25;
    }

    /**
     * Adds loan details section with professional formatting
     */
    private static float addLoanDetailsSection(PDPageContentStream contentStream, Loan loan, float y)
            throws IOException {
        addSectionHeader(contentStream, "Loan Details", y);
        y -= 12;

        // Create two columns for loan details
        float col2X = PAGE_WIDTH / 2 + 50;
        float fieldY = y;

        // Column 1
        addField(contentStream, "Loan Number:", loan.getLoanId(), fieldY);
        fieldY -= LINE_SPACING;
        addField(contentStream, "Account Type:", loan.getAccountType().toString(), fieldY);
        fieldY -= LINE_SPACING;
        addField(contentStream, "Principal Amount:", CURRENCY.format(loan.getLoanAmount()), fieldY);

        // Reset Y for column 2
        fieldY = y;
        // Column 2
        addField(contentStream, "Interest Rate:", String.format("%.2f%%", loan.getInterestRate()),
                fieldY, col2X);
        fieldY -= LINE_SPACING;
        addField(contentStream, "Term Length:", loan.getTermMonths() + " months", fieldY, col2X);

        return y -= 25;
    }

    /**
     * Adds payment schedule section with calculated values
     */
    private static float addPaymentSection(PDPageContentStream contentStream, Loan loan, float y)
            throws IOException {
        addSectionHeader(contentStream, "Payment Schedule", y);
        y -= 12;

        // Add payment details in a structured format
        addField(contentStream, "Monthly Payment:", CURRENCY.format(loan.getMonthlyPayment()), y);
        y -= LINE_SPACING;
        addField(contentStream, "Total Payment:", CURRENCY.format(loan.getTotalPayment()), y);
        y -= LINE_SPACING;
        addField(contentStream, "Total Interest:",
                CURRENCY.format(loan.getTotalPayment() - loan.getLoanAmount()), y);

        // Add payment instructions
        y -= LINE_SPACING * 2;
        String instructions = "Monthly payments are due on the same day of each month as the issue date. " +
                "Payments can be made via direct debit, online banking, or at any BankIT branch.";
        addMultiLineText(contentStream, instructions, y);

        return y -= 25;
    }

    /**
     * Adds signature section with proper formatting and spaces
     */
    private static float addSignatureSection(PDPageContentStream contentStream, float y) throws IOException {
        addSectionHeader(contentStream, "Signatures", y);
        y -= 30;

        // Customer signature box
        addSignatureBox(contentStream, "Customer Signature", y, "Date");
        y -= 50;

        // Officer signature box
        addSignatureBox(contentStream, "Loan Officer Signature", y, "Date");
        y -= 50;

        // Witness signature box (if needed)
        addSignatureBox(contentStream, "Witness Signature", y, "Date");

        return y - 30;
    }

    /**
     * Adds a professional signature box with date field
     */
    private static void addSignatureBox(PDPageContentStream contentStream, String label, float y,
                                        String dateLabel) throws IOException {
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA, CONTENT_FONT_SIZE);
        contentStream.newLineAtOffset(MARGIN_LEFT, y + 10); // Reduced spacing
        contentStream.showText(label);
        contentStream.endText();

        float boxHeight = 30; // Reduced from 40
        float boxWidth = 200;
        contentStream.addRect(MARGIN_LEFT, y - boxHeight, boxWidth, boxHeight);
        contentStream.stroke();

        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA, CONTENT_FONT_SIZE);
        contentStream.newLineAtOffset(MARGIN_LEFT + boxWidth + 40, y + 10);
        contentStream.showText(dateLabel + ": _________________");
        contentStream.endText();
    }

    /**
     * Adds the risk assessment section for internal documents
     */
    private static float addRiskAssessmentSection(PDPageContentStream contentStream, Loan loan, float y)
            throws IOException {
        addSectionHeader(contentStream, "Risk Assessment", y);
        y -= 12;

        // Add risk metrics in a structured format
        float col2X = PAGE_WIDTH / 2 + 50;
        float fieldY = y;

        // Column 1 - Risk Metrics
        addField(contentStream, "Risk Level:", "Standard", fieldY);
        fieldY -= LINE_SPACING;
        addField(contentStream, "Credit Score:", "Not Available", fieldY);
        fieldY -= LINE_SPACING;
        addField(contentStream, "Default Risk:", "Low", fieldY);

        // Reset Y for column 2
        fieldY = y;
        // Column 2 - Additional Metrics
        addField(contentStream, "Debt-to-Income:", "N/A", fieldY, col2X);
        fieldY -= LINE_SPACING;
        addField(contentStream, "Collateral Value:", "N/A", fieldY, col2X);
        fieldY -= LINE_SPACING;
        addField(contentStream, "Previous Defaults:", "None", fieldY, col2X);

        y = fieldY - LINE_SPACING;

        // Add risk notes
        y -= LINE_SPACING;
        String riskNotes = "Standard risk assessment based on customer profile and loan parameters. " +
                "Monthly payment represents " + calculatePaymentToIncome(loan) + "% of stated income.";
        addMultiLineText(contentStream, riskNotes, y);

        return y - 15;
    }

    /**
     * Adds the audit section for internal documents
     */
    private static float addAuditSection(PDPageContentStream contentStream, float y) throws IOException {
        addSectionHeader(contentStream, "Audit Information", y);
        y -= LINE_SPACING;

        // Add audit trail information
        addField(contentStream, "Created Date:", DateUtils.getCurrentDateForDisplay(), y);
        y -= LINE_SPACING;
        addField(contentStream, "Created By:", "System", y);
        y -= LINE_SPACING;
        addField(contentStream, "Document ID:", generateDocumentId(), y);
        y -= LINE_SPACING;
        addField(contentStream, "Version:", VERSION, y);
        y -= LINE_SPACING;

        return y - SECTION_SPACING;
    }

    /**
     * Adds the approval chain section for internal documents
     */
    private static float addApprovalSection(PDPageContentStream contentStream, float y) throws IOException {
        addSectionHeader(contentStream, "Approval Chain", y);
        y -= 12;

        // Add approval boxes with status
        y = addApprovalBox(contentStream, "Loan Officer Review", "Pending", y);
        y -= LINE_SPACING * 2;
        y = addApprovalBox(contentStream, "Department Head Approval", "Pending", y);
        y -= LINE_SPACING * 2;
        y = addApprovalBox(contentStream, "Risk Assessment Review", "Pending", y);
        y -= LINE_SPACING * 2;
        y = addApprovalBox(contentStream, "Final Authorization", "Pending", y);

        return y - 30;
    }

    /**
     * Adds a single approval box with signature line and status
     */
    private static float addApprovalBox(PDPageContentStream contentStream, String title, String status,
                                        float y) throws IOException {
        // Add title
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, CONTENT_FONT_SIZE);
        contentStream.newLineAtOffset(MARGIN_LEFT, y);
        contentStream.showText(title);
        contentStream.endText();

        // Add status
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA, CONTENT_FONT_SIZE);
        contentStream.newLineAtOffset(PAGE_WIDTH - MARGIN_RIGHT - 100, y);
        contentStream.showText("Status: " + status);
        contentStream.endText();

        // Add signature line
        y -= LINE_SPACING;
        contentStream.moveTo(MARGIN_LEFT, y);
        contentStream.lineTo(MARGIN_LEFT + 200, y);
        contentStream.stroke();

        // Add date line
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA, CONTENT_FONT_SIZE);
        contentStream.newLineAtOffset(MARGIN_LEFT + 250, y);
        contentStream.showText("Date: _________________");
        contentStream.endText();

        return y;
    }

    /**
     * Adds detailed terms and conditions section
     */
    private static float addDetailedTermsSection(PDPageContentStream contentStream, float y)
            throws IOException {
        addSectionHeader(contentStream, "Terms and Conditions", y);
        y -= 12;;

        String[] terms = {
                "1. Payment Terms: Monthly payments must be made by the due date specified in the payment schedule.",
                "2. Late Payments: A late fee of 5% will be charged for payments received after the due date.",
                "3. Early Repayment: The borrower may repay the loan early without penalty.",
                "4. Default: Failure to make payments may result in legal action and negative credit reporting.",
                "5. Changes: Terms may not be modified unless agreed upon in writing by both parties.",
                "6. Insurance: The borrower must maintain required insurance coverage throughout the loan term."
        };

        for (String term : terms) {
            addMultiLineText(contentStream, term, y);
            y -= LINE_SPACING * 1.5f;
        }

        return y;
    }

    /**
     * Adds document control information
     */
    private static float addDocumentControl(PDPageContentStream contentStream, float y) throws IOException {
        String docId = generateDocumentId();
        String docDate = DateUtils.getCurrentDateForDisplay();

        // Add control info in top right corner
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA, CONTENT_FONT_SIZE);
        contentStream.newLineAtOffset(PAGE_WIDTH - MARGIN_RIGHT - 200, y);
        contentStream.showText("Control #: " + docId);
        contentStream.endText();

        // Add date below control number
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA, CONTENT_FONT_SIZE);
        contentStream.newLineAtOffset(PAGE_WIDTH - MARGIN_RIGHT - 200, y - LINE_SPACING);
        contentStream.showText("Date: " + docDate);
        contentStream.endText();

        return y - (LINE_SPACING * 2);
    }

    /**
     * Adds important dates section
     */
    private static float addDatesSection(PDPageContentStream contentStream, Loan loan, float y)
            throws IOException {
        addSectionHeader(contentStream, "Important Dates", y);
        y -= 12;

        // Add dates in two columns
        float col2X = PAGE_WIDTH / 2 + 50;
        float fieldY = y;

        // Column 1
        addField(contentStream, "Issue Date:", loan.getIssueDate().format(DATE_FORMATTER), fieldY);
        fieldY -= LINE_SPACING;
        addField(contentStream, "First Payment:",
                loan.getIssueDate().plusMonths(1).format(DATE_FORMATTER), fieldY);

        // Reset Y for column 2
        fieldY = y;
        // Column 2
        addField(contentStream, "Due Date:", loan.getDueDate().format(DATE_FORMATTER), fieldY, col2X);
        fieldY -= LINE_SPACING;
        addField(contentStream, "Payment Day:",
                String.format("Day %d of each month", loan.getIssueDate().getDayOfMonth()),
                fieldY, col2X);

        return y - 20;
    }

    /**
     * Adds document control section for internal documents
     */
    private static float addDocumentControlSection(PDPageContentStream contentStream, float y)
            throws IOException {
        addSectionHeader(contentStream, "Document Control", y);
        y -= LINE_SPACING;

        String docId = generateDocumentId();
        String docDate = DateUtils.getCurrentDateForDisplay();

        addField(contentStream, "Document ID:", docId, y);
        y -= LINE_SPACING;
        addField(contentStream, "Generated Date:", docDate, y);
        y -= LINE_SPACING;
        addField(contentStream, "Document Type:", "Internal Loan Record", y);
        y -= LINE_SPACING;
        addField(contentStream, "Security Level:", "Confidential", y);
        y -= LINE_SPACING;
        addField(contentStream, "Version:", VERSION, y);

        return y - SECTION_SPACING;
    }

    /**
     * Utility method to calculate payment to income ratio
     */
    private static String calculatePaymentToIncome(Loan loan) {
        // Placeholder - would normally calculate based on actual income data
        return "30";
    }

    /**
     * Formats a phone number consistently
     */
    private static String formatPhoneNumber(String phone) {
        if (phone == null || phone.length() != 11) return phone;
        return String.format("%s-%s-%s",
                phone.substring(0, 3),
                phone.substring(3, 7),
                phone.substring(7));
    }

    /**
     * Adds a watermark to the document
     */
    private static void addWatermark(PDPageContentStream contentStream, String text) throws IOException {
        contentStream.saveGraphicsState();
        contentStream.setNonStrokingColor(0.9f, 0.9f, 0.9f); // Light gray
        contentStream.transform(new Matrix(
                (float) Math.cos(Math.PI / 4), (float) Math.sin(Math.PI / 4),
                -(float) Math.sin(Math.PI / 4), (float) Math.cos(Math.PI / 4),
                PAGE_WIDTH/2, PAGE_HEIGHT/2
        ));

        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 60);
        contentStream.newLineAtOffset(-100, 0);
        contentStream.showText(text);
        contentStream.endText();
        contentStream.restoreGraphicsState();
    }

    /**
     * Adds a section header with consistent formatting
     */
    private static void addSectionHeader(PDPageContentStream contentStream, String title, float y)
            throws IOException {
        // Add section title
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, SECTION_FONT_SIZE);
        contentStream.newLineAtOffset(MARGIN_LEFT, y);
        contentStream.showText(title);
        contentStream.endText();

        // Add underline
        float titleWidth = SECTION_FONT_SIZE * title.length() * 0.5f;
        contentStream.setLineWidth(0.5f);
        contentStream.moveTo(MARGIN_LEFT, y - 2);
        contentStream.lineTo(MARGIN_LEFT + titleWidth, y - 2);
        contentStream.stroke();
    }

    /**
     * Adds a field with label and value
     */
    private static void addField(PDPageContentStream contentStream, String label, String value, float y)
            throws IOException {
        addField(contentStream, label, value, y, MARGIN_LEFT);
    }

    /**
     * Adds a field with label and value at specified x position
     */
    private static void addField(PDPageContentStream contentStream, String label, String value,
                                 float y, float x) throws IOException {
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, CONTENT_FONT_SIZE);
        contentStream.newLineAtOffset(x, y);
        contentStream.showText(label);

        contentStream.setFont(PDType1Font.HELVETICA, CONTENT_FONT_SIZE);
        contentStream.newLineAtOffset(FIELD_INDENT, 0);
        contentStream.showText(value != null ? value : "");
        contentStream.endText();
    }

    /**
     * Adds multi-line text with word wrapping
     */
    private static void addMultiLineText(PDPageContentStream contentStream, String text, float y)
            throws IOException {
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA, CONTENT_FONT_SIZE);
        contentStream.newLineAtOffset(MARGIN_LEFT, y);

        // Calculate available width for text
        float availableWidth = PAGE_WIDTH - MARGIN_LEFT - MARGIN_RIGHT;

        String[] words = text.split(" ");
        float spaceWidth = CONTENT_FONT_SIZE * 0.3f;
        float currentWidth = 0;
        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {
            float wordWidth = getWordWidth(word, CONTENT_FONT_SIZE);

            if (currentWidth + wordWidth > availableWidth) {
                // Write current line and start new line
                contentStream.showText(currentLine.toString());
                contentStream.newLineAtOffset(0, -LINE_SPACING);
                currentLine = new StringBuilder(word);
                currentWidth = wordWidth;
            } else {
                // Add word to current line
                if (currentLine.length() > 0) {
                    currentLine.append(" ");
                    currentWidth += spaceWidth;
                }
                currentLine.append(word);
                currentWidth += wordWidth;
            }
        }

        // Write final line if any
        if (currentLine.length() > 0) {
            contentStream.showText(currentLine.toString());
        }

        contentStream.endText();
    }

    /**
     * Adds content to the document with automatic page breaks
     * @param document The PDF document
     * @param loan The loan data
     * @param documentType Type of document ("internal" or "customer")
     */
    private static void addContent(PDDocument document, Loan loan, String documentType) throws IOException {
        PDPage currentPage = null;
        PDPageContentStream contentStream = null;
        float y = MARGIN_TOP;
        float pageHeight = PDRectangle.A4.getHeight();
        float minY = MARGIN_BOTTOM + 30; // Minimum space needed for footer

        // Create first page
        currentPage = new PDPage(PDRectangle.A4);
        document.addPage(currentPage);
        contentStream = new PDPageContentStream(document, currentPage);

        // Add document header
        y = addDocumentHeader(document, contentStream,
                documentType.equals("internal") ? "Internal Loan Record" : "Loan Agreement",
                y);

        // Add bank information section
        if (y < minY) {
            contentStream.close();
            currentPage = new PDPage(PDRectangle.A4);
            document.addPage(currentPage);
            contentStream = new PDPageContentStream(document, currentPage);
            y = MARGIN_TOP;
        }
        y = addBankInformation(contentStream, y - SECTION_SPACING);

        // Add document control section for internal document
        if (documentType.equals("internal")) {
            if (y < minY) {
                contentStream.close();
                currentPage = new PDPage(PDRectangle.A4);
                document.addPage(currentPage);
                contentStream = new PDPageContentStream(document, currentPage);
                y = MARGIN_TOP;
            }
            y = addDocumentControlSection(contentStream, y - SECTION_SPACING);
        }

        // Add customer information section
        if (y < minY) {
            contentStream.close();
            currentPage = new PDPage(PDRectangle.A4);
            document.addPage(currentPage);
            contentStream = new PDPageContentStream(document, currentPage);
            y = MARGIN_TOP;
        }
        y = addCustomerSection(contentStream, loan, y - SECTION_SPACING);

        // Add loan details section
        if (y < minY) {
            contentStream.close();
            currentPage = new PDPage(PDRectangle.A4);
            document.addPage(currentPage);
            contentStream = new PDPageContentStream(document, currentPage);
            y = MARGIN_TOP;
        }
        y = addLoanDetailsSection(contentStream, loan, y - SECTION_SPACING);

        // Add payment schedule section
        if (y < minY) {
            contentStream.close();
            currentPage = new PDPage(PDRectangle.A4);
            document.addPage(currentPage);
            contentStream = new PDPageContentStream(document, currentPage);
            y = MARGIN_TOP;
        }
        y = addPaymentSection(contentStream, loan, y - SECTION_SPACING);

        // Add dates section
        if (y < minY) {
            contentStream.close();
            currentPage = new PDPage(PDRectangle.A4);
            document.addPage(currentPage);
            contentStream = new PDPageContentStream(document, currentPage);
            y = MARGIN_TOP;
        }
        y = addDatesSection(contentStream, loan, y - SECTION_SPACING);

        // Add terms and conditions
        if (y < minY) {
            contentStream.close();
            currentPage = new PDPage(PDRectangle.A4);
            document.addPage(currentPage);
            contentStream = new PDPageContentStream(document, currentPage);
            y = MARGIN_TOP;
        }
        y = addDetailedTermsSection(contentStream, y - SECTION_SPACING);

        // Add risk assessment for internal document
        if (documentType.equals("internal")) {
            if (y < minY) {
                contentStream.close();
                currentPage = new PDPage(PDRectangle.A4);
                document.addPage(currentPage);
                contentStream = new PDPageContentStream(document, currentPage);
                y = MARGIN_TOP;
            }
            y = addRiskAssessmentSection(contentStream, loan, y - SECTION_SPACING);
        }

        // Add approval chain for internal document
        if (documentType.equals("internal")) {
            if (y < minY) {
                contentStream.close();
                currentPage = new PDPage(PDRectangle.A4);
                document.addPage(currentPage);
                contentStream = new PDPageContentStream(document, currentPage);
                y = MARGIN_TOP;
            }
            y = addApprovalSection(contentStream, y - SECTION_SPACING);
        }

        // Add signature section for customer document
        if (!documentType.equals("internal")) {
            if (y < minY) {
                contentStream.close();
                currentPage = new PDPage(PDRectangle.A4);
                document.addPage(currentPage);
                contentStream = new PDPageContentStream(document, currentPage);
                y = MARGIN_TOP;
            }
            y = addSignatureSection(contentStream, y - SECTION_SPACING);
        }

        // Add footer to last page
        addDocumentFooter(contentStream,
                documentType.equals("internal") ? "CONFIDENTIAL - INTERNAL USE ONLY" : "OFFICIAL LOAN AGREEMENT",
                document.getNumberOfPages());

        // Close the final content stream
        contentStream.close();
    }

    /**
     * Adds the document footer with page numbers and control info
     * @param contentStream The PDF content stream
     * @param documentType The type of document
     * @param totalPages Total number of pages in document
     */
    private static void addDocumentFooter(PDPageContentStream contentStream, String documentType, int totalPages)
            throws IOException {
        // Add left-aligned footer text with document ID and type
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA, FOOTER_FONT_SIZE);
        contentStream.newLineAtOffset(MARGIN_LEFT, MARGIN_BOTTOM);

        // Create footer text with document ID, type, and generation date
        String footerText = String.format("Document ID: %s | %s | Generated: %s | %s",
                generateDocumentId(),
                documentType,
                DateUtils.getCurrentDateForDisplay(),
                VERSION);

        contentStream.showText(footerText);
        contentStream.endText();

        // Add page numbers at bottom right
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA, FOOTER_FONT_SIZE);
        contentStream.newLineAtOffset(PAGE_WIDTH - MARGIN_RIGHT - 50, MARGIN_BOTTOM);
        contentStream.showText(String.format("Page %d of %d", totalPages, totalPages));
        contentStream.endText();

        // Add horizontal line above footer
        contentStream.setLineWidth(0.5f);
        contentStream.moveTo(MARGIN_LEFT, MARGIN_BOTTOM + 15);
        contentStream.lineTo(PAGE_WIDTH - MARGIN_RIGHT, MARGIN_BOTTOM + 15);
        contentStream.stroke();
    }

    /**
     * Calculates width of text based on font size
     */
    private static float getWordWidth(String word, float fontSize) {
        return word.length() * fontSize * 0.5f;
    }

    /**
     * Generates a unique document ID
     */
    private static String generateDocumentId() {
        return "DOC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    /**
     * Utility method to convert degrees to radians
     */
    private static double toRadians(double degrees) {
        return degrees * Math.PI / 180;
    }
}