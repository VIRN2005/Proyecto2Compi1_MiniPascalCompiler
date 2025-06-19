package SematicAnalisis;

import ErrorClasses.SemanticError;
import ErrorClasses.SemanticErrorCollector;
import Symbols.*;

import MiniPascalClasses.MiniPascalBaseVisitor;
import MiniPascalClasses.MiniPascalParser;
import org.antlr.v4.runtime.Token;

import javax.imageio.stream.MemoryCacheImageInputStream;
import java.util.List;

public class SemanticAnalizer extends MiniPascalBaseVisitor<Void> {
    private SymbolTable symbolTable;
    private SemanticErrorCollector errorCollector;
    private int functionLevel = 0;

    public SemanticAnalizer(SymbolTable symbolTable, SemanticErrorCollector errorCollector){
        this.symbolTable = symbolTable;
        this.errorCollector = errorCollector;
    }

    public SymbolTable getSymbolTable() {
        return symbolTable;
    }

    public SemanticErrorCollector getErrorCollector() {
        return errorCollector;
    }

    public int getFunctionLevel() {
        return functionLevel;
    }

    @Override
    public Void visitProgram_block(MiniPascalParser.Program_blockContext ctx){
        this.visitSrc(ctx.src());
        return null;
    }

    @Override
    public Void visitDeclaration(MiniPascalParser.DeclarationContext ctx){
        if(ctx.function_block() != null){
            this.visitFunction_block(ctx.function_block());
        }
        return null;
    }

    @Override
    public Void visitFunction_block(MiniPascalParser.Function_blockContext ctx){
        for(MiniPascalParser.FunctionContext functionDeclarationContext : ctx.function()){
            this.visitFunction(functionDeclarationContext);
        }
        return null;
    }

    @Override
    public Void visitFunction(MiniPascalParser.FunctionContext ctx){
        String identifier = ctx.ID().getText();
        System.out.println("--------------------");
        System.out.println("Visiting: " + ctx.getText());
        Symbol symbol = symbolTable.lookup_variable(identifier);
        functionLevel = symbol.getScopeLevel();
        this.visitBody(ctx.body());
        functionLevel = 0;
        return null;
    }

    @Override
    public Void visitSrc(MiniPascalParser.SrcContext ctx){
        this.visitDeclaration(ctx.declaration());
        this.visitBody(ctx.body());
        return null;
    }

    @Override
    public Void visitBody(MiniPascalParser.BodyContext ctx){
        for(MiniPascalParser.StatementContext statementContext : ctx.statement()){
            if(statementContext instanceof MiniPascalParser.Simple_statementContext){
                this.visitSimple_statement((MiniPascalParser.Simple_statementContext) statementContext);
            }
            if(statementContext instanceof MiniPascalParser.Nested_statementContext){
                this.visitNested_statement((MiniPascalParser.Nested_statementContext) statementContext);
            }
        }
        return null;
    }

    @Override
    public Void visitSimple_statement(MiniPascalParser.Simple_statementContext ctx){
        if(ctx.simple() instanceof MiniPascalParser.SimpleAssigmentContext){
            this.visitSimpleAssigment((MiniPascalParser.SimpleAssigmentContext) ctx.simple());
        }
        if (ctx.simple() instanceof MiniPascalParser.SimpleCallFunctionContext){
            this.visitSimpleCallFunction((MiniPascalParser.SimpleCallFunctionContext) ctx.simple());
        }
        if (ctx.simple() instanceof MiniPascalParser.SimpleReadContext){
            this.visitSimpleRead((MiniPascalParser.SimpleReadContext) ctx.simple());
        }
        if (ctx.simple() instanceof MiniPascalParser.SimpleWriteContext){
            this.visitSimpleWrite((MiniPascalParser.SimpleWriteContext) ctx.simple());
        }
        return null;
    }

    @Override
    public Void visitSimpleAssigment(MiniPascalParser.SimpleAssigmentContext ctx){
        if(ctx.assigment() instanceof MiniPascalParser.AssigmentVarContext) {
            this.visitAssigmentVar((MiniPascalParser.AssigmentVarContext) ctx.assigment());
        }
        if(ctx.assigment() instanceof MiniPascalParser.AssigmentArrayContext) {
            this.visitAssigmentArray((MiniPascalParser.AssigmentArrayContext) ctx.assigment());
        }
        return null;
    }

    @Override
    public Void visitNested_statement(MiniPascalParser.Nested_statementContext ctx){
        if(ctx.nested() instanceof MiniPascalParser.NestedIfContext){
            this.visitNestedIf((MiniPascalParser.NestedIfContext) ctx.nested());
        }
        if(ctx.nested() instanceof MiniPascalParser.NestedWhileContext){
            this.visitNestedWhile((MiniPascalParser.NestedWhileContext) ctx.nested());
        }
        if(ctx.nested() instanceof MiniPascalParser.NestedForContext){
            this.visitNestedFor((MiniPascalParser.NestedForContext) ctx.nested());
        }
        if(ctx.nested() instanceof MiniPascalParser.NestedRepeatContext){
            this.visitNestedRepeat((MiniPascalParser.NestedRepeatContext) ctx.nested());
        }
        return null;
    }

