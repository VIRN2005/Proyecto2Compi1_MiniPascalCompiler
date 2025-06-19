package LLVM;

import IntermediateRepresentation.TACInstruction;

import java.util.*;
import java.util.regex.Pattern;

public class LLVMTranslator {
    private int tempVarCounter = 0;
    private List<String> llvmInstructions;
    private List<String> stringDeclarations = new ArrayList<>();
    private Set<String> allocatedVariables = new HashSet<>();
    private Map<String, String> lastAssignedValue = new HashMap<>();
    private Set<String> tempVariables = new HashSet<>();

    public List<String> translateAll(List<TACInstruction> instructions) {
        inferTypes(instructions);
        llvmInstructions = new ArrayList<>();

        // LLVM IR header
        addLLVMHeader();

        // Translate string declarations first
        for (TACInstruction inst : instructions) {
            if (inst.getOperation().equals("ASSIGN") && inst.getOp1().startsWith("\"")) {
                llvmInstructions.add(translateString(inst));
            }
        }

        // Global declarations
        llvmInstructions.add("%struct._IO_FILE = type { i8*, i32, i32, i32, i8*, i8*, i8*, i8*, i8*, i32, i32, i32, i32, i8*, i8*, i8*, i32, i32, i32 }");
        llvmInstructions.add("@str_fmt = unnamed_addr constant [4 x i8] c\"%d\\0A\\00\"");
        llvmInstructions.add("@stdin = external global %struct._IO_FILE*");
        llvmInstructions.add("@double_fmt = private unnamed_addr constant [4 x i8] c\"%f\\0A\\00\"");

        // Check if there's an array allocation instruction
        boolean hasArray = instructions.stream()
                .anyMatch(inst -> inst.getOperation().equals("ALLOCATE_ARRAY"));

        // Add the array constant declaration only if there's an array
        if (hasArray) {
            llvmInstructions.add("@.str = private unnamed_addr constant [2 x i8] c\" \\00\"");
        }

        // Main function definition
        llvmInstructions.add("\ndefine i32 @main() {");

        // Translate TAC instructions
        for (TACInstruction inst : instructions) {
            if(inst.getOperation().equals("FUNCTION")){
                break;
            }
            if (!inst.getOperation().equals("STRING")) {
                String translation = translate(inst);
                if (!translation.isEmpty()) {
                    llvmInstructions.add(translation);
                }
            }
        }

        // Return from main
        llvmInstructions.add("ret i32 0");
        llvmInstructions.add("}");

        //Start declaring functions
        int functionIndex = 0;
        for(int i = 0; i < instructions.size(); i++){
            if(instructions.get(i).getOperation().equals("FUNCTION")){
                functionIndex = 1;
            }
            if(functionIndex == 1){
                String translation = translate(instructions.get(i));
                if(!translation.isEmpty()){
                    llvmInstructions.add(translation);
                }
            }
        }

        // Function declarations
        llvmInstructions.addAll(generateFunctionDeclarations());

        return llvmInstructions;
    }

    private void addLLVMHeader() {
        llvmInstructions.add("; ModuleID = 'MiniPascal'");
        llvmInstructions.add("source_filename = \"output.ll\"");
        llvmInstructions.add("target datalayout = \"e-m:e-p270:32:32-p271:32:32-p272:64:64-i64:64-f80:128-n8:16:32:64-S128\"");
        llvmInstructions.add("target triple = \"x86_64-pc-linux-gnu\"\n");
    }

    private String translateLabel(TACInstruction instruction) {
        String label = instruction.getResult();
        return label + ":";
    }

    private String translateString(TACInstruction instruction) {
        String resultVar = instruction.getResult();
        String stringLiteral = instruction.getOp1().substring(1, instruction.getOp1().length() - 1); // Remove quotes
        int stringLength = stringLiteral.length() + 1; // Include the null terminator

        // Generate a unique temporary name for the string constant
        String constantName = "@.str." + generateTempVariable();

        // Declare the string literal
        String declaration = constantName + " = private unnamed_addr constant [" + stringLength + " x i8] c\"" + stringLiteral + "\\00\"";
        llvmInstructions.add(declaration);

        lastAssignedValue.put(resultVar, constantName); // Store the constant name
        return ""; // Return the declaration so it can be added to llvmInstructions
    }

