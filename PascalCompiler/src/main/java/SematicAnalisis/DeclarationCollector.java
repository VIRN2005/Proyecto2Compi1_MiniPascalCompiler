package SematicAnalisis;

import ErrorClasses.SemanticError;
import ErrorClasses.SemanticErrorCollector;

import Symbols.Symbol;
import Symbols.SymbolTable;
import Symbols.Type;
import Symbols.ArraySymbol;
import Symbols.VariableSymbol;
import Symbols.MatrixSymbol;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import Symbols.FunctionSymbol;

import MiniPascalClasses.MiniPascalBaseVisitor;
import MiniPascalClasses.MiniPascalParser;

public class DeclarationCollector extends MiniPascalBaseVisitor<Void>{
    private SymbolTable symbolTable;
    private SemanticErrorCollector errorCollector;
    private int functionLevel = 0;

    public DeclarationCollector(SymbolTable symbolTable, SemanticErrorCollector errorCollector){
        this.symbolTable = symbolTable;
        this.errorCollector = errorCollector;
    }

    @Override
    public Void visitProgram_block(MiniPascalParser.Program_blockContext ctx){
        System.out.println("Declarando Variables");
        String identifier = ctx.ID().getText();

        if(symbolTable.lookup_variable(identifier) != null){
            errorCollector.addError(new SemanticError("SEMANTIC ERROR",ctx.ID().getSymbol().getLine(), ctx.ID().getSymbol().getCharPositionInLine(), "Variable already declared: " + identifier + " at scope level " + symbolTable.getScopeLevel()));
        } else {
            Symbol program = new Symbol(identifier, new Type("program"), symbolTable.getScopeLevel());
            symbolTable.add(program);
        }
        if(ctx.src() != null){
            this.visitSrc(ctx.src());
        }
        return null;
    }

    @Override
    public Void visitSrc(MiniPascalParser.SrcContext ctx){
        System.out.println("Entrando a src");
        if(ctx.declaration() != null){
            this.visitDeclaration(ctx.declaration());
        }
        return null;
    }

    @Override
    public Void visitDeclaration(MiniPascalParser.DeclarationContext ctx){
        System.out.println("Entrando a declaration");
        if(ctx.var_block()!= null){
            this.visitVar_block(ctx.var_block());
        }

        if(ctx.function_block() != null){
            this.visitFunction_block(ctx.function_block());
        }

        return null;
    }

    @Override
    public Void visitVar_block(MiniPascalParser.Var_blockContext ctx){
        if(ctx.variables()!=null){
            for(MiniPascalParser.VariablesContext variable : ctx.variables()){
                if(variable instanceof MiniPascalParser.Variable_declarationContext){
                    MiniPascalParser.Variable_declarationContext var = (MiniPascalParser.Variable_declarationContext) variable;
                    this.visitVariable_declaration(var);
                }
                else if(variable instanceof MiniPascalParser.Array_declarationContext){
                    MiniPascalParser.Array_declarationContext var = (MiniPascalParser.Array_declarationContext) variable;
                    this.visitArray_declaration(var);
                }
            }
        }
        return null;
    }

    @Override
    public Void visitVariable_declaration(MiniPascalParser.Variable_declarationContext ctx){
        this.visitVarNames(ctx.varNames()); //Aqui haces copia del metodo visitVariableDeclaratino

        //Retreive Identifiers
        List<String> identifiers = ctx.varNames().ID().stream().map(
                ID -> ID.getText()).collect(Collectors.toList());

        //Retrieve Type
        Type type = new Type(ctx.type().getText());

        for (int i = 0; i < identifiers.size(); i++) {
            if(symbolTable.lookup_variable_in_scope(identifiers.get(i)) != -1){
                errorCollector.addError(new SemanticError("SEMANTIC ERROR",ctx.varNames().ID(i).getSymbol().getLine(), ctx.varNames().ID(i).getSymbol().getCharPositionInLine(), "Variable already declared: " + identifiers.get(i) + " at scope level " + symbolTable.getScopeLevel()));
            } else {
                VariableSymbol variable = new VariableSymbol(identifiers.get(i), type, false, symbolTable.getScopeLevel());
                symbolTable.add(variable);
            }
        }

        return null;
    }

