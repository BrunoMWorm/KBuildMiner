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

import java.util.Arrays;
import junit.framework.TestCase;
import org.junit.Test;

/**
 *
 * @author leonardo
 */
public class FunctionCallTest extends TestCase {

    @Test
    public void testIsActive() {
        FunctionCallExpression ftnCall =
                new IsActiveFunctionCallExpression(new LongLiteralExpression(123)) ;
        assertTrue(ftnCall.getArguments().equals(Arrays.asList(new LongLiteralExpression(123)))) ;
        assertTrue(((IsActiveFunctionCallExpression) ftnCall).getArgument().equals(new LongLiteralExpression(123))) ;
        assertTrue(ftnCall.getFunctionName().equals("is_active")) ;
    }

    @Test
    public void testBooleanFunctionCall() {
        FunctionCallExpression ftnCall =
                new BooleanFunctionCallExpression(new LongLiteralExpression(123)) ;
        assertTrue(ftnCall.getArguments().equals(Arrays.asList(new LongLiteralExpression(123)))) ;
        assertTrue(((BooleanFunctionCallExpression) ftnCall).getArgument().equals(new LongLiteralExpression(123))) ;
        assertTrue(ftnCall.getFunctionName().equals("bool")) ;
    }

    @Test
    public void testIsSubstringFunctionCall() {
        FunctionCallExpression ftnCall =
                new IsSubstringFunctionCallExpression
                (new StringLiteralExpression("abc"),
                new StringLiteralExpression("cde")) ;

        assertTrue(ftnCall.getArguments().equals(Arrays.asList
                (new StringLiteralExpression("abc"),
                new StringLiteralExpression("cde")))) ;

        assertTrue(((IsSubstringFunctionCallExpression) ftnCall).
                getFirstArgument().equals(new StringLiteralExpression("abc"))) ;
        assertTrue(((IsSubstringFunctionCallExpression) ftnCall).
                getSecondArgument().equals(new StringLiteralExpression("cde"))) ;

        assertTrue(ftnCall.getFunctionName().equals("is_substr")) ;
    }
}