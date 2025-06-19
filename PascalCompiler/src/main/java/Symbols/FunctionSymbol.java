package Symbols;

import java.util.List;

public class FunctionSymbol extends Symbol{
    private List<Symbol> parameters;
    private Type returnType;

    public FunctionSymbol(String identifier, Type returnType, List<Symbol> parameters, int scopeLevel) {
        super(identifier, new Type("function"), scopeLevel);
        this.parameters = parameters;
        this.returnType = returnType;
    }

    public Type getReturnType() {
        return returnType;
    }

    public void setReturnType(Type returnType) {
        this.returnType = returnType;
    }

    public List<Symbol> getParameters() {
        return parameters;
    }

    public void setParameters(List<Symbol> parameters) {
        this.parameters = parameters;
    }

    @Override
    public String toString() {
        return String.format("%s, returnType: %s, parameters: %s)",
                getIdentifier(), returnType, parameters);
    }
}