    @Override
    public Void visitAssigmentVar(MiniPascalParser.AssigmentVarContext ctx){
        String identifier = ctx.ID().getText();
        System.out.println("");
        Symbol temp = symbolTable.lookup_variable(identifier);


        if(temp instanceof FunctionSymbol) return null;

        VariableSymbol symbol = (VariableSymbol) temp;

        if(symbol == null ){
            errorCollector.addError(new SemanticError("SEMANTIC ERROR",ctx.ID().getSymbol().getLine(), ctx.ID().getSymbol().getCharPositionInLine(), "Variable not declared: " + identifier));
            System.out.println("Variable not declared: " + identifier);
            return null;
        }

        //Check if scope is good
        if(symbol.getScopeLevel() != functionLevel){
            errorCollector.addError(new SemanticError("SEMANTIC ERROR", ctx.ID().getSymbol().getLine(), ctx.ID().getSymbol().getCharPositionInLine(), "Assignment to undefined symbol: " + identifier));
            return null;
        }

        Type type = symbol.getType();
        System.out.println("--------------------");
        System.out.println("Visiting: " + ctx.getText());
        String assignedType = determineAssignmentType(ctx.expression(), type.getName());

        if(!type.getName().equals(assignedType)){
            errorCollector.addError(new SemanticError("SEMANTIC ERROR",ctx.ID().getSymbol().getLine(), ctx.ID().getSymbol().getCharPositionInLine(), "Type mismatch on variable " + identifier  + ": Needed "+ type.getName() + ", found " + assignedType));
        }else if(assignedType.equals(type.getName())){
            symbol.setInitialized(true);
        }

        return null;
    }

    @Override
    public Void visitAssigmentArray(MiniPascalParser.AssigmentArrayContext ctx) { //TODO: Check this
        String identifier = ctx.ID().getText();
        Symbol symbol = symbolTable.lookup_variable(identifier);

        if(symbol == null){
            errorCollector.addError(new SemanticError("SEMANTIC ERROR",ctx.ID().getSymbol().getLine(), ctx.ID().getSymbol().getCharPositionInLine(), "Variable not declared: " + identifier));
            return null;
        }

        if(symbol instanceof ArraySymbol) {
            ArraySymbol arraySymbol = (ArraySymbol) symbol;
            String first = determineAssignmentType(ctx.expression(0), arraySymbol.getType().getName());

            if (!first.equals("integer")) {
                errorCollector.addError(new SemanticError("SEMANTIC ERROR", ctx.expression(0).getStart().getLine(), ctx.expression(0).getStart().getCharPositionInLine(), "Array index must be integer"));
            }

            String assignedType = determineAssignmentType(ctx.expression(1), arraySymbol.getType().getName());
            System.out.println();
            System.out.println("--------------------");
            System.out.println("Visiting: " + ctx.getText());
            System.out.println("Expresion 1: " + ctx.expression(0).getText());
            System.out.println("Expresion 2" + ctx.expression(1).getText());
            if (!assignedType.equals("integer")) {
                errorCollector.addError(new SemanticError("SEMANTIC ERROR", ctx.expression(1).getStart().getLine(), ctx.expression(1).getStart().getCharPositionInLine(), "Type mismatch on array " + identifier + ": Needed " + arraySymbol.getType().getName() + ", found " + assignedType));
            }
        }else if(symbol instanceof MatrixSymbol){
            MatrixSymbol matrixSymbol = (MatrixSymbol) symbol;
            String first = determineAssignmentType(ctx.expression(0), matrixSymbol.getType().getName());
            String second = determineAssignmentType(ctx.expression(1), matrixSymbol.getType().getName());

            System.out.println("Expresion 1: " + ctx.expression(0).getText());
            System.out.println("Expresion 2" + ctx.expression(1).getText());
            System.out.println("Expresion 3" + ctx.expression(2).getText());

            if(!first.equals("integer")){
                errorCollector.addError(new SemanticError("SEMANTIC ERROR", ctx.expression(0).getStart().getLine(), ctx.expression(0).getStart().getCharPositionInLine(), "Array index must be integer"));
            }
            if(!second.equals("integer")){
                errorCollector.addError(new SemanticError("SEMANTIC ERROR", ctx.expression(1).getStart().getLine(), ctx.expression(1).getStart().getCharPositionInLine(), "Array index must be integer"));
            }

            String assignedType = determineAssignmentType(ctx.expression(2), matrixSymbol.getType().getName());
            System.out.println("--------------------");
            System.out.println("Visiting: " + ctx.getText());
            if (!assignedType.equals("integer")) {
                errorCollector.addError(new SemanticError("SEMANTIC ERROR", ctx.expression(2).getStart().getLine(), ctx.expression(2).getStart().getCharPositionInLine(), "Type mismatch on array " + identifier + ": Needed " + matrixSymbol.getType().getName() + ", found " + assignedType));
            }
        }

        return null;
    }

