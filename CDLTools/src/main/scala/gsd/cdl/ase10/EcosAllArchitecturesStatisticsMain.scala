package gsd.cdl.ase10

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
import collection.immutable.PagedSeq
import util.parsing.input.PagedSeqReader
import org.kiama.rewriting.Rewriter._
import scala.collection.mutable
import java.io.{FileWriter, PrintWriter, File}
import gsd.cdl.model._
import gsd.cdl.statistics.CDLModel
import gsd.cdl.parser.combinator.IMLParser

object EcosAllArchitecturesStatisticsMain extends IMLParser{

  val flavors = List( NoneFlavor, BoolFlavor, BoolDataFlavor, DataFlavor )
  val types = List ( PackageType, ComponentType, OptionType, InterfaceType )

  implicit def convertSet( set : java.util.Set[String] ) = {
    var ret = Set[String]()
    val it = set.iterator
    while( it.hasNext ) ret = ret + it.next
    ret
  }

   def main( args: Array[String]){

     var modelStatistics = Map[String,CDLModel]()
     val allFeatures = mutable.Set[String]()

     val fineGrainedOut = new PrintWriter(new FileWriter( "output/fineGrainedStats_all_architectures.csv" ) )

      val files = (new File("../ecos/output/")).listFiles.filter( _.getName.endsWith( "_iml.txt" ) )
//      val files = Set[File]( new File( "../ecos/output/rattler_iml.txt"), new File( "../ecos/output/pc_vmWare_iml.txt" ) )

      for( val f <- files ){
        println( "==========================================")
        println( "starting parsing of " + f.getName )
        val name = f.getName.substring( 0, f.getName.lastIndexOf('_') )

        parseAll(cdl, new PagedSeqReader(PagedSeq fromFile f)) match{
          case Success(res,_) => {

            val stats = calculateStats( name, res, fineGrainedOut )
            modelStatistics = modelStatistics + ( stats.target -> stats )
            val it = stats.features.iterator
            while( it.hasNext ) allFeatures + it.next

          }
          case x => println( "failure: " + x )
        }

      }

     // calculate intersections
     modelStatistics.values.foreach( x => x.featuresInCommonWithI386 = ( x.features ** modelStatistics( "pc_vmWare").features ).size )

     println( "Ok, our total number of features is: " + allFeatures.size )
     writeToCSV( modelStatistics, new File( "output/allArchitecturesStatistics.csv" ) )

     fineGrainedOut.close
  }

  def calculateStats( name: String, topLevelNodes: List[Node], fineGrainedOut : PrintWriter ):CDLModel = {
    val ret = new CDLModel
    ret.target = name

    val allNodes = collectl{
      case n:Node => n
    }(topLevelNodes)

    ret.nodes = allNodes.size

    ret.none = allNodes.filter( _.flavor == NoneFlavor ).size
    ret.bool = allNodes.filter( _.flavor == BoolFlavor ).size
    ret.booldata = allNodes.filter( _.flavor == BoolDataFlavor ).size
    ret.data = allNodes.filter( _.flavor == DataFlavor ).size

    ret.packages = allNodes.filter( _.cdlType == PackageType ).size
    ret.components = allNodes.filter( _.cdlType == ComponentType ).size
    ret.options = allNodes.filter( _.cdlType == OptionType ).size
    ret.interfaces = allNodes.filter( _.cdlType == InterfaceType ).size

    allNodes.foreach( x => ret.features.add( x.id ) )

    writeFineGrainedStats( allNodes, fineGrainedOut, ret.target )

    // search for interesting interfaces
    val allInterfaces = allNodes.filter( _.cdlType == InterfaceType )
    val constrainedInterfaces = allInterfaces.filter( !_.reqs.isEmpty )
    val interestingInterfaces = constrainedInterfaces.filter( x => collectl{
             case LongIntLiteral( n ) if( n != 0 && n != 1 ) => x.id
    }(x.reqs).size > 0 )

    println( "=======================================\nInteresting interfaces: " )
    interestingInterfaces.foreach( x => println( x.id + " requires " + x.reqs ) )


    ret
  }

  def writeToCSV( stats:Map[String,CDLModel], f:File ){
    val out = new PrintWriter(new FileWriter( f ) )

    out.println( "Target; Nodes; none; bool; booldata; data; packages; components; options; interfaces; in common with I386" )
    
    for( val s <- stats.values ){
      out.println( s.target + ";" + s.nodes + ";" +
                    s.none + ";" + s.bool + ";" + s.booldata + ";" + s.data + ";" +
                    s.packages + ";" + s.components + ";" + s.options + ";" + s.interfaces + ";" +
                    s.featuresInCommonWithI386 );

    }
    out.close
  }

  def writeFineGrainedStats( allNodes : List[Node], out : PrintWriter, target:String ){
    out.println( target + ";;;;;;" )
    out.print( ";;all"); flavors.foreach( x => out.print( ";" + x ) ); out.println
    for( t <- types ){
      val thisTypeNodes = allNodes.filter( _.cdlType == t )
      out.print( ";" + t + ";" + thisTypeNodes.size )
      for( f <- flavors ){
        out.print( ";" + thisTypeNodes.filter( _.flavor == f ).size )
      }
      out.println
    }
  }
  }