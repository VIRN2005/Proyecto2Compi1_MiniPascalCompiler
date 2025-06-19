package org.main;

import MiniPascalClasses.MiniPascalBaseVisitor;
import MiniPascalClasses.MiniPascalParser;


public class MyVisitor extends MiniPascalBaseVisitor<String> {
    @Override
    public String visitProgram_block(MiniPascalParser.Program_blockContext ctx) {
        return ctx.PROGRAM().getText() + " " + ctx.ID().getText() + ctx.SEMI().getText() + "/n" + this.visitSrc(ctx.src());
    }

    @Override
    public String visitSrc(MiniPascalParser.SrcContext ctx) {
        return this.visitDeclaration(ctx.declaration()) + "/n" + this.visitBody(ctx.body()) + ctx.DOT().getText();
    }

    //Variable Declaration
    @Override
    public String visitDeclaration(MiniPascalParser.DeclarationContext ctx) {
        String str = "";

        if(ctx.var_block() != null)
        {
            str += this.visitVar_block(ctx.var_block());
        }

        if (ctx.function_block() != null)
        {
            str += this.visitFunction_block(ctx.function_block());
        }

        return str;
    }

    @Override
    public String visitVar_block(MiniPascalParser.Var_blockContext ctx) {
        String str = ctx.VAR().getText() + "/n" + ctx.variables(0).getText();

        if(ctx.variables().size() > 1)
        {
            for(int i = 1; i < ctx.variables().size(); i++)
            {
                str += ctx.variables(i).getText();
            }
        }

        return str;
    }

    @Override
    public String visitVariable_declaration(MiniPascalParser.Variable_declarationContext ctx) {
        return this.visitVarNames(ctx.varNames()) + ctx.COLON().getText() + ctx.type().getText() + ctx.SEMI().getText();
    }

    @Override
    public String visitArray_declaration(MiniPascalParser.Array_declarationContext ctx) {
        return ctx.ID().getText() + ctx.COLON().getText() + this.visitArray(ctx.array()) + ctx.SEMI().getText();
    }

    @Override
    public String visitConstant_declaration(MiniPascalParser.Constant_declarationContext ctx) {
        return this.visitVarNames(ctx.varNames()) + ctx.constType().getText() + ctx.SEMI().getText();
    }

    @Override
    public String visitConstant_initialization(MiniPascalParser.Constant_initializationContext ctx) {
        return ctx.ID().getText() + ctx.COLON().getText() + ctx.constType().getText() + ctx.ASSIGN().getText() + ctx.CONST_VAL().getText() + ctx.SEMI().getText();
    }

    @Override
    public String visitVariable_initialization(MiniPascalParser.Variable_initializationContext ctx) {
        return ctx.assigment().getText();
    }

    @Override
    public String visitVarNames(MiniPascalParser.VarNamesContext ctx) {
        String str = ctx.ID(0).getText();

        if(ctx.ID().size() > 1)
        {
            for(int i = 1; i < ctx.ID().size(); i++)
            {
                str += ctx.COMMA(i-1).getText() + ctx.ID(i).getText();
            }
        }

        return str;
    }

    @Override
    public String visitFunction_block(MiniPascalParser.Function_blockContext ctx) {
        String str = "";
        if(ctx.function().size()>1)
            for(int i = 0; i < ctx.function().size(); i++)
            {
                str += this.visitFunction(ctx.function(i));
            }

        return str;
    }

    //Variable Types
    @Override
    public String visitArray_Type(MiniPascalParser.Array_TypeContext ctx) {
        return ctx.arrayType().getText();
    }

    @Override
    public String visitString_Type(MiniPascalParser.String_TypeContext ctx) {
        return ctx.STRING().getText();
    }

    @Override
    public String visitINT(MiniPascalParser.INTContext ctx) {
        return ctx.INTEGER().getText();
    }

    @Override
    public String visitBOOL(MiniPascalParser.BOOLContext ctx) {
        return ctx.BOOLEAN().getText();
    }

    @Override
    public String visitCHAR(MiniPascalParser.CHARContext ctx) {
        return ctx.CHAR().getText();
    }

    @Override
    public String visitCONSTCH(MiniPascalParser.CONSTCHContext ctx) {
        return ctx.CONSTCHAR().getText();
    }

