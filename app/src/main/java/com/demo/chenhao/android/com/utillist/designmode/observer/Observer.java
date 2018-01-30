package com.demo.chenhao.android.com.utillist.designmode.observer;

/**
 * @Features: 定义一个观察者接口
 * @author: create by chenhao on 2018/1/30
 */

interface Observer {
    /**
     * 更新观察者数据
     * @param subJect
     */
     void update(SubJect subJect);
}
