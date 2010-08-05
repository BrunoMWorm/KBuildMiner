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
package gsd.cdl

import scala.collection.mutable

object EcosFMGrammarBuilder {

  var maxHierarchyLevel = 0
  
  def main( args : Array[String] ){
    
    
    var names = mutable.Set[String]()
    var descr:List[EcosNode] = Nil
    
    for( line <- scala.io.Source.fromFile("input.txt").getLines ){
      val l = line.split(":")
      
      val d = new EcosNode( l(1), l(0), l(5), l(2), Integer.parseInt(l(3)), if( l(4) == "1" ) true else false )
      descr = d :: descr
//      println( d.getName() + "=" + d.isBool() )
      
    }
    
    val root = new EcosNode( "ROOT", "Configuration root node", "", null, 0, false );
    descr.foreach( x => if( x.getParent == "" ) x.setParent( "ROOT" ) )
    descr = root :: descr
    
    for( d <- descr ){
      val children = descr.filter( x => x.getParent == d.getName )
      if( !children.isEmpty ){
    	  print( d.getName + ":" )
    	  for( c <- children ){
    		  print( " " + c.getName )
    		  if( c.isBool )
    			  print( "?" )
		  }
    	  println(";\\")
      }else{
        val l = goUp( d, descr )
        if( l > maxHierarchyLevel )
          maxHierarchyLevel = l
      }
      
      
    }

    println( "=========\nMax. hierarchy level:" + maxHierarchyLevel )
    
  }
  
  def goUp( d : EcosNode, descr : List[EcosNode] ) : Int = {
    val parent = descr.filter( x => d.getParent == x.getName );
    if( parent.size == 1 )
      return goUp( parent(0), descr ) + 1
    else
        return 0
  }
  
}
