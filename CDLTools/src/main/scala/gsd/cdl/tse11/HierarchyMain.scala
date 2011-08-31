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

package gsd.cdl.tse11

import gsd.cdl.model.{ImlTreeAttributes, Node}
import org.kiama.util.PrettyPrinter
import gsd.cdl.parser.EcosIml.CupParser

object HierarchyMain extends ImlTreeAttributes with PrettyPrinter{

  def main(args: Array[String]) {

    if (args.size < 1){
      System.err println "Usage: HierarchyMain <iml-file>"
      System exit 1
    }

    val iml = CupParser parseFile args(0)
    println( pretty( iml.rootNode ) )

  }

  def pretty( t: Node ) =
      super.pretty (show (t))

  def show( t: Node): Doc =
      t match {
        case n@Node(i,ntype,display,_,_,_,_,_,_,_,_,children) =>
          text( if( display == "") i else display ) <+>
          text( "(" + ntype + ")" ) <+>
          text("[") <> value( n->numchildren ) <> text("]") <>
          show( children )
      }

  def show( nodes: Seq[Node]): Doc =
    if( nodes isEmpty )
      empty
    else
      nest (line <> ssep (nodes map show, line))


//    everywheretd{
//      query{
//        case n:Node => println( n.id + "[" + ( n->numchildren ) + "]")
//      }
//    }(rootNode)

}