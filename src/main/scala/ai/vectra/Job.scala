package ai.vectra


import org.apache.flink.streaming.api.scala._


object Job {
  def main(args: Array[String]): Unit = {
    // set up the execution environment
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    val data = env.fromElements("/0.mnist.bin", "/5.mnist.bin")
    data.map(new MNISTClassifier).print()

    // execute program
    env.execute("Flink ONNX - MNIST Classifier")
  }
}
