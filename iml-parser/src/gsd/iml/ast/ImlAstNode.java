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
 * and open the template in the editor. blabla
 */

package gsd.iml.ast;

import java.util.List;

/**
 *
 * @author leonardo
 */
public abstract class ImlAstNode extends ImlAstObject {
    private List<? extends ImlAstNode> children ;

    public ImlAstNode(String name, List<? extends ImlAstNode> children) {
        super(name) ;
        this.children = children ;
    }

    public boolean hasChildren() {
        List<? extends ImlAstNode> _children = getChildren() ;
        return (_children != null) && (_children.size() > 0) ;
    }

    public List<? extends ImlAstNode> getChildren() {
        return children ;
    }

    @Override
    abstract public boolean equals(Object obj) ;

    @Override
    abstract public int hashCode() ;

    @Override
    public abstract String toString() ;
}
