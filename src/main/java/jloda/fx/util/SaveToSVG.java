/*
 *  SaveToSVG.java Copyright (C) 2023 Daniel H. Huson
 *
 *  (Some files contain contributions from other authors, who are then mentioned separately.)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package jloda.fx.util;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.chart.Chart;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import jloda.fx.window.MainWindowManager;
import jloda.thirdparty.PngEncoderFX;
import jloda.util.Basic;
import jloda.util.FileUtils;
import jloda.util.StringUtils;
import org.fxmisc.richtext.TextExt;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;


/**
 * save a root node and all descendants to an SVG image.
 * This is very incomplete. It doesn't reproduce CSS styling and doesn't reproduce effects and non-color paints
 * Daniel Huson, 6.2023
 */
public class SaveToSVG {
	/**
	 * draws given root node to a file in SVG format
	 *
	 * @param root the root node to be saved
	 * @param file the file
	 * @throws IOException failed
	 */
	public static void apply(Node root, File file) throws IOException {
		apply(root, root.getBoundsInLocal(), file);
	}

	/**
	 * draws given root node to a file in SVG format
	 *
	 * @param root   the root node to be saved
	 * @param file   the file
	 * @param bounds the bounds to use
	 * @throws IOException failed
	 *                     todo: implement rotation of elements
	 */
	public static void apply(Node root, Bounds bounds, File file) throws IOException {
		if (file.exists())
			Files.delete(file.toPath());

		var buf = new StringBuilder();

		var currentDateTime = LocalDateTime.now();
		var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		var formattedDateTime = currentDateTime.format(formatter);
		buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>%n<!-- Creator: %s -->%n<!-- Created: %s -->%n<!-- Software: JLODA2 https://github.com/husonlab/jloda2 -->%n".formatted(System.getProperty("user.name"), formattedDateTime));

		buf.append("<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"%.1f\" height=\"%.1f\" viewBox=\"%.1f %.1f %.1f %.1f\">\n"
				.formatted(bounds.getWidth(), bounds.getHeight(), bounds.getMinX(), bounds.getMinY(), bounds.getWidth(), bounds.getHeight()));
			/*
			buf.append("""
					<defs>
						<clipPath id="global-clip">
					    	<rect x="%.1f" y="%.1f" width="%.1f" height="%.1f" />
					 	</clipPath>
					</defs>%n""".formatted(svgMinX, svgMinY, svgWidth, svgHeight));

				then attach this to all items that should be clipped:
				<use xlink:href="#global-clip" />
			 */

		if (MainWindowManager.isUseDarkTheme()) {
			appendRect(buf, -5, -5, bounds.getWidth() + 10, bounds.getHeight() + 10, 0, new ArrayList<>(), Color.TRANSPARENT, Color.web("rgb(60, 63, 65)"));
		}

		for (var n : BasicFX.getAllRecursively(root, n -> true)) {
			// System.err.println("n: " + n.getClass().getSimpleName());
			if (jloda.fx.util.SaveToPDF.isNodeVisible(n)) {
				buf.append(getSVG(root, n));
			}
		}
		buf.append("</svg>\n");
		try (var writer = FileUtils.getOutputWriterPossiblyZIPorGZIP(file.getPath())) {
			writer.write(buf.toString());
		}
	}

