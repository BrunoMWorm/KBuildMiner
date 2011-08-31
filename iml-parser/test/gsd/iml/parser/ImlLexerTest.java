/*
 * Copyright (c) 2011 Leonardo Passos <lpassos@gsd.uwaterloo.ca>
 *
 * This file is part of iml-parser.
 *
 * Iml-parser is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Iml-parser is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Iml-parser.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gsd.iml.parser;

import gsd.iml.test.util.TestFile;
import java.io.FileReader;
import java.io.StringReader;

import junit.framework.TestCase;

import org.junit.Test;

/**
 *
 * @author leonardo
 */


public class ImlLexerTest extends TestCase {

    @Test
    public void testPlainTokens() {
        FileReader reader = null;
        int[] expectedToken = {
                Token.ACTIVE_IF,
                Token.AND,
                Token.BOOL,
                Token.BOOLDATA,
                Token.BT_AND,
                Token.BT_LEFT,
                Token.BT_OR,
                Token.BT_RIGHT,
                Token.BT_XOR,
                Token.CALCULATED,
                Token.COLON,
                Token.COMMA,
                Token.COMPONENT,
                Token.DATA,
                Token.DEFAULT_VALUE,
                Token.DESCRIPTION,
                Token.DISPLAY,
                Token.DIVIDE,
                Token.DOT,
                Token.EQ,
                Token.FLAVOR,
                Token.GT,
                Token.GTEQ,
                Token.IMPLEMENTS,
                Token.IMPLIES,
                Token.INTERFACE,
                Token.IS_ACTIVE,
                Token.IS_ENABLED,
                Token.IS_SUBSTRING,
                Token.LBRACE,
                Token.LBRACK,
                Token.LEGAL_VALUES,
                Token.LPAR,
                Token.LT,
                Token.LTEQ,
                Token.MINUS,
                Token.MINUS_MINUS,
                Token.MOD,
                Token.NEQ,
                Token.NONE,
                Token.NOT,
                Token.OPTION,
                Token.OR,
                Token.PACKAGE,
                Token.PLUS,
                Token.QUESTION,
                Token.RBRACE,
                Token.RBRACK,
                Token.REQUIRES,
                Token.RPAR,
                Token.TIMES,
                Token.TO};
        
        try {
            reader = new FileReader(TestFile.get("gsd/iml/parser/plain_tokens"));
            ImlLexer lexer = new ImlLexer(reader);
            
            int token;
            int i = 0 ;

            while ((token = lexer.next_token().sym) != Token.EOF) {
                assertEquals(token, expectedToken[i]) ;
                i++ ;
            }

            reader.close() ;

        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(e == null) ;
        } 
    }

    @Test
    public void testIdentifiers() {
        try {
            FileReader reader = new FileReader(TestFile.get("gsd/iml/parser/identifiers"));
            ImlLexer lexer = new ImlLexer(reader);

            int token ;
            while((token = lexer.next_token().sym) != Token.EOF) {
                assertEquals(token, Token.ID) ;
            }
        } catch (Exception e) {
            e.printStackTrace() ;
            assertTrue(e == null) ;
        }
    }

    @Test
    public void testParseLong() {
        try {
            ImlLexer lexer = new ImlLexer(new StringReader("0x7fffffff"));
            assertTrue(lexer.next_token().value.equals(2147483647L)) ;
        } catch (Exception e) {
            e.printStackTrace() ;
            assertTrue(e == null) ;
        }
    }

    @Test
    public void testLongLiterals() {
        try {
            FileReader reader = new FileReader(TestFile.get("gsd/iml/parser/long_literals"));
            ImlLexer lexer = new ImlLexer(reader);

            int token ;
            while((token = lexer.next_token().sym) != Token.EOF) {
                assertEquals(token, Token.LONG_LITERAL) ;
            }
        } catch (Exception e) {
            e.printStackTrace() ;
            assertTrue(e == null) ;
        }
    }

    @Test
    public void testDoubleLiterals() {
        try {
            FileReader reader = new FileReader(TestFile.get("gsd/iml/parser/double_literals"));
            ImlLexer lexer = new ImlLexer(reader);

            int token ;
            while((token = lexer.next_token().sym) != Token.EOF) {
                assertEquals(token, Token.DOUBLE_FLOATING_POINT_LITERAL) ;
            }
        } catch (Exception e) {
            e.printStackTrace() ;
            assertTrue(e == null) ;
        }
    }

    @Test
    public void testStringLiterals() {
        try {
            FileReader reader = new FileReader(TestFile.get("gsd/iml/parser/string_literals"));
            ImlLexer lexer = new ImlLexer(reader);

            int token ;
            while((token = lexer.next_token().sym) != Token.EOF) {
                assertEquals(token, Token.STRING_LITERAL) ;
            }
        } catch (Exception e) {
            e.printStackTrace() ;
            assertTrue(e == null) ;
        }
    }

}
