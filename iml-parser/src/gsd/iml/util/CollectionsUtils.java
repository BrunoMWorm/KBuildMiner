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

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author leonardo
 */
public class CollectionsUtils {
    public static <T> Collection<T> filter(Filter<T> filter, Collection<T> collection) {
        List<T> res = new LinkedList<T>() ;
        for(T elem : collection)
            if (filter.accepts(elem))
                res.add(elem) ;
        return res ;
    }


    public static <T> boolean isEmpty(Collection<T> c) {
        return c == null || c.isEmpty() ;
    }

    @SuppressWarnings("unchecked")
    public static <U, T> T foldRight(Collection<U> list, Function<U,T> f, T value) {
        return foldRight((U[]) list.toArray(), f, value, 0, list.size()) ;
    }

    @SuppressWarnings("unchecked")
    public static <T> T foldRight(Collection<T> list, Function<T, T> f) {
        if (list.size() == 0)
            return null ;

        T[] arr = (T[]) list.toArray() ;
        return foldRight(arr, f, arr[arr.length - 1], 0, arr.length - 1) ;
    }

    private static <U, T> T foldRight(U[] collection, Function<U,T> f, T value, int i, int size) {
        if (i == size)
            return value ;

        return f.apply(collection[i], foldRight(collection, f, value, i + 1, size)) ;
    }
}