    @Override
    public Void visitRead(MiniPascalParser.ReadContext ctx){
        System.out.println("--------------------");
        System.out.println("Visiting: " + ctx.getText());
        if(ctx.ID() != null){
            String identifier = ctx.ID().getText();
            VariableSymbol symbol = (VariableSymbol) symbolTable.lookup_variable(identifier);

            if(symbol == null){
                errorCollector.addError(new SemanticError("SEMANTIC ERROR",ctx.ID().getSymbol().getLine(), ctx.ID().getSymbol().getCharPositionInLine(), "Variable not declared: " + identifier));
                return null;
            }

            Type type = symbol.getType();

            if(type.getName().equals("boolean")){
                errorCollector.addError(new SemanticError("SEMANTIC ERROR",ctx.ID().getSymbol().getLine(), ctx.ID().getSymbol().getCharPositionInLine(), "Cannot read boolean variable: " + identifier));
            }

            symbol.setInitialized(true);

        } else if (ctx.arrayExpression() != null) {
            String identifier = ctx.arrayExpression().ID().getText();
            ArraySymbol symbol = (ArraySymbol) symbolTable.lookup_variable(identifier);
            if (symbol == null) {
                errorCollector.addError(new SemanticError("SEMANTIC ERROR", ctx.ID().getSymbol().getLine(), ctx.ID().getSymbol().getCharPositionInLine(), "Array not declared: " + identifier));
                return null;
            }

            Type type = symbol.getType();

            if (type.getName().equals("boolean")) {
                errorCollector.addError(new SemanticError("SEMANTIC ERROR", ctx.arrayExpression().ID().getSymbol().getLine(), ctx.arrayExpression().ID().getSymbol().getCharPositionInLine(), "Cannot read boolean array: " + identifier));
            }

            this.visitArrayExpression(ctx.arrayExpression());
        }
        return null;
    }

    @Override
    public Void visitArrayExpression(MiniPascalParser.ArrayExpressionContext ctx){
        //Get the array symbol and identifier
        String identifier = ctx.ID().getText();
        Symbol symbol = symbolTable.lookup_variable(identifier);

        //Check if the symbol is declared
        if (symbol == null) {
            errorCollector.addError(new SemanticError("SEMANTIC ERROR", ctx.ID().getSymbol().getLine(), ctx.ID().getSymbol().getCharPositionInLine(), "Assignment to undeclared array symbol: " + identifier));
            return null;
        }

        //Check if the array position is integer
        if (ctx.expression().size() > 1) {
            String assignedType1 = determineAssignmentType(ctx.expression(0), "integer");
            String assignedType2 = determineAssignmentType(ctx.expression(1), "integer");

            checkArrayTypes(ctx, assignedType1, assignedType2);

        } else {
            String assignedType = determineAssignmentType(ctx.expression(0), "assignType");
            if (!assignedType.equals("integer")) {
                errorCollector.addError(new SemanticError("SEMANTIC ERROR", ctx.expression(0).getStart().getLine(), ctx.expression(0).getStart().getCharPositionInLine(), "Array index must be integer"));
            }
        }
        return null;
    }

    @Override
    public Void visitWriteNormal(MiniPascalParser.WriteNormalContext ctx){
        System.out.println("--------------------");
        System.out.println("Visiting: " + ctx.getText());
        if(ctx.ID() != null){
            //Get the identifier
            String identifier = ctx.ID().getText();
            VariableSymbol symbol = (VariableSymbol) symbolTable.lookup_variable(identifier);

            //Check if the symbol is declared
            if(symbol == null) {
                errorCollector.addError(new SemanticError("SEMANTIC ERROR", ctx.ID().getSymbol().getLine(), ctx.ID().getSymbol().getCharPositionInLine(), "Variable not declared: " + identifier));
                return null;
            }

            //Check if the symbol is boolean
            Type type = symbol.getType();
            if(type.getName().equals("boolean")){
                errorCollector.addError(new SemanticError("SEMANTIC ERROR", ctx.ID().getSymbol().getLine(), ctx.ID().getSymbol().getCharPositionInLine(), "Cannot write boolean variable: " + identifier));
            }
            return null;
        } else if(ctx.arrayExpression() != null){
            //Get the identifier
            String identifier = ctx.arrayExpression().ID().getText();
            ArraySymbol symbol = (ArraySymbol) symbolTable.lookup_variable(identifier);

            //Check if the symbol is declared
            if(symbol == null) {
                errorCollector.addError(new SemanticError("SEMANTIC ERROR", ctx.arrayExpression().ID().getSymbol().getLine(), ctx.arrayExpression().ID().getSymbol().getCharPositionInLine(), "Array not declared: " + identifier));
                return null;
            }

            //Check if the symbol is boolean
            Type type = symbol.getType();
            if(type.getName().equals("boolean")){
                errorCollector.addError(new SemanticError("SEMANTIC ERROR", ctx.arrayExpression().ID().getSymbol().getLine(), ctx.arrayExpression().ID().getSymbol().getCharPositionInLine(), "Cannot write boolean array: " + identifier));
            }
            return null;
        }
        return null;
    }

    @Override
    public Void visitNestedIf(MiniPascalParser.NestedIfContext ctx){
        //Get the if block
        MiniPascalParser.If_blockContext ifBlock = ctx.if_block();
        System.out.println("--------------------");
        System.out.println("Visiting: " + ctx.getText());

        this.visit(ifBlock);

        return null;
    }

