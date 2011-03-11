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

import model._
import util.parsing.input.PagedSeqReader
import collection.immutable.PagedSeq
import kiama.rewriting.Rewriter
import io.Source
import java.io.{PrintWriter, FileWriter, File}
import model.CDLExpressionConversions

object CDLBooleanTranslationMain extends IMLParser with Rewriter{

  var nodesById = Map[String,Node]()

  def main( args: Array[String] ){

//    parseAll(cdl, new PagedSeqReader(PagedSeq fromFile "iml_temp.txt")) match{
    parseAll(cdl, new PagedSeqReader(PagedSeq fromFile "../ecos/output/pc_vmWare_iml.txt")) match{
      case Success(res,_) => doRewritingAndExport( res )
      case x => println( "failure: " + x )
    }

  }

  def doRewritingAndExport( topLevelNodes:List[Node] ){

    val allNodes = collectl{
      case n:Node => n
    }(topLevelNodes)

    println("-------------------")
    println("finished...")

    // populate nodesById map
    allNodes.foreach( n => nodesById += (n.id -> n) )

    writeToCSV( allNodes, new File( "output/constraints_with_stats.csv") )

    val formula = getFormula( topLevelNodes )

    println( "\n================================================" )
    println( "The formula:")
    formula.foreach( println )

    exportFormulaFile( allNodes, formula, new File( "output/formula_i386.txt" ) )

    createTagFile( topLevelNodes )
  }

  def exportFormulaFile( allNodes : List[Node], conjuncts : List[CDLExpression], f:File ){
    val out = new PrintWriter( new FileWriter( f ) )
    allNodes.foreach( n => out.println( "@ " + n.id ) )
    conjuncts.foreach( out.println )
    out.close
  }

  def createTagFile( topLevelNodes : List[Node] ){
    val out = new PrintWriter( new FileWriter( "output/i386_siblings.tags" ) )
    everywheretd{ query {
      case Node(n,_,_,_,_,_,_,_,_,_,_,children) if( !children.isEmpty ) =>
          out.println( n + ": " + children.map( _.id ).reduceLeft[String]( (a,b) => a + " " + b ) )
    }}( Node("1",PackageType,"",None,BoolDataFlavor,None,None,None,List(),List(),List(),topLevelNodes) )
    out.close
  }

