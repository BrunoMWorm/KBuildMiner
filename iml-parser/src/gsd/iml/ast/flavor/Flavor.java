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

package gsd.iml.ast.flavor;

import gsd.iml.ast.ImlAstObject;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author leonardo
 */
abstract public class Flavor extends ImlAstObject {

    public Flavor(String name) {
        super(name) ;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true ;

        if (! (obj instanceof Flavor))
            return false ;

        Flavor f = (Flavor) obj ;
        return f.getName().equals(getName()) ;
    }

    @Override
    public int hashCode() {
        return getName().hashCode() ;
    }
    
    @Override
    public String toString() {
        return getName() ;
    }
}
