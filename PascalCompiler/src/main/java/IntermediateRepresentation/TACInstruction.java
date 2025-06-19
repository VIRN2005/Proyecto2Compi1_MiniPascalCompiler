package IntermediateRepresentation;

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

    @Override
    public String toString() {
        return result + " = " + operation + " " + op1 + (op2 == null ? "" : ", " + op2);
    }
}
