package IntermediateRepresentation;

import MiniPascalClasses.MiniPascalBaseVisitor;
import MiniPascalClasses.MiniPascalParser;
import Symbols.Symbol;
import Symbols.SymbolTable;
import Symbols.Type;

import java.util.*;
import java.util.stream.Collectors;

public class TACGenerator extends MiniPascalBaseVisitor<Void> {
    private TACInstructionCollector ir;
    private SymbolTable symbolTable;
    private Set<String> labels;
    private int labelCounter = 0;
    private int tempCounter = 0;
    private StringBuilder output;

    public TACGenerator(TACInstructionCollector ir, SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
        this.ir = ir;
        this.labels = new HashSet<>();
        this.output = new StringBuilder();
        this.ir.setOutput(output);
    }

    public String generateLabel() {
        String label = "Label" + labelCounter++;
        labels.add(label);
        return label;
    }

    public String generateTemp() {
        return "_t" + tempCounter++;
    }

    public String getDefaultValue(Type type) {
        switch (type.getName()) {
            case "integer":
                return "0";
            case "boolean_":
                return "false";
            case "string":
                return "\" \"";
            case "char":
                return "' '";
            default:
                return "null";
        }
    }

    @Override
    public Void visitProgram_block(MiniPascalParser.Program_blockContext ctx) {
        output.append("Generating TAC instructions...\n");
        String identifier = ctx.ID().getText();
        TACInstruction programLabel = new TACInstruction("LABEL", identifier, null, null);
        ir.addInstruction(programLabel);
        if (ctx.src() != null) {
            visit(ctx.src());
        }
        return null;
    }

    @Override
    public Void visitSrc(MiniPascalParser.SrcContext ctx) {
        if (ctx.declaration() != null) {
            visit(ctx.declaration());
        }
        if (ctx.body() != null) {
            visit(ctx.body());
        }
        if(ctx.declaration().function_block() != null){
            visit(ctx.declaration().function_block());
        }
        return null;
    }

    @Override
    public Void visitDeclaration(MiniPascalParser.DeclarationContext ctx) {
        if (ctx.var_block() != null) {
            visit(ctx.var_block());
        }
        return null;
    }

    @Override
    public Void visitVar_block(MiniPascalParser.Var_blockContext ctx) {
        if (ctx.variables() != null) {
            for (MiniPascalParser.VariablesContext variable : ctx.variables()) {
                if (variable instanceof MiniPascalParser.Variable_declarationContext) {
                    MiniPascalParser.Variable_declarationContext var = (MiniPascalParser.Variable_declarationContext) variable;
                    visit(var);
                } else if (variable instanceof MiniPascalParser.Array_declarationContext) {
                    MiniPascalParser.Array_declarationContext var = (MiniPascalParser.Array_declarationContext) variable;
                    visit(var);
                }
            }
        }
        return null;
    }

    @Override
    public Void visitVariable_declaration(MiniPascalParser.Variable_declarationContext ctx) {
        output.append("Declaring variables...\n");
        List<String> identifiers = ctx.varNames().ID().stream().map(
                ID -> ID.getText()).collect(Collectors.toList());
        Type type = new Type(ctx.type().getText());
        for (String identifier : identifiers) {

            String value = getDefaultValue(type);
            TACInstruction allocate = new TACInstruction("ALLOCATE", identifier, null, null);
            ir.addInstruction(allocate);
            output.append("Allocated variable: ").append(identifier).append("\n");

            TACInstruction assign = new TACInstruction("ASSIGN", identifier, value, null);
            ir.addInstruction(assign);
            output.append("Assigned value: ").append(value).append(" to variable: ").append(identifier).append("\n");
        }
        return null;
    }