    private String translateAssignArray(TACInstruction instruction) {
        String arrayName = instruction.getOp1();
        String index = instruction.getOp2();
        String value = instruction.getResult();
        String elementPtr = "%" + arrayName + "_ptr_" + index;
        String valueToLoad = lastAssignedValue.getOrDefault(value, "%" + value + "_val");

        if (!valueToLoad.startsWith("%")) {
            valueToLoad = "%" + value + "_val";
            llvmInstructions.add(valueToLoad + " = load i32, i32* %" + value);
        }

        String getElemPtr = elementPtr + " = getelementptr inbounds i32, i32* %" + arrayName + ", i32 " + index;
        String storeElem = "store i32 " + valueToLoad + ", i32* " + elementPtr;

        return getElemPtr + "\n" + storeElem;
    }

    private String generateTempVariable(String prefix) {
        String tempVar = prefix + "_t" + tempVarCounter;
        tempVarCounter++;
        return tempVar;
    }

    private String translateAssign(TACInstruction instruction) {
        String result = instruction.getResult();
        String operand1 = instruction.getOp1();
        String allocation = translateAllocate(instruction);

        if (operand1.startsWith("\"") && operand1.endsWith("\"")) {
            // String assignment logic remains the same
            // ... (code omitted for brevity)
        } else if (operand1.matches("-?\\d+(\\.\\d+)?")) {
            // Check if the operand is a real number
            boolean isReal = operand1.contains(".");
            String valueType = isReal ? "double" : "i32";
            lastAssignedValue.put(result, operand1);
            return allocation + (allocation.isEmpty() ? "" : "\n") + "store " + valueType + " " + operand1 + ", " + valueType + "* %" + result;
        } else {
            String operand1Val = lastAssignedValue.getOrDefault(operand1, "%" + operand1 + "_val");
            boolean isReal = operand1Val.contains(".");
            String valueType = isReal ? "double" : "i32";
            lastAssignedValue.put(result, operand1Val);
            return allocation + (allocation.isEmpty() ? "" : "\n") + "store " + valueType + " " + operand1Val + ", " + valueType + "* %" + result;
        }
        return "";
    }