	/**
	 * constructs the SVG description for a node that represents a shape or image
	 *
	 * @param root the root node containing the node, used for determining the apparent size and angle
	 * @param node the node to report
	 * @return the SVG string or empty string
	 */
	public static String getSVG(Node root, Node node) {
		var scaleFactor = SaveToPDF.computeScaleFactor(root, node);
		var strokeWidth = (node instanceof Shape shape ? scaleFactor * shape.getStrokeWidth() : 1.0);
		var strokeDashArray = (node instanceof Shape shape ? shape.getStrokeDashArray().stream().map(v -> scaleFactor*v).toList():new ArrayList<Double>());

		var buf = new StringBuilder();
		try {
			if (node instanceof Pane pane) { // this might contain a background color
				if (pane.getBackground() != null && pane.getBackground().getFills().size() == 1) {
					var fill = pane.getBackground().getFills().get(0);
					if (fill.getFill() instanceof Color) {
						var bounds = root.sceneToLocal(pane.localToScene(pane.getBoundsInLocal()));
						appendRect(buf, (bounds.getMinX()), (bounds.getMinY()), bounds.getWidth(), bounds.getHeight(), strokeWidth, strokeDashArray, null, fill.getFill());
					}
				}
			} else if (node instanceof Line line) {
				var x1 = (root.sceneToLocal(line.localToScene(line.getStartX(), line.getStartY())).getX());
				var y1 = (root.sceneToLocal(line.localToScene(line.getStartX(), line.getStartY())).getY());
				var x2 = (root.sceneToLocal(line.localToScene(line.getEndX(), line.getEndY())).getX());
				var y2 = (root.sceneToLocal(line.localToScene(line.getEndX(), line.getEndY())).getY());
				appendLine(buf, x1, y1, x2, y2, strokeWidth, strokeDashArray, line.getStroke());
			} else if (node instanceof Rectangle rectangle) {
				var bounds = root.sceneToLocal(rectangle.localToScene(rectangle.getBoundsInLocal()));
				appendRect(buf, (bounds.getMinX()), (bounds.getMinY()), bounds.getWidth(), bounds.getHeight(), strokeWidth, strokeDashArray, rectangle.getStroke(), rectangle.getFill());
			} else if (node instanceof Circle circle) {
				var bounds = root.sceneToLocal(circle.localToScene(circle.getBoundsInLocal()));
				var r = (0.5 * bounds.getHeight());
				var x = (bounds.getCenterX());
				var y = (bounds.getCenterY());
				appendCircle(buf, x, y, r, strokeWidth, strokeDashArray, circle.getStroke(), circle.getFill());
			} else if (node instanceof Ellipse ellipse) {
				var bounds = root.sceneToLocal(ellipse.localToScene(ellipse.getBoundsInLocal()));
				var rx = (ellipse.getRadiusX());
				var ry = (ellipse.getRadiusY());
				var x = (bounds.getCenterX());
				var y = (bounds.getCenterY());
				appendEllipse(buf, x, y, rx, ry, strokeWidth, strokeDashArray, ellipse.getStroke(), ellipse.getFill());
			} else if (node instanceof QuadCurve curve) {
				var sX = (root.sceneToLocal(curve.localToScene(curve.getStartX(), curve.getStartY())).getX());
				var sY = (root.sceneToLocal(curve.localToScene(curve.getStartX(), curve.getStartY())).getY());
				var cX = (root.sceneToLocal(curve.localToScene(curve.getControlX(), curve.getControlY())).getX());
				var cY = (root.sceneToLocal(curve.localToScene(curve.getControlX(), curve.getControlY())).getY());
				var tX = (root.sceneToLocal(curve.localToScene(curve.getEndX(), curve.getEndY())).getX());
				var tY = (root.sceneToLocal(curve.localToScene(curve.getEndX(), curve.getEndY())).getY());
				appendQuadCurve(buf, sX, sY, cX, cY, tX, tY, strokeWidth, strokeDashArray, curve.getStroke());
			} else if (node instanceof CubicCurve curve) {
				var sX = (root.sceneToLocal(curve.localToScene(curve.getStartX(), curve.getStartY())).getX());
				var sY = (root.sceneToLocal(curve.localToScene(curve.getStartX(), curve.getStartY())).getY());
				var c1X = (root.sceneToLocal(curve.localToScene(curve.getControlX1(), curve.getControlY1())).getX());
				var c1Y = (root.sceneToLocal(curve.localToScene(curve.getControlX1(), curve.getControlY1())).getY());
				var c2X = (root.sceneToLocal(curve.localToScene(curve.getControlX2(), curve.getControlY2())).getX());
				var c2Y = (root.sceneToLocal(curve.localToScene(curve.getControlX2(), curve.getControlY2())).getY());
				var tX = (root.sceneToLocal(curve.localToScene(curve.getEndX(), curve.getEndY())).getX());
				var tY = (root.sceneToLocal(curve.localToScene(curve.getEndX(), curve.getEndY())).getY());
				appendCubicCurve(buf, sX, sY, c1X, c1Y, c2X, c2Y, tX, tY, strokeWidth, strokeDashArray, curve.getStroke());
			} else if (node instanceof Path path) {
				if (!SaveToPDF.containedInText(path))
					appendPath(buf, path, root, strokeWidth, strokeDashArray, path.getStroke());
			} else if (node instanceof Polygon polygon) {
				var points = new ArrayList<Point2D>();
				for (var i = 0; i < polygon.getPoints().size(); i += 2) {
					var x = (root.sceneToLocal(polygon.localToScene(polygon.getPoints().get(i), polygon.getPoints().get(i + 1))).getX());
					var y = (root.sceneToLocal(polygon.localToScene(polygon.getPoints().get(i), polygon.getPoints().get(i + 1))).getY());
					points.add(new Point2D(x, y));
				}
				appendPolygon(buf, points, strokeWidth, strokeDashArray, polygon.getStroke(), polygon.getFill());
			} else if (node instanceof Polyline polyline) {
				var points = new ArrayList<Point2D>();
				for (var i = 0; i < polyline.getPoints().size(); i += 2) {
					var x = (root.sceneToLocal(polyline.localToScene(polyline.getPoints().get(i), polyline.getPoints().get(i + 1))).getX());
					var y = (root.sceneToLocal(polyline.localToScene(polyline.getPoints().get(i), polyline.getPoints().get(i + 1))).getY());
					points.add(new Point2D(x, y));
				}
				appendPolyline(buf, points, strokeWidth, strokeDashArray, polyline.getStroke());
			} else if (node instanceof Text text) {
				if (!text.getText().isBlank()) {
					double screenAngle = SaveToPDF.getAngleOnScreen(text);
					var localBounds = text.getBoundsInLocal();
					var origX = localBounds.getMinX();
					var origY = localBounds.getMinY() + 0.87f * localBounds.getHeight();
					var rotateAnchorX = root.sceneToLocal(text.localToScene(origX, origY)).getX();
					var rotateAnchorY = root.sceneToLocal(text.localToScene(origX, origY)).getY();
					if (SaveToPDF.isMirrored(text)) // todo: this is untested:
						screenAngle = 360 - screenAngle;
					var fontHeight = scaleFactor * text.getFont().getSize();
					var altFontHeight =scaleFactor*0.87 * localBounds.getHeight();
					if (!(text instanceof TextExt) && Math.abs(fontHeight - altFontHeight) > 2)
						fontHeight = altFontHeight;
					appendText(buf, (rotateAnchorX), (rotateAnchorY), screenAngle, text.getText(), text.getFont(), fontHeight, text.getFill());
				}
			} else if (node instanceof ImageView imageView) {
				var bounds = root.sceneToLocal(imageView.localToScene(imageView.getBoundsInLocal()));
				var x = (bounds.getMinX());
				var width = (bounds.getWidth());
				var y = (bounds.getMinY());
				var height = (bounds.getHeight());
				appendImage(buf, x, y, width, height, imageView.getImage());
			} else if (node instanceof Shape3D || node instanceof Canvas || node instanceof Chart) {
				var parameters = new SnapshotParameters();
				parameters.setFill(Color.TRANSPARENT);
				var snapShot = node.snapshot(parameters, null);
				var bounds = root.sceneToLocal(node.localToScene(node.getBoundsInLocal()));
				var x = (bounds.getMinX());
				var width = (bounds.getWidth());
				var y = (bounds.getMinY());
				var height = (bounds.getHeight());
				appendImage(buf, x, y, width, height, snapShot);
			}
		} catch (Exception ex) {
			Basic.caught(ex);
		}
		return buf.toString();
	}


