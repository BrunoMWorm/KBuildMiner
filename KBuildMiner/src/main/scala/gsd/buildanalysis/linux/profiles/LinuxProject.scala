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
package gsd.buildanalysis.linux.profiles

import gsd.buildanalysis.linux.model.{BNode, MakefileDetails, TreeHelper}
import java.io.File
import gsd.buildanalysis.linux.{Expression, Project}

class LinuxProject( basedir: String ) extends Project( basedir ) with TreeHelper{

  val TOP_MAKEFILE_FOLDERS = "arch/x86" :: "block" :: "crypto" :: "drivers" ::
                             "firmware" :: "fs" :: "init" :: "ipc" :: "kernel" ::
                             "lib" :: "mm" :: "net" :: "samples" :: "security" ::
                             "sound" :: Nil
  val Manual_PCs = "override/linux-2.6.28.6/manual_presence_conditions.xml"

  val OVERRIDE_Folder = "override/linux-2.6.28.6"

  def getTopMakefileFolders: List[String] = TOP_MAKEFILE_FOLDERS

  def getOverrideFolder = OVERRIDE_Folder

  def lookupSubMakefile( currentMakefile: String, relativePath: String): String = {

    val rp = if( currentMakefile.startsWith("arch/x86/") &&
                  relativePath.startsWith("arch/x86/") )
      relativePath.substring( "arch/x86/".length )
    else
      relativePath

    val currentFolder = currentMakefile.substring( 0, currentMakefile.lastIndexOf('/') )
    val newPath = basedir + "/" + currentFolder + "/" + rp

    findMakefile( currentFolder + "/" + rp ) match{
      case Some( m ) => {println(">>>>" + m); m}
      case None => Predef.error( "Neither a KBuild nor a Makefile exists in folder " + newPath )
    }

  }

  /**
   * Lookup source file of object node
   */
  def getSource( b: BNode, oF: String, gen: Boolean ): Option[String] ={

    val mf = b->mfScope match{
      case BNode(_,_,_,MakefileDetails(m)) => m
      case _ => Predef.error( "No Makefile node!" )
    }

    val currentFolder = if( oF startsWith "/" )
        "" // absolute object path
      else if( mf == "arch/x86/Makefile" )
        "" // exception for arch/x86/Makefile, which runs in / and, thus,
           // has full relative paths, e.g. arch/x86/kernel/head.o
      else
        mf.substring( 0, mf lastIndexOf '/' )

    // check that source file paths don't start with one or more "/"
    def sanitize( f: String ): String =
      if( f startsWith "/" ) sanitize(f substring 1) else f

    val cPath = currentFolder + "/" + oF + ".c"
    val c = new File( basedir + "/" + cPath )
    if( c.exists || gen ) // safe assumption, since no assembler files are generated
      Some( sanitize( cPath ) )
    else{
      // check for assembler source
      val asmPath = currentFolder + "/" + oF + ".S"
      val a = new File( basedir + "/" + asmPath )
      if( a exists )
        Some( sanitize( asmPath ) )
      else
        None
    }

  }

  def getManualPCs: Map[String,Expression] = Map()

}