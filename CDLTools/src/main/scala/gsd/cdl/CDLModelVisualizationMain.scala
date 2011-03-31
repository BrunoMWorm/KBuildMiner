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

import gsd.graph.{DirectedGraph, Graph}
import java.io.{PrintWriter, FileWriter, File}

object CDLModelVisualizationMain extends optional.Application{

  case class EcosFeature( val name: String ){
    override def toString() = name.split("_").toList match{
      case prefix :: rest if !rest.isEmpty => rest.reduceLeft[String]( _ + "_" + _ )
      case _ => name
    }
  }

  def main( iml: Option[String], out: Option[String] ){

    val f = new File( "output" )
    if( !f.exists ) f.mkdirs

    val _iml = getArg( iml, "../../ecos/output/pc_vmWare_iml.txt" )
    val _out = getArg( out, "output/pc_vmWare.gv" )

    val model = EcosIML.parseFile( _iml )

    val vertexMap = Map() ++ model.nodesById.keySet.toList.zip((1 to model.nodesById.size).toList)

    val edges = model.childParentMap.map( x => ( EcosFeature( x._1 ), EcosFeature( x._2 ) ) )

//    val g = DirectedGraph[Int](
//      Set( vertexMap.values.toList:_* ),
//      Graph.toMultiMap( edges ) ).reverseEdges

    val g = DirectedGraph[EcosFeature](
      Set( model.nodesById.keySet.map( EcosFeature ).toList: _* ),
      Graph.toMultiMap( edges ) ).reverseEdges

    val o = new PrintWriter( new FileWriter( _out ) )
    o.println( g.toGraphvizString )
    o.close

  }

  private def getArg[T]( arg: Option[T], default: String ): String =
    arg match{
      case Some( s )  => s.toString
      case None       => default
    }
  
  def name( s: String ): String =
    s.split("_").last

}