    @Override
    public Void visitIfBody(MiniPascalParser.IfBodyContext ctx){
        //Get the expression
        String assignedType = determineAssignmentType(ctx.expression(), "boolean");
        System.out.println("If Expression: " + ctx.expression().getText());
        System.out.println("Assigned Type: " + assignedType);

        //Check if the assigned type is boolean
        if(!assignedType.equals("boolean")){
            errorCollector.addError(new SemanticError("SEMANTIC ERROR",ctx.expression().getStart().getLine(), ctx.expression().getStart().getCharPositionInLine(), "Type mismatch on if expression: Needed boolean, found " + assignedType));
        }

        this.visitBody(ctx.body());

        visitIfBlocks(ctx.else_if_block(), ctx.else_block());

        return null;
    }

    @Override
    public Void visitIfStat(MiniPascalParser.IfStatContext ctx){
        //Get the expression
        String assignedType = determineAssignmentType(ctx.expression(), "boolean");
        System.out.println("If Expression: " + ctx.expression().getText());
        System.out.println("Assigned Type: " + assignedType);

        //Check if the assigned type is boolean
        if(!assignedType.equals("boolean")){
            errorCollector.addError(new SemanticError("SEMANTIC ERROR",ctx.expression().getStart().getLine(), ctx.expression().getStart().getCharPositionInLine(), "Type mismatch on if expression: Needed boolean, found " + assignedType));
        }

        this.visit(ctx.statement());

        visitIfBlocks(ctx.else_if_block(), ctx.else_block());

        return null;
    }

    @Override
    public Void visitElseIfBody(MiniPascalParser.ElseIfBodyContext ctx){
        //Get the expression
        String assignedType = determineAssignmentType(ctx.expression(), "boolean");
        System.out.println("Else If Expression: " + ctx.expression().getText());
        System.out.println("Assigned Type: " + assignedType);

        //Check if the assigned type is boolean
        if(!assignedType.equals("boolean")){
            errorCollector.addError(new SemanticError("SEMANTIC ERROR",ctx.expression().getStart().getLine(), ctx.expression().getStart().getCharPositionInLine(), "Type mismatch on else if expression: Needed boolean, found " + assignedType));
        }

        this.visitBody(ctx.body());

        return null;
    }

    @Override
    public Void visitElseIfStat(MiniPascalParser.ElseIfStatContext ctx){
        //Get the expression
        String assignedType = determineAssignmentType(ctx.expression(), "boolean");
        System.out.println("Else If Expression: " + ctx.expression().getText());
        System.out.println("Assigned Type: " + assignedType);

        //Check if the assigned type is boolean
        if(!assignedType.equals("boolean")){
            errorCollector.addError(new SemanticError("SEMANTIC ERROR",ctx.expression().getStart().getLine(), ctx.expression().getStart().getCharPositionInLine(), "Type mismatch on else if expression: Needed boolean, found " + assignedType));
        }

        this.visit(ctx.statement());

        return null;
    }

    @Override
    public Void visitElseBody(MiniPascalParser.ElseBodyContext ctx){
        this.visitBody(ctx.body());
        return null;
    }

    @Override
    public Void visitElseStat(MiniPascalParser.ElseStatContext ctx){
        this.visit(ctx.statement());
        return null;
    }

    @Override
    public Void visitNestedWhile(MiniPascalParser.NestedWhileContext ctx){
        //Get the while block
        MiniPascalParser.While_loopContext whileBlock = ctx.while_loop();
        this.visit(whileBlock);
        return null;
    }

    @Override
    public Void visitWhileStat(MiniPascalParser.WhileStatContext ctx){
        System.out.println("--------------------");
        System.out.println("Visiting: " + ctx.getText());

        //Get the expression
        String assignedType = determineAssignmentType(ctx.expression(), "boolean");
        System.out.println("While Expression: " + ctx.expression().getText());
        System.out.println("Assigned Type: " + assignedType);

        //Check if the assigned type is boolean
        if(!assignedType.equals("boolean")){
            errorCollector.addError(new SemanticError("SEMANTIC ERROR",ctx.expression().getStart().getLine(), ctx.expression().getStart().getCharPositionInLine(), "Type mismatch on while expression: Needed boolean, found " + assignedType));
        }

        this.visit(ctx.statement());

        return null;
    }

    @Override
    public Void visitWhileBody(MiniPascalParser.WhileBodyContext ctx){
        System.out.println("--------------------");
        System.out.println("Visiting: " + ctx.getText());

        //Get the expression
        String assignedType = determineAssignmentType(ctx.expression(), "boolean");
        System.out.println("While Expression: " + ctx.expression().getText());
        System.out.println("Assigned Type: " + assignedType);

        //Check if the assigned type is boolean
        if(!assignedType.equals("boolean")){
            errorCollector.addError(new SemanticError("SEMANTIC ERROR",ctx.expression().getStart().getLine(), ctx.expression().getStart().getCharPositionInLine(), "Type mismatch on while expression: Needed boolean, found " + assignedType));
        }

        this.visitBody(ctx.body());
        return null;
    }

    @Override
    public Void visitNestedFor(MiniPascalParser.NestedForContext ctx){
        //Get the for block
        MiniPascalParser.For_loopContext forBlock = ctx.for_loop();
        this.visit(forBlock);
        return null;
    }

