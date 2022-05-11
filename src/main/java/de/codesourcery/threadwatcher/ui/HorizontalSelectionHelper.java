/**
 * Copyright 2013 Tobias Gierke <tobias.gierke@code-sourcery.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.codesourcery.threadwatcher.ui;

import java.awt.*;

public abstract class HorizontalSelectionHelper<T> {

    public static final int DRAG_RADIUS_IN_PIXELS = 10;

    public static final int UNSET = -1;

    private int xDragStart = UNSET;
    private int xDragEnd = UNSET;
    private boolean selectionMarked = false;
    private final Color selectionXORColor;

    private SelectedInterval lastSelection;
    private DraggedMarker draggedMarker = DraggedMarker.NONE;

    public static final class SelectedInterval {
        public final int xMin;
        public final int xMax;

        public SelectedInterval(int xMin, int xMax) {
            this.xMin = xMin;
            this.xMax = xMax;
        }

        public SelectedInterval withMinX(int newMinX) {
            return new SelectedInterval(newMinX, xMax);
        }

        public SelectedInterval withMaxX(int newMaxX) {
            return new SelectedInterval(xMin, newMaxX);
        }
    }

    public static enum DraggedMarker {
        NONE,
        START,
        END;
    }

    public final DraggedMarker getDraggedMarker() {
        return draggedMarker;
    }

    public final void setDraggedMarker(DraggedMarker marker) {
        if (marker == null) {
            throw new IllegalArgumentException("marker must not be NULL.");
        }
        this.draggedMarker = marker;
    }

    public final DraggedMarker getDragMarkerForPoint(Point p) {
        if (isCloseToLastSelectionStart(p)) {
            return DraggedMarker.START;
        }
        if (isCloseToLastSelectionEnd(p)) {
            return DraggedMarker.END;
        }
        return DraggedMarker.NONE;
    }

    public final boolean isCloseToLastSelectionStart(Point point) {
        if (getLastSelection() != null && isValid(point)) {
            return Math.abs(getLastSelection().xMin - point.x) <= DRAG_RADIUS_IN_PIXELS;
        }
        return false;
    }

    public final boolean isCloseToLastSelectionEnd(Point point) {
        if (getLastSelection() != null && isValid(point)) {
            return Math.abs(getLastSelection().xMax - point.x) <= DRAG_RADIUS_IN_PIXELS;
        }
        return false;
    }

    public HorizontalSelectionHelper(Color selectionXORColor) {
        this.selectionXORColor = selectionXORColor;
    }

    public final boolean isSelecting() {
        return xDragStart != UNSET;
    }

    public final boolean isSelectionAvailable() {
        return xDragStart != UNSET && xDragEnd != UNSET;
    }

    protected abstract T getLastSelectionModelObject();

    public abstract int getMinX();

    public abstract int getMaxX();

    public final SelectedInterval getLastSelection() {
        return lastSelection;
    }

    public SelectedInterval setLastSelection(SelectedInterval interval) {
        this.lastSelection = interval;
        return interval;
    }

    public final void stopSelecting(Point point, Graphics graphics, int height) {
        if (isSelectionAvailable()) {
            final int end = isValid(point) ? point.x : xDragEnd;
            final int min = Math.min(xDragStart, end);
            final int max = Math.max(xDragStart, end);

            lastSelection = new SelectedInterval(min, max);
            selectionFinished(min, max);

            if (selectionMarked) {
                renderSelection(graphics, getYOffset(), height); // clear selection
            }

            xDragStart = xDragEnd = UNSET;
            selectionMarked = false;
        } else {
            clearSelection(graphics, height);
        }
    }

    public final boolean isValid(Point p) {
        return p.x >= getMinX() && p.x <= getMaxX();
    }

    protected abstract void selectionFinished(int start, int end);

    public void updateSelection(Point point, Graphics graphics, int height) {
        if (!isValid(point)) {
            return;
        }
        if (xDragStart == UNSET) {
            xDragStart = point.x;
        } else {
            renderSelection(graphics, point.x, height);
        }
    }

    public void repaint(Graphics graphics, int height) {
        if (isSelectionAvailable()) {
            graphics.setXORMode(selectionXORColor);
            int xmin = Math.min(xDragStart, xDragEnd);
            int xmax = Math.max(xDragStart, xDragEnd);
            graphics.fillRect(xmin, getYOffset(), xmax - xmin, height + 1);
            selectionMarked = true;
        }
    }

    public final void paintSelection(Graphics graphics, int xmin, int xmax, int height) {
        graphics.setXORMode(selectionXORColor);
        graphics.fillRect(xmin, getYOffset(), xmax - xmin, height + 1);
    }

    private final void renderSelection(Graphics graphics, int newXDragEnd, int height) {
        graphics.setXORMode(selectionXORColor);
        if (selectionMarked) // clear old selection
        {
            int xmin = Math.min(xDragStart, xDragEnd);
            int xmax = Math.max(xDragStart, xDragEnd);
            graphics.fillRect(xmin, getYOffset(), xmax - xmin, height + 1);
            selectionMarked = false;
        }

        if (newXDragEnd != UNSET) { // mark new selection
            int xmin = Math.min(xDragStart, newXDragEnd);
            int xmax = Math.max(xDragStart, newXDragEnd);
            graphics.fillRect(xmin, getYOffset(), xmax - xmin, height + 1);
            selectionMarked = true;
        }
        this.xDragEnd = newXDragEnd;
    }

    public final void clearSelection(Graphics graphics, int height) {
        if (selectionMarked) {
            renderSelection(graphics, getYOffset(), height); // clear selection
        }

        xDragStart = xDragEnd = UNSET;
        selectionMarked = false;
        selectionCleared();
    }

    protected abstract int getYOffset();

    protected abstract void selectionCleared();
}