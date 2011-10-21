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

package gsd.cdl

import model._
import org.kiama.rewriting.Rewriter._
import collection.mutable

object AnalysisHelpers {

  def findGroups( model: IML, gtype : String ) = collectl{

        case Node(n,_,_,_,_,_,_,_,_,_,_,children) => {

          val groupedNodesOnThisLevel = mutable.Map[Node,List[Node]]()

          for( c <- children ){
            val implements = c.implements.map( _ match {
              case Identifier( id ) => model.nodesById.get( id )
              case _ => None }
            ).filter( _ != None ).map( _.get )

            val constrainedInterfaces = gtype match{
              case "xor" => implements.filter( interf =>
                              interf.reqs.contains( Eq( Identifier(interf.id), LongIntLiteral( 1 ) ) ) ||
                              interf.reqs.contains( Eq( LongIntLiteral( 1 ), Identifier(interf.id) ) ) )
              case "mutex" => implements.filter( interf =>
                              interf.reqs.contains( LessThanOrEq( Identifier(interf.id), LongIntLiteral( 1 ) ) ) ||
                              interf.reqs.contains( GreaterThanOrEq( LongIntLiteral( 1 ), Identifier(interf.id) ) ) )
              case "or" => implements filter { interf => interf.reqs exists { _ match{
                case GreaterThanOrEq( Identifier( interf.id ), LongIntLiteral( 1 ) ) => true
                case GreaterThan( Identifier( interf.id ), LongIntLiteral( 0 ) ) => true
                case LessThan( LongIntLiteral(0), Identifier( interf.id ) ) => true
                case LessThanOrEq( LongIntLiteral(1), Identifier( interf.id ) ) => true
                case _ => false
              }}}
            }


            constrainedInterfaces.foreach( ri => groupedNodesOnThisLevel.get( ri ) match {
              case Some( i ) => groupedNodesOnThisLevel+=( ri -> ( c :: i ) )
              case None => groupedNodesOnThisLevel += ( ri -> ( c :: Nil ) )
            })

          }

          groupedNodesOnThisLevel.toList
      }

    }(model.rootNode).filter( !_.isEmpty ).flatten


}