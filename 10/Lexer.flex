import Tokens.*;
%%

%class Lexer
%unicode
%line
%column
%function getNextToken
%type Token
%public

InputCharacter = [^\r\n]
LineTerminator = [\r|\n|\r\n]
WhiteSpace     = {LineTerminator} | [ \t\n]
ID = [_a-zA-Z][_a-zA-Z0-9]*
INTEGER = 0|[1-9][0-9]*
STRINGCONSTANT = \".*\"

/* comments */
Comment = {TraditionalComment} | {EndOfLineComment} | {DocumentationComment}
TraditionalComment   = "/*" [^*] ~"*/" | "/*" "*"+ "/"
// Comment can be the last line of the file, without line terminator.
EndOfLineComment     = "//" {InputCharacter}* {LineTerminator}?
DocumentationComment = "/**" {CommentContent} "*"+ "/"
CommentContent       = ( [^*] | \*+ [^/*] )*

%%

//Symbols
"{"                  { return new OpenBraceToken(yyline, yycolumn); }
"}"                  { return new CloseBraceToken(yyline, yycolumn); }
"("                  { return new OpenParenToken(yyline, yycolumn); }
")"                  { return new CloseParenToken(yyline, yycolumn); }
"["                  { return new OpenBracketToken(yyline, yycolumn); }
"]"                  { return new CloseBracketToken(yyline, yycolumn); }
"\."                 { return new DotToken(yyline, yycolumn); }
","                  { return new CommaToken(yyline, yycolumn); }
";"                  { return new SemicolonToken(yyline, yycolumn); }
"+"                  { return new PlusToken(yyline, yycolumn); }
"-"                  { return new MinusToken(yyline, yycolumn); }
"*"                  { return new AsteriskToken(yyline, yycolumn); }
"/"                  { return new DivisionToken(yyline, yycolumn); }
"&"                  { return new AndToken(yyline, yycolumn); }
"|"                  { return new OrToken(yyline, yycolumn); }
"<"                  { return new LessToken(yyline, yycolumn); }
">"                  { return new GreaterToken(yyline, yycolumn); }
"="                  { return new EqualToken(yyline, yycolumn); }
"~"                  { return new TildeToken(yyline, yycolumn); }

//Keywords
"class"             { return new ClassToken(yyline, yycolumn); }
"constructor"       { return new ConstructorToken(yyline, yycolumn); }
"function"          { return new FunctionToken(yyline, yycolumn); }
"method"            { return new MethodToken(yyline, yycolumn); }
"field"             { return new FieldToken(yyline, yycolumn); }
"static"            { return new StaticToken(yyline, yycolumn); }
"var"               { return new VarToken(yyline, yycolumn); }
"int"               { return new IntToken(yyline, yycolumn); }
"char"              { return new CharToken(yyline, yycolumn); }
"boolean"           { return new BooleanToken(yyline, yycolumn); }
"void"              { return new VoidToken(yyline, yycolumn); }
"true"              { return new TrueToken(yyline, yycolumn); }
"false"             { return new FalseToken(yyline, yycolumn); }
"null"              { return new NullToken(yyline, yycolumn); }
"this"              { return new ThisToken(yyline, yycolumn); }
"let"               { return new LetToken(yyline, yycolumn); }
"do"                { return new DoToken(yyline, yycolumn); }
"if"                { return new IfToken(yyline, yycolumn); }
"else"              { return new ElseToken(yyline, yycolumn); }
"while"             { return new WhileToken(yyline, yycolumn); }
"return"            { return new ReturnToken(yyline, yycolumn); }

{INTEGER}	     { return new IntegerToken(yytext(),yyline, yycolumn); }
{ID}		     { return new IdToken(yytext(), yyline, yycolumn); }
{STRINGCONSTANT} { return new  StringToken(yytext(), yyline, yycolumn);}
{EndOfLineComment} { /* Ignore */ }
{Comment}        { /* Ignore */ }
{WhiteSpace}     { /* Ignore */ }

<<EOF>>              { return new EndofStringToken(yyline, yycolumn); }
