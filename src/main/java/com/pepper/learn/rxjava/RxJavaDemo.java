package com.pepper.learn.rxjava;



import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class RxJavaDemo {



    public static void main(String[] args){

        Observable.create((ObservableOnSubscribe<Integer>) e -> {
            System.out.println("Observable emit 1");
            e.onNext(1);
            System.out.println("Observable emit 2");
            e.onNext(2);
            System.out.println("Observable emit 3");
            e.onNext(3);
            e.onComplete();
            System.out.println("Observable emit 4");
            e.onNext(4);
        }).subscribe(new Observer<Integer>() {

            private Integer i;

            private Disposable mDisposable;

            @Override
            public void onSubscribe(Disposable d) {
                System.out.println("onSubscribe : " + d.isDisposed());
                mDisposable = d;
            }

            @Override
            public void onNext(Integer value) {
                System.out.println("onNext : value : " + value);
                i++;
                if (i == 2) {
                    // 在RxJava 2.x 中，新增的Disposable可以做到切断的操作，让Observer观察者不再接收上游事件
                    mDisposable.dispose();
                    System.out.println( "onNext : isDisposable : " + mDisposable.isDisposed());
                }
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("onError : value : " + e.getMessage());
            }

            @Override
            public void onComplete() {
                System.out.println("onComplete");
            }
        });
    }
}
