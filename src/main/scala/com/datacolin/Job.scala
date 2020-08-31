package com.datacolin


import org.apache.flink.streaming.api.scala._


object Job {
  def main(args: Array[String]): Unit = {
    // set up the execution environment
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    val data = env.fromElements(1.0, 2.0, 3.0, 4.0, 5.0)
    data.map{_.toFloat}.map(new AddFive).print()

    // execute program
    env.execute("Flink ONNX - Add Five")
  }
}
