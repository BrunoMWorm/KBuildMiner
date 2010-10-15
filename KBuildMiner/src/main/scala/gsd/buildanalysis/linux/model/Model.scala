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
package gsd.buildanalysis.linux.model

import gsd.buildanalysis.linux.{Project, Expression}
import gsd.common.Logging

/**
 * Represents the AST we derive from the whole build system.
 */
case class BNode( ntype: BNodeType,
//                  parent: Option[BNode],
                  children: List[BNode],
                  exp: Option[Expression],
                  details: BNodeDetails )

sealed abstract class BNodeType

case object RootNode extends BNodeType
case object IfBNode extends BNodeType
case object MakefileBNode extends BNodeType
case object ObjectBNode extends BNodeType
case object TempCompositeListBNode extends BNodeType
case object TempReferenceBNode extends BNodeType


sealed abstract class BNodeDetails

case object NoDetails extends BNodeDetails

case class ObjectDetails( objectFile: String,
                          built_as: Option[String],
                          extension: String,
                          generated: Boolean,
                          sourceFile: Option[String],
                          fullPathToObject: Option[String] ) extends BNodeDetails

case class MakefileDetails( makefile: String ) extends BNodeDetails

case class TempReferenceDetails( variable: String,
                                 selectionSuffix: String ) extends BNodeDetails

case class TempCompositeListDetails( listName: String,
                                     suffix: Option[String] ) extends BNodeDetails

