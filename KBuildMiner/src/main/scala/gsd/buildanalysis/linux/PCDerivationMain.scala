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

class PCDerivationMain( proj: Project ) extends Rewriter with TreeHelper with Logging with ExpressionUtils{

  case class Path( children: List[Path], node: BNode )

  def calculatePCs( tree: BNode ): Map[String,Expression] = {

    val objectFiles = collectl{
      case b@BNode( _, _, _, ObjectDetails( _, _, _, _, Some( sF ), _ ) ) => b
    }(tree)

    var ret = Map[String,Expression]()

    for( o <- objectFiles ){

      val oF = getSourceFile( o )
      debug( o toString )

      moveUpStrategy()(o) match{
        case Some( p: Path ) => {
          val exp = path2Exp(p)
          ret.get( oF ) match{
            case Some( e ) => ret += ( oF -> ( e | exp ) )
            case None => ret += ( oF -> exp )
          }
          debug( oF + ": " + PersistenceManager.pp( exp ) )
        }
        case _ => ;
      }

    }

    // finally, try to simplify all expressions
    Map( ret.map( x => ( x._1, simplify( x._2 ) ) ).toList: _* )

  }

  def path2Exp( p: Path ): Expression = {
    getExp( p ) &
            p.children.
              map( path2Exp ).
              foldLeft( True(): Expression )( _ | _ )
  }

  def getExp( p: Path ) = p.node.exp match{
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
            trace( b.ntype.toString + " --> " + PersistenceManager.getDetails( b ).toString )
        }

        t match{
          case b@BNode( RootNode, _, _, _ ) => Some( Path( List(), b ) )
          case b@BNode( _, _, _, _ ) => {
            val next = b->moveUp
            val res = next.map( moveUpStrategy( cache + t ) ).filter( _ != None )
            if( res isEmpty )
              None
            else{
              Some( Path( res.map( _ match{
                case Some( p:Path ) => p
                case _ => Predef error "Path expected"
              } ), b ) )
            }
          }
          case _ => None
        }
      }

    }

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