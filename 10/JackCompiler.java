import java.io.StringReader;
import java.io.IOException;
import Tokens.*;

public class JackCompiler{
    private Lexer cel = null;
	private Token cc = null;
	private String xml = "";
	
	public String getXml(){
		return xml;
	}
	public JackCompiler(){}
	
	

    public void parser(String str) throws JackParserException, IOException {
	StringReader sr;
	try{
	    sr = new StringReader(str);
	    cel = new Lexer(sr);
		
	} catch (Exception e){
	    System.err.println(e);
	    System.exit(1);
	}
	
	cc = cel.getNextToken();
	this.xml += "<class>\n";
	clase();
	this.xml += "</class>";

	if (cc instanceof EndofStringToken){

	    cc = null;
	    
	    return;
	}
	
	if (cc != null){
	 throw new JackParserException("Token unexpected ", cc);
	}
	return;	    
	}

	public void clase() throws JackParserException, IOException {
		if ( cc instanceof ClassToken){
			this.xml += "<keyword> class </keyword>\n";
			cc = cel.getNextToken();
			if (cc instanceof IdToken){
				this.xml += "  <identifier> "+ cc.getId()+ " </identifier>\n";
				cc = cel.getNextToken();
				if ( cc instanceof OpenBraceToken){
					this.xml += "  <symbol> { </symbol>\n";
					cc = cel.getNextToken();
					while( cc instanceof StaticToken | cc instanceof FieldToken ){
					this.xml += "  <classVarDec>\n";
					classVarDec();
					this.xml += "  </classVarDec>\n";
					}
					while(cc instanceof ConstructorToken | cc instanceof FunctionToken | cc instanceof MethodToken){
					this.xml += "  <subroutineDec>\n";
					subroutineDec();
					this.xml += "  </subroutineDec>\n";
					}
					if ( cc instanceof CloseBraceToken ){
						this.xml += "  <symbol> } </symbol>\n";
						cc = cel.getNextToken();
						return;
					} else throw new JackParserException("Error '}' excepted. ", cc);
				} else throw new JackParserException("Error '{' excepted. ", cc);
			} else throw new JackParserException("Error identifier excepted. ", cc);
		} else throw new JackParserException("Error 'Class' excepted. ", cc);
	}

	public void classVarDec() throws JackParserException, IOException{
		if ( cc instanceof StaticToken | cc instanceof FieldToken){
			this.xml += "<keyword> "+ cc.getId() +" </keyword>\n";
			cc = cel.getNextToken();
			type();
			if ( cc instanceof IdToken){
				varName();
				while (cc instanceof CommaToken){
					this.xml += "<symbol> , </symbol>\n";
					cc = cel.getNextToken();
					if ( cc instanceof IdToken){
						varName();
					} else throw new JackParserException("Error identifier excepted. ", cc);
				}
				if (cc instanceof SemicolonToken){
					this.xml += "<symbol> ; </symbol>\n";
					cc = cel.getNextToken();
					return;
				} else throw new JackParserException("Error ';' excepted. ", cc);
			} else throw new JackParserException("Error identifier excepted. ", cc);
		} return; //else throw new JackParserException("Error 'static, field' excepted. ", cc);
	}

	public void type() throws JackParserException, IOException {
		if (cc instanceof IntToken | cc instanceof BooleanToken | cc instanceof CharToken){
			this.xml += "  <keyword> "+ cc.getId()+ " </keyword>\n";
			cc = cel.getNextToken();
			return;
		}else if(cc instanceof IdToken){
			this.xml += "  <identifier> "+ cc.getId()+ " </identifier>\n";
			cc = cel.getNextToken();
			return;
		}
		 else throw new JackParserException("Error 'Int', 'Boolean', 'Char' excepted. ", cc);
	}

	public void subroutineDec() throws JackParserException, IOException {
		if ( cc instanceof ConstructorToken | cc instanceof FunctionToken | cc instanceof MethodToken){
			this.xml += "  <keyword> "+ cc.getId()+ " </keyword>\n";
			cc = cel.getNextToken();
			if ( cc instanceof VoidToken| cc instanceof IntToken | cc instanceof BooleanToken | cc instanceof CharToken | cc instanceof IdToken){
				if(cc instanceof IdToken) this.xml += "  <identifier> "+ cc.getId()+ " </identifier>\n";
				else { this.xml += "  <keyword> "+ cc.getId()+ " </keyword>\n";	}
				cc = cel.getNextToken();
				if ( cc instanceof IdToken){
					this.xml += "  <identifier> "+ cc.getId()+ " </identifier>\n";
					cc = cel.getNextToken();
					if (cc instanceof OpenParenToken){
						this.xml += "<symbol> ( </symbol>\n";
						cc = cel.getNextToken();
						this.xml += "<parameterList>\n";
						parameterList();
						this.xml += "</parameterList>\n";
						if( cc instanceof CloseParenToken){
							this.xml += "<symbol> ) </symbol>\n";
							cc = cel.getNextToken();
							this.xml += "<subroutineBody>\n";
							subroutineBody();
							this.xml += "</subroutineBody>\n";
						} else throw new JackParserException("Error ')' excepted. ", cc); 
					} else throw new JackParserException("Error '(' excepted. ", cc);
				} else throw new JackParserException("Error identifier excepted. ", cc);
			} else throw new JackParserException("Error 'Void','Int', 'Boolean', 'Char' excepted. ", cc);
		} else throw new JackParserException("Error 'constructor', 'funcion', 'method'" , cc);
	}

