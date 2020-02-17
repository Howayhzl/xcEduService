package com.xuecheng.test.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Producer01 {

    //队列名称
    private static final String QUEUE="helloword";

    public static void main(String[] args) {
        //1.建立生产者与消息队列服务连接
          //通过创建连接工厂创建新的连接和mq建立连接
        ConnectionFactory connectionFactory = new ConnectionFactory();
        // 设置连接工厂的参数
        connectionFactory.setUsername("127.0.0.1");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        connectionFactory.setVirtualHost("/");
        Connection connection = null;
        Channel channel = null;
        try {
            //建立新连接
             connection = connectionFactory.newConnection();
            // 2 建立连接通道
            //创建会话通道，生产者和mq所有的通信都在channel 通道中完成.每个连接可以创建多个通道，每个通道代表一个会话任务
             channel = connection.createChannel();
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
            // 4.发送消息
            // String exchange, String routingKey, BasicProperties props, byte[] body
            /**
             * String exchange:Exchange的名称，如果没有指定，则使用Default Exchange
             * String routingKey:消息的路由Key，是用于Exchange（交换机）将消息转发到指定的消息队列
             * BasicProperties props:消息包含的属性
             * byte[] body:消息体，消息内容
             */
            //消息内容
            String message = "hello！黑马程序员";
            channel.basicPublish("",QUEUE,null,message.getBytes());
            System.out.println("send to mq"+message);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (channel!=null){
                try {
                    channel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                }
            }
            if (connection!=null){
                try {
                    connection.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

    }
}