    @Override
    public String visitCONSTSTR(MiniPascalParser.CONSTSTRContext ctx) {
        return ctx.CONSTSTR().getText();
    }

    @Override
    public String visitArray(MiniPascalParser.ArrayContext ctx) {
        String str = ctx.ARRAY().getText() + ctx.LBRACK().getText() + this.visitRange(ctx.range(0));

        if(ctx.COMMA() != null)
        {
            str += ctx.COMMA().getText() + this.visitRange(ctx.range(1));
        }

        str += ctx.RBRACK().getText() + ctx.OF().getText() + ctx.arrayType().getText();

        return str;
    }

    @Override
    public String visitRange(MiniPascalParser.RangeContext ctx) {
        return ctx.NUM(0).getText() + ctx.DOT(0).getText() + ctx.DOT(1).getText() + ctx.NUM(1).getText();
    }

    //Functions
    @Override
    public String visitFunction_variables_normal(MiniPascalParser.Function_variables_normalContext ctx) {
        String str = ctx.ID(0).getText();

        if(ctx.COMMA() != null)
        {
            for(int i = 1; i < ctx.ID().size(); i++)
            {
                str += ctx.COMMA(i-1).getText() + ctx.ID(i).getText();
            }
        }

        str += ctx.COLON().getText() + ctx.type().getText();

        return str;
    }

    @Override
    public String visitFunction_variables_array(MiniPascalParser.Function_variables_arrayContext ctx) {
        String str = ctx.ID(0).getText();

        if(ctx.COMMA() != null)
        {
            for(int i = 1; i < ctx.ID().size(); i++)
            {
                str += ctx.COMMA(i-1).getText() + ctx.ID(i).getText();
            }
        }

        str += ctx.COLON().getText() + this.visitArray(ctx.array());

        return str;
    }

    @Override
    public String visitFunction_variables_const(MiniPascalParser.Function_variables_constContext ctx) {
        String str = ctx.ID(0).getText();

        if(ctx.COMMA() != null)
        {
            for(int i = 1; i < ctx.ID().size(); i++)
            {
                str += ctx.COMMA(i-1).getText() + ctx.ID(i).getText();
            }
        }

        str += ctx.COLON().getText() + ctx.constType().getText();

        return str;
    }

    @Override
    public String visitFunction(MiniPascalParser.FunctionContext ctx) {
        String str = ctx.FUNCTION().getText() + ctx.ID().getText() + ctx.LPAREN().getText();

        if(ctx.function_variables().size()>0)
        {
            str += ctx.function_variables(0).getText();

            if(ctx.function_variables().size() > 1)
            {
                for(int i = 1; i < ctx.function_variables().size(); i++)
                {
                    str += ctx.SEMI(i-1).getText() + ctx.function_variables(i).getText();
                }
            }
        }

        str += ctx.RPAREN().getText() + ctx.COLON().getText() + ctx.type().getText() + ctx.SEMI(ctx.SEMI().size()-2).getText() + "/n";

        if(ctx.var_block() != null)
        {
            str += ctx.var_block().getText();
        }

        str += this.visitBody(ctx.body()) + ctx.SEMI(ctx.SEMI().size()-1).getText();

        return str;
    }

    //Body
    @Override
    public String visitBody(MiniPascalParser.BodyContext ctx) {
        String str;
        str = ctx.BEGIN().getText() + "/n";
        for (int i = 0; i < ctx.statement().size(); i++){
            str += ctx.statement(i)+ "/n";
        }
        return str;
    }

    //Statement Types
    @Override
    public String visitSimple_statement(MiniPascalParser.Simple_statementContext ctx) {
        return ctx.simple().getText() + "/n";
    }

    @Override
    public String visitNested_statement(MiniPascalParser.Nested_statementContext ctx) {
        return ctx.nested().getText() + "/n";
    }

    @Override
    public String visitSimpleAssigment(MiniPascalParser.SimpleAssigmentContext ctx) {
        return ctx.assigment().getText() + "/n";
    }

    @Override
    public String visitSimpleRead(MiniPascalParser.SimpleReadContext ctx) {
        return this.visitRead(ctx.read()) + "/n";
    }

