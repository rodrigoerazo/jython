/*
 [The 'BSD licence']
 Copyright (c) 2004 Terence Parr and Loring Craymer
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:
 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
 3. The name of the author may not be used to endorse or promote products
    derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

/** Python 2.3.3 Grammar
 *
 *  Terence Parr and Loring Craymer
 *  February 2004
 *
 *  Converted to ANTLR v3 November 2005 by Terence Parr.
 *
 *  This grammar was derived automatically from the Python 2.3.3
 *  parser grammar to get a syntactically correct ANTLR grammar
 *  for Python.  Then Terence hand tweaked it to be semantically
 *  correct; i.e., removed lookahead issues etc...  It is LL(1)
 *  except for the (sometimes optional) trailing commas and semi-colons.
 *  It needs two symbols of lookahead in this case.
 *
 *  Starting with Loring's preliminary lexer for Python, I modified it
 *  to do my version of the whole nasty INDENT/DEDENT issue just so I
 *  could understand the problem better.  This grammar requires
 *  PythonTokenStream.java to work.  Also I used some rules from the
 *  semi-formal grammar on the web for Python (automatically
 *  translated to ANTLR format by an ANTLR grammar, naturally <grin>).
 *  The lexical rules for python are particularly nasty and it took me
 *  a long time to get it 'right'; i.e., think about it in the proper
 *  way.  Resist changing the lexer unless you've used ANTLR a lot. ;)
 *
 *  I (Terence) tested this by running it on the jython-2.1/Lib
 *  directory of 40k lines of Python.
 *
 *  REQUIRES ANTLR v3
 *
 *  Updated to Python 2.5 by Frank Wierzbicki.
 *
 */

parser grammar PythonPartial;

options {
    tokenVocab=Python;
}

@header { 
package org.python.antlr;
} 

@members {
    private ErrorHandler errorHandler = new FailFastHandler();

    protected Object recoverFromMismatchedToken(IntStream input, int ttype, BitSet follow)
        throws RecognitionException {

        Object o = errorHandler.recoverFromMismatchedToken(this, input, ttype, follow);
        if (o != null) {
            return o;
        }
        return super.recoverFromMismatchedToken(input, ttype, follow);
    }

}

@rulecatch {
catch (RecognitionException e) {
    throw e;
}
}

//single_input: NEWLINE | simple_stmt | compound_stmt NEWLINE
single_input

    : NEWLINE
    | simple_stmt
    | compound_stmt NEWLINE?
    ;

//eval_input: testlist NEWLINE* ENDMARKER
eval_input

    : LEADING_WS? (NEWLINE)* testlist? (NEWLINE)* EOF
    ;

//not in CPython's Grammar file
dotted_attr 
    : NAME
      ( (DOT NAME)+ 
      | 
      )
    ;

//attr is here for Java  compatibility.  A Java foo.getIf() can be called from Jython as foo.if
//     so we need to support any keyword as an attribute.

attr
    : NAME
    | AND
    | AS
    | ASSERT
    | BREAK
    | CLASS
    | CONTINUE
    | DEF
    | DELETE
    | ELIF
    | EXCEPT
    | EXEC
    | FINALLY
    | FROM
    | FOR
    | GLOBAL
    | IF
    | IMPORT
    | IN
    | IS
    | LAMBDA
    | NOT
    | OR
    | ORELSE
    | PASS
    | PRINT
    | RAISE
    | RETURN
    | TRY
    | WHILE
    | WITH
    | YIELD
    ;

//decorator: '@' dotted_name [ '(' [arglist] ')' ] NEWLINE
decorator 

    : AT dotted_attr 
    ( LPAREN
      ( arglist
        
      | 
      )
      RPAREN
    | 
    ) NEWLINE
    ;

//decorators: decorator+
decorators 
    : decorator+
      
    ;

//funcdef: [decorators] 'def' NAME parameters ':' suite
funcdef

    : decorators? DEF NAME parameters COLON suite
    
    ;

//parameters: '(' [varargslist] ')'
parameters 
    : LPAREN 
      (varargslist 
      | 
      )
      RPAREN
    ;

//not in CPython's Grammar file
defparameter 

    : fpdef (ASSIGN test)?
      
    ;

//varargslist: ((fpdef ['=' test] ',')*
//              ('*' NAME [',' '**' NAME] | '**' NAME) |
//              fpdef ['=' test] (',' fpdef ['=' test])* [','])
varargslist 

    : defparameter (options {greedy=true;}:COMMA defparameter)*
      (COMMA
          (STAR NAME (COMMA DOUBLESTAR NAME)?
          | DOUBLESTAR NAME
          )?
      )?
      
    | STAR NAME (COMMA DOUBLESTAR NAME)?
      
    | DOUBLESTAR NAME
      
    ;