    public Void visitArray_declaration(MiniPascalParser.Array_declarationContext ctx){
        String identifier = ctx.ID().getText();
        Type type = new Type(ctx.array().arrayType().getText());

        //Array Type
        if(ctx.array().range().size() == 1){
            if(symbolTable.lookup_variable(identifier) != null){
                errorCollector.addError(new SemanticError("SEMANTIC ERROR",ctx.ID().getSymbol().getLine(), ctx.ID().getSymbol().getCharPositionInLine(), "Variable already declared: " + identifier + " at scope level " + symbolTable.getScopeLevel()));
            } else {
                Integer size1 = Integer.parseInt(ctx.array().range(0).NUM(0).getText());
                Integer size2 = Integer.parseInt(ctx.array().range(0).NUM(1).getText());

                List<VariableSymbol> elements = new ArrayList<>();
                for (int i = 0; i < size2; i++) {
                    VariableSymbol element = new VariableSymbol(identifier + "[" + (i+1) + "]", type, false, symbolTable.getScopeLevel());
                    elements.add(element);
                }

                ArraySymbol array = new ArraySymbol(identifier, type, elements, symbolTable.getScopeLevel());
                symbolTable.add(array);
            }
        }
        else if(ctx.array().range().size() == 2){
            if(symbolTable.lookup_variable(identifier) != null){
                errorCollector.addError(new SemanticError("SEMANTIC ERROR",ctx.ID().getSymbol().getLine(), ctx.ID().getSymbol().getCharPositionInLine(), "Variable already declared: " + identifier + " at scope level " + symbolTable.getScopeLevel()));
            } else {
                Integer size1 = Integer.parseInt(ctx.array().range(0).NUM(0).getText());
                Integer size2 = Integer.parseInt(ctx.array().range(0).NUM(1).getText());
                Integer size3 = Integer.parseInt(ctx.array().range(1).NUM(0).getText());
                Integer size4 = Integer.parseInt(ctx.array().range(1).NUM(1).getText());

                VariableSymbol[][] elements = new VariableSymbol[size2][size4];
                for (int i = 0; i < size2; i++) {
                    for (int j = 0; j < size4; j++) {
                        VariableSymbol element = new VariableSymbol(identifier + "[" + (i+1) + "][" + (j+1) + "]", type, false, symbolTable.getScopeLevel());
                        elements[i][j] = element;
                    }
                }

                MatrixSymbol matrix = new MatrixSymbol(identifier, type, elements, symbolTable.getScopeLevel());
                symbolTable.add(matrix);
            }
        }

        return null;
    }

    @Override
    public Void visitFunction_block(MiniPascalParser.Function_blockContext ctx){
        for(MiniPascalParser.FunctionContext function : ctx.function()){
            this.visitFunction(function);
        }
        return null;
    }

    @Override
    public Void visitFunction(MiniPascalParser.FunctionContext ctx){
        functionLevel++;
        for(int i = 0; i < functionLevel; i++){
            symbolTable.enterScope();
        }

        String identifier = ctx.ID().getText();
        Type returnType = new Type(ctx.type().getText());

        List<Symbol> parameters = new ArrayList<>();

        if(ctx.function_variables()!=null){
            for(MiniPascalParser.Function_variablesContext variable : ctx.function_variables()){
                if(variable instanceof MiniPascalParser.Function_variables_normalContext){
                    MiniPascalParser.Function_variables_normalContext var = (MiniPascalParser.Function_variables_normalContext) variable;
                    Type varType = new Type(var.type().getText());
                    for(int i = 0; i < var.ID().size(); i++){
                        String varName = var.ID(i).getText();
                        Symbol varSymbol = new Symbol(varName, varType,symbolTable.getScopeLevel());
                        parameters.add(varSymbol);
                    }
                }
            }

        }


        FunctionSymbol function = new FunctionSymbol(identifier, returnType, parameters, symbolTable.getScopeLevel());
        symbolTable.add(function);

        if(ctx.var_block()!=null){
            this.visitVar_block(ctx.var_block());
        }

        for(int i = 0; i < functionLevel; i++){
            symbolTable.exitScope();
        }

        return null;
    }

    public void viewSymbolTable(){
        symbolTable.allSymbols();
    }

}