    @Override
    public String visitSimpleWrite(MiniPascalParser.SimpleWriteContext ctx) {
        return ctx.write().getText() + "/n";
    }

    @Override
    public String visitSimpleCallFunction(MiniPascalParser.SimpleCallFunctionContext ctx) {
        return this.visitCall_function(ctx.call_function()) + "/n";
    }

    @Override
    public String visitNestedIf(MiniPascalParser.NestedIfContext ctx) {
        return ctx.if_block().getText() + "/n";
    }

    @Override
    public String visitNestedWhile(MiniPascalParser.NestedWhileContext ctx) {
        return ctx.while_loop().getText() + "/n";
    }

    @Override
    public String visitNestedFor(MiniPascalParser.NestedForContext ctx) {
        return ctx.for_loop().getText() + "/n";
    }

    @Override
    public String visitNestedRepeat(MiniPascalParser.NestedRepeatContext ctx) {
        return this.visitRepeat_loop(ctx.repeat_loop()) + "/n";
    }

    //Expressions to assign or compare
    @Override
    public String visitExprParen(MiniPascalParser.ExprParenContext ctx) {
        return ctx.LPAREN().getText() + ctx.expression().getText() + ctx.RPAREN().getText();
    }

    @Override
    public String visitExprMult(MiniPascalParser.ExprMultContext ctx) {
        String str = ctx.expression(0).getText();

        if (ctx.ASTERISK()!= null)
        {
            str += ctx.ASTERISK().getText();
        }
        else if(ctx.DIV() != null)
        {
            str += ctx.DIV().getText();
        }
        else if(ctx.MOD() != null)
        {
            str += ctx.MOD().getText();
        }
        else if(ctx.SLASH() != null)
        {
            str += ctx.SLASH().getText();
        }

        str += ctx.expression(1).getText();
        return str;
    }

    @Override
    public String visitExprSum(MiniPascalParser.ExprSumContext ctx) {
        String str = ctx.expression(0).getText();

        if (ctx.PLUS()!= null)
        {
            str += ctx.PLUS().getText();
        }
        else if(ctx.MINUS() != null)
        {
            str += ctx.MINUS().getText();
        }

        str += ctx.expression(1).getText();
        return str;
    }

    @Override
    public String visitExprComp(MiniPascalParser.ExprCompContext ctx) {
        String str = ctx.expression(0).getText();

        if (ctx.EQUAL()!= null)
        {
            str += ctx.EQUAL().getText();
        }
        else if(ctx.NOTEQUAL() != null)
        {
            str += ctx.NOTEQUAL().getText();
        }
        else if(ctx.LT() != null)
        {
            str += ctx.LT().getText();
        }
        else if(ctx.LE() != null)
        {
            str += ctx.LE().getText();
        }
        else if(ctx.GT() != null)
        {
            str += ctx.GT().getText();
        }
        else if(ctx.GE() != null)
        {
            str += ctx.GE().getText();
        }

        str += ctx.expression(1).getText();
        return str;
    }

    @Override
    public String visitExprLogic(MiniPascalParser.ExprLogicContext ctx) {
        String str = ctx.expression(0).getText();
        if (ctx.AND()!= null)
        {
            str += ctx.AND().getText();
        }
        else if(ctx.OR() != null)
        {
            str += ctx.OR().getText();
        }

        str += ctx.expression(1).getText();
        return str;
    }

    @Override
    public String visitExprNot(MiniPascalParser.ExprNotContext ctx) {
        return ctx.NOT().getText() + ctx.expression().getText();
    }

    @Override
    public String visitExprNeg(MiniPascalParser.ExprNegContext ctx) {
        return ctx.MINUS().getText() + ctx.expression().getText();
    }

    @Override
    public String visitExprChar(MiniPascalParser.ExprCharContext ctx) {
        return ctx.CHR().getText();
    }

    @Override
    public String visitExprStr(MiniPascalParser.ExprStrContext ctx) {
        return ctx.STR().getText();
    }

    @Override
    public String visitExprBool(MiniPascalParser.ExprBoolContext ctx) {
        if(ctx.TRUE() != null)
        {
            return ctx.TRUE().getText();
        }

        return ctx.FALSE().getText();
    }

    @Override
    public String visitExprInt(MiniPascalParser.ExprIntContext ctx) {
        return ctx.NUM().getText();
    }