    public List<String> generateFunctionDeclarations() {
        List<String> declarations = new ArrayList<>();

        // write_double for printing doubles
        declarations.add("define void @write_double(double %num) {");
        declarations.add("  %buf = alloca [32 x i8], align 1");
        declarations.add("  %buf_ptr = getelementptr inbounds [32 x i8], [32 x i8]* %buf, i32 0, i32 0");
        declarations.add("  call i32 (i8*, i8*, ...) @sprintf(i8* %buf_ptr, i8* getelementptr inbounds ([4 x i8], [4 x i8]* @double_fmt, i32 0, i32 0), double %num)");
        declarations.add("  call i32 @puts(i8* %buf_ptr)");
        declarations.add("  ret void");
        declarations.add("}");

        // write_int function for printing integers
        declarations.add("define void @write_int(i32 %num) {");
        declarations.add("  %buf = alloca [32 x i8], align 1");
        declarations.add("  %buf_ptr = getelementptr inbounds [32 x i8], [32 x i8]* %buf, i32 0, i32 0");
        declarations.add("  call i32 (i8*, i8*, ...) @sprintf(i8* %buf_ptr, i8* getelementptr inbounds ([4 x i8], [4 x i8]* @str_fmt, i32 0, i32 0), i32 %num)");
        declarations.add("  call i32 @puts(i8* %buf_ptr)");
        declarations.add("  ret void");
        declarations.add("}");

        // write_string function for printing strings
        declarations.add("define void @write_string(i8* %str) {");
        declarations.add("  %str_ptr = alloca i8*");
        declarations.add("  store i8* %str, i8** %str_ptr");
        declarations.add("  %str_val = load i8*, i8** %str_ptr");
        declarations.add("  call i32 @puts(i8* %str_val)");
        declarations.add("  ret void");
        declarations.add("}");

        // read function for reading input
        declarations.add("define i32 @read() {");
        declarations.add("  %buf = alloca [32 x i8], align 1");
        declarations.add("  %buf_ptr = getelementptr inbounds [32 x i8], [32 x i8]* %buf, i32 0, i32 0");
        declarations.add("  %stdin_val = load %struct._IO_FILE*, %struct._IO_FILE** @stdin");
        declarations.add("  %result = call i8* @fgets(i8* %buf_ptr, i32 32, %struct._IO_FILE* %stdin_val)");
        declarations.add("  %num = call i32 @atoi(i8* %buf_ptr)");
        declarations.add("  ret i32 %num");
        declarations.add("}");

        // Function declarations for standard library functions
        declarations.add("declare i32 @atoi(i8*)");
        declarations.add("declare i32 @sprintf(i8*, i8*, ...)");
        declarations.add("declare i32 @puts(i8*)");
        declarations.add("declare i8* @fgets(i8*, i32, %struct._IO_FILE*)");
        declarations.add("declare void @exit(i32)");
        return declarations;
    }
    private String getLastLoadedVariable() {
        String lastLoadedVariable = null;
        for (String instruction : llvmInstructions) {
            if (instruction.startsWith("%") && instruction.contains("= load i32, i32*")) {
                String[] parts = instruction.split(" ");
                lastLoadedVariable = parts[parts.length - 1];
            }
        }
        return lastLoadedVariable;
    }
    private String translateRead(TACInstruction instruction) {
        // Generate a unique temporary name for the input variable
        String inputVar = generateTempVariable("input");

        // Use the unique temporary name to store the result of the read function
        llvmInstructions.add("%" + inputVar + " = call i32 @read()");

        // Find the last loaded variable
        String lastLoadedVariable = getLastLoadedVariable();

        // If a last loaded variable is found, store the result in that variable
        if (lastLoadedVariable != null) {
            llvmInstructions.add("store i32 %" + inputVar + ", i32* " + lastLoadedVariable);
            lastAssignedValue.put(lastLoadedVariable.substring(1), lastLoadedVariable);
        }

        // Create a new temporary variable to load the value
        String tempVar = generateTempVariable("temp");
        llvmInstructions.add("%" + tempVar + " = load i32, i32* " + lastLoadedVariable);

        // Update lastAssignedValue to point to the new temporary variable
        lastAssignedValue.put(tempVar, "%" + tempVar);

        return "";
    }






    private String translateLoad(TACInstruction instruction) {
        String result = instruction.getResult();
        String operand = instruction.getOp1();
        String operandValue = lastAssignedValue.get(operand);

        if (operandValue != null && Pattern.matches("@.str.*", operandValue)) {
            return "%" + result + "_val = bitcast i8* " + operandValue + " to i32*";
        } else if (tempVariables.contains(operand) && operandValue != null) {
            return "%" + result + "_val = load i32, i32* " + operandValue;
        } else if (lastAssignedValue.containsKey(operand)) {
            return "%" + result + "_val = load i32, i32* %" + operand;
        } else {
            return "%" + result + "_val = load i32, i32* %" + operand;
        }
    }

    private String translateWrite(TACInstruction instruction) {
        String operand1 = instruction.getOp1();
        String operandValue = lastAssignedValue.get(operand1);

        if (operandValue != null && operandValue.startsWith("@.str")) {
            // If the operand is a string literal
            return "call void @write_string(i8* " + operandValue + ")";
        } else {
            // If the operand is an integer variable or temporary variable
            return "call void @write_int(i32 %" + operand1 + "_val)"; // Normal integer write
        }
    }

    private Map<String, String> variableTypes = new HashMap<>();