//fpdef: NAME | '(' fplist ')'
fpdef
    : NAME 
   
    | (LPAREN fpdef COMMA) => LPAREN fplist RPAREN
   
    | LPAREN fplist RPAREN
   
    ;

//fplist: fpdef (',' fpdef)* [',']
fplist 
    : fpdef
      (options {greedy=true;}:COMMA fpdef)* (COMMA)?
      
    ;

//stmt: simple_stmt | compound_stmt
stmt 
    : simple_stmt
      
    | compound_stmt
      
    ;

//simple_stmt: small_stmt (';' small_stmt)* [';'] NEWLINE
simple_stmt 
    : small_stmt (options {greedy=true;}:SEMI small_stmt)* (SEMI)? (NEWLINE|EOF)
      
    ;

//small_stmt: (expr_stmt | print_stmt  | del_stmt | pass_stmt | flow_stmt |
//             import_stmt | global_stmt | exec_stmt | assert_stmt)
small_stmt : expr_stmt
           | print_stmt
           | del_stmt
           | pass_stmt
           | flow_stmt
           | import_stmt
           | global_stmt
           | exec_stmt
           | assert_stmt
           ;

expr_stmt 

    : ((testlist augassign) => testlist
        ( (augassign yield_expr 
            
          )
        | (augassign testlist
            
          )
        )
    | (testlist ASSIGN) => testlist
        (
        | ((ASSIGN testlist)+
       
          )
        | ((ASSIGN yield_expr)+
       
          )
        )
    | testlist
      
    )
    ;

//augassign: ('+=' | '-=' | '*=' | '/=' | '%=' | '&=' | '|=' | '^=' |
//            '<<=' | '>>=' | '**=' | '//=')
augassign 
    : PLUSEQUAL 
    | MINUSEQUAL 
    | STAREQUAL 
    | SLASHEQUAL 
    | PERCENTEQUAL 
    | AMPEREQUAL 
    | VBAREQUAL 
    | CIRCUMFLEXEQUAL 
    | LEFTSHIFTEQUAL 
    | RIGHTSHIFTEQUAL 
    | DOUBLESTAREQUAL 
    | DOUBLESLASHEQUAL 
    ;

//print_stmt: 'print' ( [ test (',' test)* [','] ] |
//                      '>>' test [ (',' test)+ [','] ] )
print_stmt
    : PRINT 
      (printlist
     
      | RIGHTSHIFT printlist
     
      |
     
      )
      ;

//not in CPython's Grammar file
printlist returns [boolean newline]
    : test (options {k=2;}: COMMA test)* (COMMA)?
    ;

//del_stmt: 'del' exprlist
del_stmt
    : DELETE exprlist
   
    ;

//pass_stmt: 'pass'
pass_stmt
    : PASS
   
    ;

//flow_stmt: break_stmt | continue_stmt | return_stmt | raise_stmt | yield_stmt
flow_stmt
    : break_stmt
    | continue_stmt
    | return_stmt
    | raise_stmt
    | yield_stmt
    ;

//break_stmt: 'break'
break_stmt
    : BREAK
   
    ;

//continue_stmt: 'continue'
continue_stmt
    : CONTINUE 
   
    ;

//return_stmt: 'return' [testlist]
return_stmt
    : RETURN 
      (testlist
     
      |
     
      )
      ;

//yield_stmt: yield_expr
yield_stmt
    : yield_expr 
    ;

//raise_stmt: 'raise' [test [',' test [',' test]]]
raise_stmt
    : RAISE (test (COMMA test
        (COMMA test)?)?)?
   
    ;

//import_stmt: import_name | import_from
import_stmt
    : import_name
    | import_from
    ;

import_name : IMPORT dotted_as_names
            ;

import_from: FROM (DOT* dotted_name | DOT+) IMPORT
              (STAR
              | import_as_names
              | LPAREN import_as_names RPAREN
              )
           ;

import_as_names : import_as_name (COMMA import_as_name)* (COMMA)?
                ;

import_as_name : NAME (AS NAME)?
               ;

dotted_as_name : dotted_name (AS NAME)?
               ;

dotted_as_names : dotted_as_name (COMMA dotted_as_name)*
                ;
dotted_name : NAME (DOT NAME)*
            ;

global_stmt : GLOBAL NAME (COMMA NAME)*
            ;

exec_stmt : EXEC expr (IN test (COMMA test)?)?
          ;

assert_stmt : ASSERT test (COMMA test)?
            ;

compound_stmt : if_stmt
              | while_stmt
              | for_stmt
              | try_stmt
              | with_stmt
              | (decorators? DEF) => funcdef
              | classdef
              ;