	public void parameterList() throws JackParserException, IOException{
		if (cc instanceof IntToken | cc instanceof BooleanToken | cc instanceof CharToken | cc instanceof IdToken){
			type();
			if(cc instanceof IdToken){
				this.xml += "  <identifier> "+ cc.getId()+ " </identifier>\n";
				cc = cel.getNextToken();
				while(cc instanceof CommaToken){
					this.xml += "<symbol> , </symbol>\n";
					cc = cel.getNextToken();
					type();
					varName();
				}
				return;
			} else throw new JackParserException("Error identifier excepted. ", cc);
		}
		else{
			return;
		}
	}

	public void subroutineBody() throws JackParserException, IOException {
		if (cc instanceof OpenBraceToken){
			this.xml += "<symbol> { </symbol>\n";
			cc = cel.getNextToken();
			while (cc instanceof VarToken){
				this.xml += "<varDec>\n";
				varDec();
				this.xml += "</varDec>\n";
			}
			this.xml += "<statements>\n";
			statements();
			this.xml += "</statements>\n";
			if ( cc instanceof CloseBraceToken){
				this.xml += "<symbol> } </symbol>\n";
				cc = cel.getNextToken();
				return;
			} else throw new JackParserException("Error '}' excepted. ", cc);
		} else throw new JackParserException("Error '{' excepted. ", cc);
	}

	public void varDec() throws JackParserException, IOException{
		if (cc instanceof VarToken){
			this.xml += "<keyword> var </keyword>\n";
			cc = cel.getNextToken();
			type();

			varName();
			while(cc instanceof CommaToken){
				this.xml += "<symbol> , </symbol>\n";
				cc = cel.getNextToken();
				varName();
			}
			if (cc instanceof SemicolonToken){
				this.xml += "<symbol> ; </symbol>\n";
				cc = cel.getNextToken();
				return;
			}  else throw new JackParserException("Error ';' excepted. ", cc);
		}
	}

	public void statements() throws JackParserException, IOException {
		while(cc instanceof LetToken | cc instanceof IfToken | cc instanceof WhileToken | cc instanceof DoToken | cc instanceof ReturnToken){
			if (cc instanceof LetToken){
				this.xml += "<letStatement>\n";
				letStatement();
				this.xml += "</letStatement>\n";
			}
			else if (cc instanceof IfToken){
				this.xml += "<ifStatement>\n";
				ifStatement();
				this.xml += "</ifStatement>\n";
			}
			else if (cc instanceof WhileToken){
				this.xml += "<whileStatement>\n";
				whileStatement();
				this.xml += "</whileStatement>\n";
			}
			else if (cc instanceof DoToken){
				this.xml += "<doStatement>\n";
				doStatement();
				this.xml += "</doStatement>\n";
			}
			else if (cc instanceof ReturnToken){
				this.xml += "<returnStatement>\n";
				returnStatement();
				this.xml += "</returnStatement>\n";
			}
		}
	}

	public void letStatement() throws JackParserException, IOException {
		this.xml += "<keyword> let </keyword>\n";
		cc = cel.getNextToken();
		if ( cc instanceof IdToken){
			this.xml += "  <identifier> "+ cc.getId()+ " </identifier>\n";
			cc = cel.getNextToken();
			varName();
			if (cc instanceof EqualToken){
				this.xml += "<symbol> = </symbol>\n";
				cc = cel.getNextToken();
				this.xml += "<expression>\n";
				expression();
				this.xml += "</expression>\n";
				if (cc instanceof SemicolonToken){
					this.xml += "<symbol> ; </symbol>\n";
					cc = cel.getNextToken();
					return;
				} else throw new JackParserException("Error ';' excepted. ", cc);
			} else throw new JackParserException("Error '=' excepted. ", cc);
		} else throw new JackParserException("Error VarName excepted. ", cc);
	}
	
