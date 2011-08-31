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

/**
 *
 * @author leonardo
 */
public class IntervalExpression extends Expression {

    private Expression from ;
    private Expression to ;

    public IntervalExpression(Expression from, Expression to) {
        super("interval") ;
        this.from = from;
        this.to = to;
    }

    public Expression getFrom() {
        return from;
    }

    public Expression getTo() {
        return to;
    }

    @Override
    public int hashCode() {
        return getName().hashCode() + getFrom().hashCode() + getTo().hashCode() ;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true ;
        
        if (! (obj instanceof IntervalExpression))
            return false ;
        
        IntervalExpression range = (IntervalExpression) obj ;
        return range.getFrom().equals(getFrom()) &&
               range.getTo().equals(getTo()) ;
    }

    @Override
    public String toString() {
        return getFrom() + " to " + getTo() ;
    }


    
}