    private void inferTypes(List<TACInstruction> instructions) {
        for (TACInstruction inst : instructions) {
            String result = inst.getResult();
            String op1 = inst.getOp1();
            String op2 = inst.getOp2();

            // Check operands for clues about data types
            if (op1 != null && op1.matches("-?\\d*\\.\\d+")) {
                variableTypes.put(result, "double");
            } else if (op2 != null && op2.matches("-?\\d*\\.\\d+")) {
                variableTypes.put(result, "double");
            } else {
                variableTypes.putIfAbsent(result, "i32");  // Default to integer
            }
        }
    }

    private String translateAllocate(TACInstruction instruction) {
        String variable = instruction.getResult();
        if (!allocatedVariables.contains(variable)) {
            allocatedVariables.add(variable);
            tempVariables.add(variable);  // Assume temporary for simplicity

            // Determine type based on previously inferred types, default to i32 if uncertain
            String type = variableTypes.getOrDefault(variable, "i32");
            return "%" + variable + " = alloca " + type;
        }
        return ""; // If already allocated, return empty string
    }

    private String generateTempVariable() {
        UUID uuid = UUID.randomUUID();
        return "_t" + uuid.toString().replace("-", "_"); // Replace hyphens for LLVM compatibility
    }

    private String translatePush(TACInstruction instruction) {
        String operand = instruction.getOp1();

        if (operand == null) {
            // Skip the instruction if the operand is null
            return "";
        }

        if (operand.startsWith("\"") && operand.endsWith("\"")) {  // Assuming string literals are quoted
            String stringLiteral = operand.substring(1, operand.length() - 1); // Remove quotes
            String constantName = llvmInstructions.stream()
                    .filter(declaration -> declaration.contains(stringLiteral))
                    .map(declaration -> declaration.split(" ")[0])
                    .findFirst()
                    .orElse(null);
            int stringLength = stringLiteral.length() + 1; // Include the null terminator
            return "call void @write_string(i8* getelementptr inbounds ([" + stringLength + " x i8], [" + stringLength + " x i8]* " + constantName + ", i32 0, i32 0))\n";
        }

        // Find the last loaded variable for the specific operand
        String lastLoadedVariable = getLastLoadedVariable(operand);

        if (lastLoadedVariable != null) {
            // If a last loaded variable is found, use it as the operand value
            String operandValue = lastLoadedVariable;

            if (isVariableDouble(operand)) {
                return "call void @write_double(double " + operandValue + ")\n";
            } else {
                return "call void @write_int(i32 " + operandValue + ")\n";
            }
        } else {
            // If no last loaded variable is found, fallback to the original logic
            String operandValue = lastAssignedValue.getOrDefault(operand, "%" + operand); // Use %operand

            if (!operandValue.startsWith("%")) { // If it's not already a register
                boolean isDouble = isVariableDouble(operand);
                String type = isDouble ? "double" : "i32";

                tempVarCounter++;
                String tempVal = "%" + generateTempVariable();
                llvmInstructions.add(tempVal + " = load " + type + ", " + type + "* %" + operand);
                operandValue = tempVal;
            }

            if (isVariableDouble(operand)) {
                return "call void @write_double(double " + operandValue + ")\n";
            } else {
                return "call void @write_int(i32 " + operandValue + ")\n";
            }
        }
    }

    private String getLastLoadedVariable(String operand) {
        String lastLoadedVariable = null;
        for (String instruction : llvmInstructions) {
            if (instruction.startsWith("%") && instruction.contains("= load") && instruction.contains("%" + operand)) {
                String[] parts = instruction.split(" ");
                lastLoadedVariable = parts[0];
            }
        }
        return lastLoadedVariable;
    }

    private boolean isVariableDouble(String variable) {
        String value = lastAssignedValue.get(variable);
        return value != null && value.matches("-?\\d*\\.\\d+");
    }

    private String translateBrFalse(TACInstruction instruction) {
        String condition = instruction.getOp1();
        String label = instruction.getResult();
        String conditionValue = lastAssignedValue.getOrDefault(condition, "%" + condition + "_val");

        return "br i1 %" + label + ", label %L" + label + ", label %" + condition + "\nL" + label + ":";
    }

