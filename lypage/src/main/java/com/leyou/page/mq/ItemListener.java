package com.leyou.page.mq;

import com.leyou.page.service.PageService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ItemListener {
    @Autowired
    private PageService pageService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "page.item.insert.queue", durable = "true"),
            exchange = @Exchange(
                    value = "ly.item.exchange",
                    type = ExchangeTypes.TOPIC  ///  . 的形式  用topic
            ),
            key = {"item.insert","item.update"}))
    public void listenInsertOrUpdate(Long spuId){
        if (spuId == null){
            return;
        }
        //对静态页进行修改(先删除旧的，在新建新的)
        pageService.createHtml(spuId);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "page.item.delete.queue", durable = "true"),
            exchange = @Exchange(
                    value = "ly.item.exchange",
                    type = ExchangeTypes.TOPIC  ///  . 的形式  用topic
            ),
            key = {"item.delete"}))
    public void listenDelete(Long spuId){
        if (spuId == null){
            return;
        }
        //对静态页进行删除
        pageService.deleteHtml(spuId);
    }
}
