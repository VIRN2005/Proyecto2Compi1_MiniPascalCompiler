package org.main;

public class MyVisitor extends MiniPascalBaseVisitor<String> {

    @Override
    public String visitProgram_block(MiniPascalParser.Program_blockContext ctx) {
        StringBuilder sb = new StringBuilder();
        sb.append(ctx.PROGRAM().getText())
                .append(" ")
                .append(ctx.ID().getText())
                .append(ctx.SEMI().getText())
                .append("\n")
                .append(visitSrc(ctx.src()));
        return sb.toString();
    }

    @Override
    public String visitSrc(MiniPascalParser.SrcContext ctx) {
        StringBuilder sb = new StringBuilder();
        sb.append(visitDeclaration(ctx.declaration()))
                .append("\n")
                .append(visitBody(ctx.body()))
                .append(ctx.DOT().getText());
        return sb.toString();
    }

    // Variable Declaration
    @Override
    public String visitDeclaration(MiniPascalParser.DeclarationContext ctx) {
        StringBuilder sb = new StringBuilder();
        if (ctx.var_block() != null) {
            sb.append(visitVar_block(ctx.var_block()));
        }
        if (ctx.function_block() != null) {
            sb.append(visitFunction_block(ctx.function_block()));
        }
        return sb.toString();
    }

    @Override
    public String visitVar_block(MiniPascalParser.Var_blockContext ctx) {
        StringBuilder sb = new StringBuilder();
        sb.append(ctx.VAR().getText())
                .append("\n")
                .append(ctx.variables(0).getText());
        if (ctx.variables().size() > 1) {
            for (int i = 1; i < ctx.variables().size(); i++) {
                sb.append(visit(ctx.variables(i)));
            }
        }
        return sb.toString();
    }

    @Override
    public String visitVariable_declaration(MiniPascalParser.Variable_declarationContext ctx) {
        return visitVarNames(ctx.varNames())
                + ctx.COLON().getText()
                + ctx.type().getText()
                + ctx.SEMI().getText();
    }

    @Override
    public String visitArray_declaration(MiniPascalParser.Array_declarationContext ctx) {
        return ctx.ID().getText()
                + ctx.COLON().getText()
                + visitArray(ctx.array())
                + ctx.SEMI().getText();
    }

    @Override
    public String visitConstant_declaration(MiniPascalParser.Constant_declarationContext ctx) {
        return visitVarNames(ctx.varNames())
                + ctx.constType().getText()
                + ctx.SEMI().getText();
    }

    @Override
    public String visitConstant_initialization(MiniPascalParser.Constant_initializationContext ctx) {
        return ctx.ID().getText()
                + ctx.COLON().getText()
                + ctx.constType().getText()
                + ctx.ASSIGN().getText()
                + ctx.CONST_VAL().getText()
                + ctx.SEMI().getText();
    }

    @Override
    public String visitVariable_initialization(MiniPascalParser.Variable_initializationContext ctx) {
        return visit(ctx.assigment());
    }

    @Override
    public String visitVarNames(MiniPascalParser.VarNamesContext ctx) {
        StringBuilder sb = new StringBuilder();
        sb.append(ctx.ID(0).getText());
        if (ctx.ID().size() > 1) {
            for (int i = 1; i < ctx.ID().size(); i++) {
                sb.append(ctx.COMMA(i - 1).getText())
                        .append(ctx.ID(i).getText());
            }
        }
        return sb.toString();
    }

