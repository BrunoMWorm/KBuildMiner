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

import java.util.List;

/**
 *
 * @author leonardo
 */
public abstract class FunctionCallExpression extends Expression {
    private List<Expression> arguments ;
    private String functionName ;

    public FunctionCallExpression(String functionName, List<Expression> arguments) {
        super("function-call") ;
        this.functionName = functionName ;
        this.arguments = arguments;
    }

    public String getFunctionName() {
        return functionName;
    }

    public List<Expression> getArguments() {
        return arguments;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true ;

        if (! (obj instanceof FunctionCallExpression))
            return false ;

        FunctionCallExpression f = (FunctionCallExpression) obj ;
        return f.getFunctionName().equals(getFunctionName()) &&
               f.getArguments().equals(getArguments()) ;
    }

    @Override
    public int hashCode() {
        return getFunctionName().hashCode() + getArguments().hashCode() ;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer() ;
        buffer.append(getFunctionName()) ;
        buffer.append("(") ;
        int numberOfArgs = getArguments().size() ;

        if (numberOfArgs >= 1) {
            for(int i = 0; i <= numberOfArgs - 2; i++)
                buffer.append(getArguments().get(i) + ", ") ;

            buffer.append(getArguments().get(numberOfArgs - 1)) ;
        }

        buffer.append(")") ;
        return buffer.toString() ;
    }
}
