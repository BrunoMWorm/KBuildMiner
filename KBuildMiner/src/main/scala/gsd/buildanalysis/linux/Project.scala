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

import model.BNode
import java.io.{FileInputStream, InputStream, File}

abstract class Project( val basedir: String ){

  def getTopMakefileFolders: List[String]

  protected def getLocalOverrideFolder: String

  def getStreamHandle( relativePathToMakefile: String ): InputStream = {
    val checkOverride = getClass.getResourceAsStream( getLocalOverrideFolder + "/" + relativePathToMakefile )
    if( checkOverride != null )
      checkOverride
    else
      new FileInputStream( basedir + "/" + relativePathToMakefile )
  }

  def lookupSubMakefile( currentMakefile: String, relativePath: String): String

  val makefileNames = "KBuild" :: "KBuild.src" :: "Makefile" :: Nil

  def findMakefile( folder: String ): Option[String] ={
    for( mf <- makefileNames ){
      val m = new File( basedir + "/" + folder + "/" + mf )
      if( m exists )
        return Some( folder + ( if( folder endsWith "/" ) "" else "/" ) + mf )
    }
    return None

//    val kbuild = new File( basedir + "/" + folder + "/" + "KBuild" )
//    if( kbuild exists )
//      Some( folder + ( if( folder endsWith "/" ) "" else "/" ) + "KBuild" )
//    else{
//      val makefile = new File( basedir + "/" + folder + "/" + "Makefile")
//      if( makefile exists )
//          Some( folder + ( if( folder endsWith "/" ) "" else "/" ) + "Makefile" )
//      else
//        None
//    }
  }

  /**
   * Lookup source file of object node
   */
  def getSource( b: BNode, oF: String, gen: Boolean ): Option[String]

  def getManualPCs: Map[String,Expression]

}