package IntermediateRepresentation;

import java.util.Objects;

public class TACInstruction {
    private String operation;
    private String result;
    private String op1;
    private String op2;

    public TACInstruction(String operation, String result, String op1, String op2) {
        this.operation = operation;
        this.result = result;
        this.op1 = op1;
        this.op2 = op2;
    }

    public TACInstruction(String operation, String result, String op1) {
        this.operation = operation;
        this.result = result;
        this.op1 = op1;
        this.op2 = null;
    }

    public String getOperation() {
        return operation;
    }

    public String getResult() {
        return result;
    }

    public String getOp1() {
        return op1;
    }

    public String getOp2() {
        return op2;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public void setOp1(String op1) {
        this.op1 = op1;
    }

    public void setOp2(String op2) {
        this.op2 = op2;
    }

    public boolean isBinaryOperation() {
        return op2 != null;
    }

    @Override
    public String toString() {
        if (op2 != null) {
            return result + " = " + op1 + " " + operation + " " + op2;
        } else {
            return result + " = " + operation + " " + op1;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        TACInstruction that = (TACInstruction) obj;
        return Objects.equals(operation, that.operation) &&
               Objects.equals(result, that.result) &&
               Objects.equals(op1, that.op1) &&
               Objects.equals(op2, that.op2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(operation, result, op1, op2);
    }
}