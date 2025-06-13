package Errors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class SemanticError {
    public enum ErrorType {
        UNDECLARED_VARIABLE("Variable no declarada"),
        REDECLARED_VARIABLE("Variable ya declarada"),
        TYPE_MISMATCH("Tipos incompatibles"),
        INVALID_OPERATION("Operación inválida"),
        INVALID_ASSIGNMENT("Asignación inválida"),
        UNDEFINED_FUNCTION("Función no definida"),
        INVALID_FUNCTION_CALL("Llamada a función inválida"),
        WRONG_PARAMETER_COUNT("Número incorrecto de parámetros"),
        RETURN_TYPE_MISMATCH("Tipo de retorno incorrecto"),
        UNREACHABLE_CODE("Código inalcanzable"),
        MISSING_RETURN("Falta declaración de retorno"),
        INVALID_ARRAY_ACCESS("Acceso inválido a arreglo"),
        DIVISION_BY_ZERO("División por cero"),
        SCOPE_ERROR("Error de ámbito"),
        CUSTOM("Error personalizado");

        private final String description;

        ErrorType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    private static final List<SemanticError> errors = Collections.synchronizedList(new ArrayList<>());

    private final ErrorType type;
    private final String customType;
    private final int line;
    private final int column;
    private final String message;
    private final String context;

    public SemanticError(ErrorType type, String message) {
        this(type, -1, -1, message, null);
    }

    public SemanticError(ErrorType type, int line, int column, String message) {
        this(type, line, column, message, null);
    }

    public SemanticError(ErrorType type, int line, int column, String message, String context) {
        this.type = Objects.requireNonNull(type, "Error type cannot be null");
        this.customType = null;
        this.line = line;
        this.column = column;
        this.message = Objects.requireNonNull(message, "Error message cannot be null");
        this.context = context;
    }

    public SemanticError(String customType, int line, int column, String message) {
        this.type = ErrorType.CUSTOM;
        this.customType = Objects.requireNonNull(customType, "Custom error type cannot be null");
        this.line = line;
        this.column = column;
        this.message = Objects.requireNonNull(message, "Error message cannot be null");
        this.context = null;
    }

    public SemanticError(String customType, int line, int column, String message, String context) {
        this.type = ErrorType.CUSTOM;
        this.customType = Objects.requireNonNull(customType, "Custom error type cannot be null");
        this.line = line;
        this.column = column;
        this.message = Objects.requireNonNull(message, "Error message cannot be null");
        this.context = context;
    }

    public ErrorType getType() {
        return type;
    }

    public String getTypeString() {
        return type == ErrorType.CUSTOM ? customType : type.name();
    }

    public String getTypeDescription() {
        return type == ErrorType.CUSTOM ? customType : type.getDescription();
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public String getMessage() {
        return message;
    }

    public String getContext() {
        return context;
    }

    public boolean hasLocation() {
        return line > 0 && column >= 0;
    }

    public boolean hasContext() {
        return context != null && !context.trim().isEmpty();
    }

    public String getFormattedMessage() {
        StringBuilder sb = new StringBuilder();

        if (hasLocation()) {
            sb.append(String.format("Error semántico en línea %d, posición %d: ", line, column + 1));
        } else {
            sb.append("Error semántico: ");
        }

        sb.append(getTypeDescription());

        if (message != null && !message.trim().isEmpty()) {
            sb.append(" - ").append(message);
        }

        if (hasContext()) {
            sb.append("\n    Contexto: ").append(context);
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        return getFormattedMessage();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        SemanticError that = (SemanticError) obj;
        return line == that.line &&
                column == that.column &&
                type == that.type &&
                Objects.equals(customType, that.customType) &&
                Objects.equals(message, that.message) &&
                Objects.equals(context, that.context);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, customType, line, column, message, context);
    }

    // Static methods for error management
    public static void addError(SemanticError error) {
        errors.add(Objects.requireNonNull(error, "Error cannot be null"));
    }

    public static void addError(ErrorType type, String message) {
        addError(new SemanticError(type, message));
    }

    public static void addError(ErrorType type, int line, int column, String message) {
        addError(new SemanticError(type, line, column, message));
    }

    public static void addError(ErrorType type, int line, int column, String message, String context) {
        addError(new SemanticError(type, line, column, message, context));
    }

    public static void addError(String customType, int line, int column, String message) {
        addError(new SemanticError(customType, line, column, message));
    }

    //Get all semantic errors
    public static List<SemanticError> getErrors() {
        return new ArrayList<>(errors);
    }


    //Get errors filtered by type
    public static List<SemanticError> getErrorsByType(ErrorType type) {
        return errors.stream()
                .filter(error -> error.getType() == type)
                .toList();
    }

    public static List<SemanticError> getErrorsByCustomType(String customType) {
        return errors.stream()
                .filter(error -> error.getType() == ErrorType.CUSTOM &&
                        Objects.equals(error.customType, customType))
                .toList();
    }

    public static boolean hasErrors() {
        return !errors.isEmpty();
    }

    public static int getErrorCount() {
        return errors.size();
    }

    public static void clearErrors() {
        errors.clear();
    }

    public static String getFormattedErrors() {
        if (errors.isEmpty()) {
            return "No se encontraron errores semánticos.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Se encontraron %d error(es) semántico(s):%n", errors.size()));

        for (int i = 0; i < errors.size(); i++) {
            sb.append(String.format("%d. %s%n", i + 1, errors.get(i).getFormattedMessage()));
            if (i < errors.size() - 1) {
                sb.append("\n");
            }
        }

        return sb.toString();
    }

    public static String getErrorsByLine() {
        if (errors.isEmpty()) {
            return "No se encontraron errores semánticos.";
        }

        return errors.stream()
                .filter(SemanticError::hasLocation)
                .sorted((e1, e2) -> {
                    int lineCompare = Integer.compare(e1.line, e2.line);
                    return lineCompare != 0 ? lineCompare : Integer.compare(e1.column, e2.column);
                })
                .map(SemanticError::getFormattedMessage)
                .reduce((e1, e2) -> e1 + "\n" + e2)
                .orElse("No hay errores con información de ubicación.");
    }
}