    @Override
    public String visitExprID(MiniPascalParser.ExprIDContext ctx) {
        return ctx.ID().getText();
    }

    @Override
    public String visitExprArray(MiniPascalParser.ExprArrayContext ctx) {
        return this.visitArrayExpression(ctx.arrayExpression());
    }

    @Override
    public String visitExprCallFunction(MiniPascalParser.ExprCallFunctionContext ctx) {
        return this.visitCall_function(ctx.call_function());
    }

    @Override
    public String visitArrayExpression(MiniPascalParser.ArrayExpressionContext ctx) {
        String str = ctx.ID().getText() + ctx.LBRACK().getText() + ctx.expression(0).getText();

        if(ctx.COMMA() != null)
        {
            str += ctx.COMMA().getText() + ctx.expression(1).getText();
        }

        str += ctx.RBRACK().getText();

        return str;
    }

    //Simple Statements
    @Override
    public String visitAssigmentVar(MiniPascalParser.AssigmentVarContext ctx) {
        return ctx.ID().getText() + ctx.ASSIGN().getText() + ctx.expression().getText() + ctx.SEMI().getText();
    }

    @Override
    public String visitAssigmentArray(MiniPascalParser.AssigmentArrayContext ctx) {
        String str = ctx.ID().getText() + ctx.LBRACK().getText() + ctx.expression(0).getText();

        if(ctx.COMMA() != null)
        {
            str += ctx.COMMA().getText() + ctx.expression(1).getText();
        }

        str += ctx.RBRACK().getText() + ctx.ASSIGN().getText() + ctx.expression(1).getText() + ctx.SEMI().getText();
        return str;
    }

    @Override
    public String visitRead(MiniPascalParser.ReadContext ctx) {
        String str = ctx.READ().getText() + ctx.LPAREN().getText();
        if(ctx.ID()!=null){
            str += ctx.ID().getText();
        }
        else if(ctx.arrayExpression()!=null){
            str += this.visitArrayExpression(ctx.arrayExpression());
        }
        str +=ctx.RPAREN().getText() +ctx.SEMI().getText();

        return str;
    }

    @Override
    public String visitWriteNormal(MiniPascalParser.WriteNormalContext ctx) {
        String str =  ctx.WRITE().getText() + ctx.LPAREN().getText() + ctx.STR().getText();

        if(ctx.COMMA() != null)
        {
            str += ctx.COMMA().getText();

            if (ctx.ID() != null)
            {
                str += ctx.ID().getText();
            }

            if(ctx.arrayExpression() != null)
            {
                str += this.visitArrayExpression(ctx.arrayExpression());
            }
        }

        str += ctx.RPAREN().getText() + ctx.SEMI().getText();

        return str;
    }

    @Override
    public String visitWriteLine(MiniPascalParser.WriteLineContext ctx) {
        String str =  ctx.WRITELN().getText() + ctx.LPAREN().getText() + ctx.STR().getText();

        if(ctx.COMMA() != null)
        {
            str += ctx.COMMA().getText();

            if (ctx.ID() != null)
            {
                str += ctx.ID().getText();
            }

            if(ctx.arrayExpression() != null)
            {
                str += this.visitArrayExpression(ctx.arrayExpression());
            }

        }

        str += ctx.RPAREN().getText() + ctx.SEMI().getText();

        return str;
    }

    @Override
    public String visitCall_function(MiniPascalParser.Call_functionContext ctx) {
        String str = ctx.ID().getText() + ctx.LPAREN().getText();

        if(ctx.expression() != null)
        {
            str += ctx.expression(0).getText();

            if(ctx.COMMA() != null)
            {
                for(int i = 1; i < ctx.expression().size(); i++)
                {
                    str += ctx.COMMA(i-1).getText() + ctx.expression(i).getText();
                }
            }
        }

        str += ctx.RPAREN().getText() + ctx.SEMI().getText();

        return str;
    }

    //Nested Statements
    @Override
    public String visitIfBody(MiniPascalParser.IfBodyContext ctx) {
        String str = ctx.IF().getText() + ctx.expression().getText() + ctx.THEN().getText() + "/n" + this.visitBody(ctx.body()) + ctx.SEMI();

        if(ctx.else_if_block() != null)
        {
            for(int i = 0; i < ctx.else_if_block().size(); i++)
            {
                str += ctx.else_if_block(i).getText();
            }
        }

        if(ctx.else_block() != null)
        {
            str += ctx.else_block().getText();
        }

        return str;
    }