	public static void appendLine(StringBuilder buf, double x1, double y1, double x2, double y2, double strokeWidth, List<Double> strokeDashArray, Paint stroke) {
		buf.append("<line x1=\"%.2f\" y1=\"%.2f\" x2=\"%.2f\" y2=\"%.2f\"".formatted(x1, y1, x2, y2));
		if (stroke instanceof Color color && stroke != Color.TRANSPARENT)
			buf.append(" stroke=\"%s\"".formatted(asSvgColor(color)));
		else
			buf.append(" stroke=\"none\"");
		buf.append(" stroke-width=\"%.2f\"".formatted(strokeWidth));
		if (!strokeDashArray.isEmpty()) {
			buf.append(" stroke-dasharray=\"").append(StringUtils.toString(strokeDashArray, ",")).append("\"");
		}
		buf.append("/>\n");
	}

	public static void appendRect(StringBuilder buf, double x, double y, double width, double height, double strokeWidth, List<Double> strokeDashArray, Paint stroke, Paint fill) {
		buf.append("<rect x=\"%.2f\" y=\"%.2f\" width=\"%.2f\" height=\"%.2f\"".formatted(x, y, width, height));
		if (stroke instanceof Color color && stroke != Color.TRANSPARENT)
			buf.append(" stroke=\"%s\"".formatted(asSvgColor(color)));
		else
			buf.append(" stroke=\"none\"");
		buf.append(" stroke-width=\"%.2f\"".formatted(strokeWidth));
		if (fill instanceof Color color && color != Color.TRANSPARENT)
			buf.append(" fill=\"%s\"".formatted(asSvgColor(color)));
		else
			buf.append(" fill=\"none\"");
		if (!strokeDashArray.isEmpty()) {
			buf.append(" stroke-dasharray=\"").append(StringUtils.toString(strokeDashArray, ",")).append("\"");
		}
		buf.append("/>\n");
	}

