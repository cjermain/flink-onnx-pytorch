package ai.vectra

import java.nio.{ByteBuffer, FloatBuffer, ByteOrder}
import org.apache.commons.io.IOUtils.toByteArray


class MNISTReader(resourceName: String) {
  var floatArray: FloatBuffer = null
  val shape = Array[Long](1, 28, 28)

  def load(): Unit = {
    // Load the MNIST image from the resources InputStream
    val byteArray = toByteArray(getClass.getResourceAsStream(resourceName))
    floatArray = ByteBuffer.wrap(byteArray).order(ByteOrder.LITTLE_ENDIAN).asFloatBuffer
  }
}
