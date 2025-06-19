package Symbols;

import com.ibm.icu.message2.Mf2DataModel;

import java.util.List;
import java.util.Optional;

public class ArraySymbol extends Symbol {
    private Type elementType;
    private List<VariableSymbol> elements;

    public ArraySymbol(String identifier, Type elementType, List<VariableSymbol> elements, int scopeLevel) {
        super(identifier, elementType, scopeLevel);
        this.elementType = elementType;
        this.elements = elements;
        super.getType().setSize(elements.size());
    }

    public Type getElementType() {
        return elementType;
    }

    public void setElementType(Type elementType) {
        this.elementType = elementType;
    }

    public List<VariableSymbol> getelements() {
        return elements;
    }

    public void setelements(List<VariableSymbol> elements) {
        this.elements = elements;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ArraySymbol(identifier: ").append(getIdentifier()).append(", elementType: ").append(", scopeLevel: ").append(getScopeLevel()).append(elementType).append(", elements: [\n");
        for (VariableSymbol element : elements) {
            sb.append(element.getIdentifier()).append(", ");
        }
        sb.append("])");
        return sb.toString();
    }

}
