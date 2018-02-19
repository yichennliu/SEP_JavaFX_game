package model.enums;

public enum Medal {
    BRONZE("Bronze"), SILVER("Silber"), GOLD("Gold");
    private String displayName;

    Medal (String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
