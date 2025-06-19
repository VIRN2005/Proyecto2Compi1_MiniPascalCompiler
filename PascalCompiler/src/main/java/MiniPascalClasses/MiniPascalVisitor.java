package MiniPascalClasses;// Generated from src/grammar/MiniPascal.g4 by ANTLR 4.13.2
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link MiniPascalParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface MiniPascalVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link MiniPascalParser#program_block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProgram_block(MiniPascalParser.Program_blockContext ctx);
	/**
	 * Visit a parse tree produced by {@link MiniPascalParser#src}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSrc(MiniPascalParser.SrcContext ctx);
	/**
	 * Visit a parse tree produced by {@link MiniPascalParser#declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDeclaration(MiniPascalParser.DeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link MiniPascalParser#var_block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVar_block(MiniPascalParser.Var_blockContext ctx);
	/**
	 * Visit a parse tree produced by the {@code variable_declaration}
	 * labeled alternative in {@link MiniPascalParser#variables}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariable_declaration(MiniPascalParser.Variable_declarationContext ctx);
	/**
	 * Visit a parse tree produced by the {@code array_declaration}
	 * labeled alternative in {@link MiniPascalParser#variables}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArray_declaration(MiniPascalParser.Array_declarationContext ctx);
	/**
	 * Visit a parse tree produced by the {@code constant_declaration}
	 * labeled alternative in {@link MiniPascalParser#variables}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstant_declaration(MiniPascalParser.Constant_declarationContext ctx);
	/**
	 * Visit a parse tree produced by the {@code constant_initialization}
	 * labeled alternative in {@link MiniPascalParser#variables}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstant_initialization(MiniPascalParser.Constant_initializationContext ctx);
	/**
	 * Visit a parse tree produced by the {@code variable_initialization}
	 * labeled alternative in {@link MiniPascalParser#variables}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariable_initialization(MiniPascalParser.Variable_initializationContext ctx);
	/**
	 * Visit a parse tree produced by {@link MiniPascalParser#varNames}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVarNames(MiniPascalParser.VarNamesContext ctx);
	/**
	 * Visit a parse tree produced by {@link MiniPascalParser#function_block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunction_block(MiniPascalParser.Function_blockContext ctx);
	/**
	 * Visit a parse tree produced by the {@code array_Type}
	 * labeled alternative in {@link MiniPascalParser#type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArray_Type(MiniPascalParser.Array_TypeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code string_Type}
	 * labeled alternative in {@link MiniPascalParser#type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitString_Type(MiniPascalParser.String_TypeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code INT}
	 * labeled alternative in {@link MiniPascalParser#arrayType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitINT(MiniPascalParser.INTContext ctx);
	/**
	 * Visit a parse tree produced by the {@code CHAR}
	 * labeled alternative in {@link MiniPascalParser#arrayType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCHAR(MiniPascalParser.CHARContext ctx);
	/**
	 * Visit a parse tree produced by the {@code BOOL}
	 * labeled alternative in {@link MiniPascalParser#arrayType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBOOL(MiniPascalParser.BOOLContext ctx);
	/**
	 * Visit a parse tree produced by the {@code CONSTCH}
	 * labeled alternative in {@link MiniPascalParser#constType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCONSTCH(MiniPascalParser.CONSTCHContext ctx);
	/**
	 * Visit a parse tree produced by the {@code CONSTSTR}
	 * labeled alternative in {@link MiniPascalParser#constType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCONSTSTR(MiniPascalParser.CONSTSTRContext ctx);
	/**
	 * Visit a parse tree produced by {@link MiniPascalParser#array}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArray(MiniPascalParser.ArrayContext ctx);
	/**
	 * Visit a parse tree produced by {@link MiniPascalParser#range}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRange(MiniPascalParser.RangeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code function_variables_normal}
	 * labeled alternative in {@link MiniPascalParser#function_variables}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunction_variables_normal(MiniPascalParser.Function_variables_normalContext ctx);
	/**
	 * Visit a parse tree produced by the {@code function_variables_array}
	 * labeled alternative in {@link MiniPascalParser#function_variables}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunction_variables_array(MiniPascalParser.Function_variables_arrayContext ctx);
	/**
	 * Visit a parse tree produced by the {@code function_variables_const}
	 * labeled alternative in {@link MiniPascalParser#function_variables}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunction_variables_const(MiniPascalParser.Function_variables_constContext ctx);
	/**
	 * Visit a parse tree produced by {@link MiniPascalParser#function}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunction(MiniPascalParser.FunctionContext ctx);
	/**
	 * Visit a parse tree produced by {@link MiniPascalParser#body}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBody(MiniPascalParser.BodyContext ctx);
	/**
	 * Visit a parse tree produced by the {@code simple_statement}
	 * labeled alternative in {@link MiniPascalParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSimple_statement(MiniPascalParser.Simple_statementContext ctx);
	/**
	 * Visit a parse tree produced by the {@code nested_statement}
	 * labeled alternative in {@link MiniPascalParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNested_statement(MiniPascalParser.Nested_statementContext ctx);
	/**
	 * Visit a parse tree produced by the {@code simpleAssigment}
	 * labeled alternative in {@link MiniPascalParser#simple}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSimpleAssigment(MiniPascalParser.SimpleAssigmentContext ctx);
	/**
	 * Visit a parse tree produced by the {@code simpleRead}
	 * labeled alternative in {@link MiniPascalParser#simple}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSimpleRead(MiniPascalParser.SimpleReadContext ctx);
	/**
	 * Visit a parse tree produced by the {@code simpleWrite}
	 * labeled alternative in {@link MiniPascalParser#simple}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSimpleWrite(MiniPascalParser.SimpleWriteContext ctx);
	/**
	 * Visit a parse tree produced by the {@code simpleCallFunction}
	 * labeled alternative in {@link MiniPascalParser#simple}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSimpleCallFunction(MiniPascalParser.SimpleCallFunctionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code nestedIf}
	 * labeled alternative in {@link MiniPascalParser#nested}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNestedIf(MiniPascalParser.NestedIfContext ctx);
	/**
	 * Visit a parse tree produced by the {@code nestedWhile}
	 * labeled alternative in {@link MiniPascalParser#nested}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNestedWhile(MiniPascalParser.NestedWhileContext ctx);
	/**
	 * Visit a parse tree produced by the {@code nestedFor}
	 * labeled alternative in {@link MiniPascalParser#nested}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNestedFor(MiniPascalParser.NestedForContext ctx);
	/**
	 * Visit a parse tree produced by the {@code nestedRepeat}
	 * labeled alternative in {@link MiniPascalParser#nested}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNestedRepeat(MiniPascalParser.NestedRepeatContext ctx);
	/**
	 * Visit a parse tree produced by the {@code exprInt}
	 * labeled alternative in {@link MiniPascalParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprInt(MiniPascalParser.ExprIntContext ctx);
	/**
	 * Visit a parse tree produced by the {@code exprNeg}
	 * labeled alternative in {@link MiniPascalParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprNeg(MiniPascalParser.ExprNegContext ctx);
	/**
	 * Visit a parse tree produced by the {@code exprComp}
	 * labeled alternative in {@link MiniPascalParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprComp(MiniPascalParser.ExprCompContext ctx);
	/**
	 * Visit a parse tree produced by the {@code exprNot}
	 * labeled alternative in {@link MiniPascalParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprNot(MiniPascalParser.ExprNotContext ctx);
	/**
	 * Visit a parse tree produced by the {@code exprStr}
	 * labeled alternative in {@link MiniPascalParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprStr(MiniPascalParser.ExprStrContext ctx);
	/**
	 * Visit a parse tree produced by the {@code exprMult}
	 * labeled alternative in {@link MiniPascalParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprMult(MiniPascalParser.ExprMultContext ctx);
	/**
	 * Visit a parse tree produced by the {@code exprChar}
	 * labeled alternative in {@link MiniPascalParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprChar(MiniPascalParser.ExprCharContext ctx);
	/**
	 * Visit a parse tree produced by the {@code exprSum}
	 * labeled alternative in {@link MiniPascalParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprSum(MiniPascalParser.ExprSumContext ctx);
	/**
	 * Visit a parse tree produced by the {@code exprParen}
	 * labeled alternative in {@link MiniPascalParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprParen(MiniPascalParser.ExprParenContext ctx);
	/**
	 * Visit a parse tree produced by the {@code exprBool}
	 * labeled alternative in {@link MiniPascalParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprBool(MiniPascalParser.ExprBoolContext ctx);
	/**
	 * Visit a parse tree produced by the {@code exprID}
	 * labeled alternative in {@link MiniPascalParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprID(MiniPascalParser.ExprIDContext ctx);
	/**
	 * Visit a parse tree produced by the {@code exprCallFunction}
	 * labeled alternative in {@link MiniPascalParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprCallFunction(MiniPascalParser.ExprCallFunctionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code exprLogic}
	 * labeled alternative in {@link MiniPascalParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprLogic(MiniPascalParser.ExprLogicContext ctx);
	/**
	 * Visit a parse tree produced by the {@code exprArray}
	 * labeled alternative in {@link MiniPascalParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprArray(MiniPascalParser.ExprArrayContext ctx);
	/**
	 * Visit a parse tree produced by {@link MiniPascalParser#arrayExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArrayExpression(MiniPascalParser.ArrayExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code assigmentVar}
	 * labeled alternative in {@link MiniPascalParser#assigment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssigmentVar(MiniPascalParser.AssigmentVarContext ctx);
	/**
	 * Visit a parse tree produced by the {@code assigmentArray}
	 * labeled alternative in {@link MiniPascalParser#assigment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssigmentArray(MiniPascalParser.AssigmentArrayContext ctx);
	/**
	 * Visit a parse tree produced by {@link MiniPascalParser#read}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRead(MiniPascalParser.ReadContext ctx);
	/**
	 * Visit a parse tree produced by the {@code writeNormal}
	 * labeled alternative in {@link MiniPascalParser#write}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWriteNormal(MiniPascalParser.WriteNormalContext ctx);
	/**
	 * Visit a parse tree produced by the {@code writeLine}
	 * labeled alternative in {@link MiniPascalParser#write}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWriteLine(MiniPascalParser.WriteLineContext ctx);
	/**
	 * Visit a parse tree produced by {@link MiniPascalParser#call_function}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCall_function(MiniPascalParser.Call_functionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ifBody}
	 * labeled alternative in {@link MiniPascalParser#if_block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfBody(MiniPascalParser.IfBodyContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ifStat}
	 * labeled alternative in {@link MiniPascalParser#if_block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfStat(MiniPascalParser.IfStatContext ctx);
	/**
	 * Visit a parse tree produced by the {@code elseIfBody}
	 * labeled alternative in {@link MiniPascalParser#else_if_block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitElseIfBody(MiniPascalParser.ElseIfBodyContext ctx);
	/**
	 * Visit a parse tree produced by the {@code elseIfStat}
	 * labeled alternative in {@link MiniPascalParser#else_if_block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitElseIfStat(MiniPascalParser.ElseIfStatContext ctx);
	/**
	 * Visit a parse tree produced by the {@code elseBody}
	 * labeled alternative in {@link MiniPascalParser#else_block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitElseBody(MiniPascalParser.ElseBodyContext ctx);
	/**
	 * Visit a parse tree produced by the {@code elseStat}
	 * labeled alternative in {@link MiniPascalParser#else_block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitElseStat(MiniPascalParser.ElseStatContext ctx);
	/**
	 * Visit a parse tree produced by the {@code forToBody}
	 * labeled alternative in {@link MiniPascalParser#for_loop}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitForToBody(MiniPascalParser.ForToBodyContext ctx);
	/**
	 * Visit a parse tree produced by the {@code forDownToBody}
	 * labeled alternative in {@link MiniPascalParser#for_loop}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitForDownToBody(MiniPascalParser.ForDownToBodyContext ctx);
	/**
	 * Visit a parse tree produced by the {@code forToStat}
	 * labeled alternative in {@link MiniPascalParser#for_loop}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitForToStat(MiniPascalParser.ForToStatContext ctx);
	/**
	 * Visit a parse tree produced by the {@code forDownToStat}
	 * labeled alternative in {@link MiniPascalParser#for_loop}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitForDownToStat(MiniPascalParser.ForDownToStatContext ctx);
	/**
	 * Visit a parse tree produced by the {@code whileBody}
	 * labeled alternative in {@link MiniPascalParser#while_loop}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhileBody(MiniPascalParser.WhileBodyContext ctx);
	/**
	 * Visit a parse tree produced by the {@code whileStat}
	 * labeled alternative in {@link MiniPascalParser#while_loop}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhileStat(MiniPascalParser.WhileStatContext ctx);
	/**
	 * Visit a parse tree produced by {@link MiniPascalParser#repeat_loop}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRepeat_loop(MiniPascalParser.Repeat_loopContext ctx);
}