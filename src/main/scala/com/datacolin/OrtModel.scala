package com.datacolin

import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import org.apache.commons.io.IOUtils.toByteArray


class OrtModel(resourceName: String, var env: OrtEnvironment = null) {
  var session: OrtSession = null

  def load(): Unit = {
    // Get the current ONNX runtime environment if not specified
    if (env == null) {
      env = OrtEnvironment.getEnvironment()
    }

    // Load the ONNX model from the resources InputStream
    val modelArray = toByteArray(getClass.getResourceAsStream(resourceName))
    session = env.createSession(modelArray)
  }
}
