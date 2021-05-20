package ai.vectra

import scala.collection.JavaConversions.mapAsJavaMap
import org.apache.flink.api.common.functions.RichMapFunction
import org.apache.flink.configuration.Configuration
import ai.onnxruntime.OnnxTensor
import com.datacolin.OrtModel


class MNISTClassifier extends RichMapFunction[String, Int] {
  var model: OrtModel = null

  override def open(config: Configuration): Unit = {
    // Load the ONNX model
    model = new OrtModel("/linear_mnist.onnx")
    model.load()
  }

  override def close(): Unit = {
    model.session.close()
    model.env.close()
  }

  def map(fileName: String): Int = {
    val reader = new MNISTReader(fileName)
    reader.load()

    val tensor = OnnxTensor.createTensor(model.env, reader.floatArray, reader.shape)
    val inputs = mapAsJavaMap(Map("x" -> tensor))

    // Run the inputs through the ONNX model
    val results = model.session.run(inputs).get(0).getValue().asInstanceOf[Array[Array[Float]]]
    val probabilities = results(0)
    val label = probabilities.zipWithIndex.maxBy(x => x._1)._2
    label
  }
}
