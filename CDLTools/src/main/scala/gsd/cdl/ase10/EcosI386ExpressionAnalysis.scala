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
import kiama.rewriting.Rewriter
import model._
import util.parsing.input.PagedSeqReader
import collection.immutable.PagedSeq
import statistics.CDLModel
import java.io.{PrintWriter, File}
import scala.collection.mutable
import gsd.cdl.statistics.CDLModel

object EcosI386ExpressionAnalysis extends IMLParser with Rewriter{

  var nodesById = Map[String,Node]()
  var childParentMap = Map[String,String]()

  def main( args: Array[String] ){
    parseAll(cdl, new PagedSeqReader(PagedSeq fromFile "../ecos/output/pc_vmWare_iml.txt")) match{
      case Success(res,_) =>{
        doExpressionAnalysis( res, "pc_vmWare" )
        doVisibilityAnalysis( res )
      }
      case x => println( "failure: " + x )
    }
  }

  def doExpressionAnalysis( topLevelNodes:List[Node], target : String ) {

    everywheretd ( query {
        case node@Node(n,_,_,_,_,_,_,_,_,_,_,children) => {
          children.foreach( x => childParentMap += ( x.id -> n ) )
          nodesById += (n -> node)
        }
    } ) (topLevelNodes)


//    val featureConstraints = Map[Node,List[Constraint]]

    println( "\n==================================")
    println( "expression analysis" )

    val featureConstraints = collectl{
      case Node( id,_,_,_,_,dv,ca,lv,re,ai,_,_) => {
        var el = List[Constraint]()
        if( ca != None ) el += Calculated( ca.get )
        if( dv != None ) el += DefaultValue( dv.get )
        if( lv != None ) el += LegalValues( lv.get )
        re.foreach( el += Requires( _ ) )
        ai.foreach( el += ActiveIf( _ ) )
        (id, el)
      }
    }( topLevelNodes )

//    val constraintLengths = featureConstraints.map( x => (x._1, x._2.foldLeft(0:Int)( (a,b) => a + countASTs( b ) ) ) )
    var numOfIdentifier = featureConstraints.map( x => {println(" " + x._1); (x._1, x._2.foldLeft(0:Int)( (a,b) => a + countIdentifiers( b ) ) ) } )

    // add the parent
    numOfIdentifier = numOfIdentifier.map( x => (x._1, x._2 + ( childParentMap.get( x._1 ) match {
      case Some(n) => 1
      case None => 0
    } ) ) )

//    val biggestAST = constraintLengths.reduceLeft[Tuple2[String,Int]]( (acc,b) => if( b._2 > acc._2) b else acc  )
//    println( "\n Ok, biggest AST has: " + biggestAST )
    val mostIdsReferenced = numOfIdentifier.reduceLeft[Tuple2[String,Int]]( (acc,b) => if( b._2 > acc._2) b else acc  )
    println( "Ok, and biggest # of identifiers has: " + mostIdsReferenced )

//    outputHistogram( constraintLengths.map( x => x._2 ), "output/histograms/astLengthsPerNode.csv" )
    outputHistogram( numOfIdentifier.map( x => x._2 ), "output/histograms/numberOfIdentifiersPerNode.csv" )

    println("===============================")

    val allConstraints = List.flatten( for( c <- featureConstraints ) yield c._2 )
    var identifiers = Set[String]()
    for( val c <- allConstraints )
      getIdentifiers( c ).foreach( identifiers += _ )

    // get calculated
    val calculatedFeatures = collects{
      case n:Node if n.calculated != None => n.id
    }( topLevelNodes )

    val calcFeaturesInConstraints = identifiers ** calculatedFeatures
    println( "The following calculated features are referenced in constraints: " )
    calcFeaturesInConstraints.foreach( println )
    println( calcFeaturesInConstraints.size )

    println
  }

//  def rewriteLegalValues( lo : LegalValuesOption ) : CDLExpression = {
//
//  }

  def getIdentifiers( c : Constraint ) = collects{
    case Identifier( id ) => id
  }( c )

  def countIdentifiers( c : Constraint ) : Int =
    collects{
      case i:Identifier => {println("Identifier: " + i); i}
    }( c ).size

//  def countASTs( c : Constraint ) : Int = {
//    collectl{
//      case e:CDLExpression => e
//      case m:MinMaxRange => m
//      case s:SingleValueRange => s
//    }(c).size + 1
//  }

  def countASTs( c : Constraint ) : Int = {
    var ret = 0
    topdown{ queryf{
      x => x match{
        case s:String => println( "String:" + s )
        case s:Integer => println( "Int: " + s )
        case _ => {
          println( "OK: " + x )
          ret+=1
        }
      }
    }}(c)
    ret
  }

  def doVisibilityAnalysis( topLevelNodes:List[Node] ){
    val allNodes = collectl{
      case n:Node => n
    }( topLevelNodes )

    val withAnyConstraint = allNodes.filter( x => x.activeIfs.size > 0 ||
            x.reqs.size > 0 || x.calculated != None || x.legalValues != None || x.defaultValue != None )

    val withAIConstraint = allNodes.filter( _.activeIfs.size > 0 )

    val withDefaultValueProperty = allNodes.filter( _.defaultValue != None )

    val withComputedDVProperty = withDefaultValueProperty.remove( _.defaultValue.get match {
              case StringLiteral(_) => true
              case IntLiteral(_) => true
              case True() => true
              case False() => true
              case _ => false
            })

    println( "====================================" )
    println( "# features with any constraint: " + withAnyConstraint.size )
    println( "# features with active_if constraint: " + withAIConstraint.size )
    println( "# features with default_value property: " + withDefaultValueProperty.size )
//    withDefaultValueProperty.foreach( x => println( x.defaultValue.get ) )
    println("=====================================" )
    println( "...from those, # of computed ones is: " + withComputedDVProperty.size )
//    withComputedDVProperty.foreach( x => println( x.defaultValue.get ) )
  }

  def outputHistogram( values : List[Int], file : String ){
		val pw = new PrintWriter( new File( file ) )
		aggregateValuesForGnuplot( values, 1 ).
            foreach( x => pw.println( x._1 + "," + x._2 ) )
		pw.close();
	}

  def aggregateValuesForGnuplot( values : List[Int], raster : Int ) = {
    val ret = mutable.Map[Int, Int]()
    for( val f <- values ){
      val newValue:Int = ( ( f / raster ) * raster ) + raster/2;
      ret.get( newValue ) match{
        case None => ret + (newValue -> 1)
        case Some(v) => ret + ( newValue -> ( v + 1 ) )
      }
    }
		Map[Int,Int]() ++ ret
	}



}