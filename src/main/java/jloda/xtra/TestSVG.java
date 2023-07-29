/*
 * TestSVG.java Copyright (C) 2023 Daniel H. Huson
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

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import jloda.fx.dialog.ExportImageDialog;
import jloda.util.Basic;

import java.io.File;
import java.io.IOException;

public class TestSVG extends Application {
	@Override
	public void start(Stage stage) throws Exception {
		var moveToA = new MoveTo(10, 10);
		var lineToB = new LineTo(100, 10);
		var quadCurveToD = new QuadCurveTo(100, 100, 200, 100);
		var lineToE = new LineTo(300, 100);
		var path = new Path(moveToA, lineToB, quadCurveToD, lineToE);
		path.getStrokeDashArray().setAll(4.0, 6.0);

		var arrowHead = new Polyline(-17, -15, 17, 0, -17, 15);
		arrowHead.getStyleClass().add("graph-node"); // yes, graph-node

		arrowHead.setTranslateX(320);
		arrowHead.setTranslateY(100);

		var group = new Group(path, arrowHead);

		group.getChildren().add(new Text(20, 20, "Test"));

		var pane = new StackPane(group);

		var exportButton = new Button("Export");
		exportButton.setOnAction(e -> {
			try {
				ExportImageDialog.saveNodeAsImage(pane, "svg", new File("/Users/huson/Desktop/test.svg"));
			} catch (IOException ex) {
				Basic.caught(ex);
			}
		});

		var root = new BorderPane();
		root.setTop(new ToolBar(exportButton));
		root.setCenter(pane);

		stage.setScene(new Scene(root, 600, 400));
		stage.show();


	}
}
