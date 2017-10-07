package com.minimal.planet

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import com.badlogic.gdx.physics.box2d.Shape.Type
import com.badlogic.gdx.utils.Array as GdxArray

class BoxPainter(ctx: Context) {

    val batch = ctx.batch

    private val lower = Vector2()
    private val upper = Vector2()

    private val bodies = GdxArray<Body>()
    private val joints = GdxArray<Joint>()

    val stroke1 = ctx.atlas.findRegion("stroke1")

    val width = 0.3f

    private val vertices = Array<Vector2>(1000) {i -> Vector2()}

    fun render(world: World, projMatrix: Matrix4) {
        batch.projectionMatrix = projMatrix
        batch.begin()
        renderBodies(world)
        batch.end()
    }

    private fun renderBodies(world: World) {
        world.getBodies(bodies)
        for (body in bodies) {
            renderBody(body)
        }
    }

    protected fun renderBody(body: Body) {
        val transform = body.transform
        for (fixture in body.fixtureList) {
            drawShape(fixture, transform, Color.WHITE)
        }
    }

    private val t = Vector2()
    private var axis = Vector2()

    private fun drawShape(fixture: Fixture, transform: Transform, color: Color) {
        if (fixture.type == Type.Circle) {
            val circle = fixture.shape as CircleShape
            t.set(circle.position)
            transform.mul(t)
            drawSolidCircle(t, circle.radius, axis.set(transform.vals[Transform.COS], transform.vals[Transform.SIN]), color)
            return
        }
        else if (fixture.type == Type.Edge) {
            val edge = fixture.shape as EdgeShape
            edge.getVertex1(vertices[0])
            edge.getVertex2(vertices[1])
            transform.mul(vertices[0])
            transform.mul(vertices[1])
            drawSolidPolygon(vertices, 2, color, true)
            return
        }
        else if (fixture.type == Type.Polygon) {
            val chain = fixture.shape as PolygonShape
            val vertexCount = chain.vertexCount
            for (i in 0..vertexCount - 1) {
                chain.getVertex(i, vertices[i])
                transform.mul(vertices[i])
            }
            drawSolidPolygon(vertices, vertexCount, color, true)
            return
        }
        else if (fixture.type == Type.Chain) {
            val chain = fixture.shape as ChainShape
            val vertexCount = chain.vertexCount
            for (i in 0..vertexCount - 1) {
                chain.getVertex(i, vertices[i])
                transform.mul(vertices[i])
            }
            drawSolidPolygon(vertices, vertexCount, color, false)
        }
    }

    private val f = Vector2()
    private val v = Vector2()
    private val lv = Vector2()

    private fun drawSolidCircle(center: Vector2, radius: Float, axis: Vector2, color: Color) {
        var angle = 0f
        val angleInc = 2 * Math.PI.toFloat() / 20
        batch.setColor(color.r, color.g, color.b, color.a)
        var i = 0
        while (i < 20) {
            v.set(Math.cos(angle.toDouble()).toFloat() * radius + center.x, Math.sin(angle.toDouble()).toFloat() * radius + center.y)
            if (i == 0) {
                lv.set(v)
                f.set(v)
                i++
                angle += angleInc
                continue
            }
            line(lv.x, lv.y, v.x, v.y)
            lv.set(v)
            i++
            angle += angleInc
        }
        line(f.x, f.y, lv.x, lv.y)
//        line(center.x, center.y, 0f, center.x + axis.x * radius, center.y + axis.y * radius, 0f)
    }

    private fun drawSolidPolygon(vertices: Array<Vector2>, vertexCount: Int, color: Color, closed: Boolean) {
        batch.setColor(color.r, color.g, color.b, color.a)
        lv.set(vertices[0])
        f.set(vertices[0])
        for (i in 1..vertexCount - 1) {
            val v = vertices[i]
            line(lv.x, lv.y, v.x, v.y)
            lv.set(v)
        }
        if (closed) line(f.x, f.y, lv.x, lv.y)
    }

    /*private fun drawSegment(x1: Vector2, x2: Vector2, color: Color) {
        renderer.color = color
        renderer.line(x1.x, x1.y, x2.x, x2.y)
    }*/

    val tmp = Vector2()
    private fun line(x1: Float, y1: Float, x2: Float, y2: Float) {
        val angle = tmp.set(x2, y2).sub(x1, y1).angle()
        val len = tmp.len()
        tmp.rotate90(-1).scl(width/2f/len)
        batch.draw(stroke1, x1+tmp.x, y1+tmp.y, 0f, 0f, len, width, 1f, 1f, angle)
    }
}