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

import kiama.rewriting.Rewriter
import kiama.attribution.Attributable

case class IML( topLevelNodes: List[Node] ){

  private val _maps = TraverseHelper getMaps topLevelNodes

  val rootNode = _maps._1
  val childParentMap = _maps._2
  val nodesById = _maps._3
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
                children : List[Node])// extends Attributable

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
case class Parent( i: Identifier ) extends Constraint
case class Requires( e : CDLExpression ) extends Constraint
case class ActiveIf( e : CDLExpression ) extends Constraint
case class Calculated( e : CDLExpression ) extends Constraint
case class DefaultValue( e : CDLExpression ) extends Constraint
// workaround, since legal_values contain a list expression, not a goal expression
case class LegalValues( lv: LegalValuesOption ) extends Constraint


// cannot inherit from Rewriter in case class (due to kiama 0.9, fixed in later (scala 2.8) versions)
private object TraverseHelper extends Rewriter{
  def getMaps( topLevelNodes: List[Node] ) ={
    val rn = Node( "root", PackageType, "Our synthetic root node", None,
                      NoneFlavor, None, None, None, List(), List(), List(), topLevelNodes )
    var cpm = Map[String,String]()
    var nbi = Map[String,Node]()
    var an = List[Node]()

    everywheretd( query {
      case node@Node(n,_,_,_,_,_,_,_,_,_,_,children) => {
        children.foreach( x => cpm += ( x.id -> n ) )
        nbi += (n -> node)
        an = node :: an
      }
    } )(rn)

    (rn, cpm, nbi, an)
  }
}
