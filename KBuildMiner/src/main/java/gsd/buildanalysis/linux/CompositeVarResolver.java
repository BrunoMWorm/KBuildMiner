///*
//
//Copyright (C) 2009 Thorsten Berger
//
//This program is free software; you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation; either version 2 of the License, or
//(at your option) any later version.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with this program; if not, write to the Free Software
//Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
//*/
///**
// *
// */
//package gsd.buildanalysis.linux;
//
//import gsd.buildanalysis.linux.model.*;
//import gsd.buildanalysis.linux.traverse.*;
//
//import java.io.File;
//import java.util.*;
//
//public class CompositeVarResolver {
//
//	public static List<List<BuildNode>> resolveExpressionForObjectNode( ObjectNode on ){
//		final List<List<BuildNode>> ret = new ArrayList<List<BuildNode>>();
//		ret.add( new LinkedList<BuildNode>() );
//
//
//		Traverser objectToTopTraverser = new Traverser(){
//
//			/* (non-Javadoc)
//			 * @see ca.uwaterloo.gsd.lmf.traverse.Traverser#traverse(ca.uwaterloo.gsd.lmf.model.BuildNode, ca.uwaterloo.gsd.lmf.traverse.TraverseListener)
//			 */
//			public void traverse(BuildNode node, TraverseListener traverseListener)
//					throws TraverseException {
//
//				if( ((ForkableTraverseListener)traverseListener).getCycleDetector().contains( node ) ){
//					System.out.println( "Cycle detected, aborting at " + node );
//					ret.remove( ((ForkableTraverseListener)traverseListener).getRoute() );
//					return;
//				}else
//					((ForkableTraverseListener)traverseListener).getCycleDetector().add( node );
//
//
//				traverseListener.nodeVisit( node );
//
//				Map<BuildNode, TraverseListener> next = new HashMap<BuildNode, TraverseListener>();
//
//				if( node instanceof TempCompositeListNode){
//					// ok, a reference, so try to find the actual parent node
//					TempCompositeListNode cln = (TempCompositeListNode)node;
//
//					MakefileNode scope = findAncestorMakefileNode( node );
//
//					// case 1: is it a composite object?
//					List<ObjectNode> compositeObjects = findObjectNodes( cln.getListName(), scope );
////						System.out.println( "#" + compositeObjects );
//					// case 2: is it referenced somewhere else?
//					List<TempReferenceNode> referenceNodes = doubleCheckTempReferences( findTempReferenceNodes( cln.getListName(), scope ) );
////						System.out.println( "##" + referenceNodes );
//					// combine both
//					List<BuildNode> parentNodes = new LinkedList<BuildNode>( compositeObjects );
//					parentNodes.addAll( referenceNodes );
//
//					if( parentNodes.size() > 0 ){
//						// yes, it's a composite object, so just continue traversing the first one
//						next.put( parentNodes.get( 0 ), traverseListener );
//						// but just after having forked (which copies the list)
//						for( int i = 1; i < parentNodes.size(); i++ ){
//							// if we found more, then fork and traverse the others too
//							ForkableTraverseListener ntl = ((ForkableTraverseListener)traverseListener).fork();
////							System.out.println( "fork!" );
//							ret.add( ntl.getRoute() );
//							next.put( parentNodes.get( i ), ntl );
//						}
//
//					}// no else, since it could be both
//
//
//
//				}else{
//					if( node.getParent() != null )
//						next.put( node.getParent(), traverseListener );
//				}
//
//				// check if we've reached the top
//				if( next.keySet().isEmpty() ){
//					if( !( node instanceof BuildRootNode ) )
//						throw new RuntimeException( "Haven't reached top, current node: " + node );
//				}
//
//				for( BuildNode parent : next.keySet() )
//					traverse( parent, next.get( parent ) );
//
//			}
//
//		};
//
//
//		objectToTopTraverser.traverse( on, new ForkableTraverseListener( ret.get( 0 ) ) );
//
//		if( ret.isEmpty() )
//			throw new RuntimeException( "Haven't reached top due to cycle abortion." );
//
//
//		return ret;
//	}
//
//	/**
//	 * Ignore references in patterns like in crypto/Makefile
//	 * crypto_algapi-$(CONFIG_PROC_FS) += proc.o
//	 * crypto_algapi-objs := algapi.o scatterwalk.o $(crypto_algapi-y)
//	 * obj-$(CONFIG_CRYPTO_ALGAPI2) += crypto_algapi.o
//	 *
//	 * i.e. ignore the inclusion of $(list-y) in list-objs, since it's unnecessary and doesn't affect variability conditions (and causes troubles...)
//	 *
//	 * @param referenceNodes
//	 * @return
//	 */
//	static List<TempReferenceNode> doubleCheckTempReferences( List<TempReferenceNode> referenceNodes ){
//		List<TempReferenceNode> ret = new LinkedList<TempReferenceNode>();
//		for( TempReferenceNode n : referenceNodes ){
//			BuildNode p = n.getParent();
//			if( p instanceof TempCompositeListNode && ((TempCompositeListNode)p).getListName().equals( n.variable ) )
//				continue;
//			else
//				ret.add( n );
//		}
//		return ret;
//	}
//
//	static class ForkableTraverseListener implements TraverseListener{
//
//		List<BuildNode> route;
//		Set<BuildNode> cycleDetector = new HashSet<BuildNode>();	// we need this for at least /fs/ramfs/Makefile and /net/mac80211/Makefile
//
//		/**
//		 * @param route
//		 */
//		public ForkableTraverseListener(List<BuildNode> route) {
//			super();
//			this.route = route;
//		}
//
//		public ForkableTraverseListener fork(){
//			return new ForkableTraverseListener( new LinkedList<BuildNode>( route ) );
//		}
//
//		/* (non-Javadoc)
//		 * @see ca.uwaterloo.gsd.lmf.traverse.TraverseListener#nodeVisit(ca.uwaterloo.gsd.lmf.model.BuildNode)
//		 */
//		public void nodeVisit(BuildNode node) {
//			route.add( node );
//		}
//
//		/**
//		 * @return the route
//		 */
//		public List<BuildNode> getRoute() {
//			return route;
//		}
//
//		/**
//		 * @return the cycleDetector
//		 */
//		public Set<BuildNode> getCycleDetector() {
//			return cycleDetector;
//		}
//
//	}
//
//
//	private static MakefileNode findAncestorMakefileNode( BuildNode bn ){
//		Traverser t = TraverseFactory.bottomToSpecificTypeUpTraverser( MakefileNode.class );
//		final Wrapper wr = new Wrapper();
//		TraverseAdapter ta = new TraverseAdapter(){
//			public void nodeVisit(BuildNode node) {
//				wr.bn = node;
//			}
//		};
//		t.traverse( bn, ta );
//		if( wr.bn instanceof MakefileNode )
//			return (MakefileNode) wr.bn;
//		else
//			return null;
//	}
//
//	private static class Wrapper{	// closures would be great!
//		public BuildNode bn;
//	}
//
//	private static List<ObjectNode> findObjectNodes( final String name, MakefileNode scope ){
//		Traverser t = TraverseFactory.getTopToBottomTraverser( true, ObjectNode.class );
//		final List<ObjectNode> ret = new LinkedList<ObjectNode>();
//		TraverseAdapter ta = new TraverseAdapter(){
//			public void nodeVisit(BuildNode node) {
//				ObjectNode on = (ObjectNode)node;
//				if( on.getObjectFile().equals( name ) )
//					ret.add( on );
//			}
//		};
//		t.traverse( scope, ta );
//		return ret;
//	}
//
//	private static List<TempReferenceNode> findTempReferenceNodes( final String name, MakefileNode scope ){
//		Traverser t = TraverseFactory.getTopToBottomTraverser( true, TempReferenceNode.class );
//		final List<TempReferenceNode> ret = new LinkedList<TempReferenceNode>();
//		TraverseAdapter ta = new TraverseAdapter(){
//			public void nodeVisit(BuildNode node) {
//				TempReferenceNode rn = (TempReferenceNode)node;
//				if( rn.variable.equals( name ) )
//					ret.add( rn );
//			}
//		};
//		t.traverse( scope, ta );
//		return ret;
//	}
//
//	public static void preprocess( MakefileNode scope, Project proj ){
//		preprocessObjectNodes( scope, scope, proj );
//	}
//
//	public static void preprocessObjectNodes( BuildNode current, MakefileNode scope, Project proj ){
//		for( BuildNode bn : current.getChildren() ){
//			if( bn instanceof ObjectNode ){
//				ObjectNode on = (ObjectNode)bn;
//
//				File source = resolveAndSetSourceForObject( on, proj );
//
////				System.out.println( on.getObjectFile() + ObjectNode.EXTENSION_SEPARATOR + on.getExtension() + ": " + source.exists() );
//
//				if( !on.isGenerated() && !source.exists() ){
//					TempCompositeListNode cln = resolveCompositeList( on, scope );
//					if( cln != null )
//						System.out.println( "   Ok, found composite list node: " + cln );
//					else
//						throw new RuntimeException( "Neither source nor composite list for " + on +  " found!" );
//				}
//
//			}
//			preprocessObjectNodes( bn, scope, proj );
//		}
//	}
//
//	public static TempCompositeListNode resolveCompositeList( ObjectNode on, BuildNode scope ){
//		for( BuildNode bn : scope.getChildren() ){
//			if( bn instanceof TempCompositeListNode ){
//				TempCompositeListNode cln = (TempCompositeListNode)bn;
//				if( cln.getListName().equals( on.getObjectFile() ) )
//					return cln;
//			}
//			TempCompositeListNode ret = resolveCompositeList( on, bn );
//			if( ret != null )
//				return ret;
//		}
//
//		return null;
//	}
//
//	public static File resolveAndSetSourceForObject( ObjectNode on, Project proj ){
//		File ret = null;
//		BuildNode parent = on;
//		while( !( parent instanceof MakefileNode ) ){
//			parent = parent.getParent();
//		}
//		MakefileNode mfNode = (MakefileNode)parent;
//
//
//		String currentFolder;
//
//		// exception for arch/x86/Makefile, which runs in / and, thus, has full relative paths, e.g. arch/x86/kernel/head.o
//		System.out.println( mfNode.getMakeFile() );
//		if( mfNode.getMakeFile().equals( "arch/x86/Makefile" ) ){
//			currentFolder = "";
//		}else{
//			currentFolder = mfNode.getMakeFile().substring( 0, mfNode.getMakeFile().lastIndexOf( '/' ) );
//		}
//
//		// same for absolute object paths
//		if( on.getObjectFile().startsWith( "/" ) )
//			currentFolder = "";
//
//
//
//		// C source
//		String sourceRelativePath = currentFolder + "/" + on.getObjectFile() + ".c";
//		String newPath = proj.basedir + File.separatorChar + sourceRelativePath;
//		ret = new File( newPath );
//		if( ret.exists() || on.isGenerated() ){		// safe assumption, since no assembler files are generated
//			on.setSourceFile( sourceRelativePath );
//		}else{
//			// check for assembler source
//			sourceRelativePath = currentFolder + "/" + on.getObjectFile() + ".S";
//			newPath = proj.basedir + File.separatorChar + sourceRelativePath;
//			ret = new File( newPath );
//			if( ret.exists() ){
//				on.setSourceFile( sourceRelativePath );
//			}
//		}
//
//		// finally, check that every source file path doesn't start with one or more "/"
//		while( on.getSourceFile()!= null && on.getSourceFile().startsWith( "/" ) )
//			on.setSourceFile( on.getSourceFile().substring( 1 ) );
//
//		return ret;
//
//	}
//
//}
