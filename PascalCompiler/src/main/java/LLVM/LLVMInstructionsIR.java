package LLVM;



import java.util.ArrayList;
import java.util.List;

/**
 * Class representing LLVM instructions in intermediate representation (IR) form.
 * This class manages the collection of LLVM instructions, providing methods to add,
 * retrieve, and clear instructions.
 */
public class LLVMInstructionsIR {
    private List<String> instructions;

    /**
     * Constructor to initialize the LLVMInstructionsIR.
     */
    public LLVMInstructionsIR() {
        instructions = new ArrayList<>();
    }

    /**
     * Adds an LLVM instruction to the IR.
     *
     * @param instruction The LLVM instruction to add.
     */
    public void addInstruction(String instruction) {
        instructions.add(instruction);
    }

    /**
     * Gets the complete list of LLVM instructions.
     *
     * @return A list of LLVM instructions.
     */
    public List<String> getInstructions() {
        return new ArrayList<>(instructions);
    }

    /**
     * Clears all the LLVM instructions from the IR.
     */
    public void clearInstructions() {
        instructions.clear();
    }

    /**
     * Gets the number of LLVM instructions in the IR.
     *
     * @return The number of LLVM instructions.
     */
    public int getInstructionCount() {
        return instructions.size();
    }

    /**
     * Gets the LLVM instruction at a specific index.
     *
     * @param index The index of the LLVM instruction to retrieve.
     * @return The LLVM instruction at the specified index.
     */
    public String getInstruction(int index) {
        if (index < 0 || index >= instructions.size()) {
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);
        }
        return instructions.get(index);
    }
}
