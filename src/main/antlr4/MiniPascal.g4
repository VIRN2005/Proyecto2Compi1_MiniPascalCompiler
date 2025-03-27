grammar MiniPascal;

options {
    caseInsensitive = true;
}

program_block
    : PROGRAM ID SEMI declaration? body DOT EOF
    ;

declaration
    : var_block? function_block?
    ;

var_block
    : VAR variables+
    ;

variables
    : varNames COLON type SEMI                              #variable_declaration
    | varNames COLON type ASSIGN expression SEMI            #variable_initialization
    | varNames COLON array SEMI                             #array_declaration
    | varNames COLON constType ASSIGN CONST_VAL SEMI        #constant_initialization
    ;

varNames
    : ID (COMMA ID)*
    ;

type
    : INTEGER
    | CHAR
    | BOOLEAN
    | STRING
    ;

array
    : ARRAY LBRACK range (COMMA range)? RBRACK OF type
    ;

range
    : NUM DOT DOT NUM
    ;

constType
    : CONSTCHAR
    | CONSTSTR
    ;

function_block
    : (function | procedure)+
    ;

function
    : FUNCTION ID LPAREN (function_variables (SEMI function_variables)*)? RPAREN COLON type SEMI
      var_block? body SEMI
    ;

procedure
    : PROCEDURE ID LPAREN (function_variables (SEMI function_variables)*)? RPAREN SEMI
      var_block? body SEMI
    ;

function_variables
    : varNames COLON type
    ;

body
    : BEGIN statement+ END
    ;

statement
    : simple_statement
    | nested_statement
    ;

simple_statement
    : assigment
    | read
    | write
    | call_function
    ;

nested_statement
    : if_block
    | while_loop
    | for_loop
    | repeat_loop
    ;

assigment
    : ID ASSIGN expression SEMI
    | ID LBRACK expression (COMMA expression)? RBRACK ASSIGN expression SEMI
    ;

read
    : READ LPAREN (ID | arrayExpression) RPAREN SEMI
    ;

write
    : WRITE LPAREN expression (COMMA expression)* RPAREN SEMI
    | WRITELN LPAREN expression (COMMA expression)* RPAREN SEMI
    ;

call_function
    : ID LPAREN (expression (COMMA expression)*)? RPAREN SEMI?
    ;

if_block
    : IF expression THEN statement (ELSE statement)?
    ;

while_loop
    : WHILE expression DO statement
    ;

for_loop
    : FOR ID ASSIGN expression TO expression DO statement
    | FOR ID ASSIGN expression DOWNTO expression DO statement
    ;

repeat_loop
    : REPEAT statement+ UNTIL expression SEMI
    ;

expression
    : expression (ASTERISK | SLASH | MOD | DIV) expression   #exprMult
    | expression (PLUS | MINUS) expression                   #exprSum
    | expression (EQUAL | NOTEQUAL | LT | LE | GE | GT) expression #exprComp
    | expression (AND | OR) expression                       #exprLogic
    | NOT expression                                         #exprNot
    | MINUS expression                                       #exprNeg
    | LPAREN expression RPAREN                               #exprParen
    | NUM                                                    #exprNum
    | ID                                                     #exprID
    | arrayExpression                                        #exprArray
    | call_function                                          #exprCallFunction
    | STR                                                    #exprStr
    | TRUE                                                   #exprBoolTrue
    | FALSE                                                  #exprBoolFalse
    ;

arrayExpression
    : ID LBRACK expression (COMMA expression)? RBRACK
    ;

PROGRAM : 'program';
VAR : 'var';
BEGIN : 'begin';
END : 'end';
FUNCTION : 'function';
PROCEDURE : 'procedure';
IF : 'if';
THEN : 'then';
ELSE : 'else';
FOR : 'for';
TO : 'to';
DOWNTO : 'downto';
DO : 'do';
WHILE : 'while';
REPEAT : 'repeat';
UNTIL : 'until';
READ : 'read';
WRITE : 'write';
WRITELN : 'writeln';
MOD : 'mod';
DIV : 'div';
AND : 'and';
OR : 'or';
NOT : 'not';
TRUE : 'true';
FALSE : 'false';
ARRAY : 'array';
OF : 'of';
INTEGER : 'integer';
CHAR : 'char';
BOOLEAN : 'boolean';
STRING : 'string';
CONSTCHAR : 'constchar';
CONSTSTR : 'conststr';

COLON : ':';
SEMI : ';';
COMMA : ',';
LPAREN : '(';
RPAREN : ')';
LBRACK : '[';
RBRACK : ']';
PLUS : '+';
MINUS : '-';
ASTERISK : '*';
SLASH : '/';
EQUAL : '=';
NOTEQUAL : '<>';
LT : '<';
LE : '<=';
GT : '>';
GE : '>=';
ASSIGN : ':=';
DOT : '.';

ID : [a-zA-Z][a-zA-Z0-9_]*;
NUM : [0-9]+;
STR : '"' .*? '"';
WS : [ \t\r\n]+ -> skip;
COMMENT : '{' .*? '}' -> skip;
LINE_COMMENT : '//' ~[\r\n]* -> skip;