	public static void appendCircle(StringBuilder buf, double x, double y, double radius, double strokeWidth, List<Double> strokeDashArray, Paint stroke, Paint fill) {
		buf.append("<circle cx=\"%.2f\" cy=\"%.2f\" r=\"%.2f\"".formatted(x, y, radius));
		if (stroke instanceof Color color && stroke != Color.TRANSPARENT)
			buf.append(" stroke=\"%s\"".formatted(asSvgColor(color)));
		else
			buf.append(" stroke=\"none\"");
		buf.append(" stroke-width=\"%.2f\"".formatted(strokeWidth));
		if (fill instanceof Color color && color != Color.TRANSPARENT)
			buf.append(" fill=\"%s\"".formatted(asSvgColor(color)));
		else
			buf.append(" fill=\"none\"");
		if (!strokeDashArray.isEmpty()) {
			buf.append(" stroke-dasharray=\"").append(StringUtils.toString(strokeDashArray, ",")).append("\"");
		}
		buf.append("/>\n");
	}

	public static void appendEllipse(StringBuilder buf, double x, double y, double rx, double ry, double strokeWidth, List<Double> strokeDashArray, Paint stroke, Paint fill) {
		buf.append("<ellipse cx=\"%.2f\" cy=\"%.2f\" rx=\"%.2f\" ry=\"%.2f\"".formatted(x, y, rx, ry));
		if (stroke instanceof Color color && stroke != Color.TRANSPARENT)
			buf.append(" stroke=\"%s\"".formatted(asSvgColor(color)));
		else
			buf.append(" stroke=\"none\"");
		buf.append(" stroke-width=\"%.2f\"".formatted(strokeWidth));
		if (fill instanceof Color color && color != Color.TRANSPARENT)
			buf.append(" fill=\"%s\"".formatted(asSvgColor(color)));
		else
			buf.append(" fill=\"none\"");
		if (!strokeDashArray.isEmpty()) {
			buf.append(" stroke-dasharray=\"").append(StringUtils.toString(strokeDashArray, ",")).append("\"");
		}
		buf.append("/>\n");
	}

	public static void appendQuadCurve(StringBuilder buf, Double sX, Double sY, Double cX, Double cY, Double tX, Double tY, double strokeWidth, List<Double> strokeDashArray, Paint stroke) {
		buf.append("<path d=\"M%.2f,%.2f Q%.2f,%.2f %.2f,%.2f\"".formatted(sX, sY, cX, cY, tX, tY));
		if (stroke instanceof Color color && stroke != Color.TRANSPARENT)
			buf.append(" stroke=\"%s\"".formatted(asSvgColor(color)));
		else
			buf.append(" stroke=\"none\"");
		buf.append(" stroke-width=\"%.2f\"".formatted(strokeWidth));
		if (!strokeDashArray.isEmpty()) {
			buf.append(" stroke-dasharray=\"").append(StringUtils.toString(strokeDashArray, ",")).append("\"");
		}
		buf.append("/>\n");
	}

	public static void appendCubicCurve(StringBuilder buf, Double sX, Double sY, Double c1X, Double c1Y, Double c2X, Double c2Y, Double tX, Double tY, double strokeWidth, List<Double> strokeDashArray, Paint stroke) {
		buf.append("<path d=\"M%.2f,%.2f C%.2f,%.2f %.2f,%.2f %.2f,%.2f\"".formatted(sX, sY, c1X, c1Y, c2X, c2Y, tX, tY));
		if (stroke instanceof Color color && stroke != Color.TRANSPARENT)
			buf.append(" stroke=\"%s\"".formatted(asSvgColor(color)));
		else
			buf.append(" stroke=\"none\"");
		buf.append(" stroke-width=\"%.2f\"".formatted(strokeWidth));
		if (!strokeDashArray.isEmpty()) {
			buf.append(" stroke-dasharray=\"").append(StringUtils.toString(strokeDashArray, ",")).append("\"");
		}
		buf.append("/>\n");
	}

