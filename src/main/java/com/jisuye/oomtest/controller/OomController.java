package com.jisuye.oomtest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/oom")
public class OomController {

    private final ProxyGenerator proxyGenerator;

    public OomController(ProxyGenerator proxyGenerator) {
        this.proxyGenerator = proxyGenerator;
    }

    // 堆溢出 启动参数 java -Xms10m -Xmx50m
    // 直接结束kill -9 使用 -XX:+CrashOnOutOfMemoryError 或-XX:+ExitOnOutOfMemoryError
    // 优雅退出（存在进程杀不掉的可能） -XX:OnOutOfMemoryError="kill -15 %p"
    // 优雅退出保险版本 -XX:OnOutOfMemoryError="kill -15 %p && sleep 5 && kill -9 %p &"
    @GetMapping("/heap")
    public String heapOOM(){
        List<Object> list = new ArrayList<>();
        long counter = 0;
        while (counter < 1024) {
            list.add(new byte[1024 * 1024]); // 1MB
            counter++;
        }
        return "suc";
    }

    // 元空间溢出
    // 堆溢出 启动参数 java -Xms10m -Xmx50m -XX:MetaspaceSize=10m
    @GetMapping("/metaspace")
    public String metaspaceOOM(){
        long counter = 0;
        while (counter < Integer.MAX_VALUE) {
            counter++;
            proxyGenerator.createNewProxy();
        }
        return "suc";
    }

    // GC overhead limit exceeded
    // 启动参数 java -Xms10m -Xmx50m
    @GetMapping("/gc")
    public String GCOverheadOOM(){
        List<Double> list = new ArrayList<>();
        int i = 0;
        while (i<Integer.MAX_VALUE) {
            i++;
            list.add(Math.random());
            if (i % 1000 == 0) {
                list.remove(0); // 移除部分对象，但速度跟不上添加速度
            }
        }
        return "suc";
    }

    // 直接内存溢出
    // 启动参数 java -Xms10m -Xmx50m -XX:MaxDirectMemorySize=10m
    @GetMapping("/direct")
    public String directMemoryOOM(){
        long counter = 0;
        List<ByteBuffer> buffers = new ArrayList<>();
        int chunkSize = 1 * 1024 * 1024; // 每次分配 1MB
        while (counter < Integer.MAX_VALUE) {
            System.out.println(counter+"...");
            counter++;
            ByteBuffer buffer = ByteBuffer.allocateDirect(chunkSize);// 1MB
            buffers.add(buffer);
            // 填充数据以确保内存实际被使用
            for (int i = 0; i < chunkSize / 4; i++) {
                buffer.putInt(i);
            }
            buffer.flip();
        }
        return "suc";
    }
    // 无法创建新的本地线程(Unable to create new native thread)
    // 启动参数 java -Xss512k    设置每个线程的栈大小为 512KB。
    // -XX:OnOutMemoryError=""  未生效
    @GetMapping("/thread")
    public String unableToCreateThreadOOM(){
        long counter = 0;
        while (counter < Integer.MAX_VALUE) {
            counter++;
            new Thread(() -> {
                try {
                    Thread.sleep(Long.MAX_VALUE);
                } catch (InterruptedException e) {
                    // ignore
                }
            }).start();
        }
        return "suc";
    }
}
