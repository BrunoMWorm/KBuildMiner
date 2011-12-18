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

import gsd.iml.ast.feature.Feature;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.Reader;
import java.util.List;

/**
 *
 * @author leonardo
 */
public class ImlParser {
    public static List<Feature> parse(String file) throws Exception {
        return parse(new FileReader(file)) ;
    }

    public static List<Feature> parse(File file) throws Exception {
        return parse(new FileReader(file)) ;
    }

    public static List<Feature> parse(Reader reader) throws Exception {
        ImlCupParser parser = new ImlCupParser(new ImlLexer(reader)) ;
        return (List<Feature>) parser.parse().value ;
    }

    public static List<Feature> parse(InputStream in) throws Exception {
        ImlCupParser parser = new ImlCupParser(new ImlLexer(in)) ;
        return (List<Feature>) parser.parse().value ;
    }
}
