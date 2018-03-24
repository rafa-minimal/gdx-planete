import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType.DynamicBody
import com.badlogic.gdx.physics.box2d.World
import io.kotlintest.matchers.plusOrMinus
import io.kotlintest.matchers.shouldBe
import ktx.box2d.body
import org.junit.jupiter.api.Test

class Box2dTest {

    val world = World(Vector2.Zero, false)

    @Test
    fun testBox2dAngle() {
        var body = world.body(DynamicBody) {
            angle = 0f
        }

        val localVector = Vector2(0f, 10f)
        var worldVector = body.getWorldVector(localVector)
        worldVector.x shouldBe 0f
        worldVector.y shouldBe 10f

        body = world.body(DynamicBody) {
            angle = MathUtils.PI / 2
        }

        worldVector = body.getWorldVector(localVector)
        worldVector.x shouldBe (-10.0 plusOrMinus 0.01)
        worldVector.y shouldBe (0.0 plusOrMinus 0.01)
    }

    @Test
    fun vectorAngle() {
        Vector2(10f, 0f).angleRad() shouldBe 0f
        Vector2(0f, 10f).angleRad() shouldBe (MathUtils.PI / 2.0 plusOrMinus (0.01))
    }
}