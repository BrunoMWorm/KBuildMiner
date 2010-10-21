/*
 * Copyright (c) 2010 Thorsten Berger <berger@informatik.uni-leipzig.de>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package gsd.buildanalysis.linux.model

import gsd.buildanalysis.linux.Expression
import kiama.attribution.Attribution._
import kiama.attribution.Attributable
import kiama.rewriting.Rewriter

/**
 * Represents the AST we derive from the whole build system.
 */
case class BNode( ntype: BNodeType,
                  subnodes: List[BNode],  // "children" collides with a field in Attributable
                  exp: Option[Expression],
                  details: BNodeDetails ) extends Attributable

sealed abstract class BNodeType

case object RootNode extends BNodeType
case object IfBNode extends BNodeType
case object MakefileBNode extends BNodeType
case object ObjectBNode extends BNodeType
case object TempCompositeListBNode extends BNodeType
case object TempReferenceBNode extends BNodeType


sealed abstract class BNodeDetails

case object NoDetails extends BNodeDetails

case class ObjectDetails( objectFile: String,
                          built_as: Option[String],
                          extension: String,
                          generated: Boolean,
                          sourceFile: Option[String],
                          fullPathToObject: Option[String] ) extends BNodeDetails

case class MakefileDetails( makefile: String ) extends BNodeDetails

case class TempReferenceDetails( variable: String,
                                 selectionSuffix: String ) extends BNodeDetails

case class TempCompositeListDetails( listName: String,
                                     suffix: Option[String] ) extends BNodeDetails

/**
 * Attribute grammar implementation...
 */
trait TreeHelper extends Rewriter{

  /**
   * Attribute that returns the "Makefile scope" of nodes, i.e. the next
   * Makefile node in the hierarchy (without current node).
   */
  val mfScope: BNode ==> BNode =
    attr{
      case BNode( RootNode, _, _, _ ) =>
        Predef.error( "No containing Makefile found!" )
      case b => b.parent[BNode] match{
        case p@BNode( MakefileBNode, _, _, _ ) => p
        case _ => b.parent[BNode]->mfScope
      }
    }

  /**
   * Attribute that calculates possible predecessors in terms of control and
   * data flow.
   */
  val moveUp: BNode ==> List[BNode] =
    attr{
      case BNode( RootNode, _, _, _ ) => List()
      case b@BNode( TempCompositeListBNode, _, _, TempCompositeListDetails( ln, _ ) ) =>{
        val compositeObjects = findCompositeObjectNodes( ln, b->mfScope )
        val referenceNodes = findTempReferenceNodes( ln, b->mfScope )
        compositeObjects ::: referenceNodes
      }
      case b:BNode => b.parent[BNode] :: Nil
    }

  private def findCompositeObjectNodes( listName: String, scope: BNode ): List[BNode] =
    collectl{
      case b@BNode( ObjectBNode, _, _, ObjectDetails( oF, _, _, false, None, _ ) ) if oF == listName => b
    }( scope )

	/**
	 * Ignore references in patterns like in crypto/Makefile
	 * crypto_algapi-$(CONFIG_PROC_FS) += proc.o
	 * crypto_algapi-objs := algapi.o scatterwalk.o $(crypto_algapi-y)
	 * obj-$(CONFIG_CRYPTO_ALGAPI2) += crypto_algapi.o
	 *
	 * i.e. ignore the inclusion of $(list-y) in list-objs, since it's unnecessary and doesn't affect variability conditions (and causes troubles...)
	 *
   */
  private def findTempReferenceNodes( name: String, scope: BNode ): List[BNode] =
    collectl{
      case b@BNode( TempReferenceBNode, _, _, TempReferenceDetails( variable, _ ) )
        if( variable == name && ( b.parent[BNode] match{
          case BNode( _, _, _, TempCompositeListDetails( lN, _ ) ) if lN == variable => false
          case _ => true
        } ) ) => b
    }( scope )

  def getSourceFile( b: BNode ): String = b match{
    case BNode( _, _, _, ObjectDetails( _, _, _, _, Some( sF ), _ ) ) => sF
    case _ => Predef.error( "Not an ObjectBNode with an associated source file!" )
  }

//
//  def findShortestPathTo( start: BNode, target: BNode, scope: BNode ) ={
//
//  }

}