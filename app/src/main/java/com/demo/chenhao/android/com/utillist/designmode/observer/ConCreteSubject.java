package com.demo.chenhao.android.com.utillist.designmode.observer;

/**
 * @Features: 具体的订阅者对象
 * @author: create by chenhao on 2018/1/30
 */

public class ConCreteSubject extends SubJect {

    public String getSubJectState() {
        return subJectState;
    }

    public void setSubJectState(String subJectState) {
        this.subJectState = subJectState;
        this.notifyObserver();
    }

    private String subJectState;


}
