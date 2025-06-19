package Symbols;

public class VariableSymbol extends Symbol {
    private boolean isInitialized;

    public VariableSymbol(String identifier, Type type, boolean isInitialized, int scopeLevel) {
        super(identifier, type, scopeLevel);
        this.isInitialized = isInitialized;
    }

    public boolean isInitialized() {
        return isInitialized;
    }

    public void setInitialized(boolean isInitialized) {
        this.isInitialized = isInitialized;
    }

    @Override
    public String toString() {
        return String.format("%s, type: %s, isInitialized: %b",
                getIdentifier(), getType(), isInitialized);
    }
}
