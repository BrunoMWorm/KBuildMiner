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
public abstract class AtomicExpression extends Expression {
    private Object value ;

    public AtomicExpression(String name, Object value) {
        super(name) ;
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true ;

        if (! (obj instanceof AtomicExpression))
            return false ;

        AtomicExpression e = (AtomicExpression) obj ;
        return (e.getName().equals(getName()) && e.getValue().equals(getValue())) ;
    }

    @Override
    public int hashCode() {
        return getName().hashCode() + getValue().hashCode() ;
    }

    @Override
    public String toString() {
        return getName() + ":" + value.toString() ;
    }
}
