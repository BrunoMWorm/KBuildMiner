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

import model._
import kiama.rewriting.Rewriter
import gsd.common.Logging

object PCDerivationMain extends Rewriter with TreeHelper with Logging with ExpressionUtils{

  case class Path( children: List[Path], node: BNode, name: String )

  def calculatePCs( tree: BNode ): Map[String,Expression] = {

    val objectFiles = collectl{
      case b@BNode( _, _, _, ObjectDetails( _, _, _, _, Some( sF ), _ ) ) => b
    }(tree)

    var ret = Map[String,Expression]()

//    val sourceFiles = collects{
//      case ObjectDetails( _, _, _, _, Some( sF ), _ ) =>
//        (sF, BNode( SourceFileBNode, List(), None, SourceFileDetails( sF ) ) )
//    }(tree)
//
//    val paths = sourceFiles map{ case (sf, sfNode) => {
//        val ch = List flatten collectl{
//          case b@BNode( _, _, _, ObjectDetails( _, _, _, _, Some( name ), _ ) ) if name == sf =>{
//            moveUpStrategy()(b) match{
//              case Some( p: Path ) => p :: Nil
//              case None => Nil
//            }
//          }
//        }(tree)
//
//        Path( ch, sfNode, sf )
//      }
//    }
//
//    val ret = paths map ( p => ( p.name, path2Exp( p ) ) )

    for( o <- objectFiles ){

      val oF = getSourceFile( o )
      debug( "Trying to find path for: " + o )

      moveUpStrategy()(o) match{
        case Some( p: Path ) => {
          debug( "...path found!" )
          val exp = path2Exp( p )
          ret.get( oF ) match{
            case Some( e ) => ret += ( oF -> ( e | exp ) )
            case None => ret += ( oF -> exp )
          }
          debug( "Expression of path for object occurrence " + oF + ": " + PersistenceManager.pp( exp ) )
        }
        case _ => debug( "...no path found!" )
      }

    }

    // finally, try to simplify all expressions
    Map( ret.map{ case (a,b) => ( a, simplify(b) ) }.toList: _* )

  }

  /**
   * Convert path to expression...
   */
  def path2Exp( p: Path ): Expression =
    if( p.children isEmpty )
      getExp( p )
    else
      getExp( p ) &
            ( FALSE /: p.children.map( path2Exp ) ) ( _ | _ )

  private def getExp( p: Path ) = p.node.exp match{
    case Some(e) => e
    case None => True()
  }


  def moveUpStrategy(): Strategy = moveUpStrategy( Set[Term]() )

  def moveUpStrategy( cache: Set[Term] ): Strategy = new Strategy{

      def apply( t: Term ): Option[Term] ={

        if( cache.exists( _ eq t ) ) // check for object identity
          None

        t match{
          case b:BNode =>
            trace( "moveUpStrategy: visiting " + node2String(b) )
        }

        t match{
          case b@BNode( RootNode, _, _, _ ) => Some( Path( List(), b, "root" ) )
          case b@BNode( _, _, _, _ ) => {
            val next = b->moveUp
            val res = next.map( moveUpStrategy( cache + t ) ).filter( _ != None )
            if( res isEmpty )
              None
            else{
              Some( Path( res.map( _ match{
                case Some( p:Path ) => p
                case _ => Predef error "Path expected"
              } ), b, b.ntype.toString ) )
            }
          }
          case _ => None
        }
      }

    }

  private def node2String( b: BNode ) =
    b.ntype.toString + " --> " + PersistenceManager.getDetails( b ).toString

//  def moveUpStrategy( s: => ForkableStrategy ): Strategy =
//    new Strategy {
//      def apply( t: Term ): Option[Term] = {
//        t match{
//          case BNode( RootNode, _, _, _ ) => Some( s(t) )
//          case b@BNode( _, _, _, _ ) => {
//            s( b )
////            b->moveUp match{
////              case Nil => None
////              case fst :: tail => {
////                moveUpStrategy( s )( fst )
////
////              }
//            val next = b->moveUp
//            val res = next.map( s ).filter( _ == None )
//            if( res isEmpty )
//              None
//            else{
//              Path( res.map( _.get ) )
//            }
//
//
//            moveUpStrategy( s )( next.first )
//            Some( s( t ) )
//          }
//        }
//
//      }
//    }

}