package Errors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SemanticErrorCollector {
    private final List<SemanticError> semanticErrors;

    public SemanticErrorCollector() {
        this.semanticErrors = new ArrayList<>();
    }

    public SemanticErrorCollector(int initialCapacity) {
        this.semanticErrors = new ArrayList<>(initialCapacity);
    }

    public void addError(SemanticError error) {
        if (error != null) {
            semanticErrors.add(error);
        }
    }

    public void addError(SemanticError.ErrorType type, String message) {
        addError(new SemanticError(type, message));
    }

    public void addError(SemanticError.ErrorType type, int line, int column, String message) {
        addError(new SemanticError(type, line, column, message));
    }

    public void addError(SemanticError.ErrorType type, int line, int column, String message, String context) {
        addError(new SemanticError(type, line, column, message, context));
    }

    public void addError(String customType, int line, int column, String message) {
        addError(new SemanticError(customType, line, column, message));
    }

    public void addErrors(List<SemanticError> errors) {
        if (errors != null) {
            errors.stream()
                    .filter(Objects::nonNull)
                    .forEach(this::addError);
        }
    }

    public boolean removeError(SemanticError error) {
        return semanticErrors.remove(error);
    }

    public int removeErrorsByType(SemanticError.ErrorType type) {
        int initialSize = semanticErrors.size();
        semanticErrors.removeIf(error -> error.getType() == type);
        return initialSize - semanticErrors.size();
    }

    public int getErrorCount() {
        return semanticErrors.size();
    }

    public boolean hasErrors() {
        return !semanticErrors.isEmpty();
    }

    public List<SemanticError> getErrors() {
        return Collections.unmodifiableList(semanticErrors);
    }

    public List<SemanticError> getErrorsCopy() {
        return new ArrayList<>(semanticErrors);
    }

    public List<SemanticError> getErrorsByType(SemanticError.ErrorType type) {
        return semanticErrors.stream()
                .filter(error -> error.getType() == type)
                .collect(Collectors.toList());
    }

    public List<SemanticError> getErrorsByCustomType(String customType) {
        return semanticErrors.stream()
                .filter(error -> error.getType() == SemanticError.ErrorType.CUSTOM &&
                        Objects.equals(error.getTypeString(), customType))
                .collect(Collectors.toList());
    }

    public List<SemanticError> getErrorsByLine(int line) {
        return semanticErrors.stream()
                .filter(error -> error.getLine() == line)
                .collect(Collectors.toList());
    }

    public List<SemanticError> getErrorsByLineRange(int startLine, int endLine) {
        return semanticErrors.stream()
                .filter(error -> error.hasLocation() &&
                        error.getLine() >= startLine &&
                        error.getLine() <= endLine)
                .collect(Collectors.toList());
    }

    public List<SemanticError> getErrorsSortedByLocation() {
        return semanticErrors.stream()
                .filter(SemanticError::hasLocation)
                .sorted((e1, e2) -> {
                    int lineCompare = Integer.compare(e1.getLine(), e2.getLine());
                    return lineCompare != 0 ? lineCompare : Integer.compare(e1.getColumn(), e2.getColumn());
                })
                .collect(Collectors.toList());
    }

    public List<SemanticError> getErrorsSortedByType() {
        return semanticErrors.stream()
                .sorted((e1, e2) -> e1.getTypeString().compareTo(e2.getTypeString()))
                .collect(Collectors.toList());
    }

    public long getErrorCountByType(SemanticError.ErrorType type) {
        return semanticErrors.stream()
                .filter(error -> error.getType() == type)
                .count();
    }

    public SemanticError getFirstError() {
        return semanticErrors.isEmpty() ? null : semanticErrors.get(0);
    }

    public SemanticError getLastError() {
        return semanticErrors.isEmpty() ? null : semanticErrors.get(semanticErrors.size() - 1);
    }

    public void clearErrors() {
        semanticErrors.clear();
    }

    public boolean containsError(SemanticError error) {
        return semanticErrors.contains(error);
    }

    public boolean containsErrorType(SemanticError.ErrorType type) {
        return semanticErrors.stream()
                .anyMatch(error -> error.getType() == type);
    }

    public String getErrorSummary() {
        if (semanticErrors.isEmpty()) {
            return "No se encontraron errores semánticos.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Resumen de errores semánticos (%d total):%n", semanticErrors.size()));

        semanticErrors.stream()
                .collect(Collectors.groupingBy(SemanticError::getTypeDescription, Collectors.counting()))
                .entrySet()
                .stream()
                .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()))
                .forEach(entry -> sb.append(String.format("  • %s: %d error(es)%n",
                        entry.getKey(), entry.getValue())));

        return sb.toString();
    }

    public String getFormattedErrorsByLine() {
        if (semanticErrors.isEmpty()) {
            return "No se encontraron errores semánticos.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Errores semánticos encontrados (%d):%n%n", semanticErrors.size()));

        getErrorsSortedByLocation().forEach(error ->
                sb.append("  ").append(error.getFormattedMessage()).append("\n")
        );

        // Add errors without location information
        List<SemanticError> errorsWithoutLocation = semanticErrors.stream()
                .filter(error -> !error.hasLocation())
                .collect(Collectors.toList());

        if (!errorsWithoutLocation.isEmpty()) {
            sb.append("\nErrores sin ubicación específica:\n");
            errorsWithoutLocation.forEach(error ->
                    sb.append("  ").append(error.getFormattedMessage()).append("\n")
            );
        }

        return sb.toString();
    }

    public String getFormattedErrorsByType() {
        if (semanticErrors.isEmpty()) {
            return "No se encontraron errores semánticos.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Errores semánticos por tipo (%d total):%n%n", semanticErrors.size()));

        semanticErrors.stream()
                .collect(Collectors.groupingBy(SemanticError::getTypeDescription))
                .entrySet()
                .stream()
                .sorted((e1, e2) -> e1.getKey().compareTo(e2.getKey()))
                .forEach(entry -> {
                    sb.append(String.format("%s (%d):%n", entry.getKey(), entry.getValue().size()));
                    entry.getValue().forEach(error ->
                            sb.append("  • ").append(error.getFormattedMessage()).append("\n")
                    );
                    sb.append("\n");
                });

        return sb.toString();
    }

    @Override
    public String toString() {
        if (semanticErrors.isEmpty()) {
            return "SemanticErrorCollector{sin errores}";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("SemanticErrorCollector{%d error(es):", semanticErrors.size()));

        for (int i = 0; i < semanticErrors.size(); i++) {
            sb.append(String.format("%n  %d. %s", i + 1, semanticErrors.get(i).toString()));
        }

        sb.append("\n}");
        return sb.toString();
    }

    public String toShortString() {
        return String.format("SemanticErrorCollector{%d error(es)}", semanticErrors.size());
    }
}