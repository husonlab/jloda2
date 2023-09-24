/*
 * TestSVGPDF.java Copyright (C) 2023 Daniel H. Huson
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
import javafx.scene.control.CheckBox;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import jloda.fx.control.RichTextLabel;
import jloda.fx.dialog.ExportImageDialog;
import jloda.fx.window.MainWindowManager;
import jloda.util.Basic;

import java.io.File;
import java.io.IOException;

public class TestSVGPDF extends Application {
	@Override
	public void start(Stage stage) throws Exception {
		var moveToA = new MoveTo(10, 10);
		var lineToB = new LineTo(100, 10);
		var quadCurveToD = new QuadCurveTo(100, 100, 200, 100);
		var lineToE = new LineTo(300, 100);
		var path = new Path(moveToA, lineToB, quadCurveToD, lineToE);
		path.getStrokeDashArray().setAll(4.0, 6.0);
		path.getStyleClass().add("graph-edge");

		var arrowHead = new Polyline(-17, -15, 17, 0, -17, 15);
		arrowHead.getStyleClass().add("graph-edge");

		arrowHead.setTranslateX(320);
		arrowHead.setTranslateY(100);

		var group = new Group(path, arrowHead);

		var rectangle = new Rectangle(100, 10, 100, 30);
		rectangle.setRotate(45);
		rectangle.setFill(Color.LIGHTBLUE);
		rectangle.setStroke(Color.GOLD);
		group.getChildren().add(rectangle);

		var text1 = new Text(20, 20, "<group Test & go>");
		//text1.setFont(Font.font("Arial", FontWeight.BOLD, 24));
		group.getChildren().add(text1);

		var text2 = new Text(20, 20, "<group Test & go>");
		text2.setFont(Font.font("Arial", FontWeight.BOLD, 24));
		text2.setTranslateY(50);
		text2.setScaleX(2);
		text2.setScaleY(2);
		group.getChildren().add(text2);

		var text3 = new RichTextLabel("<group Test & go>");
		text3.setTranslateX(150);
		text3.setTranslateY(150);
		text3.setBackgroundColor(Color.YELLOW);
		text3.setRotate(30);
		group.getChildren().add(text3);

		var line1 = new Line(-50, 0, -50, 100);
		group.getChildren().add(line1);

		var line2 = new Line(-40, 0, -40, 100);
		line2.setScaleX(2);
		line2.setScaleY(2);
		group.getChildren().add(line2);

		var circle = new Circle(50, 50, 8);
		circle.setFill(Color.LIGHTGREEN);
		circle.setStroke(Color.DARKGREEN);
		group.getChildren().add(circle);

		var box = new Rectangle(50 - 8, 50 - 8, 16, 16);
		box.setFill(Color.TRANSPARENT);
		box.setStroke(Color.DARKGOLDENROD);
		group.getChildren().add(box);

		var pane = new StackPane(group);

		var exportButtonSVG = new Button("Export SVG");
		exportButtonSVG.setOnAction(e -> {
			try {
				ExportImageDialog.saveNodeAsImage(pane, "svg", new File("stdout"));
			} catch (IOException ex) {
				Basic.caught(ex);
			}
		});

		var exportButtonPDF = new Button("Export PDF");
		exportButtonPDF.setOnAction(e -> {
			try {
				ExportImageDialog.saveNodeAsImage(pane, "pdf", new File("/Users/huson/Desktop/test.pdf"));
			} catch (IOException ex) {
				Basic.caught(ex);
			}
		});

		var darkCheckBox = new CheckBox("Dark");
		darkCheckBox.selectedProperty().addListener((v, o, n) -> {
			MainWindowManager.setUseDarkTheme(n);
			MainWindowManager.ensureDarkTheme(stage, MainWindowManager.isUseDarkTheme());
		});

		var root = new BorderPane();
		root.setTop(new ToolBar(exportButtonSVG, exportButtonPDF, new Separator(), darkCheckBox));
		root.setCenter(pane);

		stage.setScene(new Scene(root, 600, 400));
		stage.show();


	}
}
