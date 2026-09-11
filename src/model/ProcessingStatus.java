package model;

/**
 * Outcome of processing a single document.
 */
public enum ProcessingStatus {
    /** The document was validated and processed successfully. */
    SUCCESS,
    /** The document was rejected before processing because it failed validation. */
    REJECTED,
    /** Validation passed but an unexpected error occurred while processing. */
    FAILED
}
