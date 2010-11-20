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

import java.io.File
import model.BNode

abstract class Project( val basedir: String ){

  def getTopMakefileFolders: List[String]

  protected def getOverrideFolder: String

  def getHandle( relativePathToMakefile: String ): File = {
    var checkOverride = new File( getOverrideFolder + "/" + relativePathToMakefile )
    if( checkOverride exists )
      return checkOverride
    else
      return new File( basedir + "/" + relativePathToMakefile )
  }

  def lookupSubMakefile( currentMakefile: String, relativePath: String): String

  def findMakefile( folder: String ): Option[String] ={
    val kbuild = new File( basedir + "/" + folder + "/" + "KBuild" )
    if( kbuild exists )
      Some( folder + ( if( folder endsWith "/" ) "" else "/" ) + "KBuild" )
    else{
      val makefile = new File( basedir + "/" + folder + "/" + "Makefile")
      if( makefile exists )
          Some( folder + ( if( folder endsWith "/" ) "" else "/" ) + "Makefile" )
      else
        None
    }
  }

//  val newPath = basedir + "/" + currentFolder + "/" + rp

  /**
   * Lookup source file of object node
   */
  def getSource( b: BNode, oF: String, gen: Boolean ): Option[String]

  def getManualPCs: Map[String,Expression]

}