    @Override
    public Void visitForToBody(MiniPascalParser.ForToBodyContext ctx){
        String visiting = ctx.getText();
        String var = ctx.ID().getText();
        Token tok = ctx.ID().getSymbol();
        String type1 = determineAssignmentType(ctx.expression(0), "integer");
        String type2 = determineAssignmentType(ctx.expression(1), "integer");
        List<MiniPascalParser.ExpressionContext> expressions = ctx.expression();

        visitForTypes(visiting, var, tok, type1, type2, expressions);

        this.visitBody(ctx.body());

        return null;
    }

    @Override
    public Void visitForDownToBody(MiniPascalParser.ForDownToBodyContext ctx){
        String visiting = ctx.getText();
        String var = ctx.ID().getText();
        Token tok = ctx.ID().getSymbol();
        String type1 = determineAssignmentType(ctx.expression(0), "integer");
        String type2 = determineAssignmentType(ctx.expression(1), "integer");
        List<MiniPascalParser.ExpressionContext> expressions = ctx.expression();

        visitForTypes(visiting, var, tok, type1, type2, expressions);

        this.visitBody(ctx.body());

        return null;
    }

    @Override
    public Void visitForToStat(MiniPascalParser.ForToStatContext ctx){
        String visiting = ctx.getText();
        String var = ctx.ID().getText();
        Token tok = ctx.ID().getSymbol();
        String type1 = determineAssignmentType(ctx.expression(0), "integer");
        String type2 = determineAssignmentType(ctx.expression(1), "integer");
        List<MiniPascalParser.ExpressionContext> expressions = ctx.expression();

        visitForTypes(visiting, var, tok, type1, type2, expressions);

        this.visit(ctx.statement());

        return null;
    }

    @Override
    public Void visitForDownToStat(MiniPascalParser.ForDownToStatContext ctx){
        String visiting = ctx.getText();
        String var = ctx.ID().getText();
        Token tok = ctx.ID().getSymbol();
        String type1 = determineAssignmentType(ctx.expression(0), "integer");
        String type2 = determineAssignmentType(ctx.expression(1), "integer");
        List<MiniPascalParser.ExpressionContext> expressions = ctx.expression();

        visitForTypes(visiting, var, tok, type1, type2, expressions);

        this.visit(ctx.statement());

        return null;
    }

    @Override
    public Void visitNestedRepeat(MiniPascalParser.NestedRepeatContext ctx){
        //Get the repeat block
        MiniPascalParser.Repeat_loopContext repeatBlock = ctx.repeat_loop();
        this.visit(repeatBlock);

        MiniPascalParser.ExpressionContext expression = repeatBlock.expression();
        this.visit(expression);
        return null;
    }

    @Override
    public Void visitRepeat_loop(MiniPascalParser.Repeat_loopContext ctx){
        for(MiniPascalParser.StatementContext statementContext : ctx.statement()){
            this.visit(statementContext);
        }
        return null;
    }

    @Override
    public Void visitSimpleCallFunction(MiniPascalParser.SimpleCallFunctionContext ctx){
        //Get the function call
        MiniPascalParser.Call_functionContext callFunction = ctx.call_function();
        String identifier = callFunction.ID().getText();
        FunctionSymbol symbol = (FunctionSymbol) symbolTable.lookup_variable(identifier);

        Integer scope = symbol.getScopeLevel();
        symbolTable.setScopeLevel(scope);
        System.out.println("--------------------");
        System.out.println("Visiting: " + ctx.getText());

        verifyFunctionParameters(ctx.call_function());

        symbolTable.setScopeLevel(0);
        return null;
    }

