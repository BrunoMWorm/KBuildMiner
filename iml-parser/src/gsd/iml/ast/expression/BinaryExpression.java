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
public abstract class BinaryExpression extends Expression {

    private Expression left ;
    private Expression right ;

    public BinaryExpression(String name, Expression left, Expression right) {
        super(name) ;
        this.left = left;
        this.right = right;
    }

    public Expression getLeft() {
        return left;
    }

    public Expression getRight() {
        return right;
    }

    @Override
    public int hashCode() {
        return getName().hashCode() + getLeft().hashCode() + getRight().hashCode() ;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true ;

        if (! (obj instanceof BinaryExpression))
            return false ;

        BinaryExpression e = (BinaryExpression) obj ;
        return (e.getName().equals(getName()) &&
                e.getLeft().equals(getLeft()) &&
                e.getRight().equals(getRight())) ;
    }
    
    @Override
    public String toString() {
        return getLeft() + " " + getName() + " " + getRight() ;
    }
}
