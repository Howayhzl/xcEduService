package com.xuecheng.test.rabbitmq;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Producer02_publish {

    private  static  final  String  QUEUE_INFORM_EMAIL  =  "queue_inform_email";
    private  static  final  String  QUEUE_INFORM_SMS  =  "queue_inform_sms";
    private  static  final  String  EXCHANGE_FANOUT_INFORM="exchange_fanout_inform";

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
            channel.queueDeclare(QUEUE_INFORM_EMAIL,true,false,false,null);
            channel.queueDeclare(QUEUE_INFORM_SMS,true,false,false,null);
            //声明一个交换机
            //声明交换机 String exchange, BuiltinExchangeType type
            /**
             * 参数明细
             * 1、交换机名称
             * 2、交换机类型，
             * fanout:Publish/Subscribe
             * topic:Topics
             * direct:Routing
             * headers:Header
             */
            channel.exchangeDeclare(EXCHANGE_FANOUT_INFORM, BuiltinExchangeType.FANOUT);

            // 绑定交换机和队列String queue, String exchange, String routingKey
            /**String queue：队列名称
             * String exchange：交换机名称
             * String routingKey：路由key 作用是交换机根据路由key的值将消息转发到指定的消息队列中，在Publish/Subscribe模式中设置为空字符串
             */
            channel.queueBind(QUEUE_INFORM_EMAIL,EXCHANGE_FANOUT_INFORM,"");
            channel.queueBind(QUEUE_INFORM_SMS,EXCHANGE_FANOUT_INFORM,"");


            // 4.发送消息
            // String exchange, String routingKey, BasicProperties props, byte[] body
            /**
             * String exchange:Exchange的名称，如果没有指定，则使用Default Exchange
             * String routingKey:消息的路由Key，是用于Exchange（交换机）将消息转发到指定的消息队列
             * BasicProperties props:消息包含的属性
             * byte[] body:消息体，消息内容
             */
            for (int i = 0; i < 5; i++) {
                //消息内容
                String message = "send inform message to user";
                //String exchange, String routingKey, BasicProperties props, byte[] body
                channel.basicPublish(EXCHANGE_FANOUT_INFORM,"",null,message.getBytes());
                System.out.println("send to mq"+message);
            }
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
