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
//import java.util.List;
//import java.util.Map;
//
//import gsd.buildanalysis.linux.model.BuildNode;
//import gsd.buildanalysis.linux.model.BuildRootNode;
//import gsd.buildanalysis.linux.model.ObjectNode;
//
///**
// * @author Thorsten Berger
// *
// */
//public class PathExpressionGenerator {
//
//	public static Expression getExpression( List<BuildNode> path ){
//		Expression ret = null;
//		boolean first = true;
//		for( BuildNode bn : path ){
//			if( bn.getExpression() != null ){
//				if( first ){
//					ret = bn.getExpression();
//					first = false;
//				}else{
//					ret = new And( ret, bn.getExpression() );
//				}
//			}
//		}
//		return ret;
//	}
//
//	public static BuildRootNode outputExpressionsForSourcefiles( Map<String, List<List<BuildNode>>> pathsToTop ){
//		BuildRootNode expressionsRootNode = new BuildRootNode();
//
//		for( String sourceFile : pathsToTop.keySet() ){
//
//			boolean generated = false;
//			Expression expr = null;
//			boolean first = true;
//			for( List<BuildNode> path : pathsToTop.get( sourceFile ) ){
//				if( first ){
//					expr = getExpression( path );
//					first = false;
//					// identify source file and try to recover isGenerated information
//					if( ((ObjectNode)path.get( 0 )).isGenerated() )
//						generated = true;
//				}else{
//					expr = new Or( expr, getExpression( path ) );
//				}
//
//			}
//			ObjectNode on = new ObjectNode( expressionsRootNode );
//			on.setSourceFile( sourceFile );
//			on.setExpression( expr );
//			on.setGenerated( generated );
//
//		}
//
//		return expressionsRootNode;
//	}
//
//}
