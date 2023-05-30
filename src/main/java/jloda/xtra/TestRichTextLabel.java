/*
 * TestRichTextLabel.java Copyright (C) 2023 Daniel H. Huson
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
import javafx.geometry.Point3D;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import jloda.fx.control.RichTextLabel;

public class TestRichTextLabel extends Application {
	@Override
	public void start(Stage stage) throws Exception {
		var root = new StackPane();

		var label = new RichTextLabel("Test");
		label.setScale(3);


		root.setOnMouseClicked(e -> {
			if (e.getClickCount() == 1) {
				var rotate = new Rotate(60, new Point3D(0, 0, 1));
				label.getTransforms().add(rotate);
			} else if (e.getClickCount() == 2) {
				var rotate = new Rotate(60, new Point3D(1, 1, 0));
				label.getTransforms().add(rotate);
			}
			e.consume();
		});

		label.getTransforms();

		root.getChildren().add(label);

		stage.setScene(new Scene(root, 800, 800));
		stage.show();
	}
}
