package com.demo.chenhao.android.com.utillist.designmode.observer;

import java.util.ArrayList;
import java.util.List;

/**
 * @Features: 定义一个订阅者抽象类
 * @author: create by chenhao on 2018/1/30
 */

public class SubJect {
    /**
     *  存放所有观察者
     */
    private List<Observer> observers = new ArrayList<>();

    /**
     * 添加观察者
     * @param observer
     */
    private void  attach(Observer observer){
        observers.add(observer);
    }

    /**
     * 删除观察者
     * @param observer
     */
    private void  deAttach(Observer observer){
        observers.add(observer);
    }

    protected void  notifyObserver( ){
        for (Observer observer : observers){
            observer.update(this);
        }

    }

}
