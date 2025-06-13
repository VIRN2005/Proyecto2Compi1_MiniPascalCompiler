package IntermediateRepresentation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class TACOptimizer {
    private List<TACInstruction> instructions;
    private Map<String, String> lastAssignedValue = new HashMap<>();

    public TACOptimizer(List<TACInstruction> instructions) {
        this.instructions = new ArrayList<>(instructions); 
    }

    public List<TACInstruction> optimize() {
        boolean changed;
        int iterations = 0;
        final int MAX_ITERATIONS = 10; 
        
        do {
            changed = false;
            int sizeBefore = instructions.size();
            
            removeRedundantLoadsAndStores();
            performConstantFolding();
            eliminateDeadCode();
            propagateCopies();
            simplifyAlgebraicExpressions();
            avoidRedundantAssignmentsAfterRead();
            skipRedundantWrites();
            
            changed = (instructions.size() != sizeBefore);
            iterations++;
        } while (changed && iterations < MAX_ITERATIONS);
        
        return new ArrayList<>(instructions);
    }

    private void removeRedundantLoadsAndStores() {
        List<TACInstruction> optimizedInstructions = new ArrayList<>();
        Map<String, TACInstruction> lastLoad = new HashMap<>();
        Map<String, TACInstruction> lastStore = new HashMap<>();

        for (TACInstruction instr : instructions) {
            if (instr.getOperation().equals("LOAD")) {
                String address = instr.getOp1();
                TACInstruction prevLoad = lastLoad.get(address);
                
                if (prevLoad != null && !wasModifiedBetween(address, prevLoad, instr)) {
                    continue; // Skip redundant load
                }
                lastLoad.put(address, instr);
                
            } else if (instr.getOperation().equals("STORE")) {
                String address = instr.getOp1();
                TACInstruction prevStore = lastStore.get(address);
                
                if (prevStore != null && Objects.equals(prevStore.getResult(), instr.getResult())) {
                    continue; // Skip redundant store
                }
                lastStore.put(address, instr);
            }
            
            optimizedInstructions.add(instr);
        }

        instructions = optimizedInstructions;
    }

    private boolean wasModifiedBetween(String address, TACInstruction start, TACInstruction end) {
        boolean foundStart = false;
        for (TACInstruction instr : instructions) {
            if (instr == start) {
                foundStart = true;
                continue;
            }
            if (instr == end) {
                break;
            }
            if (foundStart && instr.getOperation().equals("STORE") && 
                Objects.equals(instr.getOp1(), address)) {
                return true;
            }
        }
        return false;
    }

    private void performConstantFolding() {
        for (TACInstruction instr : instructions) {
            String op = instr.getOperation();
            if ((op.equals("+") || op.equals("-") || op.equals("*") || op.equals("/")) &&
                isNumeric(instr.getOp1()) && isNumeric(instr.getOp2())) {
                
                try {
                    double result = calculate(instr.getOp1(), instr.getOp2(), op);
    
                    if (isInteger(instr.getOp1()) && isInteger(instr.getOp2()) && 
                        result == Math.floor(result)) {
                        instr.setOp1(String.valueOf((int)result));
                    } else {
                        instr.setOp1(String.valueOf(result));
                    }
                    instr.setOperation("ASSIGN");
                    instr.setOp2(null);
                } catch (ArithmeticException e) {
                    
                }
            }
        }
    }

    private void eliminateDeadCode() {
        Set<String> usedVariables = new HashSet<>();
  
        boolean changed = true;
        while (changed) {
            changed = false;
            int sizeBefore = usedVariables.size();
            
            for (TACInstruction instr : instructions) {

                boolean instrIsNeeded = false;
                
                if (instr.getOp1() != null && (usedVariables.contains(instr.getOp1()) || 
                    isOutputInstruction(instr))) {
                    instrIsNeeded = true;
                }
                if (instr.getOp2() != null && usedVariables.contains(instr.getOp2())) {
                    instrIsNeeded = true;
                }
                
                if (isOutputInstruction(instr)) {
                    instrIsNeeded = true;
                }
                
                if (instrIsNeeded) {
                    if (instr.getOp1() != null) usedVariables.add(instr.getOp1());
                    if (instr.getOp2() != null) usedVariables.add(instr.getOp2());
                }
            }
            
            changed = (usedVariables.size() != sizeBefore);
        }

        List<TACInstruction> optimizedInstructions = new ArrayList<>();
        for (TACInstruction instr : instructions) {
            if (isOutputInstruction(instr) || 
                usedVariables.contains(instr.getResult()) ||
                (instr.getOp1() != null && usedVariables.contains(instr.getOp1())) ||
                (instr.getOp2() != null && usedVariables.contains(instr.getOp2()))) {
                optimizedInstructions.add(instr);
            }
        }

        instructions = optimizedInstructions;
    }

    private boolean isOutputInstruction(TACInstruction instr) {
        return instr.getOperation().equals("CALL") || 
               instr.getOperation().equals("RETURN") ||
               instr.getOperation().equals("STORE") ||
               (instr.getOperation().equals("CALL") && "write".equals(instr.getOp1()));
    }

    private void propagateCopies() {
        Map<String, String> copyMap = new HashMap<>();

        for (TACInstruction instr : instructions) {
            if (instr.getOperation().equals("ASSIGN") && 
                !instr.getResult().contains("[") && 
                !isNumeric(instr.getOp1())) {
                copyMap.put(instr.getResult(), instr.getOp1());
            }

            if (instr.getOp1() != null && copyMap.containsKey(instr.getOp1())) {
                instr.setOp1(copyMap.get(instr.getOp1()));
            }
            if (instr.getOp2() != null && copyMap.containsKey(instr.getOp2())) {
                instr.setOp2(copyMap.get(instr.getOp2()));
            }

            if (instr.getResult() != null) {
                copyMap.entrySet().removeIf(entry -> 
                    entry.getValue().equals(instr.getResult()));
            }
        }
    }

    private void simplifyAlgebraicExpressions() {
        for (TACInstruction instr : instructions) {
            String op = instr.getOperation();
            String op1 = instr.getOp1();
            String op2 = instr.getOp2();
            
            if (op2 != null) {
                // x + 0 = x, x - 0 = x
                if ((op.equals("+") || op.equals("-")) && op2.equals("0")) {
                    instr.setOperation("ASSIGN");
                    instr.setOp2(null);
                }
                // x * 1 = x, x / 1 = x
                else if ((op.equals("*") || op.equals("/")) && op2.equals("1")) {
                    instr.setOperation("ASSIGN");
                    instr.setOp2(null);
                }
                // x * 0 = 0
                else if (op.equals("*") && op2.equals("0")) {
                    instr.setOperation("ASSIGN");
                    instr.setOp1("0");
                    instr.setOp2(null);
                }
                // 0 + x = x
                else if (op.equals("+") && op1.equals("0")) {
                    instr.setOperation("ASSIGN");
                    instr.setOp1(op2);
                    instr.setOp2(null);
                }
            }
        }
    }

    private boolean isNumeric(String str) {
        if (str == null) return false;
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isInteger(String str) {
        if (str == null) return false;
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private double calculate(String op1, String op2, String operation) {
        double num1 = Double.parseDouble(op1);
        double num2 = Double.parseDouble(op2);
        switch (operation) {
            case "+": return num1 + num2;
            case "-": return num1 - num2;
            case "*": return num1 * num2;
            case "/": 
                if (num2 == 0) throw new ArithmeticException("Division by zero");
                return num1 / num2;
            default:
                throw new IllegalArgumentException("Unsupported operation: " + operation);
        }
    }

    private void avoidRedundantAssignmentsAfterRead() {
        List<TACInstruction> optimizedInstructions = new ArrayList<>();
        
        for (int i = 0; i < instructions.size(); i++) {
            TACInstruction current = instructions.get(i);
            optimizedInstructions.add(current);
            
            if (current.getOperation().equals("CALL") && "read".equals(current.getOp1())) {
                if (i + 1 < instructions.size()) {
                    TACInstruction next = instructions.get(i + 1);
                    if (next.getOperation().equals("ASSIGN") && 
                        Objects.equals(next.getOp1(), current.getResult())) {
                        i++; 
                    }
                }
            }
        }

        instructions = optimizedInstructions;
    }

    private void skipRedundantWrites() {
        List<TACInstruction> optimizedInstructions = new ArrayList<>();
        
        for (int i = 0; i < instructions.size(); i++) {
            TACInstruction current = instructions.get(i);
            
            if (current.getOperation().equals("PUSH")) {
                if (i + 1 < instructions.size()) {
                    TACInstruction next = instructions.get(i + 1);
                    if (next.getOperation().equals("CALL") && "write".equals(next.getOp1())) {
                        optimizedInstructions.add(current);
                        continue;
                    }
                }
            }
            
            optimizedInstructions.add(current);
        }

        instructions = optimizedInstructions;
    }
}