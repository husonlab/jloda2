/*
 * GraphGML.java Copyright (C) 2022 Daniel H. Huson
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
 */

package jloda.graph.io;

import jloda.graph.EdgeArray;
import jloda.graph.Graph;
import jloda.graph.Node;
import jloda.graph.NodeArray;
import jloda.util.parse.NexusStreamParser;

import java.io.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * i/o in GraphGML
 * daniel huson, 3.2021
 */
public class GraphGML {

	/**
	 * write a graph in GraphGML
	 *
	 * @param graph             the graph
	 * @param w                 the writer
	 * @param labelNodeValueMap for given labels, provides a node value maps
	 * @param labelEdgeValueMap for given labels, provides node value maps
	 */
	public static void writeGML(Graph graph, String comment, String graphLabel, boolean directed, int graphId, Writer w, Map<String, NodeArray<String>> labelNodeValueMap, Map<String, EdgeArray<String>> labelEdgeValueMap) throws IOException {
		if (labelNodeValueMap == null)
			labelNodeValueMap = Collections.emptyMap();
		if (labelEdgeValueMap == null)
			labelEdgeValueMap = Collections.emptyMap();

		var gmlInfo = new GMLInfo(comment, directed, graphId, graphLabel);
		w.write("graph [\n");
		if (gmlInfo.comment() != null)
			w.write("\tcomment \"" + gmlInfo.comment() + "\"\n");
		w.write("\tdirected " + (gmlInfo.directed() ? 1 : 0) + "\n");
		w.write("\tid " + gmlInfo.id() + "\n");
		if (gmlInfo.label() != null)
			w.write("\tlabel \"" + gmlInfo.label() + "\"\n");

		var nodeLabels = labelNodeValueMap.get("label");

		for (var v : graph.nodes()) {
			w.write("\tnode [\n");
			w.write("\t\tid " + v.getId() + "\n");
			if ((nodeLabels != null && nodeLabels.containsKey(v)))
				w.write("\t\tlabel \"" + nodeLabels.get(v) + "\"\n");
			for (String aLabel : labelNodeValueMap.keySet().stream().filter(a -> !a.equals("label")).collect(Collectors.toList())) {
				var value = labelNodeValueMap.get(aLabel).get(v);
				w.write("\t\t" + aLabel + " \"" + value + "\"\n");
			}
			w.write("\t]\n");
		}

		var edgeLabels = labelEdgeValueMap.get("label");

		for (var e : graph.edges()) {
			w.write("\tedge [\n");
			w.write("\t\tsource " + e.getSource().getId() + "\n");
			w.write("\t\ttarget " + e.getTarget().getId() + "\n");

			if ((edgeLabels != null && edgeLabels.containsKey(e)))
				w.write("\t\tlabel \"" + edgeLabels.get(e) + "\"\n");
			for (String aLabel : labelEdgeValueMap.keySet().stream().filter(a -> !a.equals("label")).collect(Collectors.toList())) {
				var value = labelEdgeValueMap.get(aLabel).get(e);
				w.write("\t\t" + aLabel + " \"" + value + "\"\n");
			}
			w.write("\t]\n");
		}
		w.write("]\n");
		w.flush();
	}