  def writeToCSV( nodes:List[Node], f:File ){

    def getVarTypes( e : CDLExpression ) =
      collectl{
        case Identifier(id) => nodesById.get( id ) match {
          case Some( n ) => ( n.cdlType.toString, n.flavor.toString )
          case None => ("unloaded","unloaded")
        }
      }(e)

    val out = new PrintWriter( new FileWriter( f ) )

    for( val n <- nodes ){
      out.println( n.id + ";" + n.cdlType + ";" + n.flavor )

      val printConstraints = ( x:( List[CDLExpression], String ) ) => {
        var ret = ""
        for( val c <- x._1 ){
          ret += " ;" + n.cdlType + "; ;" + x._2 + ";" + c + ";"
          ret += getVarTypes( c ).foldLeft("")( (a,b) => a + ( if( a!="" ) "," else "" ) + b._1 )
          ret += ";"
          ret += getVarTypes( c ).foldLeft("")( (a,b) => a + ( if( a!="" ) "," else "" ) + b._2 )
          ret += ";;;" // leave two columns for manually rewritten constraints
          ret += n.id implies rewriteConstraint( c )

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



  implicit def string2Expression( s : String ):CDLExpression = Identifier( s )
  implicit def node2Expression( n : Node ):CDLExpression = Identifier( n.id )


  def getFormula( topLevelNodes:List[Node] ) = {

    // 1st: start with constraints
    // save constraints per node in a map:
    var constraintsPerNode = Map[String, CDLExpression]()
    // get cross-tree constraints conjuncts
    val constraintConjuncts = List.flatten( collectl{
      case n:Node => {
        val constraints = rewriteConstraints( n )
        constraintsPerNode += ( n.id -> makeConjunct( constraints ) )
        constraints.map( n.id implies _ )
      }
    }(topLevelNodes) )

    // 2nd: get the hierarchy constraints
    var childParentMap = Map[String,String]()
    // get hierarchy conjuncts
    val hierarchyConjuncts = collectl{
      case Node(n,_,_,_,_,_,_,_,_,_,_,children) =>  {
          children.foreach( x => childParentMap += ( x.id -> n ) )
          val optional = children.foldLeft( True():CDLExpression )( (a,b) => a & (b implies n) )
          val mandat = children.filter( _.cdlType != InterfaceType ).filter( _.cdlType != PackageType).
                  filter( x => x.flavor == NoneFlavor || x.flavor == DataFlavor ).
                                      foldLeft( True():CDLExpression )( (a,b) => a & ( (n & constraintsPerNode( b.id ) ) implies b ) )
          optional & mandat
        }
    }(topLevelNodes)


    // all top-level features are mandatory
    // FIXME: not really, still depends on their cross-tree constraints
    // so, we should add them, but currently, it's still sufficient....
    val topLevelMandatoryConjuncts = topLevelNodes.map( _.id )

    // process interfaces
    val interfaceConjuncts = collectl{
        case Node(n,InterfaceType,_,_,fl,_,_,_,_,_,_,_) if( fl == BoolFlavor || fl == BoolDataFlavor ) => {
          // you can only understand the following if you're stoned (or drunk, depending on your preference)
          ( n implies makeDisjunct( impls( n ) ) ) &
          ( childParentMap( n ) & constraintsPerNode( n ) & makeDisjunct( impls( n ) ) ) implies n
          
          // FIXME: childParentMap could return none, but haven't seen any interface as top-level node so far
        }
        case Node(n,InterfaceType,_,_,DataFlavor,_,_,_,_,_,_,_) => {
          // any constraint on a data interface just affects the data value and is used for grouping
          // thus, independent of its constraints and the state of the implementors, it is always
          // active and enabled when the parent is (mandatory) -> thus, imposing its group constraint to the model
          ( childParentMap( n ) ) implies n

          // FIXME: childParentMap could return none, but haven't seen any interface as top-level node so far
        }
    }(topLevelNodes)

    println( "============= \n interface conjuncts:" )
    interfaceConjuncts.foreach( println )
    println( "=============" )

    val conjuncts = hierarchyConjuncts ::: constraintConjuncts ::: interfaceConjuncts
//    val conjuncts = hierarchyConjuncts ::: constraintConjuncts ::: topLevelMandatoryConjuncts ::: interfaceConjuncts
    val simplifiedConjuncts = conjuncts.map( rewrite( cleanupRule <* simplifyRule )( _ )  )  // try to further simplify; also
                                                                                             // cleanup again (have some LoadedIdentifiers
                                                                                             // still in there due to interface constraints
    simplifiedConjuncts.filter( _ != True() )
  }

  def makeConjunct( l : List[CDLExpression] ) =
    l.foldLeft( True(): CDLExpression )( (a,b) => a & b )

  def makeDisjunct( l : List[CDLExpression] ) =
    l.foldLeft( False(): CDLExpression )( (a,b) => a | b )

  def rewriteConstraints( n:Node ) = {
    var ret = List[CDLExpression]()
    for( val c <- ( n.activeIfs ::: n.reqs ) ){
      print( c.toString + " ====> ")
      val newConstraint = rewriteConstraint( c )
      println( newConstraint )
      println( "---")
      ret = newConstraint :: ret
    }
    ret
  }

  def rewriteConstraint( e : CDLExpression ) =
    rewrite( resolveIdentifierRule <*
             removeUnloadedIdentifierRule <*
             interfaceReferenceRule <*  // important to place it before equalsRule!
             equalsRule <*
             plainIntegerRule <* // important to place after equalsRule!
             simplifyRule <*
             dropNonBooleanConstraints <*
             cleanupRule )( e )


  val resolveIdentifierRule = everywheretd {
    rule{
      case Identifier( id ) => nodesById.get( id ) match {
        case Some( n ) => LoadedIdentifier( id, n.cdlType, n.flavor )
        case None => UnloadedIdentifier( id ) 
      }
    }
  }

  val removeUnloadedIdentifierRule = everywheretd {
    rule{
      case UnloadedIdentifier( id ) => False()
    }
  }

    val interfaceReferenceRule = everywheretd {
    rule{

      // For these interfaces, it's important to always reference the interface symbol in the conjunction
      // as well, since the interface can have additional constraints (at least the parent constraint)
      // that could disable it, In this case, the states of all the nodes implementing the interface don't
      // play a role at all.

      case Eq( interf@LoadedIdentifier( id, InterfaceType, _ ), IntLiteral( 0 ) ) => !interf
      //                    impls( id ).foldLeft( True():CDLExpression )( (a,b) => a & !b )

      //      case GreaterThan( LoadedIdentifier( id, InterfaceType, _ ), IntLiteral( 0 ) ) =>
      //                    impls( id ).foldLeft( False():CDLExpression )( (a,b) => a | b )
      //      case NEq( LoadedIdentifier( id, InterfaceType, _ ), IntLiteral( 0 ) ) =>
      //                    impls( id ).foldLeft( False():CDLExpression )( (a,b) => a | b )
      //      case NEq( IntLiteral( 0 ), LoadedIdentifier( id, InterfaceType, _ ) ) =>
      //                    impls( id ).foldLeft( False():CDLExpression )( (a,b) => a | b )


      // or groups
      // here, it's sufficient to reference the interface node, since the or constraint is already
      // incorporated in the interface constraint, introduced in the getFormula() method
      case GreaterThan( interf@LoadedIdentifier( id, InterfaceType, _ ), IntLiteral( 0 ) ) => interf
      case GreaterThanOrEq( interf@LoadedIdentifier( id, InterfaceType, _ ), IntLiteral( 1 ) ) => interf
      case NEq( interf@LoadedIdentifier( id, InterfaceType, _ ), IntLiteral( 0 ) ) => interf
      case NEq( IntLiteral( 0 ), interf@LoadedIdentifier( id, InterfaceType, _ ) ) => interf

      // mutex group: interface <= 1
      // don't reference the interface symbol here!
      case GreaterThanOrEq( IntLiteral( 1 ), interf@LoadedIdentifier( id, InterfaceType, _ ) ) =>
                    mutex( impls( id ) )

      case Eq( interf@LoadedIdentifier( id, InterfaceType, _ ), IntLiteral( 1 ) ) =>
        interf & xor( impls( id ) )
      case Eq( IntLiteral( 1 ), interf@LoadedIdentifier( id, InterfaceType, _ ) ) =>
        interf & xor( impls( id ) )

    }
  }

  val equalsRule = everywheretd {
    rule{                   
      case Eq( id:LoadedIdentifier, StringLiteral( v ) ) if v != "" => id
      case Eq( id:LoadedIdentifier, IntLiteral( v ) ) if v != 0 => id
      case Eq( id:LoadedIdentifier, IntLiteral( 0 ) ) => !id
      case NEq( IntLiteral( 0 ), id:LoadedIdentifier ) => id
      case GreaterThan( id:LoadedIdentifier, IntLiteral( 0 ) ) => id
      case FunctionCall("is_substr", (id:LoadedIdentifier) :: StringLiteral( v ) :: _ ) => id
      case FunctionCall("is_substr", False() :: StringLiteral( v ) :: _ ) => False()
    }
  }

  val plainIntegerRule = attempt {
    rule{
      case IntLiteral( 0 ) => False()
    }
  }

  val simplifyRule = everywherebu {
    rule{
      case Or( False(), e ) => e
      case Or( e, False() ) => e
      case Not( False() ) => True()
      case Implies( e, True() ) => True()
      case And( False(), _ ) => False()
      case And( _, False() ) => False()
      case And( e, True() ) => e
      case Implies( False(), _ ) => True()
      case Implies( e, False() ) => !e
    }
  }

  val cleanupRule = everywheretd {
    rule{
      case LoadedIdentifier( id, _, _ ) => Identifier( id )
    }
  }

  val isItcompleteyBooleanNow = topdownS({
    rule{
      case t:And => t
      case t:Or => t
      case t:Implies => t
      case t:True => t
      case t:False => t
      case t:LoadedIdentifier => t
      case t:Not => t
    }
  }, { s => all(all(failure)) }
  )

  val returnTrue = rule{
    case _ => True()
  }

  val dropNonBooleanConstraints = isItcompleteyBooleanNow <+ returnTrue // if first rule fails, execute second one

  implicit def iterator2List( it : Iterator[LoadedIdentifier] ) : List[LoadedIdentifier] = {
    var ret = List[LoadedIdentifier]()
    for( l <- it ) ret = l :: ret
    ret
  }

  def impls( interfaceID : String ) : List[LoadedIdentifier] =
    nodesById.values.
      filter( _.implements contains Identifier( interfaceID ) ).
        map( x => LoadedIdentifier( x.id, x.cdlType, x.flavor ) )

  def xor( ids : List[LoadedIdentifier] ) = {
    val disjuncts = for( i <- ids ) yield ids.filter( _ != i ).foldLeft( i:CDLExpression )( ( a,b) => a & !b )
    disjuncts.foldLeft( False():CDLExpression )( (a,b) => a | b )
  }

  def mutex( ids : List[LoadedIdentifier] ) = {
    val disjuncts = for( i <- ids ) yield i implies ids.filter( _ != i ).foldLeft( True():CDLExpression )( ( a,b ) => a & !b )
    disjuncts.foldLeft( True():CDLExpression )( (a,b) => a & b )
  }



  //    def nodesWithoutInterfaces(s : => Strategy) : Strategy =
  //      new Strategy {
  //          def apply (t : Term) : Option[Term] = {
  ////            println( ">" + t )
  //              t match {
  //                  case LoadedIdentifier(n,t,fl) => Some( True() )
  //                  case _ => s( t )
  //              }
  //          }
  //      }


}