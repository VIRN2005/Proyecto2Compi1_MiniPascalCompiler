package ErrorClasses;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Token;
import java.util.ArrayList;

public class MyErrorListener extends org.antlr.v4.runtime.BaseErrorListener {

    public record MyError(String errorMsg, int line, int charPositionInLine) {}
    public static ArrayList<MyError> errors = new ArrayList<>();
    public boolean isLexer;

    public MyErrorListener(boolean isLexer){
        this.isLexer = isLexer;
    }

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer,
                            Object offendingSymbol,
                            int line, int charPositionInLine,
                            String msg,
                            RecognitionException e) {
        msg = msg.replace("no viable alternative at input", "ninguna alternativa viable en la entrada");
        msg = msg.replace("token recognition error at:","error de reconocimiento de token en:");
        msg = msg.replace("extraneous input","entrada ajena");
        msg = msg.replace("expecting","se esperaba");
        msg = msg.replace("missing","falta");
        msg = msg.replace("mismatched input","entrada incorrecta");
        msg = msg.replace("at","en");
        String errormsg = "Error en la línea " + line + ", columna " + charPositionInLine + ": " + msg;
        if(!isLexer)
            errormsg += "\n" + underlineError(recognizer,(Token)offendingSymbol, line, charPositionInLine);
        errors.add(new MyError(errormsg, line, charPositionInLine));
    }

    protected String underlineError(Recognizer recognizer,
                                  Token offendingToken, int line,
                                  int charPositionInLine) {
        CommonTokenStream tokens =
                (CommonTokenStream)recognizer.getInputStream();
        String input = tokens.getTokenSource().getInputStream().toString();
        String[] lines = input.split("\n");
        String errorLine = lines[line - 1];
        String errorLinePointer = "";
        for (int i=0; i<charPositionInLine; i++) errorLinePointer += " ";
        int start = offendingToken.getStartIndex();
        int stop = offendingToken.getStopIndex();
        if ( start>=0 && stop>=0 ) {
            for (int i=start; i<=stop; i++) errorLinePointer += "^";
        }
        return errorLine + "\n" + errorLinePointer;
    }
}
