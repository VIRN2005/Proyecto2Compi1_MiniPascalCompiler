package ErrorClasses;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Token;
import java.util.ArrayList;


public class MyErrorListener extends org.antlr.v4.runtime.BaseErrorListener {
    public record MyError(String errorMsg, int line, int charPositionInLine, String severity) {}
    public static ArrayList<MyError> errors = new ArrayList<>();
    public boolean isLexer;

    public MyErrorListener(boolean isLexer) {
        this.isLexer = isLexer;
    }

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer,
                            Object offendingSymbol,
                            int line, int charPositionInLine,
                            String msg,
                            RecognitionException e) {
        
        String translatedMsg = translateToPascalStyle(msg);
        String severity = isLexer ? "Error" : "Error";
        
        String filename = getSourceFileName(recognizer);
        String pascalErrorMsg = String.format("%s(%d,%d) %s: %s", 
            filename, line, charPositionInLine, severity, translatedMsg);
        
        if (!isLexer && offendingSymbol instanceof Token) {
            String underline = underlineError(recognizer, (Token) offendingSymbol, line, charPositionInLine);
            if (underline != null && !underline.trim().isEmpty()) {
                pascalErrorMsg += "\n" + underline;
            }
        }
        
        errors.add(new MyError(pascalErrorMsg, line, charPositionInLine, severity));
    }

    private String translateToPascalStyle(String msg) {
        if (msg == null) return "Error de sintaxis";
        
        String result = msg;
        
        // Traducciones específicas al estilo Pascal
        result = result.replace("no viable alternative at input", "símbolo inesperado");
        result = result.replace("token recognition error at:", "carácter ilegal:");
        result = result.replace("extraneous input", "símbolo extra");
        result = result.replace("expecting", "se esperaba");
        result = result.replace("missing", "falta");
        result = result.replace("mismatched input", "símbolo incorrecto");
        result = result.replace("at", "en");
        result = result.replace("EOF", "fin de archivo");
        result = result.replace("'{'", "BEGIN");
        result = result.replace("'}'", "END");
        result = result.replace("';'", "punto y coma");
        result = result.replace("'('", "paréntesis izquierdo");
        result = result.replace("')'", "paréntesis derecho");
        result = result.replace("'['", "corchete izquierdo");
        result = result.replace("']'", "corchete derecho");
        result = result.replace("','", "coma");
        result = result.replace("'='", "signo igual");
        result = result.replace("':'", "dos puntos");
        
        result = result.replaceAll("'([^']*)'", "$1");
        
        return result;
    }

    private String getSourceFileName(Recognizer<?, ?> recognizer) {
        try {
            if (recognizer != null && recognizer.getInputStream() instanceof CommonTokenStream) {
                CommonTokenStream tokens = (CommonTokenStream) recognizer.getInputStream();
                String sourceName = tokens.getSourceName();
                if (sourceName != null && !sourceName.equals("<unknown>")) {
                    // Extraer solo el nombre del archivo sin la ruta
                    int lastSlash = Math.max(sourceName.lastIndexOf('/'), sourceName.lastIndexOf('\\'));
                    return lastSlash >= 0 ? sourceName.substring(lastSlash + 1) : sourceName;
                }
            }
        } catch (Exception e) {
            
        }
        return "programa";
    }

    protected String underlineError(Recognizer<?, ?> recognizer,
                                  Token offendingToken, int line,
                                  int charPositionInLine) {
        try {
            CommonTokenStream tokens = (CommonTokenStream) recognizer.getInputStream();
            String input = tokens.getTokenSource().getInputStream().toString();
            String[] lines = input.split("\n");
            
            if (line <= 0 || line > lines.length) {
                return "";
            }
            
            String errorLine = lines[line - 1];
            StringBuilder pointer = new StringBuilder();
            
            // Crear espacios hasta la posición del error
            for (int i = 0; i < charPositionInLine; i++) {
                if (i < errorLine.length() && errorLine.charAt(i) == '\t') {
                    pointer.append('\t');
                } else {
                    pointer.append(' ');
                }
            }
            
            // Agregar indicador del error
            int start = offendingToken.getStartIndex();
            int stop = offendingToken.getStopIndex();
            
            if (start >= 0 && stop >= 0) {
                int tokenLength = Math.min(stop - start + 1, 20); // Limitar longitud
                for (int i = 0; i < tokenLength; i++) {
                    pointer.append('^');
                }
            } else {
                pointer.append('^');
            }
            
            return errorLine + "\n" + pointer.toString();
            
        } catch (Exception e) {
            return "";
        }
    }

    public static String getAllErrors() {
        if (errors.isEmpty()) {
            return "";
        }
        
        StringBuilder result = new StringBuilder();
        for (MyError error : errors) {
            result.append(error.errorMsg()).append("\n");
        }
        
        result.append("\n").append(errors.size())
              .append(errors.size() == 1 ? " error encontrado." : " errores encontrados.");
        
        return result.toString();
    }

    public static boolean hasErrors() {
        return !errors.isEmpty();
    }

    public static void clearErrors() {
        errors.clear();
    }

    public static int getErrorCount() {
        return errors.size();
    }
}