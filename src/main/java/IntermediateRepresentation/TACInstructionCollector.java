package IntermediateRepresentation;

import java.util.ArrayList;
import java.util.List;

public class TACInstructionCollector {
    private List<TACInstruction> instructions = new ArrayList<>();
    private StringBuilder output = new StringBuilder();
    private boolean debugMode = false;

    public TACInstructionCollector() {
    }

    public TACInstructionCollector(boolean debugMode) {
        this.debugMode = debugMode;
    }

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }

    public void addInstruction(TACInstruction instruction) {
        if (instruction != null) {
            instructions.add(instruction);
            if (debugMode) {
                output.append("Added instruction: ").append(instruction).append("\n");
            }
        } else {
            if (debugMode) {
                output.append("Warning: Attempted to add a null instruction.\n");
            }
        }
    }

    public void addInstructions(List<TACInstruction> instructionList) {
        if (instructionList != null) {
            for (TACInstruction instruction : instructionList) {
                addInstruction(instruction);
            }
        }
    }

    public List<TACInstruction> getInstructions() {
        return new ArrayList<>(instructions); 
    }

    public void clearInstructions() {
        instructions.clear();
        output.setLength(0);
    }

    public int size() {
        return instructions.size();
    }

    public boolean isEmpty() {
        return instructions.isEmpty();
    }

    public TACInstruction getInstruction(int index) {
        if (index >= 0 && index < instructions.size()) {
            return instructions.get(index);
        }
        return null;
    }

    public String getDebugOutput() {
        return output.toString();
    }

    public String printInstructions() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < instructions.size(); i++) {
            result.append(String.format("%3d: %s\n", i, instructions.get(i)));
        }
        return result.toString();
    }

    public String printInstructionsWithLineNumbers() {
        return printInstructions();
    }

    @Override
    public String toString() {
        return "TACInstructionCollector{" +
                "instructions=" + instructions.size() +
                ", debugMode=" + debugMode +
                '}';
    }
}