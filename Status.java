/**
 * The lifecycle states a device can be in.
 * Using an enum instead of plain strings means an invalid status can't be stored.
 */
public enum Status {
    AVAILABLE("Available"),
    DEPLOYED("Deployed"),
    IN_REPAIR("In Repair"),
    RETIRED("Retired");

    private final String label;

    Status(String label) {
        this.label = label;
    }

    /** Converts saved or typed text (e.g. "In Repair" or "IN_REPAIR") back into a Status. */
    public static Status fromText(String text) {
        if (text != null) {
            for (Status status : values()) {
                if (status.label.equalsIgnoreCase(text.trim()) || status.name().equalsIgnoreCase(text.trim())) {
                    return status;
                }
            }
        }
        throw new IllegalArgumentException("Unknown status: " + text);
    }

    /** The friendly name shown in the table, combo box, and CSV file. */
    @Override
    public String toString() {
        return label;
    }
}
