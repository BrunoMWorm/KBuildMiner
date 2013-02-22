package gsd.cdl.tse11

import gsd.cdl.model._
import io.Source
import gsd.cdl.AnalysisHelpers
import org.kiama.rewriting.Rewriter._
import scala.Tuple2
import gsd.cdl.model.ActiveIf
import gsd.cdl.model.IML
import gsd.cdl.model.DefaultValue
import gsd.cdl.model.Identifier
import scala.Some
import gsd.cdl.model.Node
import gsd.cdl.model.False
import gsd.cdl.model.Calculated
import gsd.cdl.model.Requires
import scala.Tuple2
import gsd.cdl.model.LegalValues
import gsd.cdl.model.Parent
import gsd.cdl.model.True
import gsd.cdl.parser.EcosIml
import java.io.{File, InputStream}

/**
 * Created with IntelliJ IDEA.
 * User: berger
 * Date: 04.02.13
 * Time: 11:59
 * To change this template use File | Settings | File Templates.
 */
class MultiModelTSE13Statistics(val models: List[IML]){

  val allStats = models map { x => new TSE11Statistics(x) }

//  def aggr( s: TSE11Statistics => Int ): Int ={
//    val (lower, upper) = allStats.map( s ).sortWith( _ < _ ).splitAt(allStats.size / 2)
//    if (allStats.size % 2 == 0) (lower.last + upper.head) / 2.0 else upper.head
//  }

  // mean
  def aggr( s: TSE11Statistics => Int ): Int = {
    val sum = (0 /: allStats.map( s ))( _ + _ )
    sum / allStats.size
  }

  def faggr( s: TSE11Statistics => Float ): Float = {
      val sum = (0.toFloat /: allStats.map( s ))( _ + _ )
      sum / allStats.size.toFloat
    }

  lazy val features = aggr( _.features.size )

  lazy val switchFeatures = aggr( _.switchFeatures.size )
  lazy val dataFeatures = aggr( _.dataFeatures.size )

  lazy val boolFlavorFeatures = aggr( _.boolFlavorFeatures )
  lazy val booldataFlavorFeatures = aggr( _.booldataFlavorFeatures )
  lazy val noneFlavorFeatures = aggr( _.noneFlavorFeatures )
//
//  lazy val numericDataFeatures = types.filter( _._2 == "Number" ).map(_._1).map( i => model.nodesById.get(i).get )
//  lazy val stringDataFeatures = types.filter( _._2 == "String" ).map(_._1).map( i => model.nodesById.get(i).get )
//
//  // loads inferred types from CSV
//  lazy val types: List[Tuple2[String, String]] = {
//    val src = Source.fromInputStream( getClass.getResourceAsStream( "/types/" + model.modelName + "-types.csv" ) )
//    val iter = src.getLines map{ _.stripLineEnd split "," }
//    iter map{
//      case Array(id,typ) => (id.trim,typ.trim)
//    } toList
//  }
//
  lazy val noneFeatures = aggr( _.noneFeatures )

  // syntactic grouping features
  lazy val groupingFeatures = aggr( _.groupingFeatures.size )
  // configurator (actual) grouping features
  lazy val configuratorGroupingFeatures = aggr( _.configuratorGroupingFeatures.size )

  // individual feature purpose statistics
  lazy val userFeatures = aggr( _.userFeatures.size )

  // all groups should contain more than one child
  lazy val orGroups = aggr( _.orGroups.size )
  lazy val xorGroups = aggr( _.xorGroups.size )
  lazy val mutexGroups = aggr( _.mutexGroups.size )

  lazy val groupingWithConstraints = aggr( _.groupingWithConstraints.size )

  /*
  contains all features prefixed with the model name
   */
  lazy val leafDepthMap = (Map[String,Int]() /: allStats.map( s => ( s.model.modelName, s.leafDepthMap ) ))( (acc,b) => acc ++ b._2.map{ case (x,y) => ( b._1 + "_" + x.id, y ) } )
//  lazy val branchingMap =
//  lazy val siblingMap =
  lazy val branchingAndSiblingMap = ( Map[String,Tuple2[Int,Int]]() /: allStats.map( s => ( s.model.modelName, s.branchingAndSiblingMap )) )( (acc,b) => acc ++ b._2.map{ case(x,y) => (b._1 + "_" + x.id, (y._1.size, y._2.size) ) } )

