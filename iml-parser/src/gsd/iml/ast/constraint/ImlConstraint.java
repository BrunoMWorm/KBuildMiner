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


package gsd.iml.ast.constraint;

import gsd.iml.ast.ImlAstObject;
import gsd.iml.ast.expression.Expression;
import java.util.List;

/**
 *
 * @author leonardo
 */
public abstract class ImlConstraint extends ImlAstObject {

    private List<? extends Expression> expressions ;

    public ImlConstraint(String name, List<? extends Expression> expressions) {
        super(name) ;
        this.expressions = expressions;
    }

    public List<? extends Expression> getExpressions() {
        return expressions;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true ;

        if (! (obj instanceof ImlConstraint))
            return false ;

        ImlConstraint ic = (ImlConstraint) obj ;
        return ic.getName().equals(getName()) &&
               ic.getExpressions().equals(getExpressions()) ;
    }

    @Override
    public int hashCode() {
        return getName().hashCode() + getExpressions().hashCode() ;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer() ;
        buffer.append(getName()) ;
        buffer.append(" [") ;
        int numberOfConstraints = getExpressions().size() ;
        if (numberOfConstraints >= 1) {
            for(int i = 0; i <=  numberOfConstraints - 2; i++)
                buffer.append(getExpressions().get(i) + " ");
            buffer.append(getExpressions().get(numberOfConstraints - 1)) ;
        }
        buffer.append("]") ;
        return buffer.toString() ;
    }
}
