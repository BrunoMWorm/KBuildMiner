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

import java.io.{PrintWriter, FileWriter, File}
import parser.EcosIml.CupParser

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

    val model = CupParser parseFile _iml

    val edges = model.childParentMap.map( x => ( EcosFeature( x._1 ), EcosFeature( x._2 ) ) )
    val features = edges.keySet ++ edges.values.toSet

    val o = new PrintWriter( new FileWriter( _out ) )
    o.println( toGraphvizString( edges, features ) )
    o.close

  }

  private def getArg[T]( arg: Option[T], default: String ): String =
    arg match{
      case Some( s )  => s.toString
      case None       => default
    }
  
  def name( s: String ): String =
    s.split("_").last

  def toGraphvizString( edges: Map[EcosFeature,EcosFeature], vertices: Set[EcosFeature]) = {
    val sb = new StringBuilder

    //Header
    sb append "digraph {\n"
    sb append "graph [" append "rankdir=" append "TB" append "]\n"
    sb append "node [" append "shape=" append "box" append "]\n"

    //Vertices
    val vertexMap = Map() ++ vertices.toList.zip((1 to vertices.size).toList)
    vertexMap.foreach { case(v,i) =>
      sb append i append "[label=\"" append v.toString.replace("\"", "\\\"") append "\"]\n"
    }

    edges.foreach { case (source, target) =>
      sb append vertexMap(target) append "->" append vertexMap(source) append "\n"
    }

    sb append "}"

    sb.toString
  }

}