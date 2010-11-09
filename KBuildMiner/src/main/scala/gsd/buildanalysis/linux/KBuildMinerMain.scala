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

import java.util.Properties
import model._
import gsd.common.Logging
import org.antlr.runtime.{CommonTokenStream, ANTLRFileStream}
import java.io.{PrintWriter, FileWriter, File, FileReader}

/**
 * Run the KBuild miner with this main class.
 */
object KBuildMinerMain extends optional.Application with Logging with BuildMinerCommons{

  val Linux_KERNEL = "../../../workspace/codebases/linux-2.6.28.6"
  val PROPERTIES = "miner.properties"
  val TOP_MAKEFILE_FOLDERS = "arch/x86" :: "block" :: "crypto" :: "drivers" ::
                             "firmware" :: "fs" :: "init" :: "ipc" :: "kernel" ::
                             "lib" :: "mm" :: "net" :: "samples" :: "security" ::
                             "sound" :: Nil
  val Manual_PCs = "override/linux-2.6.28.6/manual_presence_conditions.xml"
  val OVERRIDE_Folder = "override/linux-2.6.28.6"

  val AST_OUTPUT = "output/makefile_tree.xml"
  val PC_OUTPUT = "output/presence_conditions.txt"


  def main( codebase: Option[String],
            astOutput: Option[String],
            pcOutput: Option[String],
            overrideFolder: Option[String],
            manualPCs: Option[String],
            saveAST: Option[String] ){

    val _codebase   = getArg( codebase, "codebase", Linux_KERNEL )
    val _astOutput  = getArg( astOutput, "astOutput", AST_OUTPUT )
    val _pcOutput   = getArg( pcOutput, "pcOutput", PC_OUTPUT )
    val _overrideFolder = getArg( overrideFolder, "overrideFolder", OVERRIDE_Folder )
    val _manualPCs  = getArg( manualPCs, "manualPCs", Manual_PCs )
    val _saveAST = getArg( saveAST, "saveAST", "true" )

    val p = new Project( _codebase, _overrideFolder )
    new File( "output/logs" ) mkdirs

    info( "Starting KBuildMiner..." )

    val ast = buildAST( p )

    if( _saveAST == "true" )
      PersistenceManager.outputXML( ast, _astOutput )

    val pcs = PCDerivationMain calculatePCs ast
    val out = new PrintWriter( new FileWriter( PC_OUTPUT ) )
    pcs.toList.sort( _._1 < _._1 ).foreach{ case (name,pc) =>
      out.println( name + ": " + PersistenceManager.pp( rewrite( removeCONFIG_Prefix)( pc ) ) )
    }
    out close

    println
  }

  private def getArg[T]( arg: Option[T], name:String, default: String ): String =
    arg match{
      case Some( s )  => s.toString
      case None       => getProperty( name, default )
    }

  def getProperty( id: String, default: String ): String = {
    if( new File( PROPERTIES ).exists ){
      val props = new Properties
      // would rather like to use getClass.getResourceAsStream, but would need to
      // put the properties file in the classpath, not just in project root
      props.load( new FileReader( PROPERTIES ) )
      props.getProperty( id, default )
    }else                                     
      default
  }

  private val removeCONFIG_Prefix = everywheretd{
    rule{
      case Identifier( i ) if i startsWith "CONFIG_" => Identifier( i substring 7 )
    }
  }

  private def buildAST( proj: Project ) =
    BNode( RootNode, TOP_MAKEFILE_FOLDERS.map{
      f => processMakefile( f + "/Makefile", Some( True() ), proj )
    }, None, NoDetails )


