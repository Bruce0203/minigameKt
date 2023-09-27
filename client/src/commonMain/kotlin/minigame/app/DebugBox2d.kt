package minigame.app

import app.Stage
import korlibs.event.Key
import korlibs.image.vector.draw
import korlibs.korge.input.keys
import korlibs.korge.view.addUpdater
import korlibs.korge.view.container
import korlibs.korge.view.graphics
import korlibs.korge.view.line
import korlibs.math.geom.Point
import korlibs.math.geom.shape.toPolygon
import korlibs.math.geom.toPointArrayList
import org.jbox2d.callbacks.DebugDraw
import org.jbox2d.common.Color3f
import org.jbox2d.common.Transform
import org.jbox2d.common.Vec2
import org.jbox2d.particle.ParticleColor
import minigame.screen

fun Stage.debugView() {
    val stage = this
    val debugBox2d = DebugBox2d(stage)
        screen.keys.down(Key.SHIFT) {
            debugBox2d.debugEnabled = !debugBox2d.debugEnabled
        }
}

class DebugBox2d(stage: Stage) : DebugDraw() {
    var debugView = stage.camera.container {  }
    var debugEnabled = false

    init {
        appendFlags(DebugDraw.e_aabbBit)
        appendFlags(DebugDraw.e_shapeBit)
        screen.addUpdater {
            stage.box2dWorld.setDebugDraw(this@DebugBox2d)
            debugView.removeFromParent()
            if (debugEnabled) {

                debugView = stage.camera.container {  }
                stage.box2dWorld.drawDebugData()
            }
        }
    }

    override fun drawPoint(argPoint: Vec2, argRadiusOnScreen: Float, argColor: Color3f) {
        TODO("Not yet implemented")
    }

    override fun drawSolidPolygon(vertices: Array<Vec2>, vertexCount: Int, color: Color3f) {
        debugView.graphics {
            val pointArrayList = vertices.map { Point(it.x * 20f, it.y * 20f) }.toPointArrayList()
            draw(pointArrayList.toPolygon())
        }
    }

    override fun drawCircle(center: Vec2, radius: Float, color: Color3f) {
//                stage.camera.circle(radius).position(center.x, center.y)
    }

    override fun drawSolidCircle(center: Vec2, radius: Float, axis: Vec2, color: Color3f) {
//                stage.camera.circle(radius).position(center.x, center.y)
    }

    override fun drawSegment(p1: Vec2, p2: Vec2, color: Color3f) {
        debugView.line(p1.run { Point(x * 20f, y * 20f) }, p2.run { Point(x * 20f, y * 20f) })
    }


    override fun drawTransform(xf: Transform) {
        TODO("Not yet implemented")
    }

    override fun drawString(x: Float, y: Float, s: String, color: Color3f) {
        TODO("Not yet implemented")
    }

    override fun drawParticles(centers: Array<Vec2>, radius: Float, colors: Array<ParticleColor>, count: Int) {
        TODO("Not yet implemented")
    }

    override fun drawParticlesWireframe(
        centers: Array<Vec2>,
        radius: Float,
        colors: Array<ParticleColor>,
        count: Int
    ) {
        TODO("Not yet implemented")
    }


}