    private String translateGoto(TACInstruction instruction) {
        String label = instruction.getResult();
        return "br label %" + label;
    }

    private String translateReturn(TACInstruction instruction) {
        String result = instruction.getResult();
        return "ret i32 %" + result + "}";
    }

    private String translateArithmetic(TACInstruction instruction) {
        String result = instruction.getResult();
        String operand1 = instruction.getOp1();
        String operand2 = instruction.getOp2();
        String op;

        switch (instruction.getOperation()) {
            case "+":
                op = "add";
                break;
            case "-":
                op = "sub";
                break;
            case "*":
                op = "mul";
                break;
            case "/":
                op = "div";
                break;
            default:
                throw new IllegalArgumentException("Unsupported arithmetic operation: " + instruction.getOperation());
        }

        String operand1Val;
        String operand2Val;
        String resultType;

        boolean isReal1 = operand1.matches("-?\\d*\\.\\d+") || variableTypes.getOrDefault(operand1, "").equals("double");
        boolean isReal2 = operand2.matches("-?\\d*\\.\\d+") || variableTypes.getOrDefault(operand2, "").equals("double");

        if (isReal1 || isReal2) {
            resultType = "double";
            operand1Val = getOpValue(operand1, "double");
            operand2Val = getOpValue(operand2, "double");
        } else {
            resultType = "i32";
            operand1Val = getOpValue(operand1, "i32");
            operand2Val = getOpValue(operand2, "i32");
        }

        String llvmInstruction;
        if (op.equals("div")) {
            if (resultType.equals("i32")) {
                llvmInstruction = "%" + result + "_val = sdiv i32 " + operand1Val + ", " + operand2Val;
            } else {
                llvmInstruction = "%" + result + "_val = fdiv double " + operand1Val + ", " + operand2Val;
            }
        } else {
            if (resultType.equals("double")) {
                llvmInstruction = "%" + result + "_val = f" + op + " double " + operand1Val + ", " + operand2Val;
            } else {
                if(result.contains("i") && operand2Val.contains("i")) {
                    return "";
                }
                llvmInstruction = "%" + result + "_val = " + op + " i32 " + operand1Val + ", " + operand2Val;
            }
        }

        llvmInstructions.add(llvmInstruction);
        lastAssignedValue.put(result, "%" + result + "_val");
        variableTypes.put(result, resultType); // Store the result type in the variableTypes map

        return "";
    }

    private String getOpValue(String operand, String type) {
        if (operand.matches("-?\\d+(\\.\\d+)?")) {
            if (type.equals("double")) {
                return operand;
            } else {
                return operand;
            }
        } else {
            String operandVal = lastAssignedValue.getOrDefault(operand, "%" + operand + "_val");
            if (!operandVal.startsWith("%")) {
                operandVal = "%" + operand + "_val";
                llvmInstructions.add(operandVal + " = load " + type + ", " + type + "* %" + operand);
            }
            return operandVal;
        }
    }


    private String translateIndex(TACInstruction instruction) {
        String arrayName = instruction.getOp1();
        String index = instruction.getOp2();
        String elementPtr = "%" + generateTempVariable("index_ptr");
        String loadVal = "%" + generateTempVariable("index_val");

        boolean isRealArray = lastAssignedValue.getOrDefault(arrayName, "").contains(".");
        String arrayType = isRealArray ? "double" : "i32";

        String getElemPtr = elementPtr + " = getelementptr inbounds [5 x " + arrayType + "], [5 x " + arrayType + "]* %" + arrayName + ", i32 0, i32 " + index;
        String loadElem = loadVal + " = load " + arrayType + ", " + arrayType + "* " + elementPtr;
        lastAssignedValue.put(instruction.getResult(), loadVal);

        return getElemPtr + "\n" + loadElem;
    }

