/*
 * Copyright (c) 2010 Thorsten Berger <berger@informatik.uni-leipzig.de>
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
package gsd.cdl.test

import org.junit.Test
import org.junit.runner.RunWith
import org.scalatest.junit.{JUnitRunner, AssertionsForJUnit}
import org.scalatest.FunSuite
import gsd.cdl.CDLBooleanTranslationMain
import gsd.cdl.model._

@RunWith(classOf[JUnitRunner])
class RewriterTest extends FunSuite{

  implicit def string2LI( i : String ) = LoadedIdentifier( i, InterfaceType, DataFlavor )

  val vars = List[LoadedIdentifier]("a", "b" , "c" )

  test("test XOR"){
    assert( CDLBooleanTranslationMain.xor( vars ) ===
            (((("a" & !"b") & !"c") | (("b" & !"a") & !"c")) | (("c" & !"a") & !"b")) )
  }

  test( "test MUTEX" ){
    assert( CDLBooleanTranslationMain.mutex( vars ) ===
            ((("a" implies (!"b" & !"c")) & ("b" implies (!"a" & !"c"))) & ("c" implies (!"a" & !"b"))) )
  }

}