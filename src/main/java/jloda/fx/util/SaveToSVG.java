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

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.chart.Chart;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import jloda.fx.window.MainWindowManager;
import jloda.thirdparty.PngEncoderFX;
import jloda.util.Basic;
import jloda.util.StringUtils;
import org.fxmisc.richtext.TextExt;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import static jloda.fx.util.SaveToPDF.*;

/**
 * save a root node and all descendants to an SVG image
 * Daniel Huson, 6.2023
 */
public class SaveToSVG {
	/**
	 * draws given pane to a file in PDF format
	 *
	 * @param root the root node to be saved
	 * @param file the file
	 * @throws IOException
	 */
	public static void apply(Node root, File file) throws IOException {
		if (file.exists())
			Files.delete(file.toPath());

		var buf = new StringBuilder();

		{
			var currentDateTime = LocalDateTime.now();
			var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			var formattedDateTime = currentDateTime.format(formatter);
			buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!-- Creator: %s -->\n<!-- Creation Date: %s -->\n".formatted(System.getProperty("user.name"), formattedDateTime));
		}

		double svgMinX;
		double svgMaxX;
		double svgWidth;

		double svgMinY;
		double svgMaxY;
		double svgHeight;
		{

			var bbox = computeBoundingBox(root);
			svgMinX = bbox.getMinX();
			svgMaxX = bbox.getMaxX();
			svgWidth = bbox.getWidth();
			svgMinY = bbox.getMinY();
			svgMaxY = bbox.getMaxY();
			svgHeight = bbox.getHeight();

			buf.append("<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"%.1f\" height=\"%.1f\" viewBox=\"%.1f %.1f %.1f %.1f\">\n"
					.formatted(svgWidth, svgHeight, svgMinX, svgMinY, svgWidth, svgHeight));
		}

		var paneWidth = root.getBoundsInLocal().getWidth();
		var paneHeight = root.getBoundsInLocal().getHeight();

		var factor = Math.min(svgWidth / paneWidth, svgHeight / paneHeight);

		Function<Double, Double> ps = s -> (s * factor);
		Function<Double, Double> px = x -> (x * factor + svgMinX);
		Function<Double, Double> py = y -> (y * factor + svgMinY);

		if (MainWindowManager.isUseDarkTheme()) {
			appendRect(buf, -5, -5, svgWidth + 10, svgHeight + 10, 0, new ArrayList<>(), Color.TRANSPARENT, Color.web("rgb(60, 63, 65)"));
		}

		for (var n : BasicFX.getAllRecursively(root, n -> true)) {
			// System.err.println("n: " + n.getClass().getSimpleName());
			if (isNodeVisible(n)) {
				buf.append(getSVG(root, px, py, ps, n));
			}
		}
		buf.append("</svg>\n");
		try (var writer = new FileWriter(file)) {
			writer.write(buf.toString());
		}
	}

	/**
	 * constructs the SVG description for a node that represents a shape or image
	 *
	 * @param pane the pane containing the node, used for determining the apparent size and angle
	 * @param px   scaling conversion of x coordinates
	 * @param py   scaling conversion of y coordinates
	 * @param ps   scaling conversion in all dimensions
	 * @param node the node to report
	 * @return the SVG string or empty string
	 */
	public static String getSVG(Node pane, Function<Double, Double> px, Function<Double, Double> py, Function<Double, Double> ps, Node node) {
		var buf = new StringBuilder();
		try {
			if (node instanceof Line line) {
				var x1 = px.apply(pane.sceneToLocal(line.localToScene(line.getStartX(), line.getStartY())).getX());
				var y1 = py.apply(pane.sceneToLocal(line.localToScene(line.getStartX(), line.getStartY())).getY());
				var x2 = px.apply(pane.sceneToLocal(line.localToScene(line.getEndX(), line.getEndY())).getX());
				var y2 = py.apply(pane.sceneToLocal(line.localToScene(line.getEndX(), line.getEndY())).getY());
				appendLine(buf, x1, y1, x2, y2, line.getStrokeWidth(), adjustDashArray(line.getStrokeDashArray()), line.getStroke());
			} else if (node instanceof Rectangle rectangle) {
				var bounds = pane.sceneToLocal(rectangle.localToScene(rectangle.getBoundsInLocal()));
				appendRect(buf, px.apply(bounds.getMinX()), py.apply(bounds.getMinY()), ps.apply(bounds.getWidth()), ps.apply(bounds.getHeight()), rectangle.getStrokeWidth(), adjustDashArray(rectangle.getStrokeDashArray()), rectangle.getStroke(), rectangle.getFill());
			} else if (node instanceof Circle circle) {
				var bounds = pane.sceneToLocal(circle.localToScene(circle.getBoundsInLocal()));
				var r = ps.apply(0.5 * bounds.getHeight());
				var x = px.apply(bounds.getCenterX());
				var y = py.apply(bounds.getCenterY());
				appendCircle(buf, x, y, r, circle.getStrokeWidth(), adjustDashArray(circle.getStrokeDashArray()), circle.getStroke(), circle.getFill());
			} else if (node instanceof Ellipse ellipse) {
				var bounds = pane.sceneToLocal(ellipse.localToScene(ellipse.getBoundsInLocal()));
				var rx = ps.apply(ellipse.getRadiusX());
				var ry = ps.apply(ellipse.getRadiusY());
				var x = px.apply(bounds.getCenterX());
				var y = py.apply(bounds.getCenterY());
				appendEllipse(buf, x, y, rx, ry, ellipse.getStrokeWidth(), adjustDashArray(ellipse.getStrokeDashArray()), ellipse.getStroke(), ellipse.getFill());
			} else if (node instanceof QuadCurve curve) {
				var sX = px.apply(pane.sceneToLocal(curve.localToScene(curve.getStartX(), curve.getStartY())).getX());
				var sY = py.apply(pane.sceneToLocal(curve.localToScene(curve.getStartX(), curve.getStartY())).getY());
				var cX = px.apply(pane.sceneToLocal(curve.localToScene(curve.getControlX(), curve.getControlY())).getX());
				var cY = py.apply(pane.sceneToLocal(curve.localToScene(curve.getControlX(), curve.getControlY())).getY());
				var tX = px.apply(pane.sceneToLocal(curve.localToScene(curve.getEndX(), curve.getEndY())).getX());
				var tY = py.apply(pane.sceneToLocal(curve.localToScene(curve.getEndX(), curve.getEndY())).getY());
				appendQuadCurve(buf, sX, sY, cX, cY, tX, tY, curve.getStrokeWidth(), adjustDashArray(curve.getStrokeDashArray()), curve.getStroke());
			} else if (node instanceof CubicCurve curve) {
				var sX = px.apply(pane.sceneToLocal(curve.localToScene(curve.getStartX(), curve.getStartY())).getX());
				var sY = py.apply(pane.sceneToLocal(curve.localToScene(curve.getStartX(), curve.getStartY())).getY());
				var c1X = px.apply(pane.sceneToLocal(curve.localToScene(curve.getControlX1(), curve.getControlY1())).getX());
				var c1Y = py.apply(pane.sceneToLocal(curve.localToScene(curve.getControlX1(), curve.getControlY1())).getY());
				var c2X = px.apply(pane.sceneToLocal(curve.localToScene(curve.getControlX2(), curve.getControlY2())).getX());
				var c2Y = py.apply(pane.sceneToLocal(curve.localToScene(curve.getControlX2(), curve.getControlY2())).getY());
				var tX = px.apply(pane.sceneToLocal(curve.localToScene(curve.getEndX(), curve.getEndY())).getX());
				var tY = py.apply(pane.sceneToLocal(curve.localToScene(curve.getEndX(), curve.getEndY())).getY());
				appendCubicCurve(buf, sX, sY, c1X, c1Y, c2X, c2Y, tX, tY, curve.getStrokeWidth(), adjustDashArray(curve.getStrokeDashArray()), curve.getStroke());
			} else if (node instanceof Path path) {
				if (!containedInText(path))
					appendPath(buf, path, pane, px, py, path.getStrokeWidth(), adjustDashArray(path.getStrokeDashArray()), path.getStroke());
			} else if (node instanceof Polygon polygon) {
				var points = new ArrayList<Point2D>();
				for (var i = 0; i < polygon.getPoints().size(); i += 2) {
					var x = px.apply(pane.sceneToLocal(polygon.localToScene(polygon.getPoints().get(i), polygon.getPoints().get(i + 1))).getX());
					var y = py.apply(pane.sceneToLocal(polygon.localToScene(polygon.getPoints().get(i), polygon.getPoints().get(i + 1))).getY());
					points.add(new Point2D(x, y));
				}
				appendPolygon(buf, points, polygon.getStrokeWidth(), adjustDashArray(polygon.getStrokeDashArray()), polygon.getStroke(), polygon.getFill());
			} else if (node instanceof Polyline polyline) {
				var points = new ArrayList<Point2D>();
				for (var i = 0; i < polyline.getPoints().size(); i += 2) {
					var x = px.apply(pane.sceneToLocal(polyline.localToScene(polyline.getPoints().get(i), polyline.getPoints().get(i + 1))).getX());
					var y = py.apply(pane.sceneToLocal(polyline.localToScene(polyline.getPoints().get(i), polyline.getPoints().get(i + 1))).getY());
					points.add(new Point2D(x, y));
				}
				appendPolyline(buf, points, polyline.getStrokeWidth(), adjustDashArray(polyline.getStrokeDashArray()), polyline.getStroke());
			} else if (node instanceof Text text) {
				if (!text.getText().isBlank()) {
					double screenAngle = getAngleOnScreen(text); // because y axis points upward in PDF
					var localBounds = text.getBoundsInLocal();
					var origX = localBounds.getMinX();
					var origY = localBounds.getMinY() + 0.87f * localBounds.getHeight();
					var rotateAnchorX = pane.sceneToLocal(text.localToScene(origX, origY)).getX();
					var rotateAnchorY = pane.sceneToLocal(text.localToScene(origX, origY)).getY();
					if (isMirrored(text)) // todo: this is untested:
						screenAngle = 360 - screenAngle;
					var fontHeight = ps.apply(text.getFont().getSize());
					var altFontHeight = ps.apply(0.87 * localBounds.getHeight());
					if (!(text instanceof TextExt) && Math.abs(fontHeight - altFontHeight) > 2)
						fontHeight = altFontHeight;
					fontHeight *= (float) getScaleFactors(text).getY();
					appendText(buf, px.apply(rotateAnchorX), py.apply(rotateAnchorY), screenAngle, text.getText(), text.getFont(), fontHeight, text.getFill());
				}
			} else if (node instanceof ImageView imageView) {
				var bounds = pane.sceneToLocal(imageView.localToScene(imageView.getBoundsInLocal()));
				var x = px.apply(bounds.getMinX());
				var width = ps.apply(bounds.getWidth());
				var y = py.apply(bounds.getMinY());
				var height = ps.apply(bounds.getHeight());
				appendImage(buf, x, y, width, height, imageView.getImage());
			} else if (node instanceof Shape3D || node instanceof Canvas || node instanceof Chart) {
				var parameters = new SnapshotParameters();
				parameters.setFill(Color.TRANSPARENT);
				var snapShot = node.snapshot(parameters, null);
				var bounds = pane.sceneToLocal(node.localToScene(node.getBoundsInLocal()));
				var x = px.apply(bounds.getMinX());
				var width = ps.apply(bounds.getWidth());
				var y = py.apply(bounds.getMinY());
				var height = ps.apply(bounds.getHeight());
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

	private static void appendPath(StringBuilder buf, Path path, Node pane, Function<Double, Double> px, Function<Double, Double> py,
								   double strokeWidth, List<Double> strokeDashArray, Paint stroke) {
		var local = new Point2D(0, 0);
		buf.append("<path d=\"");
		try {
			for (var element : path.getElements()) {
				//System.err.println("Element: " + element);
				if (element instanceof MoveTo moveTo) {
					local = new Point2D(moveTo.getX(), moveTo.getY());
					var t = pane.sceneToLocal(path.localToScene(local.getX(), local.getY()));
					buf.append(" M%.2f,%.2f".formatted(px.apply(t.getX()), py.apply(t.getY())));
				} else if (element instanceof LineTo lineTo) {
					local = new Point2D(lineTo.getX(), lineTo.getY());
					var t = pane.sceneToLocal(path.localToScene(local.getX(), local.getY()));
					buf.append(" L%.2f,%.2f".formatted(px.apply(t.getX()), py.apply(t.getY())));
				} else if (element instanceof HLineTo lineTo) {
					local = new Point2D(lineTo.getX(), local.getY());
					var t = pane.sceneToLocal(path.localToScene(local.getX(), local.getY()));
					buf.append(" L%.2f,%.2f".formatted(px.apply(t.getX()), py.apply(t.getY())));
				} else if (element instanceof VLineTo lineTo) {
					local = new Point2D(local.getX(), lineTo.getY());
					var t = pane.sceneToLocal(path.localToScene(local.getX(), local.getY()));
					buf.append(" L%.2f,%.2f".formatted(px.apply(t.getX()), py.apply(t.getY())));
				} else if (element instanceof ArcTo arcTo) {
					local = new Point2D(arcTo.getX(), arcTo.getY());
					var t = pane.sceneToLocal(path.localToScene(local.getX(), local.getY()));
					double radiusX = arcTo.getRadiusX();
					double radiusY = arcTo.getRadiusY();
					double xAxisRotation = arcTo.getXAxisRotation();
					boolean largeArcFlag = arcTo.isLargeArcFlag();
					boolean sweepFlag = arcTo.isSweepFlag();
					buf.append(" A%.2f,%.2f %.2f %d,%d %.2f,%.2f".formatted(radiusX, radiusY, xAxisRotation, (largeArcFlag ? 1 : 0), (sweepFlag ? 1 : 0), px.apply(t.getX()), py.apply(t.getY())));
				} else if (element instanceof QuadCurveTo curveTo) {
					var c = pane.sceneToLocal(path.localToScene(curveTo.getControlX(), curveTo.getControlY()));
					var t = pane.sceneToLocal(path.localToScene(curveTo.getX(), curveTo.getY()));
					buf.append(" Q%.2f,%.2f %.2f,%.2f".formatted(px.apply(c.getX()), py.apply(c.getY()), px.apply(t.getX()), py.apply(t.getY())));
				} else if (element instanceof CubicCurveTo curveTo) {
					var c1 = pane.sceneToLocal(path.localToScene(curveTo.getControlX1(), curveTo.getControlY1()));
					var c2 = pane.sceneToLocal(path.localToScene(curveTo.getControlX2(), curveTo.getControlY2()));
					var t = pane.sceneToLocal(path.localToScene(curveTo.getX(), curveTo.getY()));
					buf.append(" C%.2f,%.2f %.2f,%.2f %.2f,%.2f".formatted(px.apply(c1.getX()), py.apply(c1.getY()), px.apply(c2.getX()), py.apply(c2.getY()), px.apply(t.getX()), py.apply(t.getY())));
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
				if (stroke instanceof Color color && stroke != Color.TRANSPARENT)
					buf.append(" stroke=\"%s\"".formatted(asSvgColor(color)));
				else
					buf.append(" stroke=\"none\"");
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
			buf.append(" font-style=\"bold\"");
		if (textFill instanceof Color color && color != Color.TRANSPARENT)
			buf.append(" fill=\"%s\"".formatted(asSvgColor(color)));
		if (angle != 0) {
			buf.append(" transform=\"rotate(%.1f %.2f %.2f)\"".formatted(angle, x, y));
		}
		buf.append(">");
		buf.append(text.replaceAll("&", " &amp;"));
		buf.append("</text>\n");
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

	public static Rectangle2D computeBoundingBox(Node pane) {
		double minX = Double.MAX_VALUE;
		double minY = Double.MAX_VALUE;
		double maxX = Double.MIN_VALUE;
		double maxY = Double.MIN_VALUE;

		for (var node : BasicFX.getAllRecursively(pane, n -> true)) {
			if (node instanceof Shape) {
				var bounds = pane.sceneToLocal(node.localToScene(node.getBoundsInLocal()));
				minX = Math.min(minX, bounds.getMinX());
				minY = Math.min(minY, bounds.getMinY());
				maxX = Math.max(maxX, bounds.getMaxX());
				maxY = Math.max(maxY, bounds.getMaxY());
			}
		}
		if (true) { // this restricts to the currently
			minX = Math.max(minX, pane.getBoundsInLocal().getMinX());
			minY = Math.max(minY, pane.getBoundsInLocal().getMinY());

			maxX = Math.min(maxX, (pane.getBoundsInLocal().getMaxX()));
			maxY = Math.min(maxY, (pane.getBoundsInLocal().getMaxY()));
		}

		return new Rectangle2D(minX, minY, maxX - minX, maxY - minY);
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

	private static List<Double> adjustDashArray(Collection<Double> dashArray) {
		return dashArray.stream().map(v -> 0.27 * v).toList();
	}
}