    private String translateArrayAssign(TACInstruction instruction) {
        String arrayName = instruction.getOp1();
        String index = instruction.getOp2();
        String value = instruction.getResult();
        String elementPtr = "%" + arrayName + "_ptr_" + index;
        String valueToLoad = lastAssignedValue.getOrDefault(value, "%" + value + "_val");

        boolean isRealArray = lastAssignedValue.getOrDefault(arrayName, "").contains(".");
        String arrayType = isRealArray ? "double" : "i32";

        if (!valueToLoad.startsWith("%")) {
            valueToLoad = "%" + value + "_val";
            llvmInstructions.add(valueToLoad + " = load " + arrayType + ", " + arrayType + "* %" + value);
        }

        String getElemPtr = elementPtr + " = getelementptr inbounds " + arrayType + ", " + arrayType + "* %" + arrayName + ", i32 " + index;
        String storeElem = "store " + arrayType + " " + valueToLoad + ", " + arrayType + "* " + elementPtr;

        return getElemPtr + "\n" + storeElem;
    }

    private String translateAllocateArray(TACInstruction instruction) {
        String arrayName = instruction.getResult();
        String arraySize = instruction.getOp2();

        if (!arraySize.matches("\\d+")) {
            arraySize = lastAssignedValue.getOrDefault(arraySize, "%" + arraySize + "_val");
        }

        boolean isRealArray = instruction.getOp1().contains(".");
        String arrayType = isRealArray ? "double" : "i32";

        llvmInstructions.add("%" + arrayName + " = alloca [" + arraySize + " x " + arrayType + "]");

        String zero = "%" + generateTempVariable("zero");
        tempVarCounter++;
        llvmInstructions.add(zero + " = " + (isRealArray ? "fadd double 0.0, 0.0" : "add i32 0, 0"));
        for (int i = 0; i < Integer.parseInt(arraySize); i++) {
            String elementPtr = "%" + generateTempVariable("elem_ptr");
            llvmInstructions.add(elementPtr + " = getelementptr inbounds [" + arraySize + " x " + arrayType + "], [" + arraySize + " x " + arrayType + "]* %" + arrayName + ", i32 0, i32 " + i);
            llvmInstructions.add("store " + arrayType + " " + zero + ", " + arrayType + "* " + elementPtr);
        }

        return "";
    }

    private String getOpValue(String operand) {
        return lastAssignedValue.getOrDefault(operand, "%" + operand + "_val");
    }

    private String translateTypeDef(TACInstruction instruction) {
        String typeName = instruction.getResult();
        String typeDefinition = instruction.getOp1();
        return "%struct." + typeName + " = type " + typeDefinition;
    }

    private String translateFunctionDeclaration(TACInstruction instruction) {
        String functionName = instruction.getResult();
        return "define void @" + functionName + "() {";
    }

    private String translateParameter(TACInstruction instruction) {
        String paramName = instruction.getResult();
        return "%" + paramName + " = alloca i32";
    }

    private String translatePhi(TACInstruction instruction) {
        String result = instruction.getResult();
        String trueLabel = instruction.getOp1();
        String falseLabel = instruction.getOp2();
        return "%" + result + " = phi i32 [%" + result + "T, %" + trueLabel + "], [%" + result + "F, %" + falseLabel + "]";
    }

    private String translateRelational(TACInstruction instruction) {
        String result = instruction.getResult();
        String operand1 = instruction.getOp1();
        String operand2 = instruction.getOp2();

        String op = "";
        switch (instruction.getOperation()) {
            case "LT":
            case "<":
                op = "icmp slt";
                break;
            case ">":
                op = "icmp sgt";
                break;
            case "==":
                op = "icmp eq";
                break;
            case "!=":
                op = "icmp ne";
                break;
            case "LE":
                op = "icmp sle";
                break;
            case "<=":
                op = "icmp sle";
                break;
            case ">=":
                op = "icmp sge";
                break;
            default:
                throw new IllegalArgumentException("Unsupported relational operation: " + instruction.getOperation());
        }

        String operand1Val;
        if (operand1.matches("-?\\d+")) {
            tempVarCounter++;
            operand1Val = "%" + generateTempVariable();
            llvmInstructions.add(operand1Val + " = add i32 " + operand1 + ", 0");
        } else {
            operand1Val = lastAssignedValue.getOrDefault(operand1, "%" + operand1 + "_val");
            if (!operand1Val.startsWith("%")) {
                operand1Val = "%" + operand1 + "_val";
                llvmInstructions.add(operand1Val + " = load i32, i32* %" + operand1);
            }
        }

        String operand2Val;
        if (operand2.matches("-?\\d+")) {
            tempVarCounter++;
            operand2Val = "%" + generateTempVariable();
            llvmInstructions.add(operand2Val + " = add i32 " + operand2 + ", 0");
        } else {
            operand2Val = lastAssignedValue.getOrDefault(operand2, "%" + operand2 + "_val");
            if (!operand2Val.startsWith("%")) {
                operand2Val = "%" + operand2 + "_val";
                llvmInstructions.add(operand2Val + " = load i32, i32* %" + operand2);
            }
        }

        return "%" + result + " = " + op + " i32 " + operand1Val + ", " + operand2Val;
    }

