/*
 * Copyright (c) 2011 Thorsten Berger <berger@informatik.uni-leipzig.de>
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

package gsd.cdl.tse11

import org.kiama.rewriting.Rewriter._
import gsd.cdl.parser.EcosIml
import gsd.cdl.AnalysisHelpers
import gsd.cdl.model._
import java.io.InputStream
import io.Source

class TSE11Statistics( val model: IML ) extends ImlTreeAttributes{

  lazy val features = model.allNodes
  lazy val rootNode = model.rootNode

  // Note that ther's usually overlap between switchFeatures and dataFeatures due to the booldata flavor,
  // whose features are both switch and data
  lazy val switchFeatures = features.filter( f => f.cdlType == PackageType || f.flavor == BoolFlavor || f.flavor == BoolDataFlavor )
  lazy val dataFeatures = features.filter( f => ( f.flavor == DataFlavor || f.flavor == BoolDataFlavor ) && f.cdlType != PackageType )

  lazy val boolFlavorFeatures = features.filter( _.flavor == BoolFlavor ).size
  lazy val booldataFlavorFeatures = features.filter( _.flavor == BoolDataFlavor ).size
  lazy val noneFlavorFeatures = features.filter( _.flavor == NoneFlavor ).size

  lazy val numericDataFeatures = types.filter( _._2 == "Number" ).map(_._1).map( i => model.nodesById.get(i).get )
  lazy val stringDataFeatures = types.filter( _._2 == "String" ).map(_._1).map( i => model.nodesById.get(i).get )

  // loads inferred types from CSV
  lazy val types: List[Tuple2[String, String]] = {
    val src = Source.fromInputStream( getClass.getResourceAsStream( "/types/" + model.modelName + "-types.csv" ) )
    val iter = src.getLines map{ _.stripLineEnd split "," }
    iter map{
      case Array(id,typ) => (id.trim,typ.trim)
    } toList
  }

  lazy val noneFeatures = features.filter( _.flavor == NoneFlavor ).size

  // all groups should contain more than one child
  lazy val orGroups = AnalysisHelpers.findGroups( model, "or" ).filter( _._2.size > 1)
  lazy val xorGroups = AnalysisHelpers.findGroups( model, "xor" ).filter( _._2.size > 1)
  lazy val mutexGroups = AnalysisHelpers.findGroups( model, "mutex" ).filter( _._2.size > 1)

  lazy val groupingWithConstraints = orGroups ::: xorGroups ::: mutexGroups

  lazy val leafDepthMap = features.filter( _->isLeaf ) map{ f => (f, f->depth)} toMap

  lazy val branchingMap = features map { f => (f, f.nchildren) } toMap
  lazy val siblingMap = features map { f => (f, f->siblings ) } toMap
  lazy val branchingAndSiblingMap = features map{ f => (f, (f.nchildren, f->siblings) ) } toMap

  lazy val avgHierarchyDepth =  ( (0 /: leafDepthMap.map(_._2) )( _ + _ ) ).toFloat / leafDepthMap.size.toFloat
  lazy val maxHierarchyDepth = ( 0 /: leafDepthMap.map(_._2) )( scala.math.max )

  // branching statistics, only for inner nodes
  lazy val maxBranchingInner = ( 0 /: branchingMap.map(_._2.size).filter( _ > 0) )( scala.math.max )
  lazy val minBranchingInner = ( 1 /: branchingMap.map(_._2.size).filter( _ > 0) )( scala.math.min )
  lazy val medianBranchingInner = median( branchingMap.map(_._2.size).filter( _ > 0).toSeq ).toString

  // branching statistics, for all nodes
  lazy val maxBranchingAll = ( 0 /: branchingMap.map(_._2.size) )( scala.math.max )
  lazy val minBranchingAll = ( 0 /: branchingMap.map(_._2.size) )( scala.math.min )
  lazy val medianBranchingAll = median( branchingMap.map(_._2.size).toSeq ).toString

    // sibling statistics, only for inner nodes
  lazy val siblingMapOfInnerNodes = siblingMap.filter( !_._1.children.isEmpty ).toMap
  lazy val maxSiblingsInner = ( 0 /: siblingMapOfInnerNodes.map( _._2.size ) ) ( scala.math.max )
  lazy val minSiblingsInner = ( 0 /: siblingMapOfInnerNodes.map( _._2.size ) )( scala.math.min )
  lazy val medianSiblingsInner = median( siblingMapOfInnerNodes.map( _._2.size ).toSeq ).toString

  // sibling statistics, all nodes
  lazy val maxSiblingsAll = ( 0 /: siblingMap.map( _._2.size ) ) ( scala.math.max )
  lazy val minSiblingsAll = ( 0 /: siblingMap.map( _._2.size ) )( scala.math.min )
  lazy val medianSiblingsAll = median( siblingMap.map( _._2.size ).toSeq ).toString

  lazy val innerNodes = features.filter( _.nchildren.size > 0 ).size
  lazy val leafNodes = features.filter( _.nchildren.isEmpty ).size
  lazy val oneChildNodes = features.filter( _.nchildren.size == 1 ).size
  lazy val twoChildNodes = features.filter( _.nchildren.size == 2 ).size


  // constraints
  lazy val declaringConstraints = features filter { f =>
    !( f.reqs.isEmpty && f.activeIfs.isEmpty && f.defaultValue == None && f.calculated == None && f.legalValues == None )
  }
  lazy val havingVisibilityCondition = features filterNot { _.activeIfs.isEmpty }

  lazy val featuresWithExplicitDefault = features filterNot { _.defaultValue == None }
  lazy val featuresWithExplicitDefaultUsingLiterals = featuresWithExplicitDefault filter { f => {
    val default = f.defaultValue.get
    default.isInstanceOf[Literal] || default == True() || default == False() }}
  lazy val featuresWithExplicitDefaultUsingExpressions = featuresWithExplicitDefault filterNot {
    featuresWithExplicitDefaultUsingLiterals contains _
  }

  lazy val derivedFeatures = features filterNot { _.calculated == None }
  lazy val derivedFeaturesUsingLiterals = derivedFeatures filter {
    _.defaultValue.get.isInstanceOf[Literal]
  }
  lazy val derivedFeaturesUsingExpressions  = derivedFeatures filterNot {
    derivedFeaturesUsingLiterals contains _
  }

  val featureConstraints = collectl{
    case Node( id,cdlType,_,_,_,dv,ca,lv,re,ai,_,_) => {
      var el = List[Constraint]()
      model.childParentMap.get( id ) match{
        case Some(n) => el = Parent( Identifier( n ) ) :: el
        case None => ;
      }
      if( ca != None ) el = Calculated( ca get ) :: el
      if( dv != None ) el = DefaultValue( dv get ) :: el
      if( lv != None ) el = LegalValues( lv get ) :: el
      re.foreach( e => el = Requires( e ) :: el )
      ai.foreach( e => el = ActiveIf( e ) :: el )
      (id, el)
    }
  }( model.rootNode )

  /**
   * all referenced IDs, including the parent feature
   */
  val referencedIDsPerFeature: Map[String,Set[String]] = featureConstraints map{ f =>
    ( f._1, collects{
      case Identifier( id ) => id
    }(f._2) )
  } toMap

  // helpers
  def p( a: Int ) = scala.math.round( ( a.toFloat / features.size.toFloat ) * 100 )

  def median( s: Seq[Int] ) = {
    val (lower, upper) = s.sortWith(_<_).splitAt(s.size / 2)
    if (s.size % 2 == 0) (lower.last + upper.head) / 2.0 else upper.head
  }

}

object TSE11Statistics{

  def apply( file: String ) =
    new TSE11Statistics( EcosIml.CupParser.parseFile( file ) )

  def apply( stream: InputStream, modelName: String ) =
      new TSE11Statistics( EcosIml.CupParser.parseStream( stream, modelName ) )

}