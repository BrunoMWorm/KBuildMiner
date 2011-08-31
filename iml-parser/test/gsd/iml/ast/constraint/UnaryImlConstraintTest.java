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

package gsd.iml.ast.constraint;

import gsd.iml.ast.expression.ConditionalExpression;
import gsd.iml.ast.expression.IdentifierExpression;
import gsd.iml.ast.expression.LongLiteralExpression;
import java.util.Arrays;
import junit.framework.TestCase;
import org.junit.Test;

/**
 *
 * @author leonardo
 */
public class UnaryImlConstraintTest extends TestCase {

    private ConditionalExpression cond =
            new ConditionalExpression(
                new IdentifierExpression("OPTION_FEATURE"),
                new LongLiteralExpression(1),
                new LongLiteralExpression(0)
            ) ;

    @Test
    public void testActiveIfConstraint() {
        UnaryImlConstraint ct = new ActiveIfConstraint(cond) ;
        assertTrue(ct.getExpression().equals(cond)) ;
        assertTrue(((ImlConstraint) ct).getExpressions().equals(Arrays.asList(cond))) ;
        assertTrue(((ImlConstraint) ct).getName().equals("active_if")) ;
    }

    @Test
    public void testRequiresConstraint() {
        UnaryImlConstraint ct = new RequiresConstraint(cond) ;
        assertTrue(ct.getExpression().equals(cond)) ;
        assertTrue(ct.getExpressions().equals(Arrays.asList(cond))) ;
        assertTrue(ct.getName().equals("requires")) ;
    }

    @Test
    public void testImplementsConstraint() {
        UnaryImlConstraint ct = new ImplementsConstraint(cond) ;
        assertTrue(ct.getExpression().equals(cond)) ;
        assertTrue(ct.getExpressions().equals(Arrays.asList(cond))) ;
        assertTrue(ct.getName().equals("implements")) ;
    }

    @Test
    public void testDefaultValueConstraint() {
        UnaryImlConstraint ct = new DefaultValueConstraint(cond) ;
        assertTrue(ct.getExpression().equals(cond)) ;
        assertTrue(ct.getExpressions().equals(Arrays.asList(cond))) ;
        assertTrue(ct.getName().equals("default_value")) ;
    }
}