  lazy val avgHierarchyDepth =  faggr( _.avgHierarchyDepth )
  lazy val maxHierarchyDepth = aggr( _.maxHierarchyDepth )

  // branching statistics, only for inner nodes
  lazy val maxBranchingInner = aggr( _.maxBranchingInner )
  lazy val minBranchingInner = aggr( _.minBranchingInner )
  lazy val medianBranchingInner = faggr( _.medianBranchingInner )

  // branching statistics, for all nodes
  lazy val maxBranchingAll = aggr( _.maxBranchingAll )
  lazy val minBranchingAll = aggr( _.minBranchingAll )
  lazy val medianBranchingAll = faggr( _.medianBranchingAll )

    // sibling statistics, only for inner nodes
//  lazy val siblingMapOfInnerNodes =
  lazy val maxSiblingsInner = aggr( _.maxSiblingsInner )
  lazy val minSiblingsInner = aggr( _.minSiblingsInner )
  lazy val medianSiblingsInner = faggr( _.medianSiblingsInner )

  // sibling statistics, all nodes
  lazy val maxSiblingsAll = aggr( _.maxSiblingsAll )
  lazy val minSiblingsAll = aggr( _.minSiblingsAll )
  lazy val medianSiblingsAll = faggr( _.medianSiblingsAll )

  lazy val innerNodes = aggr( _.innerNodes )
  lazy val leafNodes = aggr( _.leafNodes )
  lazy val oneChildNodes = aggr( _.oneChildNodes )
  lazy val twoChildNodes = aggr( _.twoChildNodes )


  // constraints
  lazy val declaringConstraints = aggr( _.declaringConstraints.size )
  lazy val havingVisibilityCondition = aggr( _.havingVisibilityCondition.size )

  lazy val havingConfigurationConstraints = aggr( _.havingConfigurationConstraints.size )

  lazy val havingValueRestrictions = aggr( _.havingValueRestrictions.size )

  lazy val featuresWithExplicitDefault = aggr( _.featuresWithExplicitDefault.size )
  lazy val featuresWithExplicitDefaultUsingLiterals = aggr( _.featuresWithExplicitDefaultUsingLiterals.size )
  lazy val featuresWithExplicitDefaultUsingExpressions = aggr( _.featuresWithExplicitDefaultUsingExpressions.size )

  lazy val derivedFeatures = aggr( _.derivedFeatures.size )
  lazy val derivedFeaturesUsingLiterals = aggr( _.derivedFeaturesUsingLiterals.size )
  lazy val derivedFeaturesUsingExpressions = aggr( _.derivedFeaturesUsingExpressions.size )

//  lazy val featureConstraints =

//  lazy val featureConstraintsInclParent =

  // contains all features from all models, where the feature name is prefixed with the model name
  lazy val referencedIDsPerFeature = (Map[String,Int]() /: allStats.map{ s => (s.model.modelName, s.referencedIDsPerFeature) } )( (acc,b) => acc ++ b._2.map{ case (x,y) => ( b._1 + "_" + x, y.size ) } )
  lazy val referencedIDsPerFeatureInclParent = (Map[String,Int]() /: allStats.map{ s => (s.model.modelName, s.referencedIDsPerFeatureInclParent) } )( (acc,b) => acc ++ b._2.map{ case (x,y) => ( b._1 + "_" + x, y.size ) } )
  lazy val referencedIDsPerFeatureAll = referencedIDsPerFeatureInclParent map{ case(f,all) =>
    (f,all, referencedIDsPerFeature.get(f).get )
  }

  // an approximation of Marcilio's CTCR metric
  lazy val ctcr = aggr( _.ctcr )
  lazy val ctcrP = aggr( _.ctcrP )

}

object MultiModelTSE13Statistics{

  def apply( files: List[File] ) =
    new MultiModelTSE13Statistics( files.map( f => { println("parsing " + f.getName); EcosIml.CupParser.parseFile( f.getPath )} ) )



}