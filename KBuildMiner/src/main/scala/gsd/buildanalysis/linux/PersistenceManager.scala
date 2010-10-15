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
package gsd.buildanalysis.linux

import kiama.rewriting.Rewriter
import gsd.common.Logging
import model._
import xml.{UnprefixedAttribute, Elem, Node, XML}


object PersistenceManager extends Rewriter with Logging{

  implicit def pimp( elem: Elem ) = new {
    import scala.xml.Null
    def %( attrs: Map[String,String] ) = {
      val seq = for( (n,v) <- attrs ) yield new UnprefixedAttribute( n, v, Null )
      ( elem /: seq ) ( _ % _ )
    }
  }

  def outputXML( root: BNode, targetFile: String ) {
    info( "Saving Build AST to: " + targetFile )
    XML.save( targetFile, getXml( root ) )
  }

  private def getXml( bn: BNode ): Node = ( bn match{
    case BNode( RootNode, ch, _, _ ) => <BuildRoot>{ getXml(ch) }</BuildRoot>
    case BNode( MakefileBNode, ch, _, _ ) => <Makefile>{ getXml(ch) }</Makefile>
    case BNode( IfBNode, ch, _, _ ) => <If>{ getXml(ch) }</If>
    case BNode( ObjectBNode, ch, _, _ ) => <Object>{ getXml(ch) }</Object>
    case BNode( TempReferenceBNode, ch, _, _ ) => <TempReference>{ getXml(ch) }</TempReference>
    case BNode( TempCompositeListBNode, ch, _, _ ) => <TempCompositeList>{ getXml(ch) }</TempCompositeList>
  } ) % getDetails( bn )

  private def getXml( ch: List[BNode] ): Seq[Node] = ch map getXml

//  implicit def toMap[A]( v: Tuple2[Option[A],String] ): Map[String,String] = v._1 match{
//    case Some( x ) => Map( v._2 -> x.toString )
//    case None => Map()
//  }
//
//  implicit def toMap[A]( v: Tuple2[A,String] ): Map[String,String] = Map( v._2 -> v._1.toString )

  def getDetails( b: BNode ): Map[String,String] = b match{
    case BNode( _, _, exp, details ) => m( exp, "expr" ) ++ ( details match{
      case MakefileDetails( mf ) =>
        m( mf, "path" )
      case ObjectDetails( oF, bA, ext, gen, sF, fP ) =>
        m( oF, "objectFile" ) ++ m( bA, "builtAs" ) ++
        m( ext, "extension" ) ++ m( gen, "generated" ) ++
        m( sF, "sourceFile" ) ++ m( fP, "fullPathToObject" )
      case TempReferenceDetails( v, sS ) =>
        m( v, "variable" ) ++ m( sS, "selectionSuffix" )
      case TempCompositeListDetails( lN, s ) =>
        m( lN, "listName" ) ++ m( s, "suffix" )
      case _ => Map.empty
    } )
  }
  
  private def m[A]( v: Option[A], label: String ): Map[String,String] = v match{
    case Some( x ) => m( x, label )
    case None => Map.empty
  }

  private def m[A]( v: A, label: String ): Map[String,String] = v match{
    case e:Expression => Map( label -> pp( e ) )
    case _ => Map[String,String]( label -> v.toString )
  }

  /**
   * pretty print expression
   */
  def pp( e: Expression ): String = e match{
    case BinaryExpression( a, b, s )  => "(" + pp(a) + " " + s + " " + pp(b) + ")"
    case UnaryExpression( a, s )      => s + pp(a)

    case Identifier( i )              => i
    case Defined( id )                => "defined(" + pp( id ) + ")"
    case IntLiteral( v )              => v toString
    case StringLiteral( v )           => "\"" + v + "\""

    case True()                       => "[TRUE]"
    case False()                      => "[FALSE]"

    case _                            => e toString
  }

}