	/**
	 * read a graph in GraphGML for that was previously saved using writeGML. This is not a general parser.
	 */
	public static GMLInfo readGML(Reader r, Graph graph, Map<String, NodeArray<String>> labelNodeValueMap, Map<String, EdgeArray<String>> labelEdgeValueMap) throws IOException {
		if (labelNodeValueMap == null)
			labelNodeValueMap = Collections.emptyMap();
		if (labelEdgeValueMap == null)
			labelEdgeValueMap = Collections.emptyMap();

		final var np = new NexusStreamParser(r);
		np.setSquareBracketsSurroundComments(false);

		graph.clear();

		np.matchIgnoreCase("graph [");
		String graphComment;
		if (np.peekMatchIgnoreCase("comment")) {
			np.matchIgnoreCase("comment");
			graphComment = NexusStreamParser.getQuotedString(np);
		} else
			graphComment = null;

		boolean graphDirected;
		if (np.peekMatchIgnoreCase("directed")) {
			np.matchIgnoreCase("directed");
			graphDirected = (np.getInt(0, 1) == 1);
		} else
			graphDirected = false;

		np.matchIgnoreCase("id");
		var graphId = np.getInt();

		String graphLabel;

		if (np.peekMatchIgnoreCase("label")) {
			np.matchIgnoreCase("label");
			graphLabel = NexusStreamParser.getQuotedString(np);
		} else
			graphLabel = null;

		var id2node = new HashMap<Integer, Node>();
		while (np.peekMatchIgnoreCase("node")) {
			np.matchIgnoreCase("node [");
			np.matchIgnoreCase("id");
			var id = np.getInt();
			var v = graph.newNode(null);
			id2node.put(id, v);
			while (!np.peekMatchIgnoreCase("]")) {
				var label = np.getLabelRespectCase();
				String value;
				if (np.peekMatchIgnoreCase("\""))
					value = NexusStreamParser.getQuotedString(np);
				else
					value = np.getWordRespectCase();
				labelNodeValueMap.computeIfAbsent(label, n -> graph.newNodeArray()).put(v, value);
			}
			np.matchIgnoreCase("]");
		}
		while (np.peekMatchIgnoreCase("edge")) {
			np.matchIgnoreCase("edge [");
			np.matchIgnoreCase("source");
			var sourceId = np.getInt();
			np.matchIgnoreCase("target");
			int targetId = np.getInt();
			if (!id2node.containsKey(sourceId))
				throw new IOException("Undefined node id: " + sourceId);
			if (!id2node.containsKey(targetId))
				throw new IOException("Undefined node id: " + targetId);
			var e = graph.newEdge(id2node.get(sourceId), id2node.get(targetId), null);
			while (!np.peekMatchIgnoreCase("]")) {
				var label = np.getLabelRespectCase();
				String value;
				if (np.peekMatchIgnoreCase("\""))
					value = NexusStreamParser.getQuotedString(np);
				else
					value = np.getWordRespectCase();
				labelEdgeValueMap.computeIfAbsent(label, n -> graph.newEdgeArray()).put(e, value);
			}
			np.matchIgnoreCase("]");
		}
		np.matchIgnoreCase("]");
		return new GMLInfo(graphComment, graphDirected, graphId, graphLabel);
	}

	public static final class GMLInfo {
		private final String comment;
		private final boolean directed;
		private final int id;
		private final String label;

		public GMLInfo(String comment, boolean directed, int id, String label) {
			this.comment = comment;
			this.directed = directed;
			this.id = id;
			this.label = label;
		}

		public String comment() {
			return comment;
		}

		public boolean directed() {
			return directed;
		}

		public int id() {
			return id;
		}

		public String label() {
			return label;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == this) return true;
			if (obj == null || obj.getClass() != this.getClass()) return false;
			var that = (GMLInfo) obj;
			return Objects.equals(this.comment, that.comment) &&
				   this.directed == that.directed &&
				   this.id == that.id &&
				   Objects.equals(this.label, that.label);
		}

		@Override
		public int hashCode() {
			return Objects.hash(comment, directed, id, label);
		}

		@Override
		public String toString() {
			return "GMLInfo[" +
				   "comment=" + comment + ", " +
				   "directed=" + directed + ", " +
				   "id=" + id + ", " +
				   "label=" + label + ']';
		}

	}

	public static void main(String[] args) throws IOException {
		var input = "graph [\n" +
					"comment \"example graph\"\n" +
					"directed 1\n" +
					"id 42\n" +
					"label \"Graph\"\n" +
					"	node [\n" +
					"		id 1\n" +
					"		label \"A\"\n" +
					"		sequence \"ACGTTGTCGTTG\"\n" +
					"	]\n" +
					"	node [\n" +
					"		id 2\n" +
					"		label \"B\"\n" +
					"		sequence \"TCGTTGGCGTTG\"\n" +
					"	]\n" +
					"	node [\n" +
					"		id 3\n" +
					"		label \"C\"\n" +
					"		sequence \"GCGTTGACGTTG\"\n" +
					"	]\n" +
					"	edge [\n" +
					"		source 1\n" +
					"		target 2\n" +
					"		overlap \"6\"\n" +
					"	]\n" +
					"	edge [\n" +
					"		source 2\n" +
					"		target 3\n" +
					"		overlap \"7\"\n" +
					"	]\n" +
					"	edge [\n" +
					"		source 3\n" +
					"		target 1\n" +
					"		overlap \"8\"\n" +
					"	]\n" +
					"]\n";

		var graph = new Graph();
		var labelNodeValueMap = new HashMap<String, NodeArray<String>>();
		var labelEdgeValueMap = new HashMap<String, EdgeArray<String>>();
		var gmlInfo = GraphGML.readGML(new StringReader(input), graph, labelNodeValueMap, labelEdgeValueMap);

		try (var w = new StringWriter()) {
			GraphGML.writeGML(graph, gmlInfo.comment(), gmlInfo.label(), gmlInfo.directed(), gmlInfo.id(), w, labelNodeValueMap, labelEdgeValueMap);
			System.out.println(w);
		}
	}
}
