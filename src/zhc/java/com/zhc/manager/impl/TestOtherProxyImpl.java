package zhc.java.com.zhc.manager.impl;

import zhc.java.com.zhc.manager.TestProxy;

public class TestOtherProxyImpl implements TestProxy {
    @Override
    public void test() {
        System.out.println("test--TestOtherProxyImpl");
    }
}
