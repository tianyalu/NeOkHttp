# `OKHttp`源码分析

[TOC]

`HTTP` `get`（请求行，请求属性集）

`HTTP` `post`(请求行，请求属性集，请求体类型`type`，请求体长度`len`)



源码

`OkHttpClient` --> `Request` --> `newCall` --> `RealCall.enqueue()`(不能重复执行) -->` Dispatcher.enqueue(AsynCall)`(等待队列、执行队列) --> 异步任务 --> `AsyncCall.execute()` --> 责任链模式 多个拦截器 response --> 

`RealCall implements Call`

//异步方法  `RealCall.enqueue(new Callback())`

```java
@Override
public void enqueue(Callback responseCallback) {
  //不能执行大于1次 enqueue
  synchronized (this) {
    if (executed) throw new IllegalStateException("Already Executed");
    executed = true;
  }
  captureCallStackTrace();
  eventListener.callStart(this);
  //拿到调度器dispatcher
  client.dispatcher().enqueue(new AsyncCall(responseCallback));
}
```

`Dispatcher.enqueue(...)`

```java
synchronized void enqueue(AsyncCall call) {
  //同时运行的异步任务 < 64 && 同时访问同一个服务器的数量 < 5
  if (runningAsyncCalls.size() < maxRequests && runningCallsForHost(call) < maxRequestsPerHost) {
    //运行的队列（双端队列）
    runningAsyncCalls.add(call);
    executorService().execute(call);
  } else {
    //等待执行的队列（双端队列）
    readyAsyncCalls.add(call);
  }
}
```

`AsyncCall.enqueue(...)`

```java
@Override protected void execute() {
  boolean signalledCallback = false;
  try {
    //责任链模式拦截器
    Response response = getResponseWithInterceptorChain();
    if (retryAndFollowUpInterceptor.isCanceled()) {
      signalledCallback = true;
      responseCallback.onFailure(RealCall.this, new IOException("Canceled"));
    } else {
      signalledCallback = true;
      responseCallback.onResponse(RealCall.this, response);
    }
  } catch (IOException e) {
    if (signalledCallback) { //这个错误是用户造成的，和OKHTTP没有关系
      // Do not signal the callback twice!
      Platform.get().log(INFO, "Callback failure for " + toLoggableString(), e);
    } else { //这个错误是OKHTTP发生的
      eventListener.callFailed(RealCall.this, e);
      responseCallback.onFailure(RealCall.this, e);
    }
  } finally {
    client.dispatcher().finished(this);
  }
}
```