	private static void appendPath(StringBuilder buf, Path path, Node pane, double strokeWidth, List<Double> strokeDashArray, Paint stroke) {
		var local = new Point2D(0, 0);
		buf.append("<path d=\"");
		try {
			for (var element : path.getElements()) {
				//System.err.println("Element: " + element);
				if (element instanceof MoveTo moveTo) {
					local = new Point2D(moveTo.getX(), moveTo.getY());
					var t = pane.sceneToLocal(path.localToScene(local.getX(), local.getY()));
					buf.append(" M%.2f,%.2f".formatted((t.getX()), (t.getY())));
				} else if (element instanceof LineTo lineTo) {
					local = new Point2D(lineTo.getX(), lineTo.getY());
					var t = pane.sceneToLocal(path.localToScene(local.getX(), local.getY()));
					buf.append(" L%.2f,%.2f".formatted((t.getX()), (t.getY())));
				} else if (element instanceof HLineTo lineTo) {
					local = new Point2D(lineTo.getX(), local.getY());
					var t = pane.sceneToLocal(path.localToScene(local.getX(), local.getY()));
					buf.append(" L%.2f,%.2f".formatted((t.getX()), (t.getY())));
				} else if (element instanceof VLineTo lineTo) {
					local = new Point2D(local.getX(), lineTo.getY());
					var t = pane.sceneToLocal(path.localToScene(local.getX(), local.getY()));
					buf.append(" L%.2f,%.2f".formatted((t.getX()), (t.getY())));
				} else if (element instanceof ArcTo arcTo) {
					local = new Point2D(arcTo.getX(), arcTo.getY());
					var t = pane.sceneToLocal(path.localToScene(local.getX(), local.getY()));
					double radiusX = arcTo.getRadiusX();
					double radiusY = arcTo.getRadiusY();
					double xAxisRotation = arcTo.getXAxisRotation();
					boolean largeArcFlag = arcTo.isLargeArcFlag();
					boolean sweepFlag = arcTo.isSweepFlag();
					buf.append(" A%.2f,%.2f %.2f %d,%d %.2f,%.2f".formatted(radiusX, radiusY, xAxisRotation, (largeArcFlag ? 1 : 0), (sweepFlag ? 1 : 0), (t.getX()), (t.getY())));
				} else if (element instanceof QuadCurveTo curveTo) {
					var c = pane.sceneToLocal(path.localToScene(curveTo.getControlX(), curveTo.getControlY()));
					var t = pane.sceneToLocal(path.localToScene(curveTo.getX(), curveTo.getY()));
					buf.append(" Q%.2f,%.2f %.2f,%.2f".formatted((c.getX()), (c.getY()), (t.getX()), (t.getY())));
				} else if (element instanceof CubicCurveTo curveTo) {
					var c1 = pane.sceneToLocal(path.localToScene(curveTo.getControlX1(), curveTo.getControlY1()));
					var c2 = pane.sceneToLocal(path.localToScene(curveTo.getControlX2(), curveTo.getControlY2()));
					var t = pane.sceneToLocal(path.localToScene(curveTo.getX(), curveTo.getY()));
					buf.append(" C%.2f,%.2f %.2f,%.2f %.2f,%.2f".formatted((c1.getX()), (c1.getY()), (c2.getX()), (c2.getY()), (t.getX()), (t.getY())));
				}
			}
		} finally {
			buf.append("\"");
			if (stroke instanceof Color color && stroke != Color.TRANSPARENT)
				buf.append(" stroke=\"%s\"".formatted(asSvgColor(color)));
			else
				buf.append(" stroke=\"none\"");
			buf.append(" stroke-width=\"%.2f\"".formatted(strokeWidth));
			if (path.getFill() instanceof Color color && color != Color.TRANSPARENT)
				buf.append(" fill=\"%s\"".formatted(asSvgColor(color)));
			else
				buf.append(" fill=\"none\"");
			if (!strokeDashArray.isEmpty()) {
				buf.append(" stroke-dasharray=\"").append(StringUtils.toString(strokeDashArray, ",")).append("\"");
			}
			buf.append("/>\n");
		}
	}

