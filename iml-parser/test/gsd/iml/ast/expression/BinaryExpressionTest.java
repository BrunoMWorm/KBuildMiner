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
public class BinaryExpressionTest extends TestCase {

    @Test
    public void testAndExpression() {
        BinaryExpression exp = new AndExpression(
                new LongLiteralExpression(1),
                new LongLiteralExpression(0));
        assertTrue(exp.getLeft().equals(new LongLiteralExpression(1))) ;
        assertTrue(exp.getRight().equals(new LongLiteralExpression(0))) ;
        assertTrue(exp.getName().equals("&&")) ;
    }

    @Test
    public void testOrExpression() {
        BinaryExpression exp = new OrExpression(
                new LongLiteralExpression(1),
                new LongLiteralExpression(0));
        assertTrue(exp.getLeft().equals(new LongLiteralExpression(1))) ;
        assertTrue(exp.getRight().equals(new LongLiteralExpression(0))) ;
        assertTrue(exp.getName().equals("||")) ;
    }

    @Test
    public void testBitwiseAndExpression() {
        BinaryExpression exp = new BitwiseAndExpression(
                new LongLiteralExpression(123),
                new LongLiteralExpression(456)) ;
        assertTrue(exp.getLeft().equals(new LongLiteralExpression(123))) ;
        assertTrue(exp.getRight().equals(new LongLiteralExpression(456))) ;
        assertTrue(exp.getName().equals("&")) ;
    }

    @Test
    public void testBitwiseOrExpression() {
        BinaryExpression exp = new BitwiseOrExpression(
                new LongLiteralExpression(123),
                new LongLiteralExpression(456)) ;
        assertTrue(exp.getLeft().equals(new LongLiteralExpression(123))) ;
        assertTrue(exp.getRight().equals(new LongLiteralExpression(456))) ;
        assertTrue(exp.getName().equals("|")) ;
    }

    @Test
    public void testImpliesExpression() {
        BinaryExpression exp = new ImpliesExpression(
                new LongLiteralExpression(0),
                new LongLiteralExpression(1)) ;
        assertTrue(exp.getLeft().equals(new LongLiteralExpression(0))) ;
        assertTrue(exp.getRight().equals(new LongLiteralExpression(1))) ;
        assertTrue(exp.getName().equals("implies")) ;
    }

    @Test
    public void testBitwiseXorExpression() {
        BinaryExpression exp = new BitwiseXorExpression(
                new LongLiteralExpression(123),
                new LongLiteralExpression(456)) ;
        assertTrue(exp.getLeft().equals(new LongLiteralExpression(123))) ;
        assertTrue(exp.getRight().equals(new LongLiteralExpression(456))) ;
        assertTrue(exp.getName().equals("^")) ;
    }


    @Test
    public void testBitwiseLeftShiftExpression() {
        BinaryExpression exp = new BitwiseLeftShiftExpression(
                new LongLiteralExpression(123),
                new LongLiteralExpression(456)) ;
        assertTrue(exp.getLeft().equals(new LongLiteralExpression(123))) ;
        assertTrue(exp.getRight().equals(new LongLiteralExpression(456))) ;
        assertTrue(exp.getName().equals("<<")) ;
    }

    @Test
    public void testBitwiseRightShiftExpression() {
        BinaryExpression exp = new BitwiseRightShiftExpression(
                new LongLiteralExpression(123),
                new LongLiteralExpression(456)) ;
        assertTrue(exp.getLeft().equals(new LongLiteralExpression(123))) ;
        assertTrue(exp.getRight().equals(new LongLiteralExpression(456))) ;
        assertTrue(exp.getName().equals(">>")) ;
    }

    @Test
    public void testPlusExpression() {
        BinaryExpression exp = new PlusExpression(
                new LongLiteralExpression(123),
                new LongLiteralExpression(456)) ;
        assertTrue(exp.getLeft().equals(new LongLiteralExpression(123))) ;
        assertTrue(exp.getRight().equals(new LongLiteralExpression(456))) ;
        assertTrue(exp.getName().equals("+")) ;
    }

    @Test
    public void testMinusExpression() {
        BinaryExpression exp = new MinusExpression(
                new LongLiteralExpression(123),
                new LongLiteralExpression(456)) ;
        assertTrue(exp.getLeft().equals(new LongLiteralExpression(123))) ;
        assertTrue(exp.getRight().equals(new LongLiteralExpression(456))) ;
        assertTrue(exp.getName().equals("-")) ;
    }

