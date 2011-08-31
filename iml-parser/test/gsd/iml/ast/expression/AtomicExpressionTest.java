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

package gsd.iml.ast.expression;

import junit.framework.TestCase;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author leonardo
 */
public class AtomicExpressionTest extends TestCase {
    
    @Test
    public void testIdentifierExpression() {
       AtomicExpression exp = new IdentifierExpression("abc") ;
       assertTrue(exp.getValue().equals("abc")) ;
       assertTrue(((IdentifierExpression) exp).getId().equals("abc")) ;
    }

    @Test
    public void testLongLiteralExpression() {
       AtomicExpression exp = new LongLiteralExpression(123) ;
       assertTrue(exp.getValue().equals(123L)) ;
       assertTrue(((LongLiteralExpression) exp).get().equals(123L)) ;
    }

    @Test
    public void testStringLiteralExpression() {
       AtomicExpression exp = new StringLiteralExpression("abc") ;
       assertTrue(exp.getValue().equals("abc")) ;
       assertTrue(((StringLiteralExpression) exp).get().equals("abc")) ;
    }

    @Test
    public void testDoubleLiteralExpression() {
       AtomicExpression exp = new DoubleLiteralExpression(1.11123) ;
       assertTrue(exp.getValue().equals(1.11123)) ;
       assertTrue(((DoubleLiteralExpression) exp).get().equals(1.11123)) ;
    }
}