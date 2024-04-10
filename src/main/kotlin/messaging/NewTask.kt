package messaging

import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.MessageProperties

class Sender {
  private val QUEUE_NAME = "task_queue"

  fun send(argv: Array<String>) {
    val factory = ConnectionFactory()
    factory.host = "localhost"

    factory.newConnection().use { connection ->
      connection.createChannel().use { channel ->
        val durable = true
        // durable을 true로 설정하면 RabbitMQ가 재시작되어도 큐가 유지된다.
        channel.queueDeclare(QUEUE_NAME, durable, false, false, null)
        // 프로그램 실행 인자를 메시지로 보낸다.
        val message = argv.joinToString(" ")

        channel.basicPublish(
          "",
          QUEUE_NAME,
          // 메시지를 디스크에 영속화한다.
          // 하지만 메시지 무손실을 보장하지 않는다. RabbitMQ가 메시지를 디스크에 저장하기 전에 죽으면 메시지는 손실될 수 있다.
          // 메시지를 무손실로 보장하려면 publisher confirms를 사용해야 한다. https://www.rabbitmq.com/docs/confirms
          MessageProperties.PERSISTENT_TEXT_PLAIN,
          message.toByteArray()
        )
        println(" [x] Sent '$message'")
      }
    }
  }
}

fun main(argv: Array<String>) {
  Sender().send(argv)
}