	public void ifStatement() throws JackParserException, IOException {
		this.xml += "<keyword> if </keyword>\n";
		cc = cel.getNextToken();
		if (cc instanceof OpenParenToken){
			this.xml += "<symbol> ( </symbol>\n";
			cc = cel.getNextToken();
			this.xml += "<expression>\n";
			expression();
			this.xml += "</expression>\n";
			if (cc instanceof CloseParenToken){
				this.xml += "<symbol> ) </symbol>\n";
				cc = cel.getNextToken();
				if (cc instanceof OpenBraceToken){
					this.xml += "<symbol> { </symbol>\n";
					cc = cel.getNextToken();
					this.xml += "<statements>\n";
					statements();
					this.xml += "</statements>\n";
					if (cc instanceof CloseBraceToken){
						this.xml += "<symbol> } </symbol>\n";
						cc = cel.getNextToken();
						if (cc instanceof ElseToken){
							this.xml += "<keyword> else </keyword>\n";
							cc = cel.getNextToken();
							if (cc instanceof OpenBraceToken){
								this.xml += "<symbol> { </symbol>\n";
								cc = cel.getNextToken();
								this.xml += "<statements>\n";
								statements();
								this.xml += "</statements>\n";
								if (cc instanceof CloseBraceToken){
									this.xml += "<symbol> } </symbol>\n";
									cc = cel.getNextToken();
									return;
								}else throw new JackParserException("Error '}' excepted. ", cc);
						} else throw new JackParserException("Error '{' excepted. ", cc);
					} return;
				} else throw new JackParserException("Error '}' excepted. ", cc);
			} else throw new JackParserException("Error '{' excepted. ", cc);
		} else throw new JackParserException("Error ')' excepted. ", cc);
	} else throw new JackParserException("Error '(' excepted. ", cc);
}

	public void whileStatement() throws JackParserException, IOException {
		this.xml += "<keyword> while </keyword>\n";
		cc = cel.getNextToken();
		if (cc instanceof OpenParenToken){
			this.xml += "<symbol> ( </symbol>\n";
			cc = cel.getNextToken();
			this.xml += "<expression>\n";
			expression();
			this.xml += "</expression>\n";
			if (cc instanceof CloseParenToken){
				this.xml += "<symbol> ) </symbol>\n";
				cc = cel.getNextToken();
				if (cc instanceof OpenBraceToken){
					this.xml += "<symbol> { </symbol>\n";
					cc = cel.getNextToken();
					this.xml += "<statements>\n";
					statements();
					this.xml += "</statements>\n";
					if (cc instanceof CloseBraceToken){
						this.xml += "<symbol> } </symbol>\n";
						cc = cel.getNextToken();
						return;					
					} else throw new JackParserException("Error '}' excepted. ", cc);
				} else throw new JackParserException("Error '{' excepted. ", cc);
			} else throw new JackParserException("Error '(' excepted. ", cc);
		} else throw new JackParserException("Error ')' excepted. ", cc);
	}

	public void doStatement()  throws JackParserException, IOException {
		this.xml += "<keyword> do </keyword>\n";
		cc = cel.getNextToken();
		if (cc instanceof IdToken){
			this.xml += "<identifier> " +cc.getId()+ " </identifier>\n";
			cc = cel.getNextToken();
			subroutineCall();
			if (cc instanceof SemicolonToken){
				this.xml += "<symbol> ; </symbol>\n";
				cc = cel.getNextToken();
				return;
			} else throw new JackParserException("Error ';' excepted. ", cc);
		} else throw new JackParserException("Error identifier excepted. ", cc);
	}

	public void returnStatement() throws JackParserException, IOException {
		this.xml += "<keyword> return </keyword>\n";
		cc = cel.getNextToken();
		if (cc instanceof SemicolonToken){
			this.xml += "<symbol> ; </symbol>\n";
			cc = cel.getNextToken();
			return;
		} 
		else{
			this.xml += "<expression>\n";
			expression();
			this.xml += "</expression>\n";
			if (cc instanceof SemicolonToken){
				this.xml += "<symbol> ; </symbol>\n";
				cc = cel.getNextToken();
				return;
			} else throw new JackParserException("Error ';' excepted. ", cc);
		}
	}
	
	public void expression() throws JackParserException, IOException {
		this.xml += "<term>\n";
		term();
		this.xml += "</term>\n";
		while (cc instanceof PlusToken | cc instanceof MinusToken | cc instanceof AsteriskToken | cc instanceof DivisionToken | cc instanceof AndToken | cc instanceof OrToken | cc instanceof LessToken | cc instanceof GreaterToken | cc instanceof EqualToken){
			this.xml += "<symbol> "+ cc.getId() +" </symbol>\n";
			this.xml += "<term>\n";
			cc = cel.getNextToken();
			term();
			this.xml += "</term>\n";
		} return;
	}

