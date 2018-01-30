package com.demo.chenhao.android.com.utillist.designmode.observer;

/**
 * @Features: 具体观察者对象
 * @author: create by chenhao on 2018/1/30
 */

public class ConCreteObserver implements  Observer {
    /**
     * 观察者状态
     */
    private String obServerState;

    /**
     * 更新观察者状态
     */
    @Override
    public void update(SubJect subJect) {
        //得到订阅者状态之后更新观察者状态
        obServerState = ((ConCreteSubject)subJect).getSubJectState();
    }
}
