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

import java.io._
import scala.collection.mutable

object EcosFeatureDescriptionGatherer {

  def main( args : Array[String] ){

    var names = mutable.Set[String]()
    var descr:List[Tuple3[String, String, String]] = Nil
    
    for( line <- scala.io.Source.fromFile("input.txt").getLines ){
      val l = line.split(":")
//      println( l(1) + ":" + l(0) + ":" + l(5) )
      if( names contains l(1) )
    	  println( "duplicate: " + l(1) )
      else
    	 names + l(1)
      if( l(2) == "" )
        println( "no parent: " + l(1) )
      
      descr = ( l(1), l(0), l(5) ) :: descr
      
    }
    outputForR( descr )
  }

    def outputForR( fds : List[ Tuple3[String, String, String] ] ) = {
	  var ret:List[Int] = Nil
	  val out = new PrintWriter( new FileWriter( new File( "histodata.txt" ) ) )
	  out.println( "          LENGTH" )
	  for( fd <- fds ){
	    var s = if( fd._3.trim().length() == 0 ) 0 else fd._3.split( "[' '|\\\n]+" ).size
	    // add title
	    s += ( if( fd._2.trim().length() == 0 ) 0 else fd._2.split( "[' '|\\\n]+" ).size )
	    out.println( fd._1 + "  " + ( s ) )
	    ret = s :: ret
	  }
	  out.close
	  ret
  }
    def outputInFiles( fds : List[Tuple3[String, String, String]] ) {
    
//    for( fd <- fds ){
//      
//      val f = new File( "fd-output" + File.separatorChar + fd.getName() + ".txt" )
//      println( f.getAbsolutePath )
//      f.createNewFile
//      val out = new PrintWriter( new FileWriter( f ) )
//      val desc = if( fd.getDescription.indexOf( fd.getName() ) == -1 )
//    	  			fd.getDescription()
//	  			  else
//	  				fd.getDescription
//      out.print( desc )
//      out.close
//      
//    }
  }
  
}
