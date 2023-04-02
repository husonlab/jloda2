/*
 * TestBracketParser.java Copyright (C) 2023 Daniel H. Huson
 *
 * (Some files contain contributions from other authors, who are then mentioned separately.)
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
 *
 */

package jloda.xtra;

import jloda.phylo.PhyloTree;

import java.io.IOException;

public class TestBracketParser {
	public static void main(String[] args) throws IOException {
		PhyloTree.SUPPORT_RICH_NEWICK=true;

		//var newick="(((((b,#H1),(c,(d,e)#H2)),(#H2,(z,f))),(a)#H1));";

		var input=new String[]{
				//"(((((b,(a)#H1),(c,e)),f),#H1));",
				"(((((b,#H1),(c,e)),f),(a)#H1));"
		};

		for(var newick:input) {
			System.err.println("in:  " + newick);
			var tree = new PhyloTree();
			tree.parseBracketNotation(newick, true);
			System.err.println("out: " + tree.toBracketString(false)+";");
			System.err.println();
		}
	}
}
