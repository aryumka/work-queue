package messaging

import com.rabbitmq.client.ConnectionFactory

class Sender {
  private val QUEUE_NAME = "task_queue"

  fun send(argv: Array<String>) {
    val factory = ConnectionFactory()
    factory.host = "localhost"

    // Connection과 Channel은 모두 java.lang.AutoCloseable을 상속받고 있다. 따라서 use를 사용하면 블록이 리턴될 때 close를 호출해준다.
    // Connection: 소켓 연결이 추상화된 객체이다. RabbitMQ는 물리적으로 단일 소켓을 통한 TCP 연결을 사용한다.
    factory.newConnection().use { connection ->
      // Channel: Connection을 공유하는 논리적인 개념의 다중화된 경량 연결이다. 실제 api가 메시지를 보내고 받는 작업을 수행한다. Connection의 생명주기에 종속적이다.
      connection.createChannel().use { channel ->
        // 큐를 선언한다. 이미 존재하는 큐를 선언하면 무시된다. 큐 생성은 멱등성(idempotent)을 가진다.
        channel.queueDeclare(QUEUE_NAME, false, false, false, null)
        val message = argv.joinToString(" ")

        // 메시지 내용은 byte array여야 한다.
        channel.basicPublish("", QUEUE_NAME, null, message.toByteArray())
        println(" [x] Sent '$message'")
      }
    }
  }
}

fun main(argv: Array<String>) {
  Sender().send(argv)
}
