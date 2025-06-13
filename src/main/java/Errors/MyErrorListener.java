package Errors;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CharStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;

public class MyErrorListener extends org.antlr.v4.runtime.BaseErrorListener {

    public record MyError(String errorMsg, int line, int charPositionInLine, String errorType) {
        public MyError(String errorMsg, int line, int charPositionInLine) {
            this(errorMsg, line, charPositionInLine, "SYNTAX");
        }

        @Override
        public String toString() {
            return String.format("[%s] Línea %d:%d - %s", errorType, line, charPositionInLine + 1, errorMsg);
        }
    }

    private static final List<MyError> errors = Collections.synchronizedList(new ArrayList<>());
    private final boolean isLexer;
    private final Map<String, String> errorTranslations;

    public MyErrorListener(boolean isLexer) {
        this.isLexer = isLexer;
        this.errorTranslations = initializeTranslations();
    }


    private Map<String, String> initializeTranslations() {
        Map<String, String> translations = new HashMap<>();
        translations.put("no viable alternative at input", "no se reconoce lo que escribiste");
        translations.put("token recognition error at:", "carácter inválido en:");
        translations.put("extraneous input", "texto inesperado");
        translations.put("expecting", "se esperaba");
        translations.put("missing", "falta");
        translations.put("mismatched input", "entrada incorrecta");
        translations.put("at", "en");
        translations.put("EOF", "final del archivo");
        translations.put("identifier", "nombre de variable");
        translations.put("number", "número");
        translations.put("string", "texto entre comillas");
        translations.put("'{' '}'", "llaves { }");
        translations.put("'(' ')'", "paréntesis ( )");
        translations.put("'[' ']'", "corchetes [ ]");
        translations.put("';'", "punto y coma ;");
        translations.put("','", "coma ,");
        translations.put("'='", "signo igual =");
        translations.put("'+'", "signo más +");
        translations.put("'-'", "signo menos -");
        translations.put("'*'", "signo multiplicación *");
        translations.put("'/'", "signo división /");
        translations.put("'<'", "signo menor que <");
        translations.put("'>'", "signo mayor que >");
        translations.put("'<='", "signo menor o igual <=");
        translations.put("'>='", "signo mayor o igual >=");
        translations.put("'=='", "signo de comparación ==");
        translations.put("'!='", "signo diferente !=");
        translations.put("'&&'", "operador Y lógico &&");
        translations.put("'||'", "operador O lógico ||");
        translations.put("'!'", "operador NO lógico !");
        return translations;
    }

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer,
                            Object offendingSymbol,
                            int line,
                            int charPositionInLine,
                            String msg,
                            RecognitionException e) {

        String translatedMsg = translateErrorMessage(msg);
        String errorType = isLexer ? "LEXER" : "PARSER";
        String formattedError = String.format("Error en línea %d, posición %d: %s",
                line, charPositionInLine + 1, translatedMsg);

        // Add context for parser errors
        if (!isLexer && offendingSymbol instanceof Token) {
            String context = generateErrorContext(recognizer, (Token) offendingSymbol, line, charPositionInLine);
            if (context != null) {
                formattedError += "\n" + context;
            }
        }

        errors.add(new MyError(formattedError, line, charPositionInLine, errorType));
    }

    private String translateErrorMessage(String msg) {
        String translatedMsg = msg;

        // First, handle specific patterns that need context
        if (msg.contains("expecting") && msg.contains("got")) {
            translatedMsg = translatedMsg.replaceAll("expecting (.*) got (.*)", "se esperaba $1 pero se encontró $2");
        }

        // Then apply word-by-word translations
        for (Map.Entry<String, String> entry : errorTranslations.entrySet()) {
            translatedMsg = translatedMsg.replace(entry.getKey(), entry.getValue());
        }

        // Clean up common grammatical issues in Spanish
        translatedMsg = translatedMsg.replace("se esperaba se esperaba", "se esperaba");
        translatedMsg = translatedMsg.replace("falta falta", "falta");

        return translatedMsg;
    }


    private String generateErrorContext(Recognizer<?, ?> recognizer, Token offendingToken,
                                        int line, int charPositionInLine) {
        try {
            if (!(recognizer.getInputStream() instanceof CommonTokenStream)) {
                return null;
            }

            CommonTokenStream tokens = (CommonTokenStream) recognizer.getInputStream();
            CharStream inputStream = tokens.getTokenSource().getInputStream();

            if (inputStream == null) {
                return null;
            }

            String input = inputStream.toString();
            if (input == null || input.isEmpty()) {
                return null;
            }

            String[] lines = input.split("\\r?\\n");

            // Validate line number
            if (line < 1 || line > lines.length) {
                return null;
            }

            String errorLine = lines[line - 1];
            String pointer = generateErrorPointer(offendingToken, charPositionInLine, errorLine.length());

            return formatErrorContext(errorLine, pointer);

        } catch (Exception e) {
            // Log the exception if you have a logging framework
            // logger.warn("Failed to generate error context", e);
            return null;
        }
    }

    private String generateErrorPointer(Token offendingToken, int charPositionInLine, int lineLength) {
        StringBuilder pointer = new StringBuilder();

        // Add spaces up to the error position
        for (int i = 0; i < charPositionInLine && i < lineLength; i++) {
            pointer.append(" ");
        }

        // Add arrows for the token length
        int start = offendingToken.getStartIndex();
        int stop = offendingToken.getStopIndex();

        if (start >= 0 && stop >= 0 && stop >= start) {
            int tokenLength = Math.min(stop - start + 1, lineLength - charPositionInLine);
            for (int i = 0; i < tokenLength; i++) {
                pointer.append("^");
            }
        } else {
            pointer.append("^");
        }

        return pointer.toString();
    }

    private String formatErrorContext(String errorLine, String pointer) {
        return String.format("    %s%n    %s", errorLine, pointer);
    }

    public static List<MyError> getErrors() {
        return new ArrayList<>(errors);
    }

    public static List<MyError> getErrorsByType(String errorType) {
        return errors.stream()
                .filter(error -> error.errorType().equals(errorType))
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
            return "No se encontraron errores.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Se encontraron %d error(es):%n", errors.size()));

        for (int i = 0; i < errors.size(); i++) {
            sb.append(String.format("%d. %s%n", i + 1, errors.get(i).errorMsg()));
            if (i < errors.size() - 1) {
                sb.append("\n");
            }
        }

        return sb.toString();
    }
}