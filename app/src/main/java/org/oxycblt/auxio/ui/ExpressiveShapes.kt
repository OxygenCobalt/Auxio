/*
 * Copyright (c) 2026 Auxio Project
 * ExpressiveShapes.kt is part of Auxio.
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
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
 
package org.oxycblt.auxio.ui

import android.graphics.Matrix
import android.graphics.Path
import android.graphics.PointF
import android.graphics.RectF
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.tan

object ExpressiveShapes {

    /** Generates the "6-Sided Cookie" path scaled to the given bounds. */
    fun getCookie6Sided(bounds: RectF): Path {
        // Raw normalized data from Material3 source
        val points =
            listOf(PointNRound(0.723f, 0.884f, 0.394f), PointNRound(0.500f, 1.099f, 0.398f))
        return generatePolygonPath(bounds, points, reps = 6)
    }

    /** Generates the "Soft Burst" path scaled to the given bounds. */
    fun getSoftBurst(bounds: RectF): Path {
        // Raw normalized data from Material3 source
        val points =
            listOf(PointNRound(0.193f, 0.277f, 0.053f), PointNRound(0.176f, 0.055f, 0.053f))
        return generatePolygonPath(bounds, points, reps = 10)
    }

    // --- Internal Logic ---

    private data class PointNRound(val x: Float, val y: Float, val r: Float)

    private data class Vertex(val x: Float, val y: Float, val r: Float)

    private fun generatePolygonPath(bounds: RectF, rawPoints: List<PointNRound>, reps: Int): Path {
        val centerX = 0.5f
        val centerY = 0.5f
        val vertices = ArrayList<Vertex>()
        val numPoints = rawPoints.size

        // 1. Generate all vertices by rotating the raw points around (0.5, 0.5)
        // Replicates 'doRepeat' logic from Compose source
        for (i in 0 until numPoints * reps) {
            val pointIndex = i % numPoints
            val p = rawPoints[pointIndex]

            // Calculate rotation for this repetition
            // In source: (it / np) * 360f / reps
            val repIndex = i / numPoints
            val rotationDeg = (repIndex * 360f) / reps

            // Rotate the point
            val (rx, ry) = rotatePoint(p.x, p.y, centerX, centerY, rotationDeg)
            vertices.add(Vertex(rx, ry, p.r))
        }

        // 2. Normalize vertices to fit exactly in 0..1 bounds
        // (The raw points might exceed 0..1 or be off-center)
        val normalizedPath = createRoundedPath(vertices)

        // 3. Transform (Scale/Translate) to target bounds
        // We compute the bounds of the normalized path to scale it correctly
        val pathBounds = RectF()
        normalizedPath.computeBounds(pathBounds, true)

        val matrix = Matrix()
        matrix.setRectToRect(pathBounds, bounds, Matrix.ScaleToFit.CENTER)
        normalizedPath.transform(matrix)

        return normalizedPath
    }

    private fun rotatePoint(
        x: Float,
        y: Float,
        cx: Float,
        cy: Float,
        degrees: Float,
    ): Pair<Float, Float> {
        val rad = Math.toRadians(degrees.toDouble())
        val cos = cos(rad)
        val sin = sin(rad)
        val dx = x - cx
        val dy = y - cy
        val nx = dx * cos - dy * sin + cx
        val ny = dx * sin + dy * cos + cy
        return Pair(nx.toFloat(), ny.toFloat())
    }

    private fun createRoundedPath(vertices: List<Vertex>): Path {
        val path = Path()
        val n = vertices.size
        if (n < 3) {
            return path
        }

        // Precompute all corner cuts so each edge connects from this corner's end
        // to the next corner's start in one consistent winding direction.
        val corners =
            List(n) { i ->
                calculateCorner(vertices[(i - 1 + n) % n], vertices[i], vertices[(i + 1) % n])
            }

        path.moveTo(corners[0].start.x, corners[0].start.y)

        for (i in 0 until n) {
            val curr = vertices[i]
            val corner = corners[i]
            val nextCorner = corners[(i + 1) % n]

            path.quadTo(curr.x, curr.y, corner.end.x, corner.end.y)
            path.lineTo(nextCorner.start.x, nextCorner.start.y)
        }
        path.close()
        return path
    }

    private data class Corner(val start: PointF, val end: PointF)

    private fun calculateCorner(prev: Vertex, curr: Vertex, next: Vertex): Corner {
        // Vector 1: curr -> prev
        val v1x = prev.x - curr.x
        val v1y = prev.y - curr.y
        val len1 = hypot(v1x, v1y)

        // Vector 2: curr -> next
        val v2x = next.x - curr.x
        val v2y = next.y - curr.y
        val len2 = hypot(v2x, v2y)

        // Angle between vectors
        // dot product = |a||b|cos(theta)
        val dot = (v1x * v2x + v1y * v2y)
        val angleRad = acos((dot / (len1 * len2)).coerceIn(-1.0f, 1.0f))

        // Distance from vertex to tangent point: d = radius / tan(theta/2)
        // The angle 'theta' here is the angle *between* the vectors.
        // The internal angle of the corner is actually related to this.
        // Standard formula for corner cut: dist = r / tan(internalAngle / 2)
        // Here 'angleRad' is the angle between the two edges.
        val tanHalf = tan(angleRad / 2.0)

        // Calculate cut distance based on radius
        var dist = if (tanHalf != 0.0) (curr.r / tanHalf).toFloat() else 0f

        // Clamp distance to avoid overlapping corners (max half the edge length)
        // Note: For "Cookie" shape, the radius is huge, so this clamping essentially
        // turns the edges into single points, creating a full curve.
        val limit = min(len1, len2) / 2.0f
        if (dist > limit) dist = limit

        // Calculate Start point (on curr->prev line)
        val startX = curr.x + (v1x / len1) * dist
        val startY = curr.y + (v1y / len1) * dist

        // Calculate End point (on curr->next line)
        val endX = curr.x + (v2x / len2) * dist
        val endY = curr.y + (v2y / len2) * dist

        return Corner(PointF(startX, startY), PointF(endX, endY))
    }
}
