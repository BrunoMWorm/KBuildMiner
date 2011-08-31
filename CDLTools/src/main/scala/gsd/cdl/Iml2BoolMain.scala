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

import java.io.PrintStream
import model.CDLBooleanTransformation
import parser.EcosIml.CupParser

object Iml2BoolMain{

  def main( args: Array[String] ){

    if( args.size < 1 )
      sys.error( "Missing parameters: <iml file> [output file]" )

    else{
      val out = if (args.size > 1) new PrintStream( args(1) ) else System.out
      val model = CupParser parseFile args(0)
      val transform = new CDLBooleanTransformation( model )
      transform exportFormula out
    }

  }

}