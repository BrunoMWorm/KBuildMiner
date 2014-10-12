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
import org.kiama.rewriting.Rewriter._
import gsd.common.Logging

object PCDerivation extends TreeHelper with Logging with ExpressionUtils{

  case class Path( children: List[Path], node: BNode, name: String )

  def calculateFilePCs( tree: BNode, manualPCs: Map[String,Expression], proj: Project ): Map[String,Expression] = {



//    val setSourceFileRule = everywheretd{
//      rule{
//        case b@BNode( ObjectBNode, ch, exp, ObjectDetails( oF, bA, ext, gen, lN, None, fP ) ) => {
//          val vars = varOcc.findAllIn( oF ).map{ case varOcc( v ) => v }
//          val sourceFile = proj.getSource( b, oF, gen )
////          sourceFile match{
////            case Some( f ) => f.rep
////          }
//          BNode( ObjectBNode, ch, exp, ObjectDetails( oF, bA, ext, gen, lN, sourceFile, fP ) )
//        }
//      }
//    }

    val usingVars = collectl{
      case b@BNode(_,_,_,ObjectDetails(oF,_,_,_,_,_,_)) if (b->uses).size>0 => {
//        trace("start getting incoming vars")
//        val i = ""
        val i = (b->in)
//        trace("end getting incoming vars")
        debug( "Using variables (" + oF + "): " + (b->uses) + " and getting in: " + i )
        b
      }
    }(tree)

    val definingVars = collectl{
      case b@BNode(_,_,_,_) if (b->defines).size>0 => {
        debug( "Defining variables: " + (b->defines) /*+ " and getting in: " + (b->in)*/ )
        b
      }
    }(tree)

    val objectFiles = collectl{
      case b@BNode( _, _, _, ObjectDetails( _, _, _, _, _, Some( sF ), _ ) ) => b
    }(tree)

    var ret = Map[String,Expression]()

    for( o <- objectFiles ){

      val oF = getSourceFile( o )
      debug( "Trying to find path for: " + o )

      calculateBNodePC( tree, o ) match{
        case Some( exp ) => {
          ret.get( oF ) match{
            case Some( e ) => ret += ( oF -> ( e | exp ) )
            case None => ret += ( oF -> exp )
          }
          debug( "Expression of path for object occurrence " + oF + ": " + PersistenceManager.pp( exp ) )
        }
        case None =>
          debug( "...no path found!" )
      }

    }

    // add manual PCs (or override derived ones with them)
    ret ++= manualPCs

    // finally, try to simplify all expressions
    Map( ret.map{ case (a,b) => ( a, simplify(b) ) }.toList: _* )

  }

  def calculateBNodePC( ast: BNode, node: BNode ): Option[Expression] =
    moveUpStrategy( node ) match{
      case Some( p: Path ) => Some( path2Exp( p ) )
      case None => None
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


  def moveUpStrategy: Strategy = moveUpStrategy( Set[Term]() )

  def moveUpStrategy( cache: Set[Term] ): Strategy = new Strategy{

      def apply( t: Term ): Option[Term] ={

        if( cache.exists( _ == t ) ) // previous check was for object identity, not supported in scala 2.9.1 anymore
          None

        t match{
          case b:BNode =>
            trace( "moveUpStrategy: visiting " + node2String(b) )
          case x =>
            sys.error("Unexpected: " + x )
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
                case Some( p:Path ) => {
                  if( p.node eq b.parent[BNode] )
                    p
                  else{
                    val ifs = findPath( b, p.node, List() )
                    (p /: ifs)( (aP,x) => Path( aP :: Nil, x, x.ntype.toString ) )
                  }
                }
                case _ => sys error "Path expected"
              } ), b, b.ntype.toString ) )
            }
          }
          case _ => None
        }
      }

    }

  def findPath( source: BNode, target: BNode, path: List[BNode] ): List[BNode] = {
    val newPath = if( source.ntype == IfBNode )
      source :: path
    else
      path

    val parent = source.parent[BNode]

    if( parent.subnodes.exists( _ eq target ) )
      newPath
    else{
      if( parent.ntype == MakefileBNode )
        Nil
      else
        findPath( parent, target, newPath )
    }
  }


  private def node2String( b: BNode ) =
    b.ntype.toString + " --> " + PersistenceManager.getDetails( b ).toString

  // TODO: FIXME
  val resolveUnknownExpressions = everywheretd{
    rule{
      case b@BNode( IfBNode, _, Some(
        UnknownExpression( Not( Eq( Identifier( i ), StringLiteral( "" ) ) ) )
      ), _ ) if( i endsWith "-y" ) => {
        val listName = i.substring( 0, i.length - 2 )
        TRUE
      }
    }
  }

}