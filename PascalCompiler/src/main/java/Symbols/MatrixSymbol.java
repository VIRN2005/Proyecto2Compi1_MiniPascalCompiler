package Symbols;

import java.util.List;

public class MatrixSymbol extends Symbol{
    private Type elementType;
    private VariableSymbol[][] elements;

    public MatrixSymbol(String identifier, Type elementType, VariableSymbol[][] elements, int scopeLevel) {
        super(identifier, elementType, scopeLevel);
        this.elementType = elementType;
        this.elements = elements;
        super.getType().setSize(elements.length);
    }

    public Type getElementType() {
        return elementType;
    }

    public void setElementType(Type elementType) {
        this.elementType = elementType;
    }

    public VariableSymbol[][] getelements() {
        return elements;
    }

    public void setelements(VariableSymbol[][] elements) {
        this.elements = elements;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("MatrixSymbol(identifier: ").append(getIdentifier()).append(", elementType: ").append(", scopeLevel: ").append(getScopeLevel()).append(elementType).append(", elements: [\n");
        for (VariableSymbol[] row : elements) {
            sb.append("[");
            for (VariableSymbol element : row) {
                sb.append(element.getIdentifier()).append(", ");
            }
            sb.append("], \n");
        }
        sb.append("])");
        return sb.toString();
    }
}
