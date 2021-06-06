package com.example.demo.service;

import java.util.concurrent.ConcurrentLinkedQueue;

public class WebSocketLogMessageManager {
    static{
        System.out.println("量词！！"+WebSocketLogMessageManager.class.hashCode());
        System.out.println(WebSocketLogMessageManager.class.getClassLoader());
        //2437c6dc
    }
    private static final ConcurrentLinkedQueue<byte[]> MESSAGE_QUEUE=new ConcurrentLinkedQueue<>();
    /**
     * 最大保留消息间隔
     */
    private static final int MAX_MESSAGE_SIZE=1000;
    /**
     * 无消息时和再次尝试获取间隔毫秒
    */
    private static final int NO_MESSAGE_WAIT_TIMEOUT_MILLISECONDS=1000;

    /**
     * 此方法调用频率高，并发高，不能加锁
     */
    public static void add(byte[] message) {
        final ConcurrentLinkedQueue<byte[]> queue=MESSAGE_QUEUE;
        if(queue.size()>MAX_MESSAGE_SIZE){
            //删除最旧的
            queue.poll();
        }
        queue.add(message);
    }

    public static byte[] take() throws InterruptedException {
            final ConcurrentLinkedQueue<byte[]> queue=MESSAGE_QUEUE;
            byte[] message=queue.poll();
            if(message!=null)return message;
            while((message=queue.poll())==null){
                synchronized (queue) {
                    //避免死锁
                    queue.wait(NO_MESSAGE_WAIT_TIMEOUT_MILLISECONDS);
                }
            }
            return message;
    }

}