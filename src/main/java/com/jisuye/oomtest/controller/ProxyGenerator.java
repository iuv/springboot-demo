package com.jisuye.oomtest.controller;

import com.jisuye.oomtest.service.SampleService;
import com.jisuye.oomtest.service.impl.SampleServiceImpl;
import org.springframework.cglib.proxy.InvocationHandler;
import org.springframework.cglib.proxy.Proxy;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Component
public class ProxyGenerator {
    private final List<Object> proxies = new ArrayList<>();

    public String createNewProxy() {
        // 创建一个新的动态代理实例
        SampleService proxy = (SampleService) Proxy.newProxyInstance(
                this.getClass().getClassLoader(),
                new Class[]{SampleService.class},
                new SampleServiceInvocationHandler(new SampleServiceImpl()));

        proxies.add(proxy); // 保持引用防止被GC

        return "Created proxy #" + proxies.size();
    }

    private static class SampleServiceInvocationHandler implements InvocationHandler {

        private final SampleService target;

        public SampleServiceInvocationHandler(SampleService target) {
            this.target = target;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            return method.invoke(target, args);
        }
    }
}