    public String determineAssignmentType (MiniPascalParser.ExpressionContext ctx, String assignType){

        System.out.println("Determining type of: " + ctx.getText());
        System.out.println("Class: " + ctx.getClass().getSimpleName());

        //Is Integer Only
        if (ctx instanceof MiniPascalParser.ExprMultContext) {
            System.out.println("ExprMult");
            //Get the first expression
            MiniPascalParser.ExprMultContext expr = (MiniPascalParser.ExprMultContext) ctx;
            String assignedType = determineAssignmentType(expr.expression(0), assignType);

            //Check if the assigned type is the same as the assign type
            if (!assignedType.equals("integer")) {
                errorCollector.addError(new SemanticError("SEMANTIC ERROR", expr.expression(0).getStart().getLine(), expr.expression(0).getStart().getCharPositionInLine(), "Type mismatch on expression: Needed integer, found " + assignedType));
            }

            //Get the second expression
            assignedType = determineAssignmentType(expr.expression(1), assignType);

            //Check if the assigned type is the same as the assign type
            if (!assignedType.equals("integer")) {
                errorCollector.addError(new SemanticError("SEMANTIC ERROR", expr.expression(1).getStart().getLine(), expr.expression(1).getStart().getCharPositionInLine(), "Type mismatch on expression: Needed integer, found " + assignedType));
            }

            return "integer";
        }

        if (ctx instanceof MiniPascalParser.ExprSumContext) {
            System.out.println("ExprSum");
            //Get the first expression
            MiniPascalParser.ExprSumContext expr = (MiniPascalParser.ExprSumContext) ctx;
            String assignedType = determineAssignmentType(expr.expression(0), assignType);

            //Check if the assigned type is the same as the assign type
            if (!assignedType.equals("integer")) {
                errorCollector.addError(new SemanticError("SEMANTIC ERROR", expr.expression(0).getStart().getLine(), expr.expression(0).getStart().getCharPositionInLine(), "Type mismatch on expression: Needed integer, found " + assignedType));
            }

            //Get the second expression
            assignedType = determineAssignmentType(expr.expression(1), assignType);

            //Check if the assigned type is the same as the assign type
            if (!assignedType.equals("integer")) {
                errorCollector.addError(new SemanticError("SEMANTIC ERROR", expr.expression(1).getStart().getLine(), expr.expression(1).getStart().getCharPositionInLine(), "Type mismatch on expression: Needed integer, found " + assignedType));
            }

            return "integer";
        }

        if (ctx instanceof MiniPascalParser.ExprNegContext) {
            System.out.println("ExprNeg");
            //Get the expression
            MiniPascalParser.ExprNegContext expr = (MiniPascalParser.ExprNegContext) ctx;
            String assignedType = determineAssignmentType(expr.expression(), assignType);

            //Check if the assigned type is the same as the assign type
            if (!assignedType.equals("integer")) {
                errorCollector.addError(new SemanticError("SEMANTIC ERROR", expr.expression().getStart().getLine(), expr.expression().getStart().getCharPositionInLine(), "Type mismatch on expression: Needed integer, found " + assignedType));
            }

            return "integer";
        }

        //Is Boolean Only
        if (ctx instanceof MiniPascalParser.ExprCompContext){
            System.out.println("ExprComp");
            //Get the first expression
            MiniPascalParser.ExprCompContext expr = (MiniPascalParser.ExprCompContext) ctx;
            String assignedType = determineAssignmentType(expr.expression(0), assignType);

            //Check if the assigned type is the same as the assign type
            if (!assignedType.equals("integer")) {
                errorCollector.addError(new SemanticError("SEMANTIC ERROR", expr.expression(0).getStart().getLine(), expr.expression(0).getStart().getCharPositionInLine(), "Type mismatch on expression: Needed integer, found " + assignedType));
            }

            //Get the second expression
            assignedType = determineAssignmentType(expr.expression(1), assignType);

            //Check if the assigned type is the same as the assign type
            if (!assignedType.equals("integer")) {
                errorCollector.addError(new SemanticError("SEMANTIC ERROR", expr.expression(1).getStart().getLine(), expr.expression(1).getStart().getCharPositionInLine(), "Type mismatch on expression: Needed integer, found " + assignedType));
            }

            return "boolean";
        }

        if (ctx instanceof MiniPascalParser.ExprLogicContext){
            System.out.println("ExprLogic");
            //Get the first expression
            MiniPascalParser.ExprLogicContext expr = (MiniPascalParser.ExprLogicContext) ctx;
            String assignedType = determineAssignmentType(expr.expression(0), assignType);

            //Check if the assigned type is the same as the assign type
            if (!assignedType.equals("boolean")) {
                errorCollector.addError(new SemanticError("SEMANTIC ERROR", expr.expression(0).getStart().getLine(), expr.expression(0).getStart().getCharPositionInLine(), "Type mismatch on expression: Needed boolean, found " + assignedType));
            }

            //Get the second expression
            assignedType = determineAssignmentType(expr.expression(1), assignType);

            //Check if the assigned type is the same as the assign type
            if (!assignedType.equals("boolean")) {
                errorCollector.addError(new SemanticError("SEMANTIC ERROR", expr.expression(1).getStart().getLine(), expr.expression(1).getStart().getCharPositionInLine(), "Type mismatch on expression: Needed boolean, found " + assignedType));
            }

            return "boolean";
        }

        if (ctx instanceof MiniPascalParser.ExprNotContext) {
            System.out.println("ExprNot");
            //Get the expression
            MiniPascalParser.ExprNotContext expr = (MiniPascalParser.ExprNotContext) ctx;
            String assignedType = determineAssignmentType(expr.expression(), assignType);

            //Check if the assigned type is the same as the assign type
            if (!assignedType.equals("boolean")) {
                errorCollector.addError(new SemanticError("SEMANTIC ERROR", expr.expression().getStart().getLine(), expr.expression().getStart().getCharPositionInLine(), "Type mismatch on expression: Needed boolean, found " + assignedType));
            }
            return "boolean";
        }

        //All Types
        if (ctx instanceof MiniPascalParser.ExprParenContext) {
            System.out.println("ExprParen");
            //Get the expression
            MiniPascalParser.ExprParenContext expr = (MiniPascalParser.ExprParenContext) ctx;
            String assignedType = determineAssignmentType(expr.expression(), assignType);

            //Check if the assigned type is the same as the assign type
            if (!assignedType.equals(assignType)) {
                errorCollector.addError(new SemanticError("SEMANTIC ERROR", expr.expression().getStart().getLine(), expr.expression().getStart().getCharPositionInLine(), "Type mismatch on expression: Needed " + assignType + ", found " + assignedType));
            }

            return assignType;
        }

        if (ctx instanceof MiniPascalParser.ExprArrayContext) {
            System.out.println("ExprArray");
            //Get the array
            MiniPascalParser.ExprArrayContext arrayExpression = (MiniPascalParser.ExprArrayContext) ctx;
            MiniPascalParser.ArrayExpressionContext array = arrayExpression.arrayExpression();

            //Get the array symbol and identifier
            String identifier = array.ID().getText();
            Symbol symbol = symbolTable.lookup_variable(identifier);

            //Check if the symbol is declared
            if (symbol == null) {
                errorCollector.addError(new SemanticError("SEMANTIC ERROR", array.ID().getSymbol().getLine(), array.ID().getSymbol().getCharPositionInLine(), "Assignment to undeclared array symbol: " + identifier));
                return assignType;
            }

            //Check if the array position is integer
            if (array.expression().size() > 1) {
                String assignedType1 = determineAssignmentType(array.expression(0), assignType);
                String assignedType2 = determineAssignmentType(array.expression(1), assignType);

                checkArrayTypes(array, assignedType1, assignedType2);

            } else {
                String assignedType = determineAssignmentType(array.expression(0), assignType);
                if (!assignedType.equals("integer")) {
                    errorCollector.addError(new SemanticError("SEMANTIC ERROR", array.expression(0).getStart().getLine(), array.expression(0).getStart().getCharPositionInLine(), "Array index must be integer"));
                }
            }

            //Check if array type is the same as the assign type
            if (!symbol.getType().getName().equals(assignType)) {
                errorCollector.addError(new SemanticError("SEMANTIC ERROR", array.ID().getSymbol().getLine(), array.ID().getSymbol().getCharPositionInLine(), "Assignment to array symbol with different type, needed " + assignType + ", found " + symbol.getType().getName()));
            }

            return assignType;
        }

        if (ctx instanceof MiniPascalParser.ExprCallFunctionContext) {
            System.out.println("ExprCallFunction");
            //Get the function call
            MiniPascalParser.ExprCallFunctionContext callFunction = (MiniPascalParser.ExprCallFunctionContext) ctx;
            MiniPascalParser.Call_functionContext callFunctionContext = callFunction.call_function();
            String identifier = callFunctionContext.ID().getText();
            FunctionSymbol symbol = (FunctionSymbol) symbolTable.lookup_variable(identifier);

            Integer scope = symbol.getScopeLevel();
            symbolTable.setScopeLevel(scope);

            verifyFunctionParameters(callFunctionContext);

            symbolTable.setScopeLevel(0);
            //Check if the function return type is the same as the assign type
            if (!symbol.getReturnType().getName().equals(assignType)) {
                return symbol.getReturnType().getName();
            }

            return assignType;

        }

        if (ctx instanceof MiniPascalParser.ExprIDContext) {
            System.out.println("ExprID");
            //Get the identifier
            MiniPascalParser.ExprIDContext id = (MiniPascalParser.ExprIDContext) ctx;
            String identifier = id.getText();
            VariableSymbol symbol = (VariableSymbol) symbolTable.lookup_variable(identifier);

            //Check if the symbol is declared
            if (symbol == null) {
                errorCollector.addError(new SemanticError("SEMANTIC ERROR", id.ID().getSymbol().getLine(), id.ID().getSymbol().getCharPositionInLine(), "Assignment to undeclared symbol: " + identifier));
                return assignType;
            }

            //Check if scope is good
            if(symbol.getScopeLevel() != functionLevel){
                errorCollector.addError(new SemanticError("SEMANTIC ERROR", id.ID().getSymbol().getLine(), id.ID().getSymbol().getCharPositionInLine(), "Assignment to undefined symbol: " + identifier));
                return assignType;
            }

            //Check if the symbol is initialized
            System.out.println(symbol.isInitialized());
            if(!symbol.isInitialized()){
                errorCollector.addError(new SemanticError("SEMANTIC ERROR", id.ID().getSymbol().getLine(), id.ID().getSymbol().getCharPositionInLine(), "Variable not initialized: " + identifier));
            }

            //Check if the symbol type is the same as the assign type
            if (!symbol.getType().getName().equals(assignType)) {
                return symbol.getType().getName();
            }

            return assignType;
        }

        if (ctx instanceof MiniPascalParser.ExprIntContext) {
            return "integer";
        }

        if (ctx instanceof MiniPascalParser.ExprBoolContext) {
            return "boolean";
        }

        if (ctx instanceof MiniPascalParser.ExprCharContext) {
            return "char";
        }

        if (ctx instanceof MiniPascalParser.ExprStrContext) {
            return "string";
        }

        return "none";
    }

