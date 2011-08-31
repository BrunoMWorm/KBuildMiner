/*
 * Copyright (c) 2011 Leonardo Passos <lpassos@gsd.uwaterloo.ca>
 *
 * This file is part of iml-parser.
 *
 * Iml-parser is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Iml-parser is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Iml-parser.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gsd.iml.parser;

import gsd.iml.ast.constraint.ActiveIfConstraint;
import gsd.iml.ast.constraint.RequiresConstraint;
import gsd.iml.ast.expression.AndExpression;
import gsd.iml.ast.expression.BinaryExpression;
import gsd.iml.ast.expression.ConditionalExpression;
import gsd.iml.ast.expression.EqualExpression;
import gsd.iml.ast.expression.IdentifierExpression;
import gsd.iml.ast.expression.IsSubstringFunctionCallExpression;
import gsd.iml.ast.expression.LongLiteralExpression;
import gsd.iml.ast.expression.MinusExpression;
import gsd.iml.ast.expression.NotExpression;
import gsd.iml.ast.expression.PlusExpression;
import gsd.iml.ast.expression.StringLiteralExpression;
import gsd.iml.ast.feature.Feature;
import gsd.iml.test.util.TestFile;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Test;

/**
 *
 * @author leonardo
 */
public class ImlParserTest extends TestCase {

    @Test
    public void testLegalValues1() {
        boolean passed = false ;
        try {
            List<Feature> features = ImlParser.parse
                    (TestFile.get("gsd/iml/parser/legal_values1"));
            assertTrue(features.size() == 1) ;
            Feature option = features.get(0) ;
            
            assertTrue(option.getLegalValues().getExpressions().get(0).equals(
                    new MinusExpression
                    (new MinusExpression(
                        new LongLiteralExpression(-1),
                        new LongLiteralExpression(2)),
                    new LongLiteralExpression(3)
                    )
            )) ;
            passed = true ;
        } catch (Exception e) {
            e.printStackTrace() ;
        }
        assertTrue(passed) ;
    }

    @Test
    public void testLegalValues2() {
        boolean passed = false ;
        try {
            List<Feature> features = ImlParser.parse
                    (TestFile.get("gsd/iml/parser/legal_values2"));
            assertTrue(features.size() == 1) ;
            Feature option = features.get(0) ;
            assertTrue(option.getLegalValues().getExpressions().equals(
                Arrays.asList(
                    new LongLiteralExpression(-1),
                    new LongLiteralExpression(-2),
                    new LongLiteralExpression(-3)
                )
            )) ;
            passed = true ;
        } catch (Exception e) {
            e.printStackTrace() ;
        }
        assertTrue(passed) ;
    }

    @Test
    public void testLegalValues3() {
        boolean passed = false ;
        try {
            List<Feature> features = ImlParser.parse
                    (TestFile.get("gsd/iml/parser/legal_values3"));
            assertTrue(features.size() == 2) ;
            Feature option = features.get(0) ;

            assertTrue(option.getLegalValues().getExpressions().equals(
                Arrays.asList(
                    new ConditionalExpression
                    (new IdentifierExpression("X"),
                     new StringLiteralExpression("a"),
                     new PlusExpression
                     (
                        new LongLiteralExpression(-3),
                        new LongLiteralExpression(5))
                    )
                )
            )) ;
            passed = true ;
        } catch (Exception e) {
            e.printStackTrace() ;
        }
        assertTrue(passed) ;
    }

    @Test
    public void testLegalValues4() {
        boolean passed = false ;
        try {
            List<Feature> features = ImlParser.parse
                    (TestFile.get("gsd/iml/parser/legal_values4"));
            assertTrue(features.size() == 2) ;
            Feature option = features.get(0) ;
            assertTrue(option.getLegalValues().getExpressions().equals(
                Arrays.asList(
                    new ConditionalExpression
                    (new IdentifierExpression("X"),
                     new StringLiteralExpression("a"),
                     new LongLiteralExpression(-3)),
                     new LongLiteralExpression(5)
                )
            )) ;
            passed = true ;
        } catch (Exception e) {
            e.printStackTrace() ;
        }
        assertTrue(passed) ;
    }

    @Test
    public void testPcVmWareI386() {
        boolean passed = false ;
        try {
            ImlParser.parse(TestFile.get("gsd/iml/parser/iml/pc_vmWare.iml")) ;
            passed = true ;
        } catch (Exception e) {
            e.printStackTrace() ;
        }
        assertTrue(passed) ;
    }

