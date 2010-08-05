/*
 * Copyright (c) 2010 Thorsten Berger <berger@informatik.uni-leipzig.de>
 *
 * This file is part of CDLTools.
 *
 * CDLTools is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CDLTools is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with CDLTools.  If not, see <http://www.gnu.org/licenses/>.
 */

package gsd.cdl.statistics;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

@XmlType
public class Feature {

    public Feature( String id ){
        this.id = id;
    }

    public Feature(){
    }

    @XmlElement
    public String id;
    @XmlElement
    public String description;
    @XmlElement
    public String parent;
    @XmlElement
    public String prompt;
    @XmlElement
    public boolean isParentCandidate;
}