    public Void visitWriteLine(MiniPascalParser.WriteLineContext ctx){
        System.out.println("--------------------");
        System.out.println("Visiting: " + ctx.getText());
        if(ctx.ID() != null){
            String identifier = ctx.ID().getText();
            VariableSymbol symbol = (VariableSymbol) symbolTable.lookup_variable(identifier);

            if(symbol == null) {
                errorCollector.addError(new SemanticError("SEMANTIC ERROR", ctx.ID().getSymbol().getLine(), ctx.ID().getSymbol().getCharPositionInLine(), "Variable not declared: " + identifier));
                return null;
            }

            Type type = symbol.getType();
            if(type.getName().equals("boolean")){
                errorCollector.addError(new SemanticError("SEMANTIC ERROR", ctx.ID().getSymbol().getLine(), ctx.ID().getSymbol().getCharPositionInLine(), "Cannot write boolean variable: " + identifier));
            }
            return null;
        } else if(ctx.arrayExpression() != null){
            String identifier = ctx.arrayExpression().ID().getText();
            ArraySymbol symbol = (ArraySymbol) symbolTable.lookup_variable(identifier);

            if(symbol == null) {
                errorCollector.addError(new SemanticError("SEMANTIC ERROR", ctx.arrayExpression().ID().getSymbol().getLine(), ctx.arrayExpression().ID().getSymbol().getCharPositionInLine(), "Array not declared: " + identifier));
                return null;
            }

            Type type = symbol.getType();
            if(type.getName().equals("boolean")){
                errorCollector.addError(new SemanticError("SEMANTIC ERROR", ctx.arrayExpression().ID().getSymbol().getLine(), ctx.arrayExpression().ID().getSymbol().getCharPositionInLine(), "Cannot write boolean array: " + identifier));
            }
            return null;
        }
        return null;
    }

