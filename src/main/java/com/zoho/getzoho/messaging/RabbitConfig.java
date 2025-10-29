package com.zoho.getzoho.messaging;


import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Value("${rabbitmq.out.queue}")     String outQueue;
    @Value("${rabbitmq.out.exchange}")  String outExchange;
    @Value("${rabbitmq.out.routingkey}") String outRoutingKey;

    @Value("${rabbitmq.in.queue}")      String inQueue;
    @Value("${rabbitmq.in.exchange}")   String inExchange;
    @Value("${rabbitmq.in.routingkey}") String inRoutingKey;

    @Value("${rabbitmq.dlq}")  String dlq;
    @Value("${rabbitmq.dlx}")  String dlx;
    @Value("${rabbitmq.dlqrk}") String dlqRoutingKey;

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory, MessageConverter mc) {
        RabbitTemplate tpl = new RabbitTemplate(connectionFactory);
        tpl.setMessageConverter(mc);
        return tpl;
    }


    @Bean
    DirectExchange outExchange() {
        return new DirectExchange(outExchange, true, false);
    }
    @Bean
    DirectExchange inExchange()  {
        return new DirectExchange(inExchange,  true, false);
    }
    @Bean
    DirectExchange deadLetterExchange() {
        return new DirectExchange(dlx, true, false);
    }

    @Bean
    Queue deadLetterQueue() {
        return QueueBuilder.durable(dlq).build();
    }

    @Bean
    Binding deadletterBinding() {
        return BindingBuilder.bind(deadLetterQueue()).to(deadLetterExchange()).with(dlqRoutingKey);
    }

    @Bean
    Queue outQueue() {
        return QueueBuilder.durable(outQueue)
                .withArgument("x-dead-letter-exchange", dlx)
                .withArgument("x-dead-letter-routing-key", dlqRoutingKey)
                .build();
    }

    @Bean
    Binding outBinding() {
        return BindingBuilder.bind(outQueue()).to(outExchange()).with(outRoutingKey);
    }


    @Bean
    Queue inQueue() {
        return QueueBuilder.durable(inQueue)
                .withArgument("x-dead-letter-exchange", dlx)
                .withArgument("x-dead-letter-routing-key", dlqRoutingKey)
                .build();
    }

    @Bean
    Binding inBinding() {
        return BindingBuilder.bind(inQueue()).to(inExchange()).with(inRoutingKey);
    }

}
