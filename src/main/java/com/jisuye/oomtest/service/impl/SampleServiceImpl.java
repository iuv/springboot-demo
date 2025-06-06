package com.jisuye.oomtest.service.impl;

import com.jisuye.oomtest.service.SampleService;
import org.springframework.stereotype.Service;

@Service
public class SampleServiceImpl implements SampleService {
    @Override
    public String doSomething() {
        return "test";
    }
}
