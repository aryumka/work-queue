package messaging

import com.rabbitmq.client.ConnectionFactory

class Sender {
  private val QUEUE_NAME = "task_queue"

  fun send(argv: Array<String>) {
    val factory = ConnectionFactory()
    factory.host = "localhost"

    factory.newConnection().use { connection ->
      connection.createChannel().use { channel ->
        channel.queueDeclare(QUEUE_NAME, false, false, false, null)
        // 프로그램 실행 인자를 메시지로 보낸다.
        val message = argv.joinToString(" ")

        channel.basicPublish("", QUEUE_NAME, null, message.toByteArray())
        println(" [x] Sent '$message'")
      }
    }
  }
}

fun main(argv: Array<String>) {
  Sender().send(argv)
}
