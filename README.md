# PyTorch on Flink with ONNX

This repository shows an example of using [Apache Flink][1] to serve an [ONNX model][2]
that was created by [PyTorch][3]. While the initial model is simple, it shows the
process by which a machine learning model can be created in Python, and then
served by a Scala program written for streaming with Flink.

[1]: https://flink.apache.org/
[2]: https://onnx.ai/
[3]: https://pytorch.org/

## Training in PyTorch

The [`training/`](./training) directory provides an example Jupyter Notebook
that uses PyTorch to create a simple network. The network is exported to ONNX
through PyTorch. The network takes a variable length floating point tensor
and adds a constant offset. This network could be easily replaced with a fully
trained neural network.

## Packaging the JAR

The ONNX model is [packaged as a resource](./src/main/resources/), so that it can be
distributed to the Flink TaskManagers in the same JAR as the code.

The Flink application follows the standard template for SBT. The JAR can be built
using `sbt` as follows:

```bash
sbt clean assembly
```

The Scala code is also tested to ensure quality, and those tests are run by `sbt`.
These tests exercise the inference of the ONNX model.

## Serving with onnxruntime

The ONNX model is served using the [`onnxruntime`][4] library. The [Java API][5]
is used, which provides a similar interface to Python. The `OrtModel` class is added as a
convenience wrapper to handle loading from the resources directory.

Running the model inference is wrapped by the `AddFive` class, which extends the
[`RichMapFunction`][6]. This allows the `open` and `close` methods to handle the model
loading and closing. The `map` method runs the input value through the ONNX model.

The types need to be handled properly, since fractional values default to `Double`
in Scala unless specifically identified.

[4]: https://github.com/microsoft/onnxruntime
[5]: https://github.com/microsoft/onnxruntime/blob/master/docs/Java_API.md
[6]: https://ci.apache.org/projects/flink/flink-docs-release-1.11/api/java/org/apache/flink/api/common/functions/RichMapFunction.html

## Streaming in Flink

With the JAR built, the Flink job can be submitted. The job is written over a static
dataset of numbers as a simple example. This could be easily replaced by another source.
The job outputs the results to stdout for simplicity, which could also be replaced by
another sink. The [DataStream API][7] is used in this example.

To run the JAR on a local cluster, make sure to start it first.

```bash
./flink-1.11.1/bin/start-cluster.sh
```

The `com.datacolin.Job` can be submitted to the cluster through the CLI:

```bash
./flink-1.11.1/bin/flink run -c com.datacolin.WordCount ./flink-onnx-pytorch/target/scala-2.11/flink-onnx-pytorch-assembly-0.0.1.jar
```

The local cluster will provide output in the `log/` directory, which can be watched:

```bash
tail -f ./flink-1.11.1/log/*.out
```

The static dataset should be output with the added offset.

[7]: https://ci.apache.org/projects/flink/flink-docs-release-1.11/dev/datastream_api.html

## Future directions

This demonstration provides the basic working pieces to get a Python machine learning
model running in a streaming Scala application in Flink. There are a number of
interesting real-time applications to try next.
