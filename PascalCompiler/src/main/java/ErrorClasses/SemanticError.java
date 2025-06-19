package ErrorClasses;


public class SemanticError {

    private String type;
    private int line;
    private int column;
    private String message;


    public SemanticError() {
    }

    public SemanticError(String type, String message) {
        this.type = type;
        this.message = message;
    }

    public SemanticError(String type, int line, int column, String message) {
        this.type = type;
        this.line = line;
        this.column = column;
        this.message = message;

    }

    public SemanticError(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Semantic Error at line " + line + ", column " + column + ": " + message;
    }

}
