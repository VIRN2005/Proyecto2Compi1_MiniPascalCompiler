grammar MiniPascal;

options {
    caseInsensitive = true;
}

// Program structure
program_block: PROGRAM ID SEMI src ;
src : declaration body DOT EOF;

// Variable and function declarations
declaration : var_block? function_block?;

var_block : VAR variables (variables)*;

variables : varNames COLON type SEMI                    #variable_declaration
          | ID COLON array SEMI                         #array_declaration
          | varNames constType SEMI                     #constant_declaration
          | ID COLON constType ASSIGN CONST_VAL SEMI    #constant_initialization
          | assigment                                   #variable_initialization
          ;

varNames : ID (COMMA ID)*;

function_block : function+;

// Variable types
type : arrayType    #array_Type
     | STRING       #string_Type;

arrayType : INTEGER     #INT
          | CHAR        #CHAR
          | BOOLEAN     #BOOL
          ;

constType : CONSTCHAR   #CONSTCH
          | CONSTSTR    #CONSTSTR
          ;

array : ARRAY LBRACK range (COMMA range)? RBRACK OF arrayType;
range : NUM DOT DOT NUM;

// Functions
function_variables : ID (COMMA ID)* COLON type          #function_variables_normal
                   | ID (COMMA ID)* COLON array         #function_variables_array
                   | ID (COMMA ID)* COLON constType     #function_variables_const
                   ;

function : FUNCTION ID LPAREN (function_variables (SEMI function_variables)*)? RPAREN COLON type SEMI
            var_block?
                body SEMI;

// Program body
body : BEGIN statement+ END;

// Statement types
statement: simple       #simple_statement
         | nested       #nested_statement;

simple : assigment      #simpleAssigment
       | read           #simpleRead
       | write          #simpleWrite
       | call_function  #simpleCallFunction
       ;

nested : if_block       #nestedIf
       | while_loop     #nestedWhile
       | for_loop       #nestedFor
       | repeat_loop    #nestedRepeat
       ;

// Expressions
expression : LPAREN expression RPAREN                                           #exprParen
           | expression (ASTERISK | SLASH | MOD | DIV) expression               #exprMult
           | expression (PLUS | MINUS) expression                               #exprSum
           | expression (EQUAL | NOTEQUAL | LT | LE | GE | GT) expression       #exprComp
           | expression (AND | OR) expression                                   #exprLogic
           | NOT expression                                                     #exprNot
           | MINUS expression                                                   #exprNeg
           | CHR                                                                #exprChar
           | STR                                                                #exprStr
           | (TRUE | FALSE)                                                     #exprBool
           | NUM                                                                #exprInt
           | ID                                                                 #exprID
           | arrayExpression                                                    #exprArray
           | call_function                                                      #exprCallFunction
           ;

arrayExpression : ID LBRACK expression (COMMA expression)? RBRACK;

// Simple statements
assigment : ID ASSIGN expression SEMI                                               #assigmentVar
          | ID LBRACK expression (COMMA expression)? RBRACK ASSIGN expression SEMI  #assigmentArray
          ;

read : READ LPAREN (ID | arrayExpression) RPAREN SEMI;

write : WRITE LPAREN STR (COMMA (ID | arrayExpression))? RPAREN SEMI       #writeNormal
      | WRITELN LPAREN STR (COMMA (ID | arrayExpression))? RPAREN SEMI    #writeLine
      ;

call_function : ID LPAREN (expression (COMMA expression)*)? RPAREN SEMI?;

// Nested statements - IF
if_block : IF expression THEN (body SEMI) (else_if_block)* (else_block)?  #ifBody
         | IF expression THEN statement (else_if_block)* (else_block)?    #ifStat
         ;

else_if_block : ELSEIF expression THEN (body SEMI) #elseIfBody
              | ELSEIF expression THEN statement   #elseIfStat
              ;

else_block : ELSE (body SEMI)   #elseBody
           | ELSE statement     #elseStat
           ;

// Nested statements - FOR
for_loop : FOR ID ASSIGN expression TO expression DO (body SEMI)           #forToBody
         | FOR ID ASSIGN expression DOWNTO expression DO (body SEMI)       #forDownToBody
         | FOR ID ASSIGN expression TO expression DO statement             #forToStat
         | FOR ID ASSIGN expression DOWNTO expression DO statement         #forDownToStat
         ;

// Nested statements - WHILE
while_loop : WHILE expression DO (body SEMI)       #whileBody
           | WHILE expression DO statement         #whileStat
           ;

// Nested statements - REPEAT
repeat_loop : REPEAT statement+ UNTIL expression SEMI;

// Reserved words
PROGRAM : 'program';
VAR : 'var';
BEGIN : 'begin';
END : 'end';
FUNCTION : 'function';
PROCEDURE : 'procedure';
IF : 'if';
THEN : 'then';
ELSE : 'else';
ELSEIF : 'else if';
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

// Symbols
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
SINGLE_QUOTE : '\'';

// Lexer rules
ID: [a-z][A-Z0-9_]*;
LETTER: [A-Z];
NUM: [0-9]+;

WS      : (' ' | '\t' | '\n' | '\r')+ -> skip;
STR     : '"' (ESC | ~["\\\r\n\t])* '"' ;
CHR     : '\'' (ESC | ~['\\\r\n\t])* '\'' ;
ESC     : '\\"'  | '\\\\' | '\\t' | '\\n' | '\\r';
CONST_VAL : '\'' (ESC | ~['\\])+ '\'' ;
IGNORE_BLOCK : '{' .*? '}' -> skip ;