    @Test
    public void testTimesExpression() {
        BinaryExpression exp = new TimesExpression(
                new LongLiteralExpression(123),
                new LongLiteralExpression(456)) ;
        assertTrue(exp.getLeft().equals(new LongLiteralExpression(123))) ;
        assertTrue(exp.getRight().equals(new LongLiteralExpression(456))) ;
        assertTrue(exp.getName().equals("*")) ;
    }

    @Test
    public void testDivideExpression() {
        BinaryExpression exp = new DivideExpression(
                new LongLiteralExpression(123),
                new LongLiteralExpression(456)) ;
        assertTrue(exp.getLeft().equals(new LongLiteralExpression(123))) ;
        assertTrue(exp.getRight().equals(new LongLiteralExpression(456))) ;
        assertTrue(exp.getName().equals("/")) ;
    }

    @Test
    public void testModExpression() {
        BinaryExpression exp = new ModExpression(
                new LongLiteralExpression(123),
                new LongLiteralExpression(456)) ;
        assertTrue(exp.getLeft().equals(new LongLiteralExpression(123))) ;
        assertTrue(exp.getRight().equals(new LongLiteralExpression(456))) ;
        assertTrue(exp.getName().equals("%")) ;
    }

    @Test
    public void testEqualExpression() {
        BinaryExpression exp = new EqualExpression(
                new LongLiteralExpression(123),
                new LongLiteralExpression(456)) ;
        assertTrue(exp.getLeft().equals(new LongLiteralExpression(123))) ;
        assertTrue(exp.getRight().equals(new LongLiteralExpression(456))) ;
        assertTrue(exp.getName().equals("==")) ;
    }

    @Test
    public void testNotEqualExpression() {
        BinaryExpression exp = new NotEqualExpression(
                new LongLiteralExpression(123),
                new LongLiteralExpression(456)) ;
        assertTrue(exp.getLeft().equals(new LongLiteralExpression(123))) ;
        assertTrue(exp.getRight().equals(new LongLiteralExpression(456))) ;
        assertTrue(exp.getName().equals("!=")) ;
    }

    @Test
    public void testLessThanExpression() {
        BinaryExpression exp = new LessThanExpression(
                new LongLiteralExpression(123),
                new LongLiteralExpression(456)) ;
        assertTrue(exp.getLeft().equals(new LongLiteralExpression(123))) ;
        assertTrue(exp.getRight().equals(new LongLiteralExpression(456))) ;
        assertTrue(exp.getName().equals("<")) ;
    }

    @Test
    public void testLessThanEqualExpression() {
        BinaryExpression exp = new LessThanEqualExpression(
                new LongLiteralExpression(123),
                new LongLiteralExpression(456)) ;
        assertTrue(exp.getLeft().equals(new LongLiteralExpression(123))) ;
        assertTrue(exp.getRight().equals(new LongLiteralExpression(456))) ;
        assertTrue(exp.getName().equals("<=")) ;
    }

    @Test
    public void testGreaterThanExpression() {
        BinaryExpression exp = new GreaterThanExpression(
                new LongLiteralExpression(123),
                new LongLiteralExpression(456)) ;
        assertTrue(exp.getLeft().equals(new LongLiteralExpression(123))) ;
        assertTrue(exp.getRight().equals(new LongLiteralExpression(456))) ;
        assertTrue(exp.getName().equals(">")) ;
    }

    @Test
    public void testGreaterThanEqualExpression() {
        BinaryExpression exp = new GreaterThanEqualExpression(
                new LongLiteralExpression(123),
                new LongLiteralExpression(456)) ;
        assertTrue(exp.getLeft().equals(new LongLiteralExpression(123))) ;
        assertTrue(exp.getRight().equals(new LongLiteralExpression(456))) ;
        assertTrue(exp.getName().equals(">=")) ;
    }

    @Test
    public void testDotExpression() {
        BinaryExpression exp = new DotExpression(
                new StringLiteralExpression("abc"),
                new StringLiteralExpression("cde")) ;
        assertTrue(exp.getLeft().equals(new StringLiteralExpression("abc"))) ;
        assertTrue(exp.getRight().equals(new StringLiteralExpression("cde"))) ;
        assertTrue(exp.getName().equals(".")) ;
    }
}