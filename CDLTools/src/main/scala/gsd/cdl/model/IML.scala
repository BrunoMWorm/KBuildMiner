/*
 * Copyright (c) 2010 Steven She <shshe@gsd.uwaterloo.ca>
 * and Thorsten Berger <berger@informatik.uni-leipzig.de>
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
package gsd.cdl.model

import org.kiama.attribution.Attributable
import org.kiama.attribution.Attribution._
import org.kiama._
import org.kiama.rewriting.Rewriter._

case class IML( topLevelNodes: List[Node] ){

  private val _maps = TraverseHelper getMaps topLevelNodes

  /**
   * The synthetic root node
   */
  val rootNode = _maps._1
  /**
   * Map doesn't contain the root node, i.e. not in the keySet
   */
  val childParentMap = _maps._2
  /**
   * Map contains root node
   */
  val nodesById = _maps._3
  /**
   * List contains root node
   */
  val allNodes = _maps._4

}

case class Node(id : String,
                cdlType : CDLType,
                display : String,
                description : Option[String],
                flavor : Flavor,
                defaultValue : Option[CDLExpression], //TODO restrict to only literals
                calculated : Option[CDLExpression],
                legalValues : Option[LegalValuesOption],
                reqs : List[CDLExpression],
                activeIfs : List[CDLExpression],
                implements : List[CDLExpression], // just identifiers
                nchildren : List[Node]) extends Attributable

sealed abstract class Flavor
case object NoneFlavor extends Flavor{
  override def toString() = "none"
}
case object BoolFlavor extends Flavor{
  override def toString() = "bool"
}
case object DataFlavor extends Flavor{
  override def toString() = "data"
}
case object BoolDataFlavor extends Flavor{
  override def toString() = "booldata"
}

sealed abstract class CDLType
case object OptionType extends CDLType{
  override def toString() = "option"
}
case object ComponentType extends CDLType{
  override def toString() = "component"
}
case object PackageType extends CDLType{
  override def toString() = "package"
}
case object InterfaceType extends CDLType{
  override def toString() = "interface"
}

sealed abstract class Constraint
case class Parent( i: Identifier ) extends Constraint // we use it as a wrapper for parent implications, it's not a concept of CDL
case class Requires( e : CDLExpression ) extends Constraint
case class ActiveIf( e : CDLExpression ) extends Constraint
case class Calculated( e : CDLExpression ) extends Constraint
case class DefaultValue( e : CDLExpression ) extends Constraint
// workaround, since legal_values contain a list expression, not a goal expression
case class LegalValues( lv: LegalValuesOption ) extends Constraint


private object TraverseHelper{

  def getMaps( topLevelNodes: List[Node] ) ={
    val rn = Node( "root", ComponentType, "Synthetic root node", None,
                      NoneFlavor, None, None, None, List(), List(), List(), topLevelNodes )
    var cpm = Map[String,String]()
    var nbi = Map[String,Node]()
    var an = List[Node]()

    everywheretd( query {
      case node@Node(n,_,_,_,_,_,_,_,_,_,_,children) => {
        children.foreach( x => cpm += ( x.id -> n ) )
        nbi += (n -> node)
        an = node :: an
      } } )(rn)

    (rn, cpm, nbi, an)
  }
}

trait ImlTreeAttributes{

  val isRoot: Node ==> Boolean =
    attr{
      case n:Node => n.parent[Node] == null
    }

  val isLeaf: Node ==> Boolean =
    attr{
      case n:Node => n.children.isEmpty
    }

  val numchildren: Node ==> Int =
    attr{
      case Node(_,_,_,_,_,_,_,_,_,_,_,List()) => 0
      case Node(_,_,_,_,_,_,_,_,_,_,_,children) => children.size + ( 0 /: children )( (a,b) => a + (b->numchildren) )
    }

  val depth: Node ==> Int =
    attr{
      case n:Node =>
        if( n->isRoot )
          0
        else
          n.parent[Node]->depth + 1
    }

  val siblings: Node ==> List[Node] =
    attr{
      case n: Node =>
        if ( n->isRoot )
          Nil
        else
          n.parent[Node].nchildren.filterNot( _ == n )
    }

}