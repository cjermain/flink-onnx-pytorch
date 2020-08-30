A Flink application project using Scala and SBT.

To run and test your application locally, you can just execute `sbt run` then select the main class that contains the Flink job.

You can also package the application into a fat jar with `sbt assembly`, then submit it as usual, with something like:

```
flink run -c com.datacolin.WordCount /path/to/your/project/my-app/target/scala-2.11/flink-onnx-assembly-0.0.1.jar
```