  private def processMakefile( mf: String, exp: Option[Expression], proj: Project ): BNode = {

    val factory = new ModelFactory(
      BNode( MakefileBNode, List(), exp, MakefileDetails( mf ) ),
      proj )

    info( "=== PreProcessing " + mf )
    // well, not really (for now)

    info( "=== Processing " + mf )

    val input = new ANTLRFileStream( proj.getHandle( mf ).getAbsolutePath )
    val lex = new FuzzyMakefileLexer( input )
    lex.setModelFactory( factory )

    val tokens = new CommonTokenStream( lex )

    // perform the lexing (and a bit more ;)
    tokens toString

    info( "=== PostProcessing " + mf )

    // set source file
    val setSourceFileRule = everywheretd{
      rule{
        case b@BNode( ObjectBNode, ch, exp, ObjectDetails( oF, bA, ext, gen, None, fP ) ) =>
          BNode( ObjectBNode, ch, exp,
            ObjectDetails( oF, bA, ext, gen, proj.getSource( b, oF, gen ), fP ) )
      }
    }
    // descend into and process the sub makefiles
    val processMakefilesRule = everywheretd{
      rule{
        case BNode( MakefileBNode, Nil, exp, MakefileDetails( m ) ) if m != mf =>
          processMakefile( m, exp, proj )
      }
    }

    val result = factory.root

    // make sure every MakefileBNode and ObjectBNode has an expression
    assert( collects{
      case b@BNode( MakefileBNode, _, None, _ ) => b
      case b@BNode( ObjectBNode, _, None, _ ) => b
    }(result) isEmpty )

    rewrite( postProcessingRule <*
             sequencerRule <*
             setSourceFileRule <*
             processMakefilesRule )( result )

  }

  /**
   * condense tree, combine nodes and skip multiple makefile invocations
   */
  val postProcessingRule = everywheretd{
    rule{
      case BNode( t, ch, e, d ) => {
//          var mMap = ch.foldLeft( Map[String, List[BNode]]() )( (map,b) => b match{
//            case BNode( MakefileBNode, _, _, MakefileDetails(mf) ) => map.get(mf) match{
//              case Some( s ) => map + ( mf -> ( b :: s ) )
//              case None => map + ( mf -> b )
//            }
//            case _ => map
//          })

        var skip = Set[BNode]()

        val new_children = ch.flatMap( _ match{

          case b@BNode( MakefileBNode, children, Some(exp), MakefileDetails(mf) ) => {
            if( skip contains b )
              Nil
            else{
              val all = ch.filter( _ match{
                case BNode(_,_,_, MakefileDetails(m) ) if m==mf => true
                case _ => false
              })
              val new_exp = all.map( _.exp.get ).reduceLeft[Expression]( _ | _ )
              skip ++= all

              BNode( MakefileBNode, children, Some( new_exp ), MakefileDetails(mf) ) :: Nil
            }
          }

          case b@BNode( ObjectBNode, children, Some(exp), od@ObjectDetails( of, _, _, _, _, _ ) ) => {
            if( skip contains b )
              Nil
            else{
              val all = ch.filter( _ match{
                case BNode(_,_,_, ObjectDetails( o, _, _, _, _, _ ) ) if o==of => true
                case _ => false
              })
              val new_exp = all.map( _.exp.get ).reduceLeft[Expression]( _ | _ )
              skip ++= all

              BNode( ObjectBNode, children, Some( new_exp ), od ) :: Nil
            }
          }

          case b@BNode( TempCompositeListBNode, children, expression, tcld@TempCompositeListDetails( ln, sf ) )
            if (sf==None||sf.get=="y") => {

              if( skip contains b )
                Nil
              else{
                val all = ch.filter( _ match{
                  case BNode(_,_,_, TempCompositeListDetails( l, s )) if (l==ln && (s==None||s.get=="y") ) => true
                  case _ => false
                })
                val newTCLchildren = children ::: all.tail.flatMap( _.subnodes )
                skip ++= all
                
                BNode( TempCompositeListBNode, newTCLchildren, expression, tcld ) :: Nil
              }
          }

          case b => b :: Nil
        })

        BNode( t, new_children, e, d )
      }
    }
  }

}