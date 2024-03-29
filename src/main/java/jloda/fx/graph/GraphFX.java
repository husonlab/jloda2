/*
 * GraphFX.java Copyright (C) 2023 Daniel H. Huson
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

package jloda.fx.graph;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import jloda.graph.*;

/**
 * provides observable list of nodes and adjacentEdges, and label properties
 * Daniel Huson, 1.20020
 */
public class GraphFX<G extends Graph> {
    private G graph;
    private final ObservableList<Node> nodeList = FXCollections.observableArrayList();
    private final ReadOnlyListWrapper<Node> readOnlyNodeList = new ReadOnlyListWrapper<>(nodeList);

    private final ObservableList<Edge> edgeList = FXCollections.observableArrayList();
    private final ReadOnlyListWrapper<Edge> readOnlyEdgeList = new ReadOnlyListWrapper<>(edgeList);
    private GraphUpdateListener graphUpdateListener;

    private final BooleanProperty empty = new SimpleBooleanProperty(true);

    private NodeArray<StringProperty> node2LabelProperty;
    private EdgeArray<StringProperty> edge2LabelProperty;

    private long _lastUpdate=0L;
    private  LongProperty lastUpdate;

    public GraphFX() {
    }

    public GraphFX(G graph) {
        setGraph(graph);
    }

    public G getGraph() {
        return graph;
    }

    public void setGraph(G graph) {
        if (this.graph != null && graphUpdateListener != null) {
            this.graph.removeGraphUpdateListener(graphUpdateListener);
        }

        if (graph != null) {
            graphUpdateListener = new GraphUpdateAdapter() {
                @Override
                public void newNode(Node v) {
                    Platform.runLater(() -> {
                        try {
                            nodeList.add(v);
                        } catch (NotOwnerException ignored) {
                        }
                    });
                    incrementLastUpdate();
                }

                @Override
                public void deleteNode(Node v) {
                    Platform.runLater(() -> {
                        try {
                            nodeList.remove(v);
                        } catch (NotOwnerException ignored) {
                        }
                    });
                    incrementLastUpdate();
                }

                @Override
                public void newEdge(Edge e) {
                    Platform.runLater(() -> {
                        try {
                            edgeList.add(e);
                        } catch (NotOwnerException ignored) {
                        }
                    });
                    incrementLastUpdate();
                }

                @Override
                public void deleteEdge(Edge e) {
                    Platform.runLater(() -> {
                        try {
                            edgeList.remove(e);
                        } catch (NotOwnerException ignored) {
                        }
                    });
                    incrementLastUpdate();
                }

                @Override
                public void nodeLabelChanged(Node v, String newLabel) {
                    try {
                        var stringProperty = node2LabelProperty.get(v);
                            Platform.runLater(() -> {
                                if (stringProperty != null)
                                    stringProperty.set(newLabel);
                                });
                    } catch (NotOwnerException ignored) {
                    }
                    incrementLastUpdate();
                }

                @Override
                public void edgeLabelChanged(Edge e, String newLabel) {
                    try {
                        var stringProperty = edge2LabelProperty.get(e);
                        if (stringProperty != null) {
                            Platform.runLater(() -> stringProperty.set(newLabel));
                        }
                    } catch (NotOwnerException ignored) {
                    }
                    incrementLastUpdate();
                }
            };
            graph.addGraphUpdateListener(graphUpdateListener);
            node2LabelProperty = new NodeArray<>(graph);
            edge2LabelProperty = new EdgeArray<>(graph);
        } else
            node2LabelProperty = null;

        empty.bind(Bindings.isEmpty(nodeList));

        this.graph = graph;
    }

    public ObservableList<Node> getNodeList() {
        return readOnlyNodeList;
    }

    public ObservableList<Edge> getEdgeList() {
        return readOnlyEdgeList;
    }

    public StringProperty nodeLabelProperty(Node v) {
        var stringProperty = node2LabelProperty.get(v);
        if (stringProperty == null) {
            stringProperty = new SimpleStringProperty(graph.getLabel(v));
            node2LabelProperty.put(v, stringProperty);
        }
        return stringProperty;
    }

    public StringProperty edgeLabelProperty(Edge e) {
        var stringProperty = edge2LabelProperty.get(e);
        if (stringProperty == null) {
            stringProperty = new SimpleStringProperty(graph.getLabel(e));
            edge2LabelProperty.put(e, stringProperty);
        }
        return stringProperty;
    }

    public boolean isEmpty() {
        return empty.get();
    }

    public ReadOnlyBooleanProperty emptyProperty() {
        return empty;
    }

    public void incrementLastUpdate() {
        if (lastUpdate != null) {
            Platform.runLater(() -> lastUpdate.set(lastUpdate.get() + 1));
        } else
            _lastUpdate++;
    }

    public double getLastUpdate() {
        if(lastUpdate!=null)
            return lastUpdate.get();
        else
            return _lastUpdate;
    }

    public LongProperty lastUpdateProperty() {
        if(lastUpdate==null) {
            lastUpdate=new SimpleLongProperty(_lastUpdate);
        }
        return lastUpdate;
    }
}