if_stmt: IF test COLON suite elif_clause*  (ORELSE COLON suite)?
       ;

elif_clause : ELIF test COLON suite
            ;

while_stmt : WHILE test COLON suite (ORELSE COLON suite)?
           ;

for_stmt : FOR exprlist IN testlist COLON suite (ELSE COLON suite)?
         ;

try_stmt : TRY COLON suite
           ( except_clause+ (ELSE COLON suite)? (FINALLY COLON suite)?
           | FINALLY COLON suite
           )?
         ;

with_stmt: WITH test (with_var)? COLON suite
         ;

with_var: (AS | NAME) expr
        ;

except_clause : EXCEPT (test (COMMA test)?)? COLON suite
              ;

suite : simple_stmt
      | NEWLINE (EOF
                | (DEDENT)+ EOF
                |INDENT (stmt)+ (DEDENT
                                |EOF
                                )
                )
      ;

test: or_test
    ( (IF or_test ELSE) => IF or_test ELSE test)?
    | lambdef
    ;

or_test : and_test (OR and_test)*
        ;

and_test : not_test (AND not_test)*
         ;

not_test : NOT not_test
         | comparison
         ;

comparison: expr (comp_op expr)*
          ;

comp_op : LESS
        | GREATER
        | EQUAL
        | GREATEREQUAL
        | LESSEQUAL
        | ALT_NOTEQUAL
        | NOTEQUAL
        | IN
        | NOT IN
        | IS
        | IS NOT
        ;

expr : xor_expr (VBAR xor_expr)*
     ;

xor_expr : and_expr (CIRCUMFLEX and_expr)*
         ;

and_expr : shift_expr (AMPER shift_expr)*
         ;

shift_expr : arith_expr ((LEFTSHIFT|RIGHTSHIFT) arith_expr)*
           ;

arith_expr: term ((PLUS|MINUS) term)*
          ;

term : factor ((STAR | SLASH | PERCENT | DOUBLESLASH ) factor)*
     ;

factor : PLUS factor
       | MINUS factor
       | TILDE factor
       | power
       | TRAILBACKSLASH
       ;

power : atom (trailer)* (options {greedy=true;}:DOUBLESTAR factor)?
      ;

atom : LPAREN 
       ( yield_expr
       | testlist_gexp
       )?
       RPAREN
     | LBRACK (listmaker)? RBRACK
     | LCURLY (dictmaker)? RCURLY
     | BACKQUOTE testlist BACKQUOTE
     | NAME
     | INT
     | LONGINT
     | FLOAT
     | COMPLEX
     | (STRING)+
     | STRINGPART
     ;

listmaker : test 
            ( list_for
            | (options {greedy=true;}:COMMA test)*
            ) (COMMA)?
          ;

testlist_gexp
    : test ( (options {k=2;}: COMMA test)* (COMMA)?
           | gen_for
           )
           
    ;

lambdef: LABMDA (varargslist)? COLON test
       ;

trailer : LPAREN (arglist)? RPAREN
        | LBRACK subscriptlist RBRACK
        | DOT NAME
        ;

subscriptlist : subscript (options {greedy=true;}:COMMA subscript)* (COMMA)?
              ;

subscript : DOT DOT DOT
          | test (COLON (test)? (sliceop)?)?
          | COLON (test)? (sliceop)?
          ;

sliceop : COLON (test)?
        ;

exprlist : expr (options {k=2;}: COMMA expr)* (COMMA)?
         ;

testlist
    : test (options {k=2;}: COMMA test)* (COMMA)?
    ;

dictmaker : test COLON test (options {k=2;}:COMMA test COLON test)* (COMMA)?
          ;

classdef: decorators (CLASS NAME (LPAREN testlist? RPAREN)? COLON suite)?
        | CLASS NAME (LPAREN testlist? RPAREN)? COLON suite
        ;

arglist : argument (COMMA argument)*
          ( COMMA
            ( STAR test (COMMA DOUBLESTAR test)?
            | DOUBLESTAR test
            )?
          )?
        |   STAR test (COMMA DOUBLESTAR test)?
        |   DOUBLESTAR test
        ;

argument : test ( (ASSIGN test) | gen_for)?
         ;

list_iter : list_for
          | list_if
          ;

list_for : FOR exprlist IN testlist (list_iter)?
         ;

list_if : IF test (list_iter)?
        ;

gen_iter: gen_for
        | gen_if
        ;

gen_for: FOR exprlist IN or_test gen_iter?
       ;

gen_if: IF test gen_iter?
      ;

yield_expr : YIELD testlist?
           ;
//XXX:
//testlist1: test (',' test)*

