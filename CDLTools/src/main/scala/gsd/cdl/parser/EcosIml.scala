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

package gsd.cdl.parser

import combinator.IMLParser
import gsd.cdl.model.IML
import gsd.iml.parser.ImlParser
import java_cup.ImlFeatureListToImlNodeList
import collection.immutable.PagedSeq
import util.parsing.input.PagedSeqReader
import java.io.InputStream

object EcosIml {

  /**
   * The new Java Cup-based parser. Cup is an LALR parser generator.
   * This implementation is an extended version of
   * the older parser combinator, and can handle more constraints
   * (from all eCos architectures) and is way faster.
   * Credits to Leonardo Passos <lpassos@gsd.uwaterloo.ca>
   */
  object CupParser{
    
    def parseFile( file: String ) =
      IML( ImlFeatureListToImlNodeList( ImlParser parse file ) )
    
    def parseStream( in: InputStream ) =
      IML( ImlFeatureListToImlNodeList( ImlParser parse in ) )
  }

  /**
   * The older parser combinator.
   * Credits to Steven She <shshe@gsd.uwaterloo.ca>
   */
  object CombinatorParser extends IMLParser{

    def parseString(s : String) =
      parseAll(cdl, s) match {
        case Success(res,_) => IML( res )
        case x => sys.error( x toString )
      }

  def parseFile(file : String) =
    parseAll(cdl, new PagedSeqReader(PagedSeq fromFile file)) match {
      case Success(res,_) => IML( res )
      case x => sys.error( x toString )
    }
  }

}
