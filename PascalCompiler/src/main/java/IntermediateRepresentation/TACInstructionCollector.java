package IntermediateRepresentation;

import java.util.ArrayList;
import java.util.List;

public class TACInstructionCollector {
    private List<TACInstruction> instructions = new ArrayList<>();
    private StringBuilder output;

    public TACInstructionCollector() {
        this.output = null;
    }

    public void setOutput(StringBuilder output) {
        this.output = output;
    }

    public void addInstruction(TACInstruction instruction) {
        if (instruction != null) {
            this.instructions.add(instruction);
            this.output.append("Added instruction: ").append(instruction).append("\n");
        } else {
            this.output.append("Attempted to add a null instruction.");
        }
    }

    public List<TACInstruction> getInstructions() {
        return this.instructions;
    }

    public String printInstructions() {
        return output.toString();
    }
}