	public static void appendPolygon(StringBuilder buf, ArrayList<Point2D> points, double strokeWidth, List<Double> strokeDashArray, Paint stroke, Paint fill) {
		buf.append("<polygon points=\"");
		for (var point : points) {
			buf.append(" %.2f,%.2f".formatted(point.getX(), point.getY()));
		}
		buf.append("\"");

		if (stroke instanceof Color color && stroke != Color.TRANSPARENT)
			buf.append(" stroke=\"%s\"".formatted(asSvgColor(color)));
		else
			buf.append(" stroke=\"none\"");
		buf.append(" stroke-width=\"%.2f\"".formatted(strokeWidth));
		if (fill instanceof Color color && color != Color.TRANSPARENT)
			buf.append(" fill=\"%s\"".formatted(asSvgColor(color)));
		else
			buf.append(" fill=\"none\"");
		if (!strokeDashArray.isEmpty()) {
			buf.append(" stroke-dasharray=\"").append(StringUtils.toString(strokeDashArray, ",")).append("\" />");
		}
		buf.append("/>\n");
	}

	public static void appendPolyline(StringBuilder buf, ArrayList<Point2D> points, double strokeWidth, List<Double> strokeDashArray, Paint stroke) {
		if (!points.isEmpty()) {
			try {
				buf.append("<path d=\"");
				buf.append(" M%.2f,%.2f".formatted(points.get(0).getX(), points.get(0).getY()));

				for (var point : points) {
					buf.append(" L%.2f,%.2f".formatted(point.getX(), point.getY()));
				}
			} finally {
				buf.append("\"");
				if (stroke instanceof Color color && stroke != Color.TRANSPARENT)
					buf.append(" stroke=\"%s\"".formatted(asSvgColor(color)));
				else
					buf.append(" stroke=\"none\"");
				buf.append(" stroke-width=\"%.2f\"".formatted(strokeWidth));
				buf.append(" fill=\"none\"");
				if (!strokeDashArray.isEmpty()) {
					buf.append(" stroke-dasharray=\"").append(StringUtils.toString(strokeDashArray, ",")).append("\" />");
				}
				buf.append("/>\n");
			}
		}
	}

	public static void appendText(StringBuilder buf, Double x, Double y, double angle, String text, Font font, Double fontSize, Paint textFill) {
		buf.append("<text x=\"%.2f\" y=\"%.2f\"".formatted(x, y));
		buf.append(" font-family=\"%s\"".formatted(getSVGFontName(font.getFamily())));
		buf.append(" font-size=\"%.1f\"".formatted(fontSize));
		if (font.getName().contains(" Italic"))
			buf.append(" font-style=\"italic\"");
		if (font.getName().contains(" Bold"))
			buf.append(" font-weight=\"bold\"");
		if (textFill instanceof Color color && color != Color.TRANSPARENT)
			buf.append(" fill=\"%s\"".formatted(asSvgColor(color)));
		if ((angle % 360.0)!=0) {
			buf.append(" transform=\"rotate(%.1f %.2f %.2f)\"".formatted(angle, x, y));
		}
		buf.append("><![CDATA[");
		buf.append(text);
		buf.append("]]></text>\n");
	}

	public static void appendImage(StringBuilder buf, double x, double y, double width, double height, Image image) {
		var encoder = new PngEncoderFX(image);
		var base64Data = Base64.getEncoder().encodeToString(encoder.pngEncode(true));
		buf.append("<image xlink:href=\"data:image/png;base64,").append(base64Data).append("\"");
		buf.append(" x=\"%.2f\" y=\"%.2f\" width=\"%.2f\" height=\"%.2f\"/>\n".formatted(x, y, width, height));
	}

	public static Object getSVGFontName(String fontFamily) {
		fontFamily = fontFamily.toLowerCase();
		if (fontFamily.startsWith("times"))
			return "Times New Roman";
		else if (fontFamily.startsWith("arial"))
			return "Arial";
		else if (fontFamily.startsWith("courier") || fontFamily.startsWith("monospaced"))
			return "Courier";
		else // if(fontFamily.startsWith("arial") || fontFamily.startsWith("helvetica") || fontFamily.startsWith("system"))
			return "Helvetica";
	}

	public static String asSvgColor(Color color) {
		var r = (int) (color.getRed() * 255);
		var g = (int) (color.getGreen() * 255);
		var b = (int) (color.getBlue() * 255);
		var alpha = color.getOpacity();

		// Convert RGB values to hexadecimal notation
		var hexColor = String.format("#%02X%02X%02X", r, g, b);

		// Append alpha value if it's different from fully opaque (1.0)
		if (alpha < 1.0) {
			String alphaHex = String.format("%02X", (int) (alpha * 255));
			hexColor += alphaHex;
		}
		return hexColor;
	}

}
