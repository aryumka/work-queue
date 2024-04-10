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
    val autoAck = true // autoAck를 true로 설정하면 RabbitMQ에 메시지를 전달한 후 바로 삭제한다. 디폴트는 true이다.
    channel.basicConsume(QUEUE_NAME, true, deliverCallback) { _: String? -> }
  }

  //시간이 걸리는 작업 수행을 모방하기 위해 메시지의 각 . 개수마다 1초를 기다린다.
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
