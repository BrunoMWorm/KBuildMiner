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

class Project( val basedir: String, val overrideFolder: String ){

  def getHandle( relativePathToMakefile: String ): File = {
    var checkOverride = new File( overrideFolder + "/" + relativePathToMakefile)
    if (checkOverride.exists)
      return checkOverride
    else
      return new File( basedir + "/" + relativePathToMakefile)
  }

  def lookupMakefile( currentMakefile: String, relativePath: String): String = {

    val rp = if( currentMakefile.startsWith("arch/x86/") &&
                  relativePath.startsWith("arch/x86/") )
      relativePath.substring( "arch/x86/".length )
    else
      relativePath

    val currentFolder = currentMakefile.substring( 0, currentMakefile.lastIndexOf('/') )
    val newPath = basedir + "/" + currentFolder + "/" + rp
    val kbuild = new File(newPath + "/" + "KBuild")

    if( kbuild.exists )
      return currentFolder + "/" + rp + "KBuild"

    val makefile = new File(newPath + "/" + "Makefile")
    if( makefile.exists )
      return currentFolder + "/" + rp + "Makefile"

    error( "Neither a KBuild nor a Makefile exists in folder " + newPath )
  }

}