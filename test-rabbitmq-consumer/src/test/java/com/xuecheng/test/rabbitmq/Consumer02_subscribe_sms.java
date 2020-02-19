package com.xuecheng.test.rabbitmq;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Consumer02_subscribe_sms {

    private  static  final  String  QUEUE_INFORM_SMS  =  "queue_inform_sms";
    private  static  final  String  EXCHANGE_FANOUT_INFORM="exchange_fanout_inform";

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
        // 2 创建会话通道,生产者和mq服务所有通信都在channel通道中完成
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
        channel.queueDeclare(QUEUE_INFORM_SMS,true,false,false,null);

        //进行交换机和队列绑定
        //String queue, String exchange, String routingKey

        /**
         * 参数明细：
         * 1、queue 队列名称
         * 2、exchange 交换机名称
         * 3、routingKey 路由key，作用是交换机根据路由key的值将消息转发到指定的队列中，在发布订阅模式中调协为空字符串
         */
        channel.queueBind(QUEUE_INFORM_SMS,EXCHANGE_FANOUT_INFORM,"");

        //实现消费方法
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
        //参数：String queue, boolean autoAck, Consumer callback
        /**
         * 参数明细：
         * 1、queue 队列名称
         * 2、autoAck 自动回复，当消费者接收到消息后要告诉mq消息已接收，如果将此参数设置为tru表示会自动回复mq，如果设置为false要通过编程实现回复
         * 3、callback，消费方法，当消费者接收到消息要执行的方法
         */
        channel.basicConsume(QUEUE_INFORM_SMS,true,defaultConsumer);
    }


}