    @Override
    public Void visitArray_declaration(MiniPascalParser.Array_declarationContext ctx) {
        output.append("Declaring arrays...\n");
        String identifier = ctx.ID().getText();
        String size = ctx.array().range(0).NUM(1).getText();
        String temp = generateTemp();
        TACInstruction allocateArray = new TACInstruction("ALLOCATE_ARRAY", temp, identifier, size);
        ir.addInstruction(allocateArray);
        output.append("Allocated array: ").append(identifier).append(" with size: ").append(size).append("\n");

        String value = getDefaultValue(new Type(ctx.array().arrayType().getText()));

        String index = generateTemp();
        TACInstruction assignIndex = new TACInstruction("ASSIGN", index, "0", null);
        ir.addInstruction(assignIndex);

        String loopLabel = generateLabel();
        String exitLabel = generateLabel();

        TACInstruction labelInstr = new TACInstruction("LABEL", loopLabel, null, null);
        ir.addInstruction(labelInstr);

        String tempVar = generateTemp();
        TACInstruction conditionInstr = new TACInstruction("LT", tempVar, index, String.valueOf(size));
        ir.addInstruction(conditionInstr);

        TACInstruction branchInstr = new TACInstruction("BREQ", exitLabel, tempVar, "false");
        ir.addInstruction(branchInstr);

        TACInstruction initElementInstr = new TACInstruction("ASSIGN", identifier + "[" + index + "]", value, null);
        ir.addInstruction(initElementInstr);

        TACInstruction incrementInstr = new TACInstruction("ADD", index, index, "1");
        ir.addInstruction(incrementInstr);

        TACInstruction gotoInstr = new TACInstruction("GOTO", loopLabel, null, null);
        ir.addInstruction(gotoInstr);

        TACInstruction exitLabelInstr = new TACInstruction("LABEL", exitLabel, null, null);
        ir.addInstruction(exitLabelInstr);


        return null;
    }

    @Override
    public Void visitFunction_block(MiniPascalParser.Function_blockContext ctx) {
        if (ctx.function() != null) {
            for (MiniPascalParser.FunctionContext function : ctx.function()) {
                visit(function);
            }
        }
        return null;
    }

    @Override
    public Void visitFunction(MiniPascalParser.FunctionContext ctx) {
        String identifier = ctx.ID().getText();
        String type = ctx.type().getText();
        String storeTemp = generateTemp();
        TACInstruction functionLabel = new TACInstruction("FUNCTION", identifier, type, null);
        output.append("Generating TAC instructions for function: ").append(identifier).append("\n");
        ir.addInstruction(functionLabel);

        if(ctx.function_variables() != null){
            for (MiniPascalParser.Function_variablesContext variable : ctx.function_variables()) {
                MiniPascalParser.Function_variables_normalContext var = (MiniPascalParser.Function_variables_normalContext) variable;
                String varType = var.type().getText();
                for (int i = 0; i < var.ID().size(); i++) {
                    String varName = var.ID(i).getText()+"_val";
                    if(i != var.ID().size()-1){
                        varName += ",";
                    }
                    TACInstruction param = new TACInstruction("PARAM", varName, varType, null);
                    ir.addInstruction(param);
                }
            }
            TACInstruction start = new TACInstruction("START", "null", null, null);
            ir.addInstruction(start);

        }else{
            TACInstruction start = new TACInstruction("START", "null", null, null);
            ir.addInstruction(start);
        }

        String returnType = ctx.type().getText();

        if (ctx.var_block() != null) {
            visit(ctx.var_block());
        }

        if (ctx.body() != null) {
            List<MiniPascalParser.StatementContext> statements = ctx.body().statement();
            System.out.printf("Visiting %d statements...\n", statements.size());
            for (MiniPascalParser.StatementContext statement : statements) {
                MiniPascalParser.Simple_statementContext simpleStatement = (MiniPascalParser.Simple_statementContext) statement;
                MiniPascalParser.SimpleAssigmentContext simpleAssignment = (MiniPascalParser.SimpleAssigmentContext) simpleStatement.simple();
                MiniPascalParser.AssigmentVarContext assigmentVar = (MiniPascalParser.AssigmentVarContext) simpleAssignment.assigment();
                MiniPascalParser.ExpressionContext expression = assigmentVar.expression();
                System.out.println("Expression: " + expression.getText());
                System.out.println("Expression class: " + expression.getClass());
                if(expression instanceof MiniPascalParser.ExprSumContext){
                    System.out.println("Expression is ExprSumContext");
                    MiniPascalParser.ExprSumContext sum = (MiniPascalParser.ExprSumContext) expression;
                    TACInstruction assign = new TACInstruction("+", identifier+"Res", sum.expression(0).getText(), sum.expression(1).getText());
                    ir.addInstruction(assign);
                }
                if(expression instanceof MiniPascalParser.ExprMultContext){
                    System.out.println("Expression is ExprMultContext");
                    MiniPascalParser.ExprMultContext mult = (MiniPascalParser.ExprMultContext) expression;
                    TACInstruction assign = new TACInstruction("*", identifier+"Res", mult.expression(0).getText(), mult.expression(1).getText());
                    ir.addInstruction(assign);
                }
            }
        }

        String last = ir.getInstructions().get(ir.getInstructions().size() - 1).getResult();

        TACInstruction returnInstr = new TACInstruction("RETURN", last+"_val", null, null);
        ir.addInstruction(returnInstr);
        return null;
    }