    private String translateBREQ(TACInstruction instruction) {
        String condition = instruction.getOp1();
        String label = instruction.getOp2();

        String conditionValue = lastAssignedValue.getOrDefault(condition, "%" + condition);

        String negatedCondition = "%negated_" + condition + " = icmp ne i1 " + conditionValue + ", 0";
        llvmInstructions.add(negatedCondition);

        return "br i1 %negated_" + condition + ", label %" + label + ", label %_L" + label;
    }

    private String translateADD(TACInstruction instruction) {
        String result = instruction.getResult();
        String operand1 = instruction.getOp1();
        String operand2 = instruction.getOp2();

        String operand1Val = lastAssignedValue.getOrDefault(operand1, "%" + operand1 + "_val");
        String operand2Val = lastAssignedValue.getOrDefault(operand2, "%" + operand2 + "_val");

        if (!operand1Val.startsWith("%")) {
            operand1Val = "%" + operand1 + "_val";
            llvmInstructions.add(operand1Val + " = load i32, i32* %" + operand1);
        }
        if (!operand2Val.startsWith("%")) {
            operand2Val = "%" + operand2 + "_val";
            llvmInstructions.add(operand2Val + " = load i32, i32* %" + operand2);
        }
        tempVarCounter++;

        String addInstruction = "%" + result + " = add i32 " + operand1Val + ", " + operand2Val;
        llvmInstructions.add(addInstruction);
        lastAssignedValue.put(result, "%" + result);

        return "";
    }

    private String translateLE(TACInstruction instruction) {
        String result = instruction.getResult();
        String operand1 = instruction.getOp1();
        String operand2 = instruction.getOp2();

        String operand1Val = lastAssignedValue.getOrDefault(operand1, "%" + operand1 + "_val");
        String operand2Val = lastAssignedValue.getOrDefault(operand2, "%" + operand2 + "_val");

        return "%" + result + " = icmp sle i32 " + operand1Val + ", " + operand2Val;
    }

    private String translateLogical(TACInstruction instruction) {
        String result = instruction.getResult();
        String operand1 = instruction.getOp1();
        String operand2 = instruction.getOp2();

        String op;
        switch (instruction.getOperation()) {
            case "AND":
                op = "and";
                break;
            case "OR":
                op = "or";
                break;
            case "NOT":
                return "%" + result + " = xor i1 %" + operand1 + ", true";
            default:
                throw new IllegalArgumentException("Unsupported logical operation: " + instruction.getOperation());
        }

        String operand1Val = getOpValue(operand1);
        String operand2Val = getOpValue(operand2);

        return "%" + result + " = " + op + " i1 " + operand1Val + ", " + operand2Val;
    }

    private String translateBoolean(TACInstruction instruction) {
        String result = instruction.getResult();
        String value = instruction.getOp1();
        return "%" + result + " = " + (value.equals("true") ? "1" : "0");
    }

    private String translateProcedureCall(TACInstruction instruction) {
        String procedureName = instruction.getOp1();
        return "call void @" + procedureName + "()";
    }

    private String translateVariableDeclaration(TACInstruction instruction) {
        String variable = instruction.getResult();
        return "%" + variable + " = alloca i32";
    }

    private String translateReturnValue(TACInstruction instruction) {
        String value = instruction.getOp1();
        return "ret i32 %" + value;
    }

    private String translateLoadVar(TACInstruction instruction) {
        String result = instruction.getResult();
        String variable = instruction.getOp1();

        return "%" + result + " = load i32, i32* %" + variable;
    }

    private String translatePushVar(TACInstruction instruction) {
        String variable = instruction.getOp1();
        return "call void @write_int(i32 %" + variable + ")";
    }

    private String translateFunction(TACInstruction instruction) {
        String functionName = instruction.getResult();
        return "define i32 @" + functionName + "(";
    }

    private String translateParam(TACInstruction instruction) {
        String paramName = instruction.getResult();
        return "i32 %" + paramName;
    }

    private String translateAssignFunction(TACInstruction instruction) {
        String result = instruction.getResult();
        String functionName = instruction.getOperation();
        String param1 = instruction.getOp1();
        String param2 = instruction.getOp2();
        return "%" + result + " = call i32 @" + functionName + "( i32 " + param1 + ", i32 " + param2 + ")";
    }



    public String translate(TACInstruction instruction) {
        switch (instruction.getOperation()) {
            case "LABEL":
                if (!llvmInstructions.isEmpty()) {
                    String prevInstruction = llvmInstructions.get(llvmInstructions.size() - 1);
                    if (!prevInstruction.startsWith("br ")) {
                        llvmInstructions.add("br label %" + instruction.getResult());
                    }
                }
                return translateLabel(instruction);
            case "ALLOCATE":
                return translateAllocate(instruction);
            case "ASSIGN":
                return translateAssign(instruction);
            case "CALL":
                if (instruction.getOp1().equals("READ")) {
                    return translateRead(instruction);
                } else if (instruction.getOp1().equals("WRITE")) {
                    return "";
                }
                return "";
            case "LOAD":
                return translateLoad(instruction);
            case "INDEX":
                return translateIndex(instruction);
            case "PUSH":
                return translatePush(instruction);
            case "BRFALSE":
                return translateBrFalse(instruction);
            case "GOTO":
                return translateGoto(instruction);
            case "RETURN":
                return translateReturn(instruction);
            case "+":
            case "-":
            case "*":
            case "/":
                return translateArithmetic(instruction);
            case "ALLOCATE_ARRAY":
                return translateAllocateArray(instruction);
            case "ARRAY_ASSIGN":
                return translateArrayAssign(instruction);
            case "STRING":
                return translateString(instruction);
            case "TYPE_DEF":
                return translateTypeDef(instruction);
            case "READ":
                return translateRead(instruction);
            case "WRITE":
                return translateWrite(instruction);
            case "ADD":
                return translateADD(instruction);
            case "LOAD_VAR":
                return translateLoadVar(instruction);
            case "PUSH_VAR":
                return translatePushVar(instruction);
            case "FUNCTION_DECL":
                return translateFunctionDeclaration(instruction);
            case "PARAMETER":
                return translateParameter(instruction);
            case "PHI":
                return translatePhi(instruction);
            case "<":
            case ">":
            case "==":
            case "!=":
            case "<=":
            case ">=":
                return translateRelational(instruction);
            case "AND":
            case "OR":
            case "NOT":
                return translateLogical(instruction);
            case "BOOLEAN":
                return translateBoolean(instruction);
            case "PROCEDURE_CALL":
                return translateProcedureCall(instruction);
            case "VAR_DECL":
                return translateVariableDeclaration(instruction);
            case "RETURN_VALUE":
                return translateReturnValue(instruction);
            case "BREQ":
                return translateBREQ(instruction);
            case "SUB":
                return translateArithmetic(instruction);
            case "LE":
                return translateRelational(instruction);
            case "LT":
                return translateRelational(instruction);
            case "FUNCTION":
                return translateFunction(instruction);
            case "PARAM":
                return translateParam(instruction);
            case "START":
                return ") {";
            default:
                return translateAssignFunction(instruction);
        }
    }
}