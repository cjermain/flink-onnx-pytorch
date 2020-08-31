package com.datacolin

import scala.collection.JavaConversions.mapAsJavaMap
import org.apache.flink.api.common.functions.RichMapFunction
import org.apache.flink.configuration.Configuration
import ai.onnxruntime.OnnxTensor


class AddFive extends RichMapFunction[Float, Float] {
  var model: OrtModel = null

  override def open(config: Configuration): Unit = {
    // Load the ONNX model
    model = new OrtModel("/add_five.onnx")
    model.load()
  }

  override def close(): Unit = {
    model.session.close()
    model.env.close()
  }

  def map(in: Float): Float = {
    val tensor = OnnxTensor.createTensor(model.env, Array(in))
    val inputs = mapAsJavaMap(Map("x" -> tensor))
    
    // Run the inputs through the ONNX model
    val results = model.session.run(inputs).get(0)
    val output = results.getValue().asInstanceOf[Array[Float]]
    output(0)
  }
}