    @Override
    public Void visitBody(MiniPascalParser.BodyContext ctx) {
        output.append("Generating TAC instructions for body...\n");

            for (MiniPascalParser.StatementContext statement : ctx.statement()) {
                System.out.println(statement.getClass());
                if (statement instanceof MiniPascalParser.Simple_statementContext) {
                    output.append("Visiting simple statement...\n");
                    MiniPascalParser.Simple_statementContext simpleStatement = (MiniPascalParser.Simple_statementContext) statement;
                    this.visitSimple_statement(simpleStatement);
                } else if (statement instanceof MiniPascalParser.Nested_statementContext){
                    MiniPascalParser.Nested_statementContext nestedStatement = (MiniPascalParser.Nested_statementContext) statement;
                    visit(nestedStatement);
                }
            }

        return null;
    }

    @Override
    public Void visitSimple_statement(MiniPascalParser.Simple_statementContext ctx) {
        if(ctx.simple() instanceof MiniPascalParser.SimpleAssigmentContext){
            output.append("Visiting simple assignment...\n");
            MiniPascalParser.SimpleAssigmentContext simpleAssignment = (MiniPascalParser.SimpleAssigmentContext) ctx.simple();
            this.visitSimpleAssigment(simpleAssignment);
        }else if(ctx.simple() instanceof MiniPascalParser.SimpleReadContext){
            MiniPascalParser.SimpleReadContext simpleRead = (MiniPascalParser.SimpleReadContext) ctx.simple();
            visit(simpleRead);
        }else if (ctx.simple() instanceof MiniPascalParser.SimpleWriteContext){
            MiniPascalParser.SimpleWriteContext simpleWrite = (MiniPascalParser.SimpleWriteContext) ctx.simple();
            visit(simpleWrite);
        }else if (ctx.simple() instanceof MiniPascalParser.SimpleCallFunctionContext){
            MiniPascalParser.SimpleCallFunctionContext simpleCallFunction = (MiniPascalParser.SimpleCallFunctionContext) ctx.simple();
            visit(simpleCallFunction);
        }
        return null;
    }

    @Override
    public Void visitNested_statement(MiniPascalParser.Nested_statementContext ctx){
        if(ctx.nested() instanceof MiniPascalParser.NestedIfContext){
            MiniPascalParser.NestedIfContext nestedIf = (MiniPascalParser.NestedIfContext) ctx.nested();
            visit(nestedIf);
        }else if(ctx.nested() instanceof MiniPascalParser.NestedForContext){
            MiniPascalParser.NestedForContext nestedFor = (MiniPascalParser.NestedForContext) ctx.nested();
            visit(nestedFor);
        }else if(ctx.nested() instanceof MiniPascalParser.NestedWhileContext){
            MiniPascalParser.NestedWhileContext nestedWhile = (MiniPascalParser.NestedWhileContext) ctx.nested();
            visit(nestedWhile);
        }else if (ctx.nested() instanceof MiniPascalParser.NestedRepeatContext){
            MiniPascalParser.NestedRepeatContext nestedRepeat = (MiniPascalParser.NestedRepeatContext) ctx.nested();
            visit(nestedRepeat);
        }
        return null;
    }
    private String posibleVar = "";
    @Override
    public Void visitSimpleAssigment(MiniPascalParser.SimpleAssigmentContext ctx) {
        if(ctx.assigment() instanceof MiniPascalParser.AssigmentVarContext){
            output.append("Visiting variable assignment...\n");
            MiniPascalParser.AssigmentVarContext assigmentVarContext = (MiniPascalParser.AssigmentVarContext) ctx.assigment();
            posibleVar = assigmentVarContext.ID().getText();
            this.visitAssigmentVar(assigmentVarContext);
        }else if(ctx.assigment() instanceof MiniPascalParser.AssigmentArrayContext){
            MiniPascalParser.AssigmentArrayContext assigmentArrayContext = (MiniPascalParser.AssigmentArrayContext) ctx.assigment();
            visit(assigmentArrayContext);
        }
        return null;
    }