    @Override
    public String visitIfStat(MiniPascalParser.IfStatContext ctx) {
        String str =  ctx.IF().getText() + ctx.expression().getText() + ctx.THEN().getText() + "/n" + ctx.statement().getText();

        if(ctx.else_if_block() != null)
        {
            for(int i = 0; i < ctx.else_if_block().size(); i++)
            {
                str += ctx.else_if_block(i).getText();
            }
        }

        if(ctx.else_block() != null)
        {
            str += ctx.else_block().getText();
        }

        return str;
    }

    @Override
    public String visitElseIfBody(MiniPascalParser.ElseIfBodyContext ctx) {
        return ctx.ELSEIF().getText() + ctx.expression().getText() + ctx.THEN().getText() + "/n" + this.visitBody(ctx.body()) + ctx.SEMI();
    }

    @Override
    public String visitElseIfStat(MiniPascalParser.ElseIfStatContext ctx) {
        return ctx.ELSEIF().getText() + ctx.expression().getText() + ctx.THEN().getText() + "/n" + ctx.statement().getText();
    }

    @Override
    public String visitElseBody(MiniPascalParser.ElseBodyContext ctx) {
        return ctx.ELSE().getText() + "/n" + this.visitBody(ctx.body()) + ctx.SEMI();
    }

    @Override
    public String visitElseStat(MiniPascalParser.ElseStatContext ctx) {
        return ctx.ELSE().getText() + "/n" + ctx.statement().getText();
    }

    @Override
    public String visitForToBody(MiniPascalParser.ForToBodyContext ctx) {
        return ctx.FOR().getText() + ctx.ID().getText() + ctx.ASSIGN().getText() + ctx.expression(0).getText() + ctx.TO().getText() + ctx.expression(1).getText() + ctx.DO().getText() + "/n" + this.visitBody(ctx.body()) + ctx.SEMI();
    }

    @Override
    public String visitForDownToBody(MiniPascalParser.ForDownToBodyContext ctx) {
        return ctx.FOR().getText() + ctx.ID().getText() + ctx.ASSIGN().getText() + ctx.expression(0).getText() + ctx.DOWNTO().getText() + ctx.expression(1).getText() + ctx.DO().getText() + "/n" + this.visitBody(ctx.body()) + ctx.SEMI();
    }

    @Override
    public String visitForToStat(MiniPascalParser.ForToStatContext ctx) {
        return ctx.FOR().getText() + ctx.ID().getText() + ctx.ASSIGN().getText() + ctx.expression(0).getText() + ctx.TO().getText() + ctx.expression(1).getText() + ctx.DO().getText() + "/n" + ctx.statement().getText();
    }

    @Override
    public String visitForDownToStat(MiniPascalParser.ForDownToStatContext ctx) {
        return ctx.FOR().getText() + ctx.ID().getText() + ctx.ASSIGN().getText() + ctx.expression(0).getText() + ctx.DOWNTO().getText() + ctx.expression(1).getText() + ctx.DO().getText() + "/n" + ctx.statement().getText();
    }

    @Override
    public String visitWhileBody(MiniPascalParser.WhileBodyContext ctx) {
        return ctx.WHILE().getText() + ctx.expression().getText() + ctx.DO().getText() + "/n" + this.visitBody(ctx.body()) + ctx.SEMI();
    }

    @Override
    public String visitWhileStat(MiniPascalParser.WhileStatContext ctx) {
        return ctx.WHILE().getText() + ctx.expression().getText() + ctx.DO().getText() + "/n" + ctx.statement().getText();
    }

    @Override
    public String visitRepeat_loop(MiniPascalParser.Repeat_loopContext ctx) {
        String str =  ctx.REPEAT().getText() + "/n";

        for(int i = 0; i < ctx.statement().size(); i++)
        {
            str += ctx.statement(i).getText();
        }

        str += ctx.UNTIL().getText() + ctx.expression().getText() + ctx.SEMI().getText();

        return str;
    }
}