    @Override
    public String visitFunction_block(MiniPascalParser.Function_blockContext ctx) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ctx.function().size(); i++) {
            sb.append(visitFunction(ctx.function(i)));
        }
        return sb.toString();
    }

    // Variable Types
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
        StringBuilder sb = new StringBuilder();
        sb.append(ctx.ARRAY().getText())
                .append(ctx.LBRACK().getText())
                .append(visitRange(ctx.range(0)));
        if (ctx.COMMA() != null) {
            sb.append(ctx.COMMA().getText())
                    .append(visitRange(ctx.range(1)));
        }
        sb.append(ctx.RBRACK().getText())
                .append(ctx.OF().getText())
                .append(ctx.arrayType().getText());
        return sb.toString();
    }

    @Override
    public String visitRange(MiniPascalParser.RangeContext ctx) {
        return ctx.NUM(0).getText()
                + ctx.DOT(0).getText()
                + ctx.DOT(1).getText()
                + ctx.NUM(1).getText();
    }

    // Functions
    @Override
    public String visitFunction_variables_normal(MiniPascalParser.Function_variables_normalContext ctx) {
        StringBuilder sb = new StringBuilder();
        sb.append(ctx.ID(0).getText());
        if (ctx.COMMA() != null) {
            for (int i = 1; i < ctx.ID().size(); i++) {
                sb.append(ctx.COMMA(i - 1).getText())
                        .append(ctx.ID(i).getText());
            }
        }
        sb.append(ctx.COLON().getText())
                .append(ctx.type().getText());
        return sb.toString();
    }

    @Override
    public String visitFunction_variables_array(MiniPascalParser.Function_variables_arrayContext ctx) {
        StringBuilder sb = new StringBuilder();
        sb.append(ctx.ID(0).getText());
        if (ctx.COMMA() != null) {
            for (int i = 1; i < ctx.ID().size(); i++) {
                sb.append(ctx.COMMA(i - 1).getText())
                        .append(ctx.ID(i).getText());
            }
        }
        sb.append(ctx.COLON().getText())
                .append(visitArray(ctx.array()));
        return sb.toString();
    }

    @Override
    public String visitFunction_variables_const(MiniPascalParser.Function_variables_constContext ctx) {
        StringBuilder sb = new StringBuilder();
        sb.append(ctx.ID(0).getText());
        if (ctx.COMMA() != null) {
            for (int i = 1; i < ctx.ID().size(); i++) {
                sb.append(ctx.COMMA(i - 1).getText())
                        .append(ctx.ID(i).getText());
            }
        }
        sb.append(ctx.COLON().getText())
                .append(ctx.constType().getText());
        return sb.toString();
    }

    @Override
    public String visitFunction(MiniPascalParser.FunctionContext ctx) {
        StringBuilder sb = new StringBuilder();
        sb.append(ctx.FUNCTION().getText())
                .append(" ")
                .append(ctx.ID().getText())
                .append(ctx.LPAREN().getText());
        if (ctx.function_variables().size() > 0) {
            sb.append(visit(ctx.function_variables(0)));
            if (ctx.function_variables().size() > 1) {
                for (int i = 1; i < ctx.function_variables().size(); i++) {
                    sb.append(ctx.SEMI(i - 1).getText())
                            .append(visit(ctx.function_variables(i)));
                }
            }
        }
        sb.append(ctx.RPAREN().getText())
                .append(ctx.COLON().getText())
                .append(ctx.type().getText())
                .append(ctx.SEMI(ctx.SEMI().size() - 2).getText())
                .append("\n");
        if (ctx.var_block() != null) {
            sb.append(visit(ctx.var_block()));
        }
        sb.append(visitBody(ctx.body()))
                .append(ctx.SEMI(ctx.SEMI().size() - 1).getText());
        return sb.toString();
    }

    // Body
    @Override
    public String visitBody(MiniPascalParser.BodyContext ctx) {
        StringBuilder sb = new StringBuilder();
        sb.append(ctx.BEGIN().getText()).append("\n");
        for (int i = 0; i < ctx.statement().size(); i++){
            sb.append(visit(ctx.statement(i))).append("\n");
        }
        sb.append(ctx.END().getText());
        return sb.toString();
    }

    // Statement Types
    @Override
    public String visitSimple_statement(MiniPascalParser.Simple_statementContext ctx) {
        return visit(ctx.simple()) + "\n";
    }

    @Override
    public String visitNested_statement(MiniPascalParser.Nested_statementContext ctx) {
        return visit(ctx.nested()) + "\n";
    }

    @Override
    public String visitSimpleAssigment(MiniPascalParser.SimpleAssigmentContext ctx) {
        return visit(ctx.assigment()) + "\n";
    }

    @Override
    public String visitSimpleRead(MiniPascalParser.SimpleReadContext ctx) {
        return visitRead(ctx.read()) + "\n";
    }

    @Override
    public String visitSimpleWrite(MiniPascalParser.SimpleWriteContext ctx) {
        return visit(ctx.write()) + "\n";
    }

    @Override
    public String visitSimpleCallFunction(MiniPascalParser.SimpleCallFunctionContext ctx) {
        return visit(ctx.call_function()) + "\n";
    }

    @Override
    public String visitNestedIf(MiniPascalParser.NestedIfContext ctx) {
        return visit(ctx.if_block()) + "\n";
    }

    @Override
    public String visitNestedWhile(MiniPascalParser.NestedWhileContext ctx) {
        return visit(ctx.while_loop()) + "\n";
    }

    @Override
    public String visitNestedFor(MiniPascalParser.NestedForContext ctx) {
        return visit(ctx.for_loop()) + "\n";
    }

    @Override
    public String visitNestedRepeat(MiniPascalParser.NestedRepeatContext ctx) {
        return visit(ctx.repeat_loop()) + "\n";
    }

    // Expressions
    @Override
    public String visitExprParen(MiniPascalParser.ExprParenContext ctx) {
        return ctx.LPAREN().getText() + visit(ctx.expression()) + ctx.RPAREN().getText();
    }

    @Override
    public String visitExprMult(MiniPascalParser.ExprMultContext ctx) {
        StringBuilder sb = new StringBuilder();
        sb.append(visit(ctx.expression(0)));
        if (ctx.ASTERISK() != null) {
            sb.append(ctx.ASTERISK().getText());
        } else if (ctx.DIV() != null) {
            sb.append(ctx.DIV().getText());
        } else if (ctx.MOD() != null) {
            sb.append(ctx.MOD().getText());
        } else if (ctx.SLASH() != null) {
            sb.append(ctx.SLASH().getText());
        }
        sb.append(visit(ctx.expression(1)));
        return sb.toString();
    }

    @Override
    public String visitExprSum(MiniPascalParser.ExprSumContext ctx) {
        StringBuilder sb = new StringBuilder();
        sb.append(visit(ctx.expression(0)));
        if (ctx.PLUS() != null) {
            sb.append(ctx.PLUS().getText());
        } else if (ctx.MINUS() != null) {
            sb.append(ctx.MINUS().getText());
        }
        sb.append(visit(ctx.expression(1)));
        return sb.toString();
    }

    @Override
    public String visitExprComp(MiniPascalParser.ExprCompContext ctx) {
        StringBuilder sb = new StringBuilder();
        sb.append(visit(ctx.expression(0)));
        if (ctx.EQUAL() != null) {
            sb.append(ctx.EQUAL().getText());
        } else if (ctx.NOTEQUAL() != null) {
            sb.append(ctx.NOTEQUAL().getText());
        } else if (ctx.LT() != null) {
            sb.append(ctx.LT().getText());
        } else if (ctx.LE() != null) {
            sb.append(ctx.LE().getText());
        } else if (ctx.GT() != null) {
            sb.append(ctx.GT().getText());
        } else if (ctx.GE() != null) {
            sb.append(ctx.GE().getText());
        }
        sb.append(visit(ctx.expression(1)));
        return sb.toString();
    }

    @Override
    public String visitExprLogic(MiniPascalParser.ExprLogicContext ctx) {
        StringBuilder sb = new StringBuilder();
        sb.append(visit(ctx.expression(0)));
        if (ctx.AND() != null) {
            sb.append(ctx.AND().getText());
        } else if (ctx.OR() != null) {
            sb.append(ctx.OR().getText());
        }
        sb.append(visit(ctx.expression(1)));
        return sb.toString();
    }

    @Override
    public String visitExprNot(MiniPascalParser.ExprNotContext ctx) {
        return ctx.NOT().getText() + visit(ctx.expression());
    }

    @Override
    public String visitExprNeg(MiniPascalParser.ExprNegContext ctx) {
        return ctx.MINUS().getText() + visit(ctx.expression());
    }

    @Override
    public String visitExprChar(MiniPascalParser.ExprCharContext ctx) {
        StringBuilder sb = new StringBuilder();
        sb.append(ctx.SINGLE_QUOTE(0).getText());
        if (ctx.ID() != null) {
            sb.append(ctx.ID().getText());
        } else if (ctx.NUM() != null) {
            sb.append(ctx.NUM().getText());
        }
        sb.append(ctx.SINGLE_QUOTE(1).getText());
        return sb.toString();
    }

    @Override
    public String visitExprStr(MiniPascalParser.ExprStrContext ctx) {
        return ctx.STR().getText();
    }

    @Override
    public String visitExprBool(MiniPascalParser.ExprBoolContext ctx) {
        return (ctx.TRUE() != null) ? ctx.TRUE().getText() : ctx.FALSE().getText();
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
        return visit(ctx.arrayExpression());
    }

    @Override
    public String visitExprCallFunction(MiniPascalParser.ExprCallFunctionContext ctx) {
        return visit(ctx.call_function());
    }

    @Override
    public String visitArrayExpression(MiniPascalParser.ArrayExpressionContext ctx) {
        StringBuilder sb = new StringBuilder();
        sb.append(ctx.ID().getText())
                .append(ctx.LBRACK().getText())
                .append(visit(ctx.expression(0)));
        if (ctx.COMMA() != null && ctx.expression().size() > 1) {
            sb.append(ctx.COMMA().getText())
                    .append(visit(ctx.expression(1)));
        }
        sb.append(ctx.RBRACK().getText());
        return sb.toString();
    }

    // Simple Statements
    @Override
    public String visitAssigmentVar(MiniPascalParser.AssigmentVarContext ctx) {
        return ctx.ID().getText()
                + ctx.ASSIGN().getText()
                + visit(ctx.expression())
                + ctx.SEMI().getText();
    }

    @Override
    public String visitAssigmentArray(MiniPascalParser.AssigmentArrayContext ctx) {
        StringBuilder sb = new StringBuilder();
        sb.append(ctx.ID().getText())
                .append(ctx.LBRACK().getText())
                .append(visit(ctx.expression(0)));
        if (ctx.COMMA() != null && ctx.expression().size() > 1) {
            sb.append(ctx.COMMA().getText())
                    .append(visit(ctx.expression(1)));
        }
        sb.append(ctx.RBRACK().getText())
                .append(ctx.ASSIGN().getText())
                .append(visit(ctx.expression(1)))
                .append(ctx.SEMI().getText());
        return sb.toString();
    }

    @Override
    public String visitRead(MiniPascalParser.ReadContext ctx) {
        StringBuilder sb = new StringBuilder();
        sb.append(ctx.READ().getText())
                .append(ctx.LPAREN().getText());
        if (ctx.ID() != null) {
            sb.append(ctx.ID().getText());
        } else if (ctx.arrayExpression() != null) {
            sb.append(visit(ctx.arrayExpression()));
        }
        sb.append(ctx.RPAREN().getText())
                .append(ctx.SEMI().getText());
        return sb.toString();
    }

    @Override
    public String visitWriteNormal(MiniPascalParser.WriteNormalContext ctx) {
        StringBuilder sb = new StringBuilder();
        sb.append(ctx.WRITE().getText())
                .append(ctx.LPAREN().getText())
                .append(ctx.CONST_VAL().getText());
        if (ctx.COMMA() != null) {
            sb.append(ctx.COMMA().getText());
            if (ctx.ID() != null) {
                sb.append(ctx.ID().getText());
            } else if (ctx.expression() != null) {
                sb.append(visit(ctx.expression()));
            } else if (ctx.STR() != null) {
                sb.append(ctx.STR().getText());
            }
        }
        sb.append(ctx.RPAREN().getText())
                .append(ctx.SEMI().getText());
        return sb.toString();
    }

    @Override
    public String visitWriteLine(MiniPascalParser.WriteLineContext ctx) {
        StringBuilder sb = new StringBuilder();
        sb.append(ctx.WRITELN().getText())
                .append(ctx.LPAREN().getText())
                .append(ctx.CONST_VAL().getText());
        if (ctx.COMMA() != null) {
            sb.append(ctx.COMMA().getText());
            if (ctx.ID() != null) {
                sb.append(ctx.ID().getText());
            } else if (ctx.expression() != null) {
                sb.append(visit(ctx.expression()));
            } else if (ctx.STR() != null) {
                sb.append(ctx.STR().getText());
            }
        }
        sb.append(ctx.RPAREN().getText())
                .append(ctx.SEMI().getText());
        return sb.toString();
    }

    @Override
    public String visitCall_function(MiniPascalParser.Call_functionContext ctx) {
        StringBuilder sb = new StringBuilder();
        sb.append(ctx.ID().getText())
                .append(ctx.LPAREN().getText());
        if (ctx.expression() != null && ctx.expression().size() > 0) {
            sb.append(visit(ctx.expression(0)));
            for (int i = 1; i < ctx.expression().size(); i++) {
                sb.append(ctx.COMMA(i - 1).getText())
                        .append(visit(ctx.expression(i)));
            }
        }
        sb.append(ctx.RPAREN().getText())
                .append(ctx.SEMI().getText());
        return sb.toString();
    }

    // Nested Statements
    @Override
    public String visitIfBody(MiniPascalParser.IfBodyContext ctx) {
        StringBuilder sb = new StringBuilder();
        sb.append(ctx.IF().getText())
                .append(ctx.expression().getText())
                .append(ctx.THEN().getText())
                .append("\n")
                .append(visitBody(ctx.body()))
                .append(ctx.SEMI().getText());
        if (ctx.else_if_block() != null) {
            for (int i = 0; i < ctx.else_if_block().size(); i++) {
                sb.append(visit(ctx.else_if_block(i)));
            }
        }
        if (ctx.else_block() != null) {
            sb.append(visit(ctx.else_block()));
        }
        return sb.toString();
    }

    @Override
    public String visitIfStat(MiniPascalParser.IfStatContext ctx) {
        StringBuilder sb = new StringBuilder();
        sb.append(ctx.IF().getText())
                .append(ctx.expression().getText())
                .append(ctx.THEN().getText())
                .append("\n")
                .append(ctx.statement().getText());
        if (ctx.else_if_block() != null) {
            for (int i = 0; i < ctx.else_if_block().size(); i++) {
                sb.append(visit(ctx.else_if_block(i)));
            }
        }
        if (ctx.else_block() != null) {
            sb.append(visit(ctx.else_block()));
        }
        return sb.toString();
    }

    @Override
    public String visitElseIfBody(MiniPascalParser.ElseIfBodyContext ctx) {
        return ctx.ELSEIF().getText()
                + ctx.expression().getText()
                + ctx.THEN().getText()
                + "\n"
                + visitBody(ctx.body())
                + ctx.SEMI().getText();
    }

    @Override
    public String visitElseIfStat(MiniPascalParser.ElseIfStatContext ctx) {
        return ctx.ELSEIF().getText()
                + ctx.expression().getText()
                + ctx.THEN().getText()
                + "\n"
                + ctx.statement().getText();
    }

    @Override
    public String visitElseBody(MiniPascalParser.ElseBodyContext ctx) {
        return ctx.ELSE().getText()
                + "\n"
                + visitBody(ctx.body())
                + ctx.SEMI().getText();
    }

    @Override
    public String visitElseStat(MiniPascalParser.ElseStatContext ctx) {
        return ctx.ELSE().getText()
                + "\n"
                + ctx.statement().getText();
    }

    @Override
    public String visitForToBody(MiniPascalParser.ForToBodyContext ctx) {
        return ctx.FOR().getText()
                + ctx.ID().getText()
                + ctx.ASSIGN().getText()
                + ctx.expression(0).getText()
                + ctx.TO().getText()
                + ctx.expression(1).getText()
                + ctx.DO().getText()
                + "\n"
                + visitBody(ctx.body())
                + ctx.SEMI().getText();
    }

    @Override
    public String visitForDownToBody(MiniPascalParser.ForDownToBodyContext ctx) {
        return ctx.FOR().getText()
                + ctx.ID().getText()
                + ctx.ASSIGN().getText()
                + ctx.expression(0).getText()
                + ctx.DOWNTO().getText()
                + ctx.expression(1).getText()
                + ctx.DO().getText()
                + "\n"
                + visitBody(ctx.body())
                + ctx.SEMI().getText();
    }

    @Override
    public String visitForToStat(MiniPascalParser.ForToStatContext ctx) {
        return ctx.FOR().getText()
                + ctx.ID().getText()
                + ctx.ASSIGN().getText()
                + ctx.expression(0).getText()
                + ctx.TO().getText()
                + ctx.expression(1).getText()
                + ctx.DO().getText()
                + "\n"
                + ctx.statement().getText();
    }

    @Override
    public String visitForDownToStat(MiniPascalParser.ForDownToStatContext ctx) {
        return ctx.FOR().getText()
                + ctx.ID().getText()
                + ctx.ASSIGN().getText()
                + ctx.expression(0).getText()
                + ctx.DOWNTO().getText()
                + ctx.expression(1).getText()
                + ctx.DO().getText()
                + "\n"
                + ctx.statement().getText();
    }

    @Override
    public String visitWhileBody(MiniPascalParser.WhileBodyContext ctx) {
        return ctx.WHILE().getText()
                + ctx.expression().getText()
                + ctx.DO().getText()
                + "\n"
                + visitBody(ctx.body())
                + ctx.SEMI().getText();
    }

    @Override
    public String visitWhileStat(MiniPascalParser.WhileStatContext ctx) {
        return ctx.WHILE().getText()
                + ctx.expression().getText()
                + ctx.DO().getText()
                + "\n"
                + ctx.statement().getText();
    }

    @Override
    public String visitRepeat_loop(MiniPascalParser.Repeat_loopContext ctx) {
        StringBuilder sb = new StringBuilder();
        sb.append(ctx.REPEAT().getText()).append("\n");
        for (int i = 0; i < ctx.statement().size(); i++) {
            sb.append(visit(ctx.statement(i)));
        }
        sb.append(ctx.UNTIL().getText())
                .append(visit(ctx.expression()))
                .append(ctx.SEMI().getText());
        return sb.toString();
    }
}
