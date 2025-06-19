package IntermediateRepresentation;

import java.util.*;

public class TACOptimizer {
    private List<TACInstruction> instructions;
    private Map<String, String> lastAssignedValue = new HashMap<>(); // Store the last value assigned to each variable

    public TACOptimizer(List<TACInstruction> instructions) {
        this.instructions = instructions;
    }

    public List<TACInstruction> optimize() {
        removeRedundantLoadsAndStores();
        performConstantFolding();
        eliminateDeadCode();
        propagateCopies();
        simplifyAlgebraicExpressions();
        avoidRedundantAssignmentsAfterRead();
        skipRedundantWrites();
        return instructions;
    }

    private void removeRedundantLoadsAndStores() {
        List<TACInstruction> optimizedInstructions = new ArrayList<>();
        Set<String> accessedVariables = new HashSet<>();

        // First pass to determine used variables
        for (TACInstruction instr : instructions) {
            if (instr.getOperation().equals("LOAD") || instr.getOperation().equals("STORE")) {
                accessedVariables.add(instr.getResult());
                accessedVariables.add(instr.getOp1());
            }
        }

        // Second pass to eliminate redundant loads and stores
        for (TACInstruction instr : instructions) {
            if ((instr.getOperation().equals("LOAD") || instr.getOperation().equals("STORE"))
                    && !accessedVariables.contains(instr.getResult())) {
                continue; // Skip redundant load/store
            }
            optimizedInstructions.add(instr);
        }

        instructions = optimizedInstructions;
    }

    private void performConstantFolding() {
        for (TACInstruction instr : instructions) {
            if (instr.getOperation().matches("[\\+\\-\\*/]") && isNumeric(instr.getOp1()) && isNumeric(instr.getOp2())) {
                double result = calculate(instr.getOp1(), instr.getOp2(), instr.getOperation());
                instr.setOperation("ASSIGN");
                instr.setOp1(String.valueOf(result));
                instr.setOp2(null);
            }
        }
    }

    private void eliminateDeadCode() {
        Set<String> usedVariables = new HashSet<>();
        List<TACInstruction> optimizedInstructions = new ArrayList<>();

        // Collect used variables
        for (TACInstruction instr : instructions) {
            if (instr.getOp1() != null) {
                usedVariables.add(instr.getOp1());
            }
            if (instr.getOp2() != null) {
                usedVariables.add(instr.getOp2());
            }
        }

        // Eliminate dead code
        for (TACInstruction instr : instructions) {
            if (instr.getOperation().equals("ASSIGN") && !usedVariables.contains(instr.getResult())) {
                continue;
            }
            optimizedInstructions.add(instr);
        }

        instructions = optimizedInstructions;
    }

    private void propagateCopies() {
        Map<String, String> copyMap = new HashMap<>();
        List<TACInstruction> optimizedInstructions = new ArrayList<>();

        for (TACInstruction instr : instructions) {
            if (instr.getOperation().equals("ASSIGN") && !instr.getResult().contains("[")) {
                copyMap.put(instr.getResult(), instr.getOp1());
            }

            // Substitute uses of copied variables
            if (copyMap.containsKey(instr.getOp1())) {
                instr.setOp1(copyMap.get(instr.getOp1()));
            }
            if (copyMap.containsKey(instr.getOp2())) {
                instr.setOp2(copyMap.get(instr.getOp2()));
            }

            optimizedInstructions.add(instr);
        }

        instructions = optimizedInstructions;
    }

    private void simplifyAlgebraicExpressions() {
        for (TACInstruction instr : instructions) {
            if (instr.getOperation().equals("+") && instr.getOp2().equals("0")) {
                instr.setOperation("ASSIGN");
                instr.setOp2(null);
            }
        }
    }

    private boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private double calculate(String op1, String op2, String operation) {
        double num1 = Double.parseDouble(op1);
        double num2 = Double.parseDouble(op2);
        switch (operation) {
            case "+":
                return num1 + num2;
            case "-":
                return num1 - num2;
            case "*":
                return num1 * num2;
            case "/":
                return num1 / num2;
            default:
                throw new IllegalArgumentException("Unsupported operation: " + operation);
        }
    }

    private void avoidRedundantAssignmentsAfterRead() {
        List<TACInstruction> optimizedInstructions = new ArrayList<>();
        boolean skipNextAssign = false;

        for (TACInstruction instr : instructions) {
            if (instr.getOperation().equals("CALL") && instr.getOp1().equals("READ")) {
                skipNextAssign = true;
                optimizedInstructions.add(instr);
                continue;
            }

            if (skipNextAssign && instr.getOperation().equals("ASSIGN")) {
                skipNextAssign = false;
                continue;
            }

            optimizedInstructions.add(instr);
        }

        instructions = optimizedInstructions;
    }

    private void skipRedundantWrites() {
        List<TACInstruction> optimizedInstructions = new ArrayList<>();
        boolean lastWasPush = false;

        for (TACInstruction instr : instructions) {
            if (instr.getOperation().equals("PUSH")) {
                lastWasPush = true;
                optimizedInstructions.add(instr);
                continue;
            }

            if (lastWasPush && instr.getOperation().equals("CALL") && instr.getOp1().equals("WRITE")) {
                lastWasPush = false;
                continue;
            }

            lastWasPush = false;
            optimizedInstructions.add(instr);
        }

        instructions = optimizedInstructions;
    }
}