    @Test
    public void testMultipleReqs() {
        boolean passed = false ;
        try {
            List<Feature> features = 
                    ImlParser.parse(TestFile.get("gsd/iml/parser/multiple_reqs")) ;
            assertTrue(features.size() == 1) ;
            Feature f = features.get(0) ;
            assertTrue(f.getRequires().size() == 3) ;

            assertTrue(f.getRequires().get(0).equals(
                    new RequiresConstraint(
                        new IsSubstringFunctionCallExpression(
                            new IdentifierExpression("CYGPKG_IO_USB_SLAVE_TESTS"),
                            new StringLiteralExpression(" tests/usbtarget")))
                        )
                     );
            assertTrue(f.getRequires().get(1).equals(
                    new RequiresConstraint(
                          new AndExpression(
                          new IdentifierExpression("CYGFUN_KERNEL_API_C"),
                            new AndExpression(
                                new IdentifierExpression("CYGFUN_KERNEL_THREADS_TIMER"),
                                new NotExpression(new IdentifierExpression("CYGINT_KNEREL_SCHEDULER_UNIQUE_PRIORITIES"))                          
                                )
                            )
                        )
                     ));
            
            assertTrue(f.getRequires().get(2).equals(
                    new RequiresConstraint(
                          new AndExpression(
                            new IdentifierExpression("CYGPKG_LIBC_STDIO"),
                            new IdentifierExpression("CYGSEM_LIBC_STDIO_THREAD_SAFE_STREAMS")
                          )
                        )
                     ));
            passed = true ;
        } catch (Exception e) {
            e.printStackTrace() ;
        }
        assertTrue(passed) ;
    }

    @Test
    public void testMultipleActiveIfs() {
        boolean passed = false ;
        try {
            List<Feature> features =
                    ImlParser.parse(TestFile.get("gsd/iml/parser/multiple_ais")) ;
            assertTrue(features.size() == 1) ;
            Feature f = features.get(0) ;
            assertTrue(f.getActiveIfs().size() == 3) ;

            assertTrue(f.getActiveIfs().get(0).equals(
                    new ActiveIfConstraint(
                        new IsSubstringFunctionCallExpression(
                            new IdentifierExpression("CYGPKG_IO_USB_SLAVE_TESTS"),
                            new StringLiteralExpression(" tests/usbtarget")))
                        )
                     );
            assertTrue(f.getActiveIfs().get(1).equals(
                    new ActiveIfConstraint(
                          new AndExpression(
                          new IdentifierExpression("CYGFUN_KERNEL_API_C"),
                            new AndExpression(
                                new IdentifierExpression("CYGFUN_KERNEL_THREADS_TIMER"),
                                new NotExpression(new IdentifierExpression("CYGINT_KNEREL_SCHEDULER_UNIQUE_PRIORITIES"))
                                )
                            )
                        )
                     ));

            assertTrue(f.getActiveIfs().get(2).equals(
                    new ActiveIfConstraint(
                          new AndExpression(
                            new IdentifierExpression("CYGPKG_LIBC_STDIO"),
                            new IdentifierExpression("CYGSEM_LIBC_STDIO_THREAD_SAFE_STREAMS")
                          )
                        )
                     ));
            passed = true ;
        } catch (Exception e) {
            e.printStackTrace() ;
        }
        assertTrue(passed) ;
    }
    
    @Test
    public void testShowcase() {
        /* The output of this test was manually checked against the original
         * file (showcase).
         */

        boolean passed = false ;
        try {
            ImlParser.parse(TestFile.get("gsd/iml/parser/showcase")) ;
            passed = true ;
        } catch (Exception e) {
            e.printStackTrace() ;
        }
        assertTrue(passed) ;       
    }

    @Test
    public void testConditional() {
        boolean passed = false ;
        try {
            List<Feature> features = ImlParser.parse
                    (TestFile.get("gsd/iml/parser/conditional"));
            assertTrue(features.size() == 1) ;
            Feature option = features.get(0) ;
            assertTrue(option.getDefaultValue().getExpression().equals(
               new ConditionalExpression(new EqualExpression(new IdentifierExpression("E"),
                                                             new IdentifierExpression("B")),
                                         new IdentifierExpression("A"),
                                         new IdentifierExpression("D")))

            ) ;
            passed = true ;
        } catch (Exception e) {
            e.printStackTrace() ;
        }
        assertTrue(passed) ;
    }

    @Test
    public void testAll() {
        System.out.println("----------------------------------------") ;
        File[] imlFiles = new File(TestFile.get("gsd/iml/parser/iml/")).listFiles(new FilenameFilter() {

            public boolean accept(File dir, String name) {
                return name.endsWith(".iml") ;
            }
        }) ;
        for(File imlFile : imlFiles) {
            boolean pass = false ;

            try {
                System.out.println("Parsing " + imlFile.getName()) ;
                ImlParser.parse(imlFile) ;
                pass = true ;
            } catch(Exception e) {
                e.printStackTrace() ;
            } finally {
                assertTrue(pass) ;
                System.out.println("Passed.") ;
                System.out.println("----------------------------------------") ;
            }
        }
    }
}