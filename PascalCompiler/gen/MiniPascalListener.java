// Generated from C:/Users/kevin/OneDrive - Universidad Tecnologica Centroamericana/Documents/2024/Q3/Compiladores I/ProyectoCompi/PascalCompiler/src/grammar/MiniPascal.g4 by ANTLR 4.13.2
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link MiniPascalParser}.
 */
public interface MiniPascalListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link MiniPascalParser#program_block}.
	 * @param ctx the parse tree
	 */
	void enterProgram_block(MiniPascalParser.Program_blockContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniPascalParser#program_block}.
	 * @param ctx the parse tree
	 */
	void exitProgram_block(MiniPascalParser.Program_blockContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniPascalParser#src}.
	 * @param ctx the parse tree
	 */
	void enterSrc(MiniPascalParser.SrcContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniPascalParser#src}.
	 * @param ctx the parse tree
	 */
	void exitSrc(MiniPascalParser.SrcContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniPascalParser#declaration}.
	 * @param ctx the parse tree
	 */
	void enterDeclaration(MiniPascalParser.DeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniPascalParser#declaration}.
	 * @param ctx the parse tree
	 */
	void exitDeclaration(MiniPascalParser.DeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniPascalParser#var_block}.
	 * @param ctx the parse tree
	 */
	void enterVar_block(MiniPascalParser.Var_blockContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniPascalParser#var_block}.
	 * @param ctx the parse tree
	 */
	void exitVar_block(MiniPascalParser.Var_blockContext ctx);
	/**
	 * Enter a parse tree produced by the {@code variable_declaration}
	 * labeled alternative in {@link MiniPascalParser#variables}.
	 * @param ctx the parse tree
	 */
	void enterVariable_declaration(MiniPascalParser.Variable_declarationContext ctx);
	/**
	 * Exit a parse tree produced by the {@code variable_declaration}
	 * labeled alternative in {@link MiniPascalParser#variables}.
	 * @param ctx the parse tree
	 */
	void exitVariable_declaration(MiniPascalParser.Variable_declarationContext ctx);
	/**
	 * Enter a parse tree produced by the {@code array_declaration}
	 * labeled alternative in {@link MiniPascalParser#variables}.
	 * @param ctx the parse tree
	 */
	void enterArray_declaration(MiniPascalParser.Array_declarationContext ctx);
	/**
	 * Exit a parse tree produced by the {@code array_declaration}
	 * labeled alternative in {@link MiniPascalParser#variables}.
	 * @param ctx the parse tree
	 */
	void exitArray_declaration(MiniPascalParser.Array_declarationContext ctx);
	/**
	 * Enter a parse tree produced by the {@code constant_declaration}
	 * labeled alternative in {@link MiniPascalParser#variables}.
	 * @param ctx the parse tree
	 */
	void enterConstant_declaration(MiniPascalParser.Constant_declarationContext ctx);
	/**
	 * Exit a parse tree produced by the {@code constant_declaration}
	 * labeled alternative in {@link MiniPascalParser#variables}.
	 * @param ctx the parse tree
	 */
	void exitConstant_declaration(MiniPascalParser.Constant_declarationContext ctx);
	/**
	 * Enter a parse tree produced by the {@code constant_initialization}
	 * labeled alternative in {@link MiniPascalParser#variables}.
	 * @param ctx the parse tree
	 */
	void enterConstant_initialization(MiniPascalParser.Constant_initializationContext ctx);
	/**
	 * Exit a parse tree produced by the {@code constant_initialization}
	 * labeled alternative in {@link MiniPascalParser#variables}.
	 * @param ctx the parse tree
	 */
	void exitConstant_initialization(MiniPascalParser.Constant_initializationContext ctx);
	/**
	 * Enter a parse tree produced by the {@code variable_initialization}
	 * labeled alternative in {@link MiniPascalParser#variables}.
	 * @param ctx the parse tree
	 */
	void enterVariable_initialization(MiniPascalParser.Variable_initializationContext ctx);
	/**
	 * Exit a parse tree produced by the {@code variable_initialization}
	 * labeled alternative in {@link MiniPascalParser#variables}.
	 * @param ctx the parse tree
	 */
	void exitVariable_initialization(MiniPascalParser.Variable_initializationContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniPascalParser#varNames}.
	 * @param ctx the parse tree
	 */
	void enterVarNames(MiniPascalParser.VarNamesContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniPascalParser#varNames}.
	 * @param ctx the parse tree
	 */
	void exitVarNames(MiniPascalParser.VarNamesContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniPascalParser#function_block}.
	 * @param ctx the parse tree
	 */
	void enterFunction_block(MiniPascalParser.Function_blockContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniPascalParser#function_block}.
	 * @param ctx the parse tree
	 */
	void exitFunction_block(MiniPascalParser.Function_blockContext ctx);
	/**
	 * Enter a parse tree produced by the {@code array_Type}
	 * labeled alternative in {@link MiniPascalParser#type}.
	 * @param ctx the parse tree
	 */
	void enterArray_Type(MiniPascalParser.Array_TypeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code array_Type}
	 * labeled alternative in {@link MiniPascalParser#type}.
	 * @param ctx the parse tree
	 */
	void exitArray_Type(MiniPascalParser.Array_TypeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code string_Type}
	 * labeled alternative in {@link MiniPascalParser#type}.
	 * @param ctx the parse tree
	 */
	void enterString_Type(MiniPascalParser.String_TypeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code string_Type}
	 * labeled alternative in {@link MiniPascalParser#type}.
	 * @param ctx the parse tree
	 */
	void exitString_Type(MiniPascalParser.String_TypeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code INT}
	 * labeled alternative in {@link MiniPascalParser#arrayType}.
	 * @param ctx the parse tree
	 */
	void enterINT(MiniPascalParser.INTContext ctx);
	/**
	 * Exit a parse tree produced by the {@code INT}
	 * labeled alternative in {@link MiniPascalParser#arrayType}.
	 * @param ctx the parse tree
	 */
	void exitINT(MiniPascalParser.INTContext ctx);
	/**
	 * Enter a parse tree produced by the {@code CHAR}
	 * labeled alternative in {@link MiniPascalParser#arrayType}.
	 * @param ctx the parse tree
	 */
	void enterCHAR(MiniPascalParser.CHARContext ctx);
	/**
	 * Exit a parse tree produced by the {@code CHAR}
	 * labeled alternative in {@link MiniPascalParser#arrayType}.
	 * @param ctx the parse tree
	 */
	void exitCHAR(MiniPascalParser.CHARContext ctx);
	/**
	 * Enter a parse tree produced by the {@code BOOL}
	 * labeled alternative in {@link MiniPascalParser#arrayType}.
	 * @param ctx the parse tree
	 */
	void enterBOOL(MiniPascalParser.BOOLContext ctx);
	/**
	 * Exit a parse tree produced by the {@code BOOL}
	 * labeled alternative in {@link MiniPascalParser#arrayType}.
	 * @param ctx the parse tree
	 */
	void exitBOOL(MiniPascalParser.BOOLContext ctx);
	/**
	 * Enter a parse tree produced by the {@code CONSTCH}
	 * labeled alternative in {@link MiniPascalParser#constType}.
	 * @param ctx the parse tree
	 */
	void enterCONSTCH(MiniPascalParser.CONSTCHContext ctx);
	/**
	 * Exit a parse tree produced by the {@code CONSTCH}
	 * labeled alternative in {@link MiniPascalParser#constType}.
	 * @param ctx the parse tree
	 */
	void exitCONSTCH(MiniPascalParser.CONSTCHContext ctx);
	/**
	 * Enter a parse tree produced by the {@code CONSTSTR}
	 * labeled alternative in {@link MiniPascalParser#constType}.
	 * @param ctx the parse tree
	 */
	void enterCONSTSTR(MiniPascalParser.CONSTSTRContext ctx);
	/**
	 * Exit a parse tree produced by the {@code CONSTSTR}
	 * labeled alternative in {@link MiniPascalParser#constType}.
	 * @param ctx the parse tree
	 */
	void exitCONSTSTR(MiniPascalParser.CONSTSTRContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniPascalParser#array}.
	 * @param ctx the parse tree
	 */
	void enterArray(MiniPascalParser.ArrayContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniPascalParser#array}.
	 * @param ctx the parse tree
	 */
	void exitArray(MiniPascalParser.ArrayContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniPascalParser#range}.
	 * @param ctx the parse tree
	 */
	void enterRange(MiniPascalParser.RangeContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniPascalParser#range}.
	 * @param ctx the parse tree
	 */
	void exitRange(MiniPascalParser.RangeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code function_variables_normal}
	 * labeled alternative in {@link MiniPascalParser#function_variables}.
	 * @param ctx the parse tree
	 */
	void enterFunction_variables_normal(MiniPascalParser.Function_variables_normalContext ctx);
	/**
	 * Exit a parse tree produced by the {@code function_variables_normal}
	 * labeled alternative in {@link MiniPascalParser#function_variables}.
	 * @param ctx the parse tree
	 */
	void exitFunction_variables_normal(MiniPascalParser.Function_variables_normalContext ctx);
	/**
	 * Enter a parse tree produced by the {@code function_variables_array}
	 * labeled alternative in {@link MiniPascalParser#function_variables}.
	 * @param ctx the parse tree
	 */
	void enterFunction_variables_array(MiniPascalParser.Function_variables_arrayContext ctx);
	/**
	 * Exit a parse tree produced by the {@code function_variables_array}
	 * labeled alternative in {@link MiniPascalParser#function_variables}.
	 * @param ctx the parse tree
	 */
	void exitFunction_variables_array(MiniPascalParser.Function_variables_arrayContext ctx);
	/**
	 * Enter a parse tree produced by the {@code function_variables_const}
	 * labeled alternative in {@link MiniPascalParser#function_variables}.
	 * @param ctx the parse tree
	 */
	void enterFunction_variables_const(MiniPascalParser.Function_variables_constContext ctx);
	/**
	 * Exit a parse tree produced by the {@code function_variables_const}
	 * labeled alternative in {@link MiniPascalParser#function_variables}.
	 * @param ctx the parse tree
	 */
	void exitFunction_variables_const(MiniPascalParser.Function_variables_constContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniPascalParser#function}.
	 * @param ctx the parse tree
	 */
	void enterFunction(MiniPascalParser.FunctionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniPascalParser#function}.
	 * @param ctx the parse tree
	 */
	void exitFunction(MiniPascalParser.FunctionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniPascalParser#body}.
	 * @param ctx the parse tree
	 */
	void enterBody(MiniPascalParser.BodyContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniPascalParser#body}.
	 * @param ctx the parse tree
	 */
	void exitBody(MiniPascalParser.BodyContext ctx);
	/**
	 * Enter a parse tree produced by the {@code simple_statement}
	 * labeled alternative in {@link MiniPascalParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterSimple_statement(MiniPascalParser.Simple_statementContext ctx);
	/**
	 * Exit a parse tree produced by the {@code simple_statement}
	 * labeled alternative in {@link MiniPascalParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitSimple_statement(MiniPascalParser.Simple_statementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code nested_statement}
	 * labeled alternative in {@link MiniPascalParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterNested_statement(MiniPascalParser.Nested_statementContext ctx);
	/**
	 * Exit a parse tree produced by the {@code nested_statement}
	 * labeled alternative in {@link MiniPascalParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitNested_statement(MiniPascalParser.Nested_statementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code simpleAssigment}
	 * labeled alternative in {@link MiniPascalParser#simple}.
	 * @param ctx the parse tree
	 */
	void enterSimpleAssigment(MiniPascalParser.SimpleAssigmentContext ctx);
	/**
	 * Exit a parse tree produced by the {@code simpleAssigment}
	 * labeled alternative in {@link MiniPascalParser#simple}.
	 * @param ctx the parse tree
	 */
	void exitSimpleAssigment(MiniPascalParser.SimpleAssigmentContext ctx);
	/**
	 * Enter a parse tree produced by the {@code simpleRead}
	 * labeled alternative in {@link MiniPascalParser#simple}.
	 * @param ctx the parse tree
	 */
	void enterSimpleRead(MiniPascalParser.SimpleReadContext ctx);
	/**
	 * Exit a parse tree produced by the {@code simpleRead}
	 * labeled alternative in {@link MiniPascalParser#simple}.
	 * @param ctx the parse tree
	 */
	void exitSimpleRead(MiniPascalParser.SimpleReadContext ctx);
	/**
	 * Enter a parse tree produced by the {@code simpleWrite}
	 * labeled alternative in {@link MiniPascalParser#simple}.
	 * @param ctx the parse tree
	 */
	void enterSimpleWrite(MiniPascalParser.SimpleWriteContext ctx);
	/**
	 * Exit a parse tree produced by the {@code simpleWrite}
	 * labeled alternative in {@link MiniPascalParser#simple}.
	 * @param ctx the parse tree
	 */
	void exitSimpleWrite(MiniPascalParser.SimpleWriteContext ctx);
	/**
	 * Enter a parse tree produced by the {@code simpleCallFunction}
	 * labeled alternative in {@link MiniPascalParser#simple}.
	 * @param ctx the parse tree
	 */
	void enterSimpleCallFunction(MiniPascalParser.SimpleCallFunctionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code simpleCallFunction}
	 * labeled alternative in {@link MiniPascalParser#simple}.
	 * @param ctx the parse tree
	 */
	void exitSimpleCallFunction(MiniPascalParser.SimpleCallFunctionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code nestedIf}
	 * labeled alternative in {@link MiniPascalParser#nested}.
	 * @param ctx the parse tree
	 */
	void enterNestedIf(MiniPascalParser.NestedIfContext ctx);
	/**
	 * Exit a parse tree produced by the {@code nestedIf}
	 * labeled alternative in {@link MiniPascalParser#nested}.
	 * @param ctx the parse tree
	 */
	void exitNestedIf(MiniPascalParser.NestedIfContext ctx);
	/**
	 * Enter a parse tree produced by the {@code nestedWhile}
	 * labeled alternative in {@link MiniPascalParser#nested}.
	 * @param ctx the parse tree
	 */
	void enterNestedWhile(MiniPascalParser.NestedWhileContext ctx);
	/**
	 * Exit a parse tree produced by the {@code nestedWhile}
	 * labeled alternative in {@link MiniPascalParser#nested}.
	 * @param ctx the parse tree
	 */
	void exitNestedWhile(MiniPascalParser.NestedWhileContext ctx);
	/**
	 * Enter a parse tree produced by the {@code nestedFor}
	 * labeled alternative in {@link MiniPascalParser#nested}.
	 * @param ctx the parse tree
	 */
	void enterNestedFor(MiniPascalParser.NestedForContext ctx);
	/**
	 * Exit a parse tree produced by the {@code nestedFor}
	 * labeled alternative in {@link MiniPascalParser#nested}.
	 * @param ctx the parse tree
	 */
	void exitNestedFor(MiniPascalParser.NestedForContext ctx);
	/**
	 * Enter a parse tree produced by the {@code nestedRepeat}
	 * labeled alternative in {@link MiniPascalParser#nested}.
	 * @param ctx the parse tree
	 */
	void enterNestedRepeat(MiniPascalParser.NestedRepeatContext ctx);
	/**
	 * Exit a parse tree produced by the {@code nestedRepeat}
	 * labeled alternative in {@link MiniPascalParser#nested}.
	 * @param ctx the parse tree
	 */
	void exitNestedRepeat(MiniPascalParser.NestedRepeatContext ctx);
	/**
	 * Enter a parse tree produced by the {@code exprInt}
	 * labeled alternative in {@link MiniPascalParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExprInt(MiniPascalParser.ExprIntContext ctx);
	/**
	 * Exit a parse tree produced by the {@code exprInt}
	 * labeled alternative in {@link MiniPascalParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExprInt(MiniPascalParser.ExprIntContext ctx);
	/**
	 * Enter a parse tree produced by the {@code exprNeg}
	 * labeled alternative in {@link MiniPascalParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExprNeg(MiniPascalParser.ExprNegContext ctx);
	/**
	 * Exit a parse tree produced by the {@code exprNeg}
	 * labeled alternative in {@link MiniPascalParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExprNeg(MiniPascalParser.ExprNegContext ctx);
	/**
	 * Enter a parse tree produced by the {@code exprComp}
	 * labeled alternative in {@link MiniPascalParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExprComp(MiniPascalParser.ExprCompContext ctx);
	/**
	 * Exit a parse tree produced by the {@code exprComp}
	 * labeled alternative in {@link MiniPascalParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExprComp(MiniPascalParser.ExprCompContext ctx);
	/**
	 * Enter a parse tree produced by the {@code exprNot}
	 * labeled alternative in {@link MiniPascalParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExprNot(MiniPascalParser.ExprNotContext ctx);
	/**
	 * Exit a parse tree produced by the {@code exprNot}
	 * labeled alternative in {@link MiniPascalParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExprNot(MiniPascalParser.ExprNotContext ctx);
	/**
	 * Enter a parse tree produced by the {@code exprStr}
	 * labeled alternative in {@link MiniPascalParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExprStr(MiniPascalParser.ExprStrContext ctx);
	/**
	 * Exit a parse tree produced by the {@code exprStr}
	 * labeled alternative in {@link MiniPascalParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExprStr(MiniPascalParser.ExprStrContext ctx);
	/**
	 * Enter a parse tree produced by the {@code exprMult}
	 * labeled alternative in {@link MiniPascalParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExprMult(MiniPascalParser.ExprMultContext ctx);
	/**
	 * Exit a parse tree produced by the {@code exprMult}
	 * labeled alternative in {@link MiniPascalParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExprMult(MiniPascalParser.ExprMultContext ctx);
	/**
	 * Enter a parse tree produced by the {@code exprChar}
	 * labeled alternative in {@link MiniPascalParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExprChar(MiniPascalParser.ExprCharContext ctx);
	/**
	 * Exit a parse tree produced by the {@code exprChar}
	 * labeled alternative in {@link MiniPascalParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExprChar(MiniPascalParser.ExprCharContext ctx);
	/**
	 * Enter a parse tree produced by the {@code exprSum}
	 * labeled alternative in {@link MiniPascalParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExprSum(MiniPascalParser.ExprSumContext ctx);
	/**
	 * Exit a parse tree produced by the {@code exprSum}
	 * labeled alternative in {@link MiniPascalParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExprSum(MiniPascalParser.ExprSumContext ctx);
	/**
	 * Enter a parse tree produced by the {@code exprParen}
	 * labeled alternative in {@link MiniPascalParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExprParen(MiniPascalParser.ExprParenContext ctx);
	/**
	 * Exit a parse tree produced by the {@code exprParen}
	 * labeled alternative in {@link MiniPascalParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExprParen(MiniPascalParser.ExprParenContext ctx);
	/**
	 * Enter a parse tree produced by the {@code exprBool}
	 * labeled alternative in {@link MiniPascalParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExprBool(MiniPascalParser.ExprBoolContext ctx);
	/**
	 * Exit a parse tree produced by the {@code exprBool}
	 * labeled alternative in {@link MiniPascalParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExprBool(MiniPascalParser.ExprBoolContext ctx);
	/**
	 * Enter a parse tree produced by the {@code exprID}
	 * labeled alternative in {@link MiniPascalParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExprID(MiniPascalParser.ExprIDContext ctx);
	/**
	 * Exit a parse tree produced by the {@code exprID}
	 * labeled alternative in {@link MiniPascalParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExprID(MiniPascalParser.ExprIDContext ctx);
	/**
	 * Enter a parse tree produced by the {@code exprCallFunction}
	 * labeled alternative in {@link MiniPascalParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExprCallFunction(MiniPascalParser.ExprCallFunctionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code exprCallFunction}
	 * labeled alternative in {@link MiniPascalParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExprCallFunction(MiniPascalParser.ExprCallFunctionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code exprLogic}
	 * labeled alternative in {@link MiniPascalParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExprLogic(MiniPascalParser.ExprLogicContext ctx);
	/**
	 * Exit a parse tree produced by the {@code exprLogic}
	 * labeled alternative in {@link MiniPascalParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExprLogic(MiniPascalParser.ExprLogicContext ctx);
	/**
	 * Enter a parse tree produced by the {@code exprArray}
	 * labeled alternative in {@link MiniPascalParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExprArray(MiniPascalParser.ExprArrayContext ctx);
	/**
	 * Exit a parse tree produced by the {@code exprArray}
	 * labeled alternative in {@link MiniPascalParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExprArray(MiniPascalParser.ExprArrayContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniPascalParser#arrayExpression}.
	 * @param ctx the parse tree
	 */
	void enterArrayExpression(MiniPascalParser.ArrayExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniPascalParser#arrayExpression}.
	 * @param ctx the parse tree
	 */
	void exitArrayExpression(MiniPascalParser.ArrayExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code assigmentVar}
	 * labeled alternative in {@link MiniPascalParser#assigment}.
	 * @param ctx the parse tree
	 */
	void enterAssigmentVar(MiniPascalParser.AssigmentVarContext ctx);
	/**
	 * Exit a parse tree produced by the {@code assigmentVar}
	 * labeled alternative in {@link MiniPascalParser#assigment}.
	 * @param ctx the parse tree
	 */
	void exitAssigmentVar(MiniPascalParser.AssigmentVarContext ctx);
	/**
	 * Enter a parse tree produced by the {@code assigmentArray}
	 * labeled alternative in {@link MiniPascalParser#assigment}.
	 * @param ctx the parse tree
	 */
	void enterAssigmentArray(MiniPascalParser.AssigmentArrayContext ctx);
	/**
	 * Exit a parse tree produced by the {@code assigmentArray}
	 * labeled alternative in {@link MiniPascalParser#assigment}.
	 * @param ctx the parse tree
	 */
	void exitAssigmentArray(MiniPascalParser.AssigmentArrayContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniPascalParser#read}.
	 * @param ctx the parse tree
	 */
	void enterRead(MiniPascalParser.ReadContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniPascalParser#read}.
	 * @param ctx the parse tree
	 */
	void exitRead(MiniPascalParser.ReadContext ctx);
	/**
	 * Enter a parse tree produced by the {@code writeNormal}
	 * labeled alternative in {@link MiniPascalParser#write}.
	 * @param ctx the parse tree
	 */
	void enterWriteNormal(MiniPascalParser.WriteNormalContext ctx);
	/**
	 * Exit a parse tree produced by the {@code writeNormal}
	 * labeled alternative in {@link MiniPascalParser#write}.
	 * @param ctx the parse tree
	 */
	void exitWriteNormal(MiniPascalParser.WriteNormalContext ctx);
	/**
	 * Enter a parse tree produced by the {@code writeLine}
	 * labeled alternative in {@link MiniPascalParser#write}.
	 * @param ctx the parse tree
	 */
	void enterWriteLine(MiniPascalParser.WriteLineContext ctx);
	/**
	 * Exit a parse tree produced by the {@code writeLine}
	 * labeled alternative in {@link MiniPascalParser#write}.
	 * @param ctx the parse tree
	 */
	void exitWriteLine(MiniPascalParser.WriteLineContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniPascalParser#call_function}.
	 * @param ctx the parse tree
	 */
	void enterCall_function(MiniPascalParser.Call_functionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniPascalParser#call_function}.
	 * @param ctx the parse tree
	 */
	void exitCall_function(MiniPascalParser.Call_functionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ifBody}
	 * labeled alternative in {@link MiniPascalParser#if_block}.
	 * @param ctx the parse tree
	 */
	void enterIfBody(MiniPascalParser.IfBodyContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ifBody}
	 * labeled alternative in {@link MiniPascalParser#if_block}.
	 * @param ctx the parse tree
	 */
	void exitIfBody(MiniPascalParser.IfBodyContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ifStat}
	 * labeled alternative in {@link MiniPascalParser#if_block}.
	 * @param ctx the parse tree
	 */
	void enterIfStat(MiniPascalParser.IfStatContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ifStat}
	 * labeled alternative in {@link MiniPascalParser#if_block}.
	 * @param ctx the parse tree
	 */
	void exitIfStat(MiniPascalParser.IfStatContext ctx);
	/**
	 * Enter a parse tree produced by the {@code elseIfBody}
	 * labeled alternative in {@link MiniPascalParser#else_if_block}.
	 * @param ctx the parse tree
	 */
	void enterElseIfBody(MiniPascalParser.ElseIfBodyContext ctx);
	/**
	 * Exit a parse tree produced by the {@code elseIfBody}
	 * labeled alternative in {@link MiniPascalParser#else_if_block}.
	 * @param ctx the parse tree
	 */
	void exitElseIfBody(MiniPascalParser.ElseIfBodyContext ctx);
	/**
	 * Enter a parse tree produced by the {@code elseIfStat}
	 * labeled alternative in {@link MiniPascalParser#else_if_block}.
	 * @param ctx the parse tree
	 */
	void enterElseIfStat(MiniPascalParser.ElseIfStatContext ctx);
	/**
	 * Exit a parse tree produced by the {@code elseIfStat}
	 * labeled alternative in {@link MiniPascalParser#else_if_block}.
	 * @param ctx the parse tree
	 */
	void exitElseIfStat(MiniPascalParser.ElseIfStatContext ctx);
	/**
	 * Enter a parse tree produced by the {@code elseBody}
	 * labeled alternative in {@link MiniPascalParser#else_block}.
	 * @param ctx the parse tree
	 */
	void enterElseBody(MiniPascalParser.ElseBodyContext ctx);
	/**
	 * Exit a parse tree produced by the {@code elseBody}
	 * labeled alternative in {@link MiniPascalParser#else_block}.
	 * @param ctx the parse tree
	 */
	void exitElseBody(MiniPascalParser.ElseBodyContext ctx);
	/**
	 * Enter a parse tree produced by the {@code elseStat}
	 * labeled alternative in {@link MiniPascalParser#else_block}.
	 * @param ctx the parse tree
	 */
	void enterElseStat(MiniPascalParser.ElseStatContext ctx);
	/**
	 * Exit a parse tree produced by the {@code elseStat}
	 * labeled alternative in {@link MiniPascalParser#else_block}.
	 * @param ctx the parse tree
	 */
	void exitElseStat(MiniPascalParser.ElseStatContext ctx);
	/**
	 * Enter a parse tree produced by the {@code forToBody}
	 * labeled alternative in {@link MiniPascalParser#for_loop}.
	 * @param ctx the parse tree
	 */
	void enterForToBody(MiniPascalParser.ForToBodyContext ctx);
	/**
	 * Exit a parse tree produced by the {@code forToBody}
	 * labeled alternative in {@link MiniPascalParser#for_loop}.
	 * @param ctx the parse tree
	 */
	void exitForToBody(MiniPascalParser.ForToBodyContext ctx);
	/**
	 * Enter a parse tree produced by the {@code forDownToBody}
	 * labeled alternative in {@link MiniPascalParser#for_loop}.
	 * @param ctx the parse tree
	 */
	void enterForDownToBody(MiniPascalParser.ForDownToBodyContext ctx);
	/**
	 * Exit a parse tree produced by the {@code forDownToBody}
	 * labeled alternative in {@link MiniPascalParser#for_loop}.
	 * @param ctx the parse tree
	 */
	void exitForDownToBody(MiniPascalParser.ForDownToBodyContext ctx);
	/**
	 * Enter a parse tree produced by the {@code forToStat}
	 * labeled alternative in {@link MiniPascalParser#for_loop}.
	 * @param ctx the parse tree
	 */
	void enterForToStat(MiniPascalParser.ForToStatContext ctx);
	/**
	 * Exit a parse tree produced by the {@code forToStat}
	 * labeled alternative in {@link MiniPascalParser#for_loop}.
	 * @param ctx the parse tree
	 */
	void exitForToStat(MiniPascalParser.ForToStatContext ctx);
	/**
	 * Enter a parse tree produced by the {@code forDownToStat}
	 * labeled alternative in {@link MiniPascalParser#for_loop}.
	 * @param ctx the parse tree
	 */
	void enterForDownToStat(MiniPascalParser.ForDownToStatContext ctx);
	/**
	 * Exit a parse tree produced by the {@code forDownToStat}
	 * labeled alternative in {@link MiniPascalParser#for_loop}.
	 * @param ctx the parse tree
	 */
	void exitForDownToStat(MiniPascalParser.ForDownToStatContext ctx);
	/**
	 * Enter a parse tree produced by the {@code whileBody}
	 * labeled alternative in {@link MiniPascalParser#while_loop}.
	 * @param ctx the parse tree
	 */
	void enterWhileBody(MiniPascalParser.WhileBodyContext ctx);
	/**
	 * Exit a parse tree produced by the {@code whileBody}
	 * labeled alternative in {@link MiniPascalParser#while_loop}.
	 * @param ctx the parse tree
	 */
	void exitWhileBody(MiniPascalParser.WhileBodyContext ctx);
	/**
	 * Enter a parse tree produced by the {@code whileStat}
	 * labeled alternative in {@link MiniPascalParser#while_loop}.
	 * @param ctx the parse tree
	 */
	void enterWhileStat(MiniPascalParser.WhileStatContext ctx);
	/**
	 * Exit a parse tree produced by the {@code whileStat}
	 * labeled alternative in {@link MiniPascalParser#while_loop}.
	 * @param ctx the parse tree
	 */
	void exitWhileStat(MiniPascalParser.WhileStatContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniPascalParser#repeat_loop}.
	 * @param ctx the parse tree
	 */
	void enterRepeat_loop(MiniPascalParser.Repeat_loopContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniPascalParser#repeat_loop}.
	 * @param ctx the parse tree
	 */
	void exitRepeat_loop(MiniPascalParser.Repeat_loopContext ctx);
}