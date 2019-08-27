package zhc.java.com.zhc.manager.proxy;

import zhc.java.com.zhc.manager.TestProxy;
import zhc.java.com.zhc.manager.impl.TestProxyImpl;

public class SubjectProxyImpl implements TestProxy {

    private TestProxy testProxy;

    public SubjectProxyImpl(TestProxy testProxy){
        this.testProxy = testProxy;
    }

    @Override
    public void test() {

        System.out.println("test--SubjectProxyImpl--begin");

        if(null == testProxy){
            testProxy = new TestProxyImpl();
        }
        testProxy.test();
        System.out.println("test--SubjectProxyImpl--end");

    }
}
