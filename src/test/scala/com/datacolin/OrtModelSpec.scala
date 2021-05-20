package com.datacolin.tests

import org.scalatest.flatspec.AnyFlatSpec
import scala.collection.JavaConversions.mapAsJavaMap 
import ai.onnxruntime.OnnxTensor
import com.datacolin.OrtModel
import ai.vectra.MNISTReader



class OrtModelSpec extends AnyFlatSpec {
  "An OrtModel" should "construct a session" in {
    val model = new OrtModel("/add_five.onnx")
    model.load()
    assert(model.session != null)
  }

  it should "run a prediction" in {
    val model = new OrtModel("/add_five.onnx")
    model.load()

    val values: Array[Float] = Array(1, 2, 3, 4, 5)
    val expectedResults: Array[Float] = Array(6, 7, 8, 9, 10)

    val inputs = mapAsJavaMap(Map("x" -> OnnxTensor.createTensor(model.env, values)))
    val results = model.session.run(inputs).get(0).getValue().asInstanceOf[Array[Float]]

    assert(results sameElements expectedResults)
  }

  it should "run a MNIST prediction" in {
    val model = new OrtModel("/linear_mnist.onnx")
    model.load()

    val reader = new MNISTReader("/5.mnist.bin")
    reader.load()

    val tensor = OnnxTensor.createTensor(model.env, reader.floatArray, reader.shape)

    val inputs = mapAsJavaMap(Map("x" -> tensor))
    val results = model.session.run(inputs).get(0).getValue().asInstanceOf[Array[Array[Float]]]
    val probabilities = results(0)
    val label = probabilities.zipWithIndex.maxBy(x => x._1)._2

    assert(label == 5)
  }
}
