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
package gsd.iml.test.util;

import java.io.File;
import org.junit.Ignore;

@Ignore
public class TestFile {

    private static String SEP = System.getProperty("file.separator");
    private static String root = System.getProperty("user.dir") + SEP + "test" + SEP ;

    public static String get(String file) {
        return root + file;
    }

    public static File getAsFile(String file) {
        return new File(get(file));
    }

    public static String getRoot() {
        return root;
    }
}
