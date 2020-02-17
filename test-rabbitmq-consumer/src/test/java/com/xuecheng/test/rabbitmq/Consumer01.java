package com.xuecheng.test.rabbitmq;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Consumer01 {


    //队列名称
    private static final String QUEUE="helloword";

    public static void main(String[] args) throws IOException, TimeoutException {
        //1.建立生产者与消息队列服务连接
        //通过创建连接工厂创建新的连接和mq建立连接
        ConnectionFactory connectionFactory = new ConnectionFactory();
        // 设置连接工厂的参数
        connectionFactory.setUsername("127.0.0.1");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        connectionFactory.setVirtualHost("/");
        //建立新连接
       Connection connection = connectionFactory.newConnection();
        // 2 建立连接通道
        //创建会话通道，生产者和mq所有的通信都在channel 通道中完成.每个连接可以创建多个通道，每个通道代表一个会话任务
        Channel channel = connection.createChannel();
        // 3.声明队列,如果Rabbit中没有此队列将自动创建
        //String queue, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments
        /**
         * string queue:队列名称
         *boolean durable:是否持久化，如持久化，队列重启后消息还在
         * boolean exclusive：是否独占连接。如果独占，则队列中只允许在该连接访问。如果connection关闭则自动删除，如果将参数设置为true可用于临时队列创建
         *  boolean autoDelete:队列不使用时是否自动删除。如果true，队列不使用时自动删除。如果将exclusive设置为true就可以实现临时队列(队列不使用后自动删除)
         *  arguments:扩展参数，可以设置一个队列的扩展参数，比如：队列存活时间
         */
        channel.queueDeclare(QUEUE,true,false,false,null);

        DefaultConsumer defaultConsumer = new DefaultConsumer(channel){
            // 当接收到消息后此方法会被调用
            /**
             * @param consumerTag 消费者标签，用来标识消费者的，在监听队列时设置chanel.basicConsume()设置
             * @param envelope 信封
             * @param properties 消息属性
             * @param body 消息体
             * @throws IOException
             */
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                // 交换机
                String exchange = envelope.getExchange();
                // 消息id，mq在channel中用来标识消息的id，用于确认消息已接受
                long deliveryTag = envelope.getDeliveryTag();
                // 消息体
                String message = new String(body,"utf-8");
                System.out.println("接收到消息："+message);
            }
        };

        //4.监听队列
        //String queue, boolean autoAck, Consumer callback

        /**String queue:队列名称
         * boolean autoAck：是否自动回复。当消费者接受到消息后回复mq消息已接受，如果设置为false需要通过编程回复
         * Consumer callback：消费方法，当消费者接受到消息后需要执行的方法
         */
        channel.basicConsume(QUEUE,true,defaultConsumer);

    }



}