    public Void verifyFunctionParameters(MiniPascalParser.Call_functionContext callFunction){
        String identifier = callFunction.ID().getText();
        Integer scope = symbolTable.lookup_variable_in_scope(identifier);

        if(scope == -1){
            errorCollector.addError(new SemanticError("SEMANTIC ERROR", callFunction.ID().getSymbol().getLine(), callFunction.ID().getSymbol().getCharPositionInLine(), "Function not declared: " + identifier));
            return null;
        }

        FunctionSymbol symbol = (FunctionSymbol) symbolTable.lookup_variable(identifier);

        //Check if the function has the same number of parameters
        Integer expectedParams = symbol.getParameters().size();
        Integer actualParams = callFunction.expression().size();

        if (expectedParams != actualParams) {
            errorCollector.addError(new SemanticError("SEMANTIC ERROR", callFunction.ID().getSymbol().getLine(), callFunction.ID().getSymbol().getCharPositionInLine(), "Function " + identifier + " expected " + expectedParams + " parameters, found " + actualParams));
        }

        //Check if the parameters are the same type
        for (int i = 0; i < expectedParams; i++) {
            String expectedType = symbol.getParameters().get(i).getType().getName();
            String actualType = determineAssignmentType(callFunction.expression(i), expectedType);

            if (!expectedType.equals(actualType)) {
                errorCollector.addError(new SemanticError("SEMANTIC ERROR", callFunction.ID().getSymbol().getLine(), callFunction.ID().getSymbol().getCharPositionInLine(), "Function " + identifier + " expected parameter " + symbol.getParameters().get(i).getIdentifier() + " to be of type " + expectedType + ", found " + actualType));
            }
        }
        return null;
    }

    public Void visitIfBlocks(List<MiniPascalParser.Else_if_blockContext> elseIfCtx, MiniPascalParser.Else_blockContext elseCtx){
        //Check if there are else if blocks
        if(elseIfCtx != null){
            for(MiniPascalParser.Else_if_blockContext elseIfBlock : elseIfCtx){
                this.visit(elseIfBlock);
            }
        }

        //Check if there is an else block
        if(elseCtx != null){
            this.visit(elseCtx);
        }
        return null;
    }

    public Void checkArrayTypes(MiniPascalParser.ArrayExpressionContext ctx, String assignedType1, String assignedType2) {
        if (!assignedType1.equals("integer")) {
            System.out.println(ctx.expression(0).getText());
            System.out.println(ctx.expression(0).getStart());
            errorCollector.addError(new SemanticError("SEMANTIC ERROR", ctx.expression(0).getStart().getLine(), ctx.expression(0).getStart().getCharPositionInLine(), "Array index must be integer"));
        }
        if (!assignedType2.equals("integer")) {
            System.out.println(ctx.expression(1).getText());
            System.out.println(ctx.expression(1).getStart());
            errorCollector.addError(new SemanticError("SEMANTIC ERROR", ctx.expression(1).getStart().getLine(), ctx.expression(1).getStart().getCharPositionInLine(), "Array index must be integer"));
        }
        return null;
    }

    public Void visitForTypes(String visiting, String var, Token tok, String type1, String type2, List<MiniPascalParser.ExpressionContext> expressions){
        System.out.println("--------------------");
        System.out.println("Visiting: " + visiting);
        VariableSymbol symbol = (VariableSymbol) symbolTable.lookup_variable(var);

        if(symbol == null){
            errorCollector.addError(new SemanticError("SEMANTIC ERROR",tok.getLine(), tok.getCharPositionInLine(), "Variable not declared: " + var));
            return null;
        }

        if (!type1.equals("integer")) {
            errorCollector.addError(new SemanticError("SEMANTIC ERROR", expressions.get(0).getStart().getLine(), expressions.get(0).getStart().getCharPositionInLine(), "Type mismatch on for expression: Needed integer, found " + type1));
        }
        if (!type2.equals("integer")) {
            errorCollector.addError(new SemanticError("SEMANTIC ERROR", expressions.get(1).getStart().getLine(), expressions.get(1).getStart().getCharPositionInLine(), "Type mismatch on for expression: Needed integer, found " + type2));
        }

        return null;
    }
}
