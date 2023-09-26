package app

import korlibs.datastructure.fastArrayListOf
import korlibs.datastructure.iterators.fastForEach
import korlibs.image.tiles.TileShapeInfo
import korlibs.image.tiles.TileShapeInfoImpl
import korlibs.image.tiles.tiled.TileData
import korlibs.korge.box2d.*
import korlibs.korge.tiled.TileMapEx
import korlibs.korge.tiled.TiledMapView
import korlibs.korge.tiled.getTiledPropString
import korlibs.korge.view.Container
import korlibs.korge.view.container
import korlibs.math.geom.Circle
import korlibs.math.geom.Matrix
import korlibs.math.geom.Point
import korlibs.math.geom.Rectangle
import korlibs.math.geom.collider.HitTestDirectionFlags
import korlibs.math.geom.shape.Shape2D
import korlibs.math.geom.shape.cachedPoints
import korlibs.math.geom.vector.VectorPath
import org.jbox2d.collision.shapes.ChainShape
import org.jbox2d.collision.shapes.CircleShape
import org.jbox2d.collision.shapes.Shape
import org.jbox2d.common.Settings
import org.jbox2d.common.Vec2
import org.jbox2d.dynamics.BodyType

const val tiledMapMultiplier = 0.1f * 0.5f

fun Container.registerCollider(tiledMapView: TiledMapView) {
    val worldView = this
    tiledMapView.forEachTileInfo { tile, x, y ->
        getCollisionShapes(tile, x, y).fastForEach { shape ->
            val body = worldView.createBody {
                type = BodyType.STATIC
                allowSleep = false
            }
            val bodyView = worldView.container()
            bodyView.body = body
            body.also { it.view = bodyView }
            body.fixture {
                friction = 1f
                this.shape = shape
                density = 0f
            }
        }
    }
}
var count: Boolean = true
fun convertShape(shape: Shape2D, offset: Point = Point.ZERO): Shape {
    return when(shape) {
        is VectorPath, is Rectangle -> {
            val points2List = shape.toVectorPath().cachedPoints.toList()
            ChainShape().apply {

                val points = points2List.toList()
                Settings.maxPolygonVertices = points.size
                createChain(points.map { (x, y) ->
                    Vec2((x * tiledMapMultiplier + offset.x), (y * tiledMapMultiplier + offset.y))
                }.toTypedArray(), points.size - if (shape is Rectangle) 0 else 0)
            }
        }
        is Circle -> CircleShape(shape.radius.times(tiledMapMultiplier))
            .also { it.p.x = shape.center.x + offset.x; it.p.y = shape.center.y + offset.y }
        else -> throw AssertionError("Unknown Shape2D: ${shape::class}")
    }
}
fun getCollisionShapes(tile: TileData, tileX: Int, tileY: Int): List<Shape> {
    val vectorPaths = fastArrayListOf<TileShapeInfo>()
    if (tile.objectGroup != null) {
        tile.objectGroup!!.objects.fastForEach {
            vectorPaths.add(
                TileShapeInfoImpl(
                    HitTestDirectionFlags.fromString(it.type),
                    it.toShape2dNoTransformed(),
                    it.getTransform().immutable,
                )
            )
        }
    }
    val shapes = fastArrayListOf<Shape>()
    vectorPaths.fastForEach {
        val tileShapeInfoImpl = it as TileShapeInfoImpl
        val shape2D = tileShapeInfoImpl.shape
        val transform = tileShapeInfoImpl.transform
        val offset = Point(tileX.toFloat() * 0.8f, tileY.toFloat() * 0.8f) + transform.transform(Point()) * tiledMapMultiplier
        shapes.add(convertShape(shape2D, offset))
    }//
    return shapes
}
fun TiledMapView.forEachTileInfo(code: (tile: TileData, x: Int, y: Int) -> Unit) {
    val tiledMapView = this
    val tileLayers = tiledMapView.children
    val collisionLayers = tileLayers.map { it as TileMapEx }.filter { it.getTiledPropString("col") !== null }
    collisionLayers.fastForEach { layer ->
        val tileData = tiledMap.data
        for (tileX in 0 until tileData.width) for (tileY in 0 until tileData.height) {
            val tileID = layer.stackedIntMap.getLast(tileX, tileY) - 1
            if (tileID == -1) continue
            val tile = tiledMapView.tiledMap.data.tilesets.first().tilesById[tileID]!!
            code(tile, tileX, tileY)
        }
    }
}

