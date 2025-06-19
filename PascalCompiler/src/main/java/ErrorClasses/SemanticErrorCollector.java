package ErrorClasses;

import java.util.ArrayList;

public class SemanticErrorCollector {

    private ArrayList<SemanticError> semanticErrors = new ArrayList<>();

    public void addError(SemanticError error) {
        semanticErrors.add(error);
    }

    public int getErrorsCount() {
        return semanticErrors.size();
    }

    public ArrayList<SemanticError> getErrors() {
        return semanticErrors;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SemanticErrorsCollector{");
        for (SemanticError error : semanticErrors) {
            sb.append("\n").append(error.toString());
        }
        sb.append("\n}");
        return sb.toString();
    }

}
