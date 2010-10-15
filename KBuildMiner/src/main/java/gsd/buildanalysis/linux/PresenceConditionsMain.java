///*
//
//Copyright (C) 2009 Thorsten Berger
//
//This program is free software; you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation; either version 2 of the License, or
//(at your option) any later version.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with this program; if not, write to the Free Software
//Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
//*/
///**
// *
// */
//package gsd.buildanalysis.linux;
//
//import gsd.buildanalysis.linux.model.BuildNode;
//import gsd.buildanalysis.linux.model.BuildRootNode;
//import gsd.buildanalysis.linux.model.ObjectNode;
//
//import java.util.HashMap;
//import java.util.Map;
//
//public class PresenceConditionsMain {
//
//	public static Map<String,Expression> getPresenceConditions( String f ){
//		BuildRootNode brn = PersistenceManager.loadBuildRootNode( f );
//		Map<String,Expression> ret = new HashMap<String, Expression>();
//		for( BuildNode bn : brn.getChildren() ){
//			if( bn instanceof ObjectNode){
//				ObjectNode on = (ObjectNode)bn;
//				ret.put( on.getSourceFile(), on.getExpression() );
//			}
//		}
//		return ret;
//	}
//
//}