	public void term() throws JackParserException, IOException {
		
		if (cc instanceof IntegerToken | cc instanceof StringToken | cc instanceof KeyWordsToken ){
			if(cc instanceof StringToken) this.xml += "<stringConstant> "+ cc.getId().substring(1, cc.getId().length() - 1) +" </stringConstant>\n";
			else if (cc instanceof IntegerToken) this.xml += "<integerConstant> "+ cc.getId() +" </integerConstant>\n";
			else{ this.xml += "<keyword> "+ cc.getId() +" </keyword>\n";}
			cc = cel.getNextToken();
			return;
		} else if (cc instanceof MinusToken | cc instanceof TildeToken){
			this.xml += "<symbol> "+ cc.getId() +" </symbol>\n";
			this.xml += "<term>\n";
			cc = cel.getNextToken();
			term();
			this.xml += "</term>\n";
		}
		else if ( cc instanceof OpenParenToken){
			this.xml += "<symbol> ( </symbol>\n";
			cc = cel.getNextToken();
			this.xml += "<expression>\n";
			expression();
			this.xml += "</expression>\n";
			if (cc instanceof CloseParenToken){
				this.xml += "<symbol> ) </symbol>\n";
				cc = cel.getNextToken();
				return;
			} else throw new JackParserException("Error, ')' expected ", cc);
		}
		else if (cc instanceof IdToken){
			this.xml += "<identifier> "+cc.getId()+" </identifier>\n";
			NameOrCall();
		} else throw new JackParserException("Error, expected: Integer, String, Keyword,'-', '~'. Found: ", cc);
	}

	public void NameOrCall() throws JackParserException, IOException {
		cc = cel.getNextToken();
		if (cc instanceof OpenBracketToken){
			varName();
		}
		else if (cc instanceof OpenParenToken | cc instanceof DotToken){
			subroutineCall();
		}
		return;	
	}

	public void varName() throws JackParserException, IOException {
		if ( cc instanceof OpenBracketToken){
			this.xml += "<symbol> [ </symbol>\n";
			cc = cel.getNextToken();
			this.xml += "<expression>\n";
			expression();
			this.xml += "</expression>\n";
			if(cc instanceof CloseBracketToken) {
				this.xml += "<symbol> ] </symbol>\n";
				cc = cel.getNextToken();
				return;
			} else throw new JackParserException("Error ']' expected, found: ", cc);
		} else if (cc instanceof IdToken){
			this.xml += "<identifier> " +cc.getId()+ " </identifier>\n";
			cc = cel.getNextToken();
			return;
		}
	}

	public void subroutineCall() throws JackParserException, IOException {
		
		if ( cc instanceof OpenParenToken){
			this.xml += "<symbol> ( </symbol>\n";
			cc = cel.getNextToken();
			this.xml += "<expressionList>\n";
			expressionList();
			this.xml += "</expressionList>\n";
			if(cc instanceof CloseParenToken){
				this.xml += "<symbol> ) </symbol>\n";
				cc = cel.getNextToken();
				return;
			}  
		} else if ( cc instanceof DotToken){
			this.xml += "<symbol> . </symbol>\n";
			cc = cel.getNextToken();
			if (cc instanceof IdToken){
				this.xml += "<identifier> " +cc.getId()+ " </identifier>\n";
				cc = cel.getNextToken();
				if ( cc instanceof OpenParenToken){
					this.xml += "<symbol> ( </symbol>\n";
					cc = cel.getNextToken();
					this.xml += "<expressionList>\n";
					expressionList();
					this.xml += "</expressionList>\n";
					if ( cc instanceof CloseParenToken){
						this.xml += "<symbol> ) </symbol>\n";
						cc = cel.getNextToken();
						return;
					} else throw new JackParserException("Error ')' expected, found: ", cc);
				} else throw new JackParserException("Error '(' expected, found: ", cc);
			} else throw new JackParserException("Error Identifier expected, found: ", cc);
	} else throw new JackParserException("Error identifier, '.' expected, found: ", cc);
}

	public void expressionList()  throws JackParserException, IOException{
		if (cc instanceof IntegerToken | cc instanceof StringToken | cc instanceof KeyWordsToken |
		cc instanceof MinusToken | cc instanceof TildeToken | cc instanceof OpenParenToken | cc instanceof IdToken ){
			this.xml += "<expression>\n";
			expression();
			this.xml += "</expression>\n";
			while(cc instanceof CommaToken){
				this.xml += "<symbol> , </symbol>\n";
				cc = cel.getNextToken();
				this.xml += "<expression>\n";
				expression();
				this.xml += "</expression>\n";
			} 
		}
		return;	
	}	
}