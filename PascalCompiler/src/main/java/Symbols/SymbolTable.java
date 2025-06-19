package Symbols;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;


public class SymbolTable {
    private Map<String, Symbol> symbols = new HashMap<>();
    public int scopeLevel = 0;

    public void add(Symbol symbol) {
        if(symbols.containsKey(symbol.getIdentifier())) {
            if(symbols.get(symbol.getIdentifier()).getScopeLevel() == symbol.getScopeLevel()) {
                System.err.println("Symbol already exists in this scope: " + symbol.getIdentifier());
            }else{
                symbols.put(symbol.getIdentifier(), symbol);
                System.out.println("Declared Symbol: " + symbol.getIdentifier() + " at scope level " + symbol.getScopeLevel());
            }
        } else {
            symbols.put(symbol.getIdentifier(), symbol);
            System.out.println("Declared Symbol: " + symbol.getIdentifier() + " at scope level " + symbol.getScopeLevel());
        }
    }

    public Symbol lookup_variable(String identifier) {
        if(symbols.containsKey(identifier)) {
            System.out.println("Found Symbol: " + identifier + " at scope level " + symbols.get(identifier).getScopeLevel());
            return symbols.get(identifier);
        }
        System.out.println("Symbol not found: " + identifier + " at scope level " + scopeLevel);
        return null;
    }

    public int lookup_variable_in_scope(String identifier) {
        if(symbols.containsKey(identifier)) {
            if(symbols.get(identifier).getScopeLevel() == scopeLevel) {
                System.out.println("Found Symbol: " + identifier + " at scope level " + symbols.get(identifier).getScopeLevel());
                return symbols.get(identifier).getScopeLevel();
            }
        }
        System.out.println("Symbol not found: " + identifier + " at scope level " + scopeLevel);
        return -1;
    }

    public Type lookup_type(String identifier){
        return get_type(lookup_variable(identifier));
    }

    public int getScopeLevel() {
        return scopeLevel;
    }

    public void enterScope() {
        scopeLevel++;
        System.out.println("Entering scope: " + scopeLevel);
    }

    public void exitScope() { //cambiar despues
        scopeLevel--;
        System.out.println("Exiting scope: " + scopeLevel);
    }

    public Type get_type(Symbol symbol){
        return symbol.getType();
    }

    public int getScopeLevel(String identifier){
        return symbols.get(identifier).getScopeLevel();
    }

    public void setScopeLevel(int scopeLevel){
        this.scopeLevel = scopeLevel;
    }

    public void allSymbols() {
        System.out.println("All symbols: ");
        for (String key : symbols.keySet()) {
            System.out.println("───────────────────────────────────────────────────────────────────────────────────");
            System.out.println(symbols.get(key).toString());
            System.out.println("───────────────────────────────────────────────────────────────────────────────────");
        }
    }

    @Override
    public String toString() {
        return symbols.values().toString();
    }

    public Map<String, Symbol> getSymbols() {
        return symbols;
    }

    private void initializeBasicTypes() {
        add(new Symbol("integer", new Type("integer", -1, Type.BasicType.integer), 0));
        add(new Symbol("boolean", new Type("boolean", -1, Type.BasicType.boolean_), 0));
        add(new Symbol("char", new Type("char", 1, Type.BasicType.user_defined), 0));
        add(new Symbol("string", new Type("string", -1, Type.BasicType.string), 0));
    }


}
