/*
 * ISearcher.java Copyright (C) 2023 Daniel H. Huson
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

package jloda.fx.find;

import javafx.beans.property.ReadOnlyBooleanProperty;

/**
 * Base interface for searchers
 * Daniel Huson, 7.2008
 */
public interface ISearcher {
    /**
     * get the name for this type of search
     *
     * @return name
     */
    String getName();

    /**
     * is a global find possible?
     *
     * @return true, if there is at least one object
     */
    ReadOnlyBooleanProperty isGlobalFindable();

    /**
     * is a selection find possible
     *
     * @return true, if at least one object is selected
     */
    ReadOnlyBooleanProperty isSelectionFindable();

    /**
     * something has been changed or selected, update tree
     */
    void updateView();

    /**
     * does this searcher support find all?
     *
     * @return true, if find all supported
     */
    boolean canFindAll();

    /**
     * set select state of all objects
     *
	 */
    void selectAll(boolean select);
}
