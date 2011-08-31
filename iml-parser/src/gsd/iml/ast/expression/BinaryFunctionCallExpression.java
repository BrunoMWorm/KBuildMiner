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

/**
 *
 * @author leonardo
 */
public class BinaryFunctionCallExpression extends FunctionCallExpression {
    private Expression argument1 ;
    private Expression argument2 ;


    public BinaryFunctionCallExpression(String name, Expression argument1, Expression argument2) {
        super(name, Arrays.asList(argument1, argument2)) ;
        this.argument1 = argument1 ;
        this.argument2 = argument2 ;
    }

    public Expression getFirstArgument() {
        return argument1 ;
    }

    public Expression getSecondArgument() {
        return argument2 ;
    }
}