    private Map<String,String> variables = new HashMap<>();

    @Override
    public Void visitAssigmentVar(MiniPascalParser.AssigmentVarContext ctx) {
        String identifier = ctx.ID().getText();
        visit(ctx.expression());
        if(ctx.expression() instanceof MiniPascalParser.ExprCallFunctionContext){
            return null;
        }
        String tempValueVar = ir.getInstructions().get(ir.getInstructions().size() - 1).getResult();

        TACInstruction assign = new TACInstruction("ASSIGN", identifier, tempValueVar, null);
        ir.addInstruction(assign);

        variables.put(identifier, tempValueVar);
        return null;
    }

    @Override //TODO
    public Void visitAssigmentArray(MiniPascalParser.AssigmentArrayContext ctx){
        String arrayIdentifier = ctx.ID().getText() + "[" + ctx.expression(0).getText() + "]";
        visit(ctx.expression(1));

        String temp = ir.getInstructions().get(ir.getInstructions().size() - 1).getResult();

        String temp2 = generateTemp();
        TACInstruction index = new TACInstruction("INDEX", temp2, arrayIdentifier, temp);
        ir.addInstruction(index);

        TACInstruction assign2 = new TACInstruction("ASSIGN", temp2, temp, null);
        ir.addInstruction(assign2);
        return null;
    }

    @Override
    public Void visitForToStat(MiniPascalParser.ForToStatContext ctx){
        output.append("Visiting For To Stat...\n");

        String loopVar = ctx.ID().getText();


        String startLabel = generateLabel();
        String endLabel = generateLabel();

        visit(ctx.expression(0));
        String initialValueTemp = ir.getInstructions().get(ir.getInstructions().size() - 1).getResult();
        TACInstruction assign = new TACInstruction("ASSIGN", loopVar, initialValueTemp, null);
        ir.addInstruction(assign);
        variables.put(loopVar, initialValueTemp);

        TACInstruction labelInstr = new TACInstruction("LABEL", startLabel, null, null);
        ir.addInstruction(labelInstr);

        visit(ctx.expression(1));
        String finalValueTemp = ir.getInstructions().get(ir.getInstructions().size() - 1).getResult();
        String conditionTemp = generateTemp();

        TACInstruction condition = new TACInstruction("LE", conditionTemp, loopVar, finalValueTemp);
        ir.addInstruction(condition);

        TACInstruction branchInstr = new TACInstruction("BRFALSE", conditionTemp, endLabel, null);
        ir.addInstruction(branchInstr);

        visit(ctx.statement());

        String temp = ir.getInstructions().get(ir.getInstructions().size() - 1).getResult();

        if (temp == null) {
            temp = "loopVar";
        }

        TACInstruction increment = new TACInstruction("+", temp , loopVar, initialValueTemp);
        ir.addInstruction(increment);

        TACInstruction assign2 = new TACInstruction("ASSIGN", loopVar, temp, null);
        ir.addInstruction(assign2);

        TACInstruction assign3 = new TACInstruction("ASSIGN", initialValueTemp, loopVar, null);
        ir.addInstruction(assign3);


        TACInstruction gotoInstr = new TACInstruction("GOTO", startLabel, null, null);
        ir.addInstruction(gotoInstr);

        TACInstruction endLabelInstr = new TACInstruction("LABEL", endLabel, null, null);
        ir.addInstruction(endLabelInstr);

        return null;
    }

    @Override
    public Void visitExprParen(MiniPascalParser.ExprParenContext ctx){
        visit(ctx.expression());
        return null;
    }

    @Override
    public Void visitExprMult(MiniPascalParser.ExprMultContext ctx){
        visit(ctx.expression(0));
        String tempValueVar1 = ir.getInstructions().get(ir.getInstructions().size() - 1).getResult();

        visit(ctx.expression(1));
        String tempValueVar2 = ir.getInstructions().get(ir.getInstructions().size() - 1).getResult();

        String op = "";
        if(ctx.ASTERISK() != null){
            op = "*";
        }else if(ctx.SLASH() != null){
            op = "/";
        }else if(ctx.DIV() != null) {
            op = "DIV";
        }else if (ctx.MOD() != null){
            op = "MOD";
        }

        String temp = generateTemp();
        TACInstruction mult = new TACInstruction(op, temp, tempValueVar1, tempValueVar2);
        ir.addInstruction(mult);
        return null;
    }

    @Override
    public Void visitExprSum(MiniPascalParser.ExprSumContext ctx){
        visit(ctx.expression(0));
        String tempValueVar1 = ir.getInstructions().get(ir.getInstructions().size() - 1).getResult();

        visit(ctx.expression(1));
        String tempValueVar2 = ir.getInstructions().get(ir.getInstructions().size() - 1).getResult();

        String op = ctx.PLUS() != null ? "+" : "-";

        String temp = generateTemp();
        TACInstruction sum = new TACInstruction(op, temp, tempValueVar1, tempValueVar2);
        ir.addInstruction(sum);
        return null;
    }

    @Override
    public Void visitExprInt(MiniPascalParser.ExprIntContext ctx){
        String value = ctx.NUM().getText();
        String temp = generateTemp();
        TACInstruction assign = new TACInstruction("ASSIGN", temp, value, null);
        ir.addInstruction(assign);
        return null;
    }

    @Override
    public Void visitExprID(MiniPascalParser.ExprIDContext ctx){
        String temp = generateTemp();
        if(!variables.containsKey(ctx.ID().getText())) {
            TACInstruction load = new TACInstruction("LOAD", temp, ctx.ID().getText(), null);
            ir.addInstruction(load);
        }
        TACInstruction assign = new TACInstruction("ASSIGN", temp, ctx.ID().getText(), null);
        ir.addInstruction(assign);
        return null;
    }

    @Override
    public Void visitExprComp(MiniPascalParser.ExprCompContext ctx){
        visit(ctx.expression(0));
        String tempValueVar1 = ir.getInstructions().get(ir.getInstructions().size() - 1).getResult();

        visit(ctx.expression(1));
        String tempValueVar2 = ir.getInstructions().get(ir.getInstructions().size() - 1).getResult();

        String op = "";
        if(ctx.EQUAL() != null){
            op = "==";
        }else if(ctx.NOTEQUAL() != null){
            op = "!=";
        }else if(ctx.LT() != null) {
            op = "<";
        }else if (ctx.LE() != null){
            op = "<=";
        }else if (ctx.GT() != null){
            op = ">";
        }else if (ctx.GE() != null){
            op = ">=";
        }

        String temp = generateTemp();
        TACInstruction comp = new TACInstruction(op, temp, tempValueVar1, tempValueVar2);
        ir.addInstruction(comp);
        return null;
    }

    @Override
    public Void visitExprCallFunction(MiniPascalParser.ExprCallFunctionContext ctx){
        //Remove last two instructions
        ir.getInstructions().remove(ir.getInstructions().size() - 1);
        ir.getInstructions().remove(ir.getInstructions().size() - 1);
        output.append("Visiting simple call function...\n");
        MiniPascalParser.Call_functionContext callFunction = ctx.call_function();
        String identifier = callFunction.ID().getText();
        TACInstruction call = new TACInstruction(identifier, posibleVar, callFunction.expression(0).getText(), callFunction.expression(1).getText());
        ir.addInstruction(call);
        return null;
    }

    @Override
    public Void visitSimpleWrite(MiniPascalParser.SimpleWriteContext ctx){
        MiniPascalParser.WriteContext write = ctx.write();
        visit(write);
        return null;
    }

    @Override
    public Void visitSimpleRead(MiniPascalParser.SimpleReadContext ctx){
        MiniPascalParser.ReadContext read = ctx.read();
        output.append("Generating TAC instructions for read...\n");
        if(read.ID() != null) {
            String identifier = read.ID().getText();
            String temp = variables.get(identifier);
            TACInstruction assign = new TACInstruction("ASSIGN", temp, identifier, null);
            ir.addInstruction(assign);

            TACInstruction push = new TACInstruction("PUSH", null, temp, null);
            ir.addInstruction(push);

            TACInstruction readInstr = new TACInstruction("CALL", null, "READ", null);
            ir.addInstruction(readInstr);
        }
        return null;
    }

    @Override
    public Void visitWriteLine(MiniPascalParser.WriteLineContext ctx){
        output.append("Visiting write line...\n");
        String str = ctx.STR().getText();
        String temp = generateTemp();

        TACInstruction tempStr = new TACInstruction("ASSIGN", temp, str, null);
        TACInstruction writeStr = new TACInstruction("PUSH", null, temp,null);
        TACInstruction callWrite = new TACInstruction("CALL", null, "WRITE");

        ir.addInstruction(tempStr);
        ir.addInstruction(writeStr);

        if(ctx.ID() != null){
            //Search in reverse order to get the last value of the variable in instructions
            for(int i = ir.getInstructions().size() - 1; i >= 0; i--){
                if(ir.getInstructions().get(i).getResult() != null && ir.getInstructions().get(i).getResult().equals(ctx.ID().getText())){
                    String temp2 = generateTemp();
                    TACInstruction assign = new TACInstruction("ASSIGN", temp2, ir.getInstructions().get(i).getResult(),null);
                    ir.addInstruction(assign);

                    TACInstruction writeId = new TACInstruction("PUSH", null, temp2,null);
                    ir.addInstruction(writeId);
                    break;
                }
            }

        }else if(ctx.arrayExpression() != null){
            String temp2 = generateTemp();
            String indexArray = ctx.arrayExpression().expression(0).getText();

            TACInstruction load = new TACInstruction("LOAD", temp2, indexArray, null);
            ir.addInstruction(load);

            TACInstruction assign = new TACInstruction("ASSIGN", temp2, indexArray,null);
            ir.addInstruction(assign);

            String temp3 = generateTemp();
            TACInstruction index = new TACInstruction("INDEX", temp3, ctx.arrayExpression().ID().getText(), temp2);
            ir.addInstruction(index);

            TACInstruction assign2 = new TACInstruction("ASSIGN", temp3, ctx.arrayExpression().ID().getText(),null);
            ir.addInstruction(assign2);

            String lastTemp = ir.getInstructions().getLast().getOp1();
            TACInstruction writeId = new TACInstruction("PUSH", null, lastTemp,null);
            ir.addInstruction(writeId);

        }

        ir.addInstruction(callWrite);

        return null;
    }

    @Override
    public Void visitIfStat(MiniPascalParser.IfStatContext ctx){
        output.append("Visiting If Stat...");

        String elseLabel = generateLabel();
        String endLabel = generateLabel();

        visit(ctx.expression());

        String temp = ir.getInstructions().get(ir.getInstructions().size() - 1).getResult();

        TACInstruction branch = new TACInstruction("BRFALSE", temp, elseLabel, null);
        ir.addInstruction(branch);

        visit(ctx.statement());
        TACInstruction gotoEnd = new TACInstruction("GOTO", endLabel, null, null);
        ir.addInstruction(gotoEnd);

        TACInstruction elseLabelInstr = new TACInstruction("LABEL", elseLabel, null, null);
        ir.addInstruction(elseLabelInstr);

        if(ctx.else_block() != null){
            visit(ctx.else_block());
        }

        TACInstruction endLabelInstr = new TACInstruction("LABEL", endLabel, null, null);
        ir.addInstruction(endLabelInstr);

        return null;
    }

    @Override
    public Void visitElseStat(MiniPascalParser.ElseStatContext ctx){
        output.append("Visiting Else Stat...");
        visit(ctx.statement());
        return null;
    }

    @Override
    public Void visitWhileBody(MiniPascalParser.WhileBodyContext ctx){
        output.append("Visiting While Body...");

        String loopLabel = generateLabel();
        String conditionLabel = generateLabel();
        String endLabel = generateLabel();

        TACInstruction labelInstr = new TACInstruction("LABEL", conditionLabel, null, null);
        ir.addInstruction(labelInstr);

        visit(ctx.expression());
        String temp = ir.getInstructions().get(ir.getInstructions().size() - 1).getResult();

        TACInstruction branch = new TACInstruction("BRFALSE", temp, endLabel, null);
        ir.addInstruction(branch);

        visit(ctx.body());

        TACInstruction gotoInstr = new TACInstruction("GOTO", conditionLabel, null, null);
        ir.addInstruction(gotoInstr);

        TACInstruction endLabelInstr = new TACInstruction("LABEL", endLabel, null, null);
        ir.addInstruction(endLabelInstr);

        return null;
    }


    public void printInstructions(){
        System.out.println(output.toString());
    }

    public void printTACInstructions(){
        for(TACInstruction instruction : ir.getInstructions()){
            System.out.println(instruction);
        }
    }

    public String findVariable(String id){
        return variables.get(id);
    }



}
