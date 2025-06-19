grammar MiniPascal;

options {
    caseInsensitive = true;
}

/*
PENDIENTES
FUNCION(FUNCION)?
*/
program_block: PROGRAM ID SEMI src ;
src : declaration body DOT EOF;
//-----------------------------------------------------Variable declaration

declaration : var_block? function_block?;

var_block : VAR variables (variables)*;


variables : varNames COLON type SEMI                    #variable_declaration                      //Variable declaration
          | ID COLON array SEMI                         #array_declaration                         //Array declaration
          | varNames constType SEMI                     #constant_declaration                      //Constant declaration
          | ID COLON constType ASSIGN CONST_VAL SEMI    #constant_initialization                   //Constant initialization
          | assigment                                   #variable_initialization                   //Variable initialization
          ;

varNames : ID (COMMA ID)*;

function_block : function+;

//-------------Variable Types

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
//-------------Variable Types

//-------------Functions
function_variables : ID (COMMA ID)* COLON type          #function_variables_normal
                   | ID (COMMA ID)* COLON array         #function_variables_array
                   | ID (COMMA ID)* COLON constType     #function_variables_const
                   ;

function : FUNCTION ID LPAREN (function_variables (SEMI function_variables)*)? RPAREN COLON type SEMI
            var_block?
                body SEMI;
//-------------Functions

//-----------------------------------------------------Variable declaration

//-----------------------------------------------------Body

body : BEGIN statement+ END;

//-------------Statement Types

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

//-------------Statement Types


//-------------Expressions to assign or compare

expression : LPAREN expression RPAREN                                           #exprParen      // Parentheses
           | expression (ASTERISK | SLASH | MOD | DIV) expression               #exprMult       // Arithmetic - MUl, DIV, MOD
           | expression (PLUS | MINUS) expression                               #exprSum        // Arithmetic - ADD, SUB
           | expression (EQUAL | NOTEQUAL | LT | LE | GE | GT) expression       #exprComp       // Comparison
           | expression (AND | OR) expression                                   #exprLogic      // Logical
           | NOT expression                                                     #exprNot        // Not
           | MINUS expression                                                   #exprNeg        // Negative
           | CHR                                                                #exprChar       // Char
           | STR                                                                #exprStr        // String
           | (TRUE | FALSE)                                                     #exprBool       // Boolean
           | NUM                                                                #exprInt        // Integer
           | ID                                                                 #exprID         // ID
           | arrayExpression                                                    #exprArray      // Array
           | call_function                                                      #exprCallFunction
           ;

arrayExpression : ID LBRACK expression (COMMA expression)? RBRACK;

//-------------Expressions to assign or compare


//-------------Simple statements

assigment : ID ASSIGN expression SEMI                                               #assigmentVar
          | ID LBRACK expression (COMMA expression)? RBRACK ASSIGN expression SEMI  #assigmentArray
          ;

read : READ LPAREN (ID | arrayExpression) RPAREN SEMI;

write : WRITE LPAREN STR (COMMA (ID | arrayExpression))? RPAREN SEMI       #writeNormal
      | WRITELN LPAREN STR (COMMA (ID | arrayExpression))? RPAREN SEMI    #writeLine
      ;

call_function : ID LPAREN (expression (COMMA expression)*)? RPAREN SEMI?;

//-------------Simple statements


//-------------Nested statements

//if
if_block : IF expression THEN (body SEMI) (else_if_block)* (else_block)?  #ifBody   //if with body
         | IF expression THEN statement (else_if_block)* (else_block)?    #ifStat   //if with statement
         ;

//else if
else_if_block : ELSEIF expression THEN (body SEMI) #elseIfBody //elseIf with body
              | ELSEIF expression THEN statement   #elseIfStat //elseIf with statement
              ;

//else
else_block : ELSE (body SEMI)   #elseBody //ELSE with body
           | ELSE statement     #elseStat //ELSE with statement
           ;

//for
for_loop : FOR ID ASSIGN expression TO expression DO (body SEMI)           #forToBody //for-to with body
         | FOR ID ASSIGN expression DOWNTO expression DO (body SEMI)       #forDownToBody //for-downto with body
         | FOR ID ASSIGN expression TO expression DO statement             #forToStat//for-to with statement
         | FOR ID ASSIGN expression DOWNTO expression DO statement         #forDownToStat//for-downto with statement
         ;

//while
while_loop : WHILE expression DO (body SEMI)       #whileBody     //while with body
           | WHILE expression DO statement         #whileStat     //while with statement
           ;
//repeat
repeat_loop : REPEAT statement+ UNTIL expression SEMI;

//-------------Nested statements

//-------------Reserved words
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
//-------------Reserved words

//-------------Symbols
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
//-------------Symbols


//-----------------------------------------------------Body

ID: [a-z][A-Z0-9_]*;
LETTER: [A-Z];
NUM: [0-9]+;

WS      : (' ' | '\t' | '\n' | '\r')+ -> skip; // Whitespace
STR     : '"' (ESC | ~["\\\r\n\t])* '"' ; // Excludes \r, \n, \t from the string content
CHR     : '\'' (ESC | ~['\\\r\n\t])* '\'' ; // Excludes \r, \n, \t from the character content
ESC     : '\\"'  | '\\\\' | '\\t' | '\\n' | '\\r'; // Escaped characters
CONST_VAL : '\'' (ESC | ~['\\])+ '\'' ;
IGNORE_BLOCK : '{' .*? '}' -> skip ; // Ignore comments