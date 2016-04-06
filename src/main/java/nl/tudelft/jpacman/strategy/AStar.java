package nl.tudelft.jpacman.strategy;
/*
 * A* algorithm implementation.
 * Copyright (C) 2007, 2009 Giuseppe Scrivano <gscrivano@gnu.org>

 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see <http://www.gnu.org/licenses/>.
 */

import java.util.*;

/**
 * A* algorithm implementation using the method design pattern.
 *
 * @author Giuseppe Scrivano
 */
@SuppressWarnings({"checkstyle:javadoctype", "checkstyle:avoidinlineconditionals",
        "checkstyle:methodlength", "checkstyle:nowhitespacebefore",
        "checkstyle:emptyforiteratorpad", "checkstyle:visibilitymodifier",
        "checkstyle:innerassignment", "checkstyle:whitespaceafter",
        "checkstyle:needbraces", "checkstyle:leftcurly"})
public abstract class AStar<T> {

    private final PriorityQueue<Path> paths;
    private final Map<T, Double> mindists;
    private Double lastCost;

    /**
     * Default c'tor.
     */
    AStar() {
        paths = new PriorityQueue<>();
        mindists = new HashMap<>();
        lastCost = 0.0;
    }

    /**
     * Check if the current node is a goal for the problem.
     *
     * @param node The node to check.
     * @return <code>true</code> if it is a goal, <code>false</code> otherwise.
     */
    protected abstract boolean isGoal(T node);

    /**
     * Cost for the operation to go to <code>to</code> from
     * <code>from</code>.
     *
     * @param from The node we are leaving.
     * @param to   The node we are reaching.
     * @return The cost of the operation.
     */
    protected abstract Double g(T from, T to);

    /**
     * Estimated cost to reach a goal node.
     * An admissible heuristic never gives a cost bigger than the real
     * one.
     * <code>from</code>.
     *
     * @param from The node we are leaving.
     * @param to   The node we are reaching.
     * @return The estimated cost to reach an object.
     */
    protected abstract Double h(T from, T to);


    /**
     * Generate the successors for a given node.
     *
     * @param node The node we want to expand.
     * @return A list of possible next steps.
     */
    protected abstract List<T> generateSuccessors(T node);

    /**
     * Total cost function to reach the node <code>to</code> from
     * <code>from</code>.
     * <p>
     * The total cost is defined as: f(x) = g(x) + h(x).
     *
     * @param from The node we are leaving.
     * @param to   The node we are reaching.
     */
    private void f(final Path p, final T from, final T to) {
        final Double g = g(from, to) + ((p.parent == null) ? 0.0 : p.parent.g);

        p.g = g;
        p.f = g + h(from, to);
    }

    /**
     * Expand a path.
     *
     * @param path The path to expand.
     */
    private void expand(final Path path) {
        final Double min = mindists.get(path.getPoint());

				/*
                 * If a better path passing for this point already exists then
				 * don't expand it.
				 */
        if (min == null || min > path.f)
        {
            mindists.put(path.getPoint(), path.f);
        }
        else
        {
            return;
        }

        final T p = path.getPoint();
        final List<T> successors = generateSuccessors(p);

        for (final T t : successors) {
            final Path newPath = new Path(path);
            newPath.setPoint(t);
            f(newPath, path.getPoint(), t);
            paths.offer(newPath);
        }
    }

    /**
     * Get the cost to reach the last node in the path.
     *
     * @return The cost for the found path.
     */
    public Double getCost() {
        return lastCost;
    }

    /**
     * Find the shortest path to a goal starting from
     * <code>start</code>.
     *
     * @param start The initial node.
     * @return A list of nodes from the initial point to a goal,
     * <code>null</code> if a path doesn't exist.
     */
    public List<T> compute(final T start) {

        final Path root = new Path();
        root.setPoint(start);

						/* Needed if the initial point has a cost.  */
        f(root, start, start);

        expand(root);

        for (; ; ) {
            final Path p = paths.poll();

            if (p == null) {
                lastCost = Double.MAX_VALUE;
                return null;
            }

            final T last = p.getPoint();

            lastCost = p.g;

            if (isGoal(last)) {
                final LinkedList<T> retPath = new LinkedList<>();

                for (Path i = p; i != null; i = i.parent) {
                    retPath.addFirst(i.getPoint());
                }

                return retPath;
            }
            expand(p);
        }
    }

    private class Path implements Comparable {
        public T point;
        public Double f;
        public Double g;
        public Path parent;

        /**
         * Default c'tor.
         */
        public Path() {
            parent = null;
            point = null;
            g = f = 0.0;
        }

        /**
         * C'tor by copy another object.
         *
         * @param p The path object to clone.
         */
        public Path(final Path p) {
            this();
            parent = p;
            g = p.g;
            f = p.f;
        }

        /**
         * Compare to another object using the total cost f.
         *
         * @param o The object to compare to.
         * @return a number < 0 if this object is smaller
         * a number = 0 if objects are the same
         * a number > 0 if this object is bigger
         */
        @Override
        public int compareTo(final Object o) {

            @SuppressWarnings("unchecked")
            //TODO seriously, neither Intellij code inspection, nor PMD, nor findbugs are happy with this.
            final Path p = (Path) o;
            return (int) (f - p.f);
        }

        /**
         * Get the last point on the path.
         *
         * @return The last point visited by the path.
         */
        public T getPoint() {
            return point;
        }

        /**
         * Set the point.
         */
        public void setPoint(final T p) {
            point = p;
        }
    }
}