module jloda2 {
	requires transitive javafx.controls;
	requires transitive javafx.graphics;
	requires transitive javafx.fxml;

	requires transitive java.desktop;

	requires org.apache.pdfbox;
	requires org.apache.fontbox;

	exports jloda.fx.dialog;
	exports jloda.fx.colorscale;
	exports jloda.fx.control;
	exports jloda.fx.control.table;
	exports jloda.fx.control.sliderhistogram;

	exports jloda.fx.find;
	exports jloda.fx.graph;
	exports jloda.fx.label;
	exports jloda.fx.icons;
	exports jloda.fx.message;
	exports jloda.fx.selection;
	exports jloda.fx.shapes;
	exports jloda.fx.undo;
	exports jloda.fx.util;
	exports jloda.fx.window;

	exports jloda.graph;
	exports jloda.graph.io;
	exports jloda.graph.algorithms;
	exports jloda.graph.fmm;

	exports jloda.phylo;
	exports jloda.util;
	exports jloda.util.interval;
	exports jloda.util.parse;
	exports jloda.util.progress;

	exports jloda.seq;
	exports jloda.thirdparty;

	opens jloda.fx.colorscale;
	opens jloda.fx.label;
	opens jloda.fx.icons;
	opens jloda.fx.control.table;
	opens jloda.fx.control.sliderhistogram;
	opens jloda.fx.find;
	opens jloda.fx.message;

	opens jloda.resources.css;
	opens jloda.resources.icons;
	opens jloda.resources.icons.dialog;
	opens jloda.resources.icons.sun;


	exports jloda.fx.geom;
	exports jloda.kmers;
	exports jloda.kmers.bloomfilter;
	exports jloda.kmers.mash;
	exports jloda.fx.workflow;
	exports jloda.phylo.algorithms;
	exports jloda.fx.selection.rubberband;

	exports jloda.xtra;
}