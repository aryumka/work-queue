package messaging

import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.DeliverCallback

class Receiver {
  //어떤 큐로부터 메시지를 받을지를 정의한다.
  private val QUEUE_NAME = "task_queue"

  fun receive() {
    val factory = ConnectionFactory()
    factory.host = "localhost"

    val connection = factory.newConnection()
    val channel = connection.createChannel()

    channel.queueDeclare(QUEUE_NAME, false, false, false, null)
    println(" [*] Waiting for messages. To exit press Ctrl+C")

    // DeliverCallback: 서버로부터 푸쉬된 메시지를 수신할 때 호출되는 콜백이다. 실제로 이 메시지를 사용할 때까지 buffer된다.
    val deliverCallback = DeliverCallback { _, delivery ->
      val message = String(delivery.body, charset("UTF-8"))
      println(" [x] Received '$message'")
      try {
        doWork(message)
      } catch (e: InterruptedException) {
        e.printStackTrace()
      } finally {
        println(" [x] Done")
      }
    }

    channel.basicConsume(QUEUE_NAME, true, deliverCallback) { _: String? -> }
  }

  fun doWork(task: String) {
    for (char in task.toCharArray()) {
      if (char == '.') {
        Thread.sleep(1000)
      }
    }
  }
}

fun main() {
  Receiver().receive()
}
