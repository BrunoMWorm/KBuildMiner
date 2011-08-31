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

package gsd.iml.util;

import java.util.Arrays;
import java.util.List;
import org.junit.Test ;
import junit.framework.TestCase;

/**
 *
 * @author leonardo
 */
@SuppressWarnings("unchecked")
public class CollectionsUtilsTest extends TestCase {

    @Test
    public void testFoldRight1() {
        List<Integer> arr = Arrays.asList(1,2,3) ;
        assertTrue(CollectionsUtils.foldRight(arr, new Function<Integer, Integer>() {
            public Integer apply(Integer arg1, Integer arg2) {
                return arg1 + arg2 ;
            }
        }, 0) == 6);
    }

    @Test
    public void testFoldRight2() {
        List<Integer> arr = Arrays.asList(1,2,3,4) ;
        assertTrue(CollectionsUtils.foldRight(arr, new Function<Integer, Integer>() {
            public Integer apply(Integer arg1, Integer arg2) {
                return arg1 + arg2 ;
            }
        }, 0) == 10);
    }

    @Test
    public void testFoldRight3() {
        List<Integer> arr = Arrays.asList(1,2,3) ;
        assertTrue(CollectionsUtils.foldRight(arr, new Function<Integer, Integer>() {
            public Integer apply(Integer arg1, Integer arg2) {
                return arg1 + arg2 ;
            }
        }, 10) == 16);
    }

    @Test
    public void testFoldRight4() {
        List<Integer> arr = Arrays.asList(1,2,3,4) ;
        assertTrue(CollectionsUtils.foldRight(arr, new Function<Integer, Integer>() {
            public Integer apply(Integer arg1, Integer arg2) {
                return arg1 + arg2 ;
            }
        }, 10) == 20);
    }

    @Test
    public void testFoldRight5() {
        List<Integer> arr = Arrays.asList(1,2,3,4) ;
        assertTrue(CollectionsUtils.foldRight(arr, new Function<Integer, String>() {
            public String apply(Integer arg1, String arg2) {
                return arg1 + arg2 ;
            }
        }, "").equals("1234"));
    }

    @Test
    public void testFoldRight6() {
        List<Integer> arr = Arrays.asList(1,2,3,4,5) ;
        assertTrue(CollectionsUtils.foldRight(arr, new Function<Integer, String>() {
            public String apply(Integer arg1, String arg2) {
                return arg1 + arg2 ;
            }
        }, "").equals("12345"));
    }

    @Test
    public void testFoldRight7() {
        List<Integer> arr = Arrays.asList(1,2,3,4) ;
        assertTrue(CollectionsUtils.foldRight(arr, new Function<Integer, String>() {
            public String apply(Integer arg1, String arg2) {
                return arg1 + arg2 ;
            }
        }, "foo").equals("1234foo"));
    }

    @Test
    public void testFoldRight8() {
        List<Integer> arr = Arrays.asList(1,2,3,4,5) ;
        assertTrue(CollectionsUtils.foldRight(arr, new Function<Integer, String>() {
            public String apply(Integer arg1, String arg2) {
                return arg1 + arg2 ;
            }
        }, "foo").equals("12345foo"));
    }

    @Test
    public void testFoldRight9() {
        List<Integer> arr = Arrays.asList() ;
        assertTrue(CollectionsUtils.foldRight(arr, new Function<Integer, String>() {
            public String apply(Integer arg1, String arg2) {
                return arg1 + arg2 ;
            }
        }, "foo").equals("foo"));
    }

    @Test
    public void testFoldRight10() {
        List<Integer> arr = Arrays.asList() ;
        assertTrue(CollectionsUtils.foldRight(arr, new Function<Integer, Integer>() {
            public Integer apply(Integer arg1, Integer arg2) {
                return arg1 + arg2 ;
            }
        }) == null);
    }

    @Test
    public void testFoldRight11() {
        List<Integer> arr = Arrays.asList(1) ;
        assertTrue(CollectionsUtils.foldRight(arr, new Function<Integer, Integer>() {
            public Integer apply(Integer arg1, Integer arg2) {
                return arg1 + arg2 ;
            }
        }).equals(1));
    }

    @Test
    public void testFoldRight12() {
        List<Integer> arr = Arrays.asList(1, 2) ;
        assertTrue(CollectionsUtils.foldRight(arr, new Function<Integer, Integer>() {
            public Integer apply(Integer arg1, Integer arg2) {
                return arg1 + arg2 ;
            }
        }).equals(3));
    }

    @Test
    public void testFoldRight13() {
        List<Integer> arr = Arrays.asList(1,2,3) ;
        assertTrue(CollectionsUtils.foldRight(arr, new Function<Integer, Integer>() {
            public Integer apply(Integer arg1, Integer arg2) {
                return arg1 + arg2 ;
            }
        }).equals(6));
    }
}