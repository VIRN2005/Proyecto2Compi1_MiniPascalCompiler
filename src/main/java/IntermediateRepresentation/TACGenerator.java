package IntermediateRepresentation;

import org.main.MiniPascalBaseVisitor;
import org.main.MiniPascalParser;
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
    
    // Cache para evitar búsquedas repetitivas
    private Map<String, String> variableCache = new HashMap<>();
    private Map<String, Type> typeCache = new HashMap<>();
    
    // Pool de strings para operadores comunes
    private static final String ASSIGN = "ASSIGN";
    private static final String ALLOCATE = "ALLOCATE";
    private static final String LABEL = "LABEL";
    private static final String GOTO = "GOTO";
    private static final String PUSH = "PUSH";
    private static final String CALL = "CALL";

    public TACGenerator(TACInstructionCollector ir, SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
        this.ir = ir;
        this.labels = new HashSet<>();
        this.output = new StringBuilder(1024); 
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

    private static final Map<String, String> DEFAULT_VALUES = Map.of(
        "integer", "0",
        "boolean_", "false",
        "string", "\" \"",
        "char", "' '"
    );

    public String getDefaultValue(Type type) {
        return DEFAULT_VALUES.getOrDefault(type.getName(), "null");
    }

    @Override
    public Void visitProgram_block(MiniPascalParser.Program_blockContext ctx) {
        output.append("Generating TAC instructions...\n");
        String identifier = ctx.ID().getText();
        ir.addInstruction(new TACInstruction(LABEL, identifier, null, null));
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
        return null;
    }

    @Override
    public Void visitDeclaration(MiniPascalParser.DeclarationContext ctx) {
        if (ctx.var_block() != null) {
            visit(ctx.var_block());
        }
        if (ctx.function_block() != null) {
            visit(ctx.function_block());
        }
        return null;
    }

    @Override
    public Void visitVar_block(MiniPascalParser.Var_blockContext ctx) {
        if (ctx.variables() != null) {
            for (MiniPascalParser.VariablesContext variable : ctx.variables()) {
                visit(variable); // Usar visit genérico en lugar de casting
            }
        }
        return null;
    }

    @Override
    public Void visitVariable_declaration(MiniPascalParser.Variable_declarationContext ctx) {
        output.append("Declaring variables...\n");
        
        // Obtener tipo una sola vez
        String typeName = ctx.type().getText();
        Type type = typeCache.computeIfAbsent(typeName, Type::new);
        String value = getDefaultValue(type);
        
        // Procesar todos los identificadores
        List<String> identifiers = ctx.varNames().ID().stream()
            .map(id -> id.getText())
            .collect(Collectors.toList());
        
        for (String identifier : identifiers) {
            ir.addInstruction(new TACInstruction(ALLOCATE, identifier, null, null));
            ir.addInstruction(new TACInstruction(ASSIGN, identifier, value, null));
            
            // Cache la variable
            variableCache.put(identifier, value);
            
            output.append("Allocated and assigned variable: ").append(identifier).append("\n");
        }
        return null;
    }

    @Override
    public Void visitArray_declaration(MiniPascalParser.Array_declarationContext ctx) {
        output.append("Declaring arrays...\n");
        String identifier = ctx.ID().getText();
        String size = ctx.array().range(0).NUM(1).getText();
        String temp = generateTemp();
        
        ir.addInstruction(new TACInstruction("ALLOCATE_ARRAY", temp, identifier, size));
        output.append("Allocated array: ").append(identifier).append(" with size: ").append(size).append("\n");

        String value = getDefaultValue(new Type(ctx.array().arrayType().getText()));
        String index = generateTemp();
        
        ir.addInstruction(new TACInstruction(ASSIGN, index, "0", null));

        String loopLabel = generateLabel();
        String exitLabel = generateLabel();

        // Generar loop de inicialización más eficiente
        ir.addInstruction(new TACInstruction(LABEL, loopLabel, null, null));
        
        String tempVar = generateTemp();
        ir.addInstruction(new TACInstruction("GE", tempVar, index, size)); // Cambiar condición
        ir.addInstruction(new TACInstruction("BRTRUE", exitLabel, tempVar, null));
        ir.addInstruction(new TACInstruction(ASSIGN, identifier + "[" + index + "]", value, null));
        ir.addInstruction(new TACInstruction("ADD", index, index, "1")); // Usar el mismo registro
        ir.addInstruction(new TACInstruction(GOTO, loopLabel, null, null));
        ir.addInstruction(new TACInstruction(LABEL, exitLabel, null, null));

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
        ir.addInstruction(new TACInstruction(LABEL, identifier, null, null));
        output.append("Generating TAC instructions for function: ").append(identifier).append("\n");

        // Procesar parámetros de función de manera más eficiente
        if(ctx.function_variables() != null){
            for(MiniPascalParser.Function_variablesContext variable : ctx.function_variables()){
                processFunction_variables(variable);
            }
        }

        if (ctx.var_block() != null) {
            visit(ctx.var_block());
        }

        if (ctx.body() != null) {
            visit(ctx.body());
        }

        // Optimizar el retorno
        List<TACInstruction> instructions = ir.getInstructions();
        if (!instructions.isEmpty()) {
            String resultTemp = generateTemp();
            String lastResult = instructions.get(instructions.size() - 1).getResult();
            ir.addInstruction(new TACInstruction(ASSIGN, resultTemp, lastResult, null));
            ir.addInstruction(new TACInstruction("RETURN", resultTemp, null, null));
        }
        return null;
    }
    
    // Método auxiliar para procesar variables de función
    private void processFunction_variables(MiniPascalParser.Function_variablesContext variable) {
        if(variable instanceof MiniPascalParser.Function_variables_normalContext){
            MiniPascalParser.Function_variables_normalContext var = (MiniPascalParser.Function_variables_normalContext) variable;
            String typeName = var.type().getText();
            Type varType = typeCache.computeIfAbsent(typeName, Type::new);
            
            for(int i = 0; i < var.ID().size(); i++){
                String varName = var.ID(i).getText();
                String temp = generateTemp();
                ir.addInstruction(new TACInstruction(ALLOCATE, temp, varName, null));
                output.append("Allocated variable: ").append(varName).append("\n");
            }
        }else if(variable instanceof MiniPascalParser.Function_variables_arrayContext){
            MiniPascalParser.Function_variables_arrayContext var = (MiniPascalParser.Function_variables_arrayContext) variable;
            
            for(int i = 0; i < var.ID().size(); i++){
                String varName = var.ID(i).getText();
                String size = var.array().range(0).NUM(0).getText();
                String temp = generateTemp();
                ir.addInstruction(new TACInstruction("ALLOCATE_ARRAY", temp, varName, size));
                output.append("Allocated array: ").append(varName).append(" with size: ").append(size).append("\n");
            }
        }
    }

    @Override
    public Void visitBody(MiniPascalParser.BodyContext ctx) {
        output.append("Generating TAC instructions for body...\n");
        
        for (MiniPascalParser.StatementContext statement : ctx.statement()) {
            visit(statement); // Usar visit genérico en lugar de casting manual
        }
        return null;
    }

    @Override
    public Void visitSimple_statement(MiniPascalParser.Simple_statementContext ctx) {
        visit(ctx.simple()); // Simplificar usando visit genérico
        return null;
    }

    @Override
    public Void visitNested_statement(MiniPascalParser.Nested_statementContext ctx){
        visit(ctx.nested()); // Simplificar usando visit genérico
        return null;
    }

    @Override
    public Void visitSimpleAssigment(MiniPascalParser.SimpleAssigmentContext ctx) {
        visit(ctx.assigment()); // Simplificar usando visit genérico
        return null;
    }

    @Override
    public Void visitAssigmentVar(MiniPascalParser.AssigmentVarContext ctx) {
        String identifier = ctx.ID().getText();
        visit(ctx.expression());

        // Obtener el último resultado de manera más eficiente
        List<TACInstruction> instructions = ir.getInstructions();
        String tempValueVar = instructions.get(instructions.size() - 1).getResult();

        ir.addInstruction(new TACInstruction(ASSIGN, identifier, tempValueVar, null));
        variableCache.put(identifier, tempValueVar); // Actualizar cache
        return null;
    }

    @Override
    public Void visitAssigmentArray(MiniPascalParser.AssigmentArrayContext ctx){
        String arrayIdentifier = ctx.ID().getText() + "[" + ctx.expression(0).getText() + "]";
        visit(ctx.expression(1));

        List<TACInstruction> instructions = ir.getInstructions();
        String temp = instructions.get(instructions.size() - 1).getResult();

        String temp2 = generateTemp();
        ir.addInstruction(new TACInstruction("INDEX", temp2, arrayIdentifier, ctx.expression(0).getText()));
        ir.addInstruction(new TACInstruction(ASSIGN, temp2, temp, null));
        return null;
    }

    @Override
    public Void visitForToStat(MiniPascalParser.ForToStatContext ctx){
        output.append("Visiting For To Stat...\n");

        String loopVar = ctx.ID().getText();
        String startLabel = generateLabel();
        String endLabel = generateLabel();

        // Optimizar las expresiones
        visit(ctx.expression(0));
        List<TACInstruction> instructions = ir.getInstructions();
        String initialValueTemp = instructions.get(instructions.size() - 1).getResult();
        ir.addInstruction(new TACInstruction(ASSIGN, loopVar, initialValueTemp, null));

        ir.addInstruction(new TACInstruction(LABEL, startLabel, null, null));

        visit(ctx.expression(1));
        instructions = ir.getInstructions();
        String finalValueTemp = instructions.get(instructions.size() - 1).getResult();
        
        String conditionTemp = generateTemp();
        ir.addInstruction(new TACInstruction("GT", conditionTemp, loopVar, finalValueTemp)); // Cambiar por mayor
        ir.addInstruction(new TACInstruction("BRTRUE", endLabel, conditionTemp, null));

        visit(ctx.statement());

        ir.addInstruction(new TACInstruction("ADD", loopVar, loopVar, "1"));
        ir.addInstruction(new TACInstruction(GOTO, startLabel, null, null));
        ir.addInstruction(new TACInstruction(LABEL, endLabel, null, null));

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
        List<TACInstruction> instructions = ir.getInstructions();
        String tempValueVar1 = instructions.get(instructions.size() - 1).getResult();

        visit(ctx.expression(1));
        instructions = ir.getInstructions();
        String tempValueVar2 = instructions.get(instructions.size() - 1).getResult();

        // Mapeo más eficiente de operadores
        String op = getMultiplicationOperator(ctx);
        String temp = generateTemp();
        ir.addInstruction(new TACInstruction(op, temp, tempValueVar1, tempValueVar2));
        return null;
    }
    
    private String getMultiplicationOperator(MiniPascalParser.ExprMultContext ctx) {
        if(ctx.ASTERISK() != null) return "*";
        if(ctx.SLASH() != null) return "/";
        if(ctx.DIV() != null) return "DIV";
        if(ctx.MOD() != null) return "MOD";
        return "*"; // default
    }

    @Override
    public Void visitExprSum(MiniPascalParser.ExprSumContext ctx){
        visit(ctx.expression(0));
        List<TACInstruction> instructions = ir.getInstructions();
        String tempValueVar1 = instructions.get(instructions.size() - 1).getResult();

        visit(ctx.expression(1));
        instructions = ir.getInstructions();
        String tempValueVar2 = instructions.get(instructions.size() - 1).getResult();

        String op = ctx.PLUS() != null ? "+" : "-";
        String temp = generateTemp();
        ir.addInstruction(new TACInstruction(op, temp, tempValueVar1, tempValueVar2));
        return null;
    }

    @Override
    public Void visitExprInt(MiniPascalParser.ExprIntContext ctx){
        String value = ctx.NUM().getText();
        String temp = generateTemp();
        ir.addInstruction(new TACInstruction(ASSIGN, temp, value, null));
        return null;
    }

    @Override
    public Void visitExprID(MiniPascalParser.ExprIDContext ctx){
        String temp = generateTemp();
        ir.addInstruction(new TACInstruction(ASSIGN, temp, ctx.ID().getText(), null));
        return null;
    }

    @Override
    public Void visitExprComp(MiniPascalParser.ExprCompContext ctx){
        visit(ctx.expression(0));
        List<TACInstruction> instructions = ir.getInstructions();
        String tempValueVar1 = instructions.get(instructions.size() - 1).getResult();

        visit(ctx.expression(1));
        instructions = ir.getInstructions();
        String tempValueVar2 = instructions.get(instructions.size() - 1).getResult();

        String op = getComparisonOperator(ctx);
        String temp = generateTemp();
        ir.addInstruction(new TACInstruction(op, temp, tempValueVar1, tempValueVar2));
        return null;
    }
    
    private String getComparisonOperator(MiniPascalParser.ExprCompContext ctx) {
        if(ctx.EQUAL() != null) return "==";
        if(ctx.NOTEQUAL() != null) return "!=";
        if(ctx.LT() != null) return "<";
        if(ctx.LE() != null) return "<=";
        if(ctx.GT() != null) return ">";
        if(ctx.GE() != null) return ">=";
        return "=="; // default
    }

    @Override
    public Void visitSimpleWrite(MiniPascalParser.SimpleWriteContext ctx){
        visit(ctx.write());
        return null;
    }

    @Override
    public Void visitWriteNormal(MiniPascalParser.WriteNormalContext ctx){
        output.append("Visiting write normal...\n");
        String str = ctx.STR().getText();
        String temp = generateTemp();

        ir.addInstruction(new TACInstruction(ASSIGN, temp, str, null));
        ir.addInstruction(new TACInstruction(PUSH, null, temp, null));

        if(ctx.ID() != null){
            // Usar cache si está disponible
            String varValue = variableCache.get(ctx.ID().getText());
            if (varValue != null) {
                ir.addInstruction(new TACInstruction(PUSH, null, varValue, null));
            } else {
                // Búsqueda reversa optimizada
                findAndPushVariable(ctx.ID().getText());
            }
        } else if(ctx.arrayExpression() != null){
            processArrayExpression(ctx.arrayExpression());
        }

        ir.addInstruction(new TACInstruction(CALL, null, "WRITE", null));
        return null;
    }
    
    private void findAndPushVariable(String varName) {
        List<TACInstruction> instructions = ir.getInstructions();
        for(int i = instructions.size() - 1; i >= 0; i--){
            TACInstruction instr = instructions.get(i);
            if(instr.getResult() != null && instr.getResult().equals(varName)){
                ir.addInstruction(new TACInstruction(PUSH, null, instr.getResult(), null));
                variableCache.put(varName, instr.getResult()); // Cache para futuro uso
                break;
            }
        }
    }
    
    private void processArrayExpression(MiniPascalParser.ArrayExpressionContext arrayExpr) {
        String temp2 = generateTemp();
        String temp3 = generateTemp();
        
        ir.addInstruction(new TACInstruction("LOAD", temp2, "1", null));
        ir.addInstruction(new TACInstruction("INDEX", temp3, arrayExpr.ID().getText(), "1"));
        
        List<TACInstruction> instructions = ir.getInstructions();
        String lastTemp = instructions.get(instructions.size() - 1).getOp1();
        ir.addInstruction(new TACInstruction(PUSH, null, lastTemp, null));
    }

    @Override
    public Void visitIfStat(MiniPascalParser.IfStatContext ctx){
        output.append("Visiting If Stat...\n");

        String elseLabel = generateLabel();
        String endLabel = generateLabel();

        visit(ctx.expression());
        List<TACInstruction> instructions = ir.getInstructions();
        String temp = instructions.get(instructions.size() - 1).getResult();

        ir.addInstruction(new TACInstruction("BRFALSE", temp, elseLabel, null));
        visit(ctx.statement());
        ir.addInstruction(new TACInstruction(GOTO, endLabel, null, null));
        ir.addInstruction(new TACInstruction(LABEL, elseLabel, null, null));

        if(ctx.else_block() != null){
            visit(ctx.else_block());
        }

        ir.addInstruction(new TACInstruction(LABEL, endLabel, null, null));
        return null;
    }

    @Override
    public Void visitElseStat(MiniPascalParser.ElseStatContext ctx){
        output.append("Visiting Else Stat...\n");
        visit(ctx.statement());
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
        return variableCache.get(id);
    }
}