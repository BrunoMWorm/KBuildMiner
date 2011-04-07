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
import gsd.cdl.model._
import util.parsing.input.PagedSeqReader
import collection.immutable.PagedSeq
import kiama.rewriting.Rewriter
import gsd.cdl.model.CDLExpressionConversions._
import java.io.{PrintStream, PrintWriter, FileWriter, File}
import gsd.cdl.IMLParser

/**
 * @deprecated use IML2BoolMain and CDLBooleanTransformation
 */
object EcosI386BooleanTranslationMain extends IMLParser with Rewriter{

  def main( args: Array[String] ){

//    parseAll(cdl, new PagedSeqReader(PagedSeq fromFile "iml_temp.txt")) match{
    parseAll(cdl, new PagedSeqReader(PagedSeq fromFile "../ecos/output/pc_vmWare_iml.txt")) match{
      case Success(res,_) => doRewritingAndExport( IML( res ) )
      case x => println( "failure: " + x )
    }

  }

  def doRewritingAndExport( model: IML ){

    println("-------------------")
    println("finished...")

    val transform = new CDLBooleanTransformation( model )
    val formula = transform.translate2conjunctList

    writeToCSV( model, new File( "output/constraints_with_stats.csv"), transform )

    println( "\n================================================" )
    println( "The formula:")
    formula.foreach( println )

    transform.exportConjunctList( formula, new PrintStream( "output/formula_i386.txt" ) )

    createTagFile( model.topLevelNodes )
  }

  def createTagFile( topLevelNodes : List[Node] ){
    val out = new PrintWriter( new FileWriter( "output/i386_siblings.tags" ) )
    everywheretd{ query {
      case Node(n,_,_,_,_,_,_,_,_,_,_,children) if( !children.isEmpty ) =>
          out.println( n + ": " + children.map( _.id ).reduceLeft[String]( (a,b) => a + " " + b ) )
    }}( Node("1",PackageType,"",None,BoolDataFlavor,None,None,None,List(),List(),List(),topLevelNodes) )
    out.close
  }

  def writeToCSV( model: IML, f:File, transform: CDLBooleanTransformation ){

    def getVarTypes( e : CDLExpression ) =
      collectl{
        case Identifier(id) => model.nodesById.get( id ) match {
          case Some( n ) => ( n.cdlType.toString, n.flavor.toString )
          case None => ("unloaded","unloaded")
        }
      }(e)

    val out = new PrintWriter( new FileWriter( f ) )

    for( val n <- model.allNodes ){
      out.println( n.id + ";" + n.cdlType + ";" + n.flavor )

      val printConstraints = ( x:( List[CDLExpression], String ) ) => {
        var ret = ""
        for( val c <- x._1 ){
          ret += " ;" + n.cdlType + "; ;" + x._2 + ";" + c + ";"
          ret += getVarTypes( c ).foldLeft("")( (a,b) => a + ( if( a!="" ) "," else "" ) + b._1 )
          ret += ";"
          ret += getVarTypes( c ).foldLeft("")( (a,b) => a + ( if( a!="" ) "," else "" ) + b._2 )
          ret += ";;;" // leave two columns for manually rewritten constraints
          ret += n.id implies transform.rewriteConstraint( c )

          ret += "\n"
        }
        ret
      }

      if( !n.activeIfs.isEmpty )
        out.print( printConstraints(n.activeIfs,"active_if" ) )
      if( !n.reqs.isEmpty )
        out.print( printConstraints(n.reqs,"requires" ) )

    }

    out.close
  }

}