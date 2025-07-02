package ErrorClasses;

import java.util.ArrayList;
import java.util.List;

public class SemanticError {

    private String type;
    private int line;
    private int column;
    private String message;
    private String symbol;
    private String sourceCode;

    private static List<SemanticError> allErrors = new ArrayList<>();

    public SemanticError() {
    }

    public SemanticError(String type, String message) {
        this.type = type;
        this.message = message;
        allErrors.add(this);
    }

    public SemanticError(String type, int line, int column, String message) {
        this.type = type;
        this.line = line;
        this.column = column;
        this.message = message;
        allErrors.add(this);
    }

    public SemanticError(String type, int line, int column, String message, String symbol) {
        this.type = type;
        this.line = line;
        this.column = column;
        this.message = message;
        this.symbol = symbol;
        allErrors.add(this);
    }

    public SemanticError(String type, int line, int column, String message, String symbol, String sourceCode) {
        this.type = type;
        this.line = line;
        this.column = column;
        this.message = message;
        this.symbol = symbol;
        this.sourceCode = sourceCode;
        allErrors.add(this);
    }

    // Getters y setters
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public int getLine() { return line; }
    public void setLine(int line) { this.line = line; }
    public int getColumn() { return column; }
    public void setColumn(int column) { this.column = column; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }
    public String getSourceCode() { return sourceCode; }
    public void setSourceCode(String sourceCode) { this.sourceCode = sourceCode; }

    private String translateErrorMessage() {
        String baseMessage = message;
        if (baseMessage == null || baseMessage.trim().isEmpty()) {
            baseMessage = getDefaultMessageForType();
        }

        if (baseMessage != null) {
            if (baseMessage.contains("Variable already declared")) {
                baseMessage = baseMessage.replaceAll("Variable already declared: (\\w+) at scope level (\\d+)",
                        "Identificador duplicado '$1'");
            }

            baseMessage = baseMessage.replace("Assignment to undefined symbol", "Variable no declarada");
            baseMessage = baseMessage.replace("Undefined variable", "Variable no declarada");
            baseMessage = baseMessage.replace("Undefined symbol", "Variable no declarada");
            baseMessage = baseMessage.replace("Type mismatch", "Tipos incompatibles");
            baseMessage = baseMessage.replace("Redeclared variable", "Variable ya declarada");
            baseMessage = baseMessage.replace("Variable already declared", "Identificador duplicado");
            baseMessage = baseMessage.replace("Division by zero", "División por cero");
            baseMessage = baseMessage.replace("Invalid operation", "Operación inválida");
            baseMessage = baseMessage.replace("Array index out of bounds", "Índice fuera de rango");
            baseMessage = baseMessage.replace("Procedure not found", "Procedimiento no encontrado");
            baseMessage = baseMessage.replace("Function not found", "Función no encontrada");
            baseMessage = baseMessage.replace("Parameter mismatch", "Parámetros incorrectos");
            baseMessage = baseMessage.replace("Return type mismatch", "Tipo de retorno incorrecto");

            // Limpiar información de scope que no es necesaria para el usuario
            baseMessage = baseMessage.replaceAll(" at scope level \\d+", "");
        }

        return baseMessage != null ? baseMessage : "Error semántico";
    }

    private String getDefaultMessageForType() {
        if (type == null) return "Error semántico";

        return switch (type.toUpperCase()) {
            case "UNDEFINED_VARIABLE", "UNDEFINED_SYMBOL" -> "Variable no declarada";
            case "TYPE_MISMATCH" -> "Tipos incompatibles";
            case "REDECLARED_VARIABLE", "VARIABLE_ALREADY_DECLARED" -> "Identificador duplicado";
            case "DIVISION_BY_ZERO" -> "División por cero";
            case "INVALID_OPERATION" -> "Operación inválida";
            case "ARRAY_INDEX_OUT_OF_BOUNDS" -> "Índice fuera de rango";
            case "PROCEDURE_NOT_FOUND" -> "Procedimiento no encontrado";
            case "FUNCTION_NOT_FOUND" -> "Función no encontrada";
            case "PARAMETER_MISMATCH" -> "Parámetros incorrectos";
            case "RETURN_TYPE_MISMATCH" -> "Tipo de retorno incorrecto";
            default -> "Error semántico";
        };
    }

    private String formatPascalError() {
        StringBuilder sb = new StringBuilder();

        // Formato: programa(línea,columna) Error: descripción
        String filename = "programa"; // Se puede parametrizar si es necesario

        if (line > 0 && column > 0) {
            sb.append(filename).append("(").append(line).append(",").append(column).append(") ");
        }

        sb.append("Error: ");

        // Usar el mensaje traducido
        String translatedMessage = translateErrorMessage();
        sb.append(translatedMessage);

        // Agregar el símbolo específico si está disponible
        if (symbol != null && !symbol.trim().isEmpty()) {
            sb.append(" '").append(symbol).append("'");
        }

        return sb.toString();
    }

    private String generateErrorPointer() {
        if (sourceCode == null || sourceCode.trim().isEmpty() ||
                line <= 0 || column <= 0) {
            return "";
        }

        String[] lines = sourceCode.split("\n");
        if (line > lines.length) {
            return "";
        }

        String errorLine = lines[line - 1];
        StringBuilder pointer = new StringBuilder();

        // Crear espacios hasta la columna del error
        for (int i = 0; i < column - 1 && i < errorLine.length(); i++) {
            if (errorLine.charAt(i) == '\t') {
                pointer.append('\t');
            } else {
                pointer.append(' ');
            }
        }

        // Agregar el indicador
        if (symbol != null && !symbol.trim().isEmpty()) {
            // Subrayar toda la longitud del símbolo
            for (int i = 0; i < symbol.length(); i++) {
                pointer.append('^');
            }
        } else {
            pointer.append('^');
        }

        return "    " + errorLine + "\n    " + pointer.toString();
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(formatPascalError());

        String errorPointer = generateErrorPointer();
        if (!errorPointer.isEmpty()) {
            result.append("\n").append(errorPointer);
        }

        return result.toString();
    }

    public static String getAllErrorsFormatted() {
        if (allErrors.isEmpty()) {
            return "";
        }

        StringBuilder result = new StringBuilder();

        for (SemanticError error : allErrors) {
            result.append(error.toString()).append("\n\n");
        }

        // Resumen final estilo Pascal
        int errorCount = allErrors.size();
        result.append(errorCount)
                .append(errorCount == 1 ? " error semántico encontrado." : " errores semánticos encontrados.");

        return result.toString();
    }

    public static void addUndefinedVariableError(int line, int column, String variableName, String sourceCode) {
        new SemanticError("UNDEFINED_VARIABLE", line, column,
                "La variable debe ser declarada antes de su uso",
                variableName, sourceCode);
    }

    public static void addTypeMismatchError(int line, int column, String expectedType, String actualType, String sourceCode) {
        String message = String.format("Se esperaba %s, se encontró %s", expectedType, actualType);
        new SemanticError("TYPE_MISMATCH", line, column, message, null, sourceCode);
    }

    public static void addRedeclaredVariableError(int line, int column, String variableName, String sourceCode) {
        new SemanticError("VARIABLE_ALREADY_DECLARED", line, column,
                "Variable already declared: " + variableName,
                variableName, sourceCode);
    }

    public static void clearAllErrors() {
        allErrors.clear();
    }

    public static boolean hasErrors() {
        return !allErrors.isEmpty();
    }

    public static int getErrorCount() {
        return allErrors.size();
    }

    public static List<SemanticError> getAllErrors() {
        return new ArrayList<>(allErrors);
    }
}