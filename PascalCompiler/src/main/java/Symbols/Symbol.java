package Symbols;

public class Symbol {

    private String identifier;
    private Type type;
    private int scopeLevel;

    public Symbol(String identifier, Type type, int scopeLevel) {
        this.identifier = identifier;
        this.type = type;
        this.scopeLevel = scopeLevel;
    }

    public String getIdentifier() {
        return identifier;
    }
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
    public Type getType() {
        return type;
    }
    public void setType(Type type) {
        this.type = type;
    }
    public int getScopeLevel() {
        return scopeLevel;
    }
    public void setScopeLevel(int scopeLevel) {
        this.scopeLevel = scopeLevel;
    }

    @Override
    public String toString() {
        return String.format("Symbol(identifier: %s, type: %s, scopeLevel: %s)", identifier, type, scopeLevel);
    }

}
