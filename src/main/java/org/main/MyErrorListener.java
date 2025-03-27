package org.main;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Token;
import java.util.ArrayList;

public class MyErrorListener extends org.antlr.v4.runtime.BaseErrorListener {

    // Registro inmutable para almacenar errores con mensajes épicos
    public record MyError(String errorMsg, int line, int charPositionInLine) {}

    public static final ArrayList<MyError> errors = new ArrayList<>();
    private final boolean isLexer;

    public MyErrorListener(boolean isLexer) {
        this.isLexer = isLexer;
    }

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer,
                            Object offendingSymbol,
                            int line, int charPositionInLine,
                            String msg,
                            RecognitionException e) {

        // Traducciones y personalizaciones para que los mensajes suenen más impactantes
        msg = msg.replace("no viable alternative at input", "¡no se encontró ninguna ruta válida en la entrada!")
                .replace("token recognition error at:", "¡Falla crítica al reconocer token en:")
                .replace("extraneous input", "¡entrada inesperada y fuera de lugar!")
                .replace("expecting", "¡se esperaba:")
                .replace("missing", "¡faltaba:")
                .replace("mismatched input", "¡entrada incompatible!")
                .replace("at", "en");

        // Construimos un mensaje de error épico
        StringBuilder sb = new StringBuilder();
        sb.append("🔥 [ERROR] Línea ")
                .append(line)
                .append(" - Columna ")
                .append(charPositionInLine)
                .append(": ")
                .append(msg);

        // Si es error del parser, agregamos la línea con la posición resaltada
        if (!isLexer && offendingSymbol instanceof Token) {
            sb.append("\n")
                    .append(generateUnderline(recognizer, (Token) offendingSymbol, line, charPositionInLine));
        }

        errors.add(new MyError(sb.toString(), line, charPositionInLine));
    }

    // Método que genera una representación gráfica de la posición del error
    private String generateUnderline(Recognizer<?, ?> recognizer, Token offendingToken, int line, int charPositionInLine) {
        CommonTokenStream tokens = (CommonTokenStream) recognizer.getInputStream();
        String inputText = tokens.getTokenSource().getInputStream().toString();
        String[] allLines = inputText.split("\n");

        // Si la línea no existe, se asigna cadena vacía
        String errorLine = (line - 1 < allLines.length) ? allLines[line - 1] : "";
        StringBuilder pointer = new StringBuilder();
        for (int i = 0; i < charPositionInLine; i++) {
            pointer.append(" ");
        }

        if (offendingToken != null) {
            int start = offendingToken.getStartIndex();
            int stop = offendingToken.getStopIndex();
            if (start >= 0 && stop >= 0) {
                // Genera tantos '^' como caracteres cubre el token conflictivo
                for (int i = start; i <= stop; i++) {
                    pointer.append("^");
                }
            }
        }

        return errorLine + "\n" + pointer;
    }
}
