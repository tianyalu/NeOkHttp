# `OKHttp`源码分析

[TOC]

## 一、`HTTP`请求

* `HTTP` `get`（请求行，请求属性集）

* `HTTP` `post`(请求行，请求属性集，请求体类型`type`，请求体长度`len`)

## 二、源码分析

### 2.1 源码主线流程分析

`OkHttpClient` --> `Request` --> `newCall` --> `RealCall.enqueue()`(不能重复执行) -->` Dispatcher.enqueue(AsynCall)`(等待队列、执行队列) --> 异步任务 --> `AsyncCall.execute()` --> 责任链模式 多个拦截器 response --> 

#### 2.1.1 入口调用

```java
    /**
     * OKHTTP异步请求的方法
     */
    private void okhttpAsyncGet() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
        //GET请求
        Request request = new Request.Builder()
                .url("https://www.baidu.com")
                .get()
                .build();
        Call call = okHttpClient.newCall(request);
        //取消请求
        //call.cancel();

        //异步方法
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "请求失败： " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String str = response.body().string();
                //response.body().byteStream();
                //response.body().charStream();
                Log.d(TAG, "异步get请求成功：" + str);
            }
        });
    }
```

#### 2.1.2 异步方法  `RealCall.enqueue(new Callback())`

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

#### 2.1.3 `Dispatcher.enqueue(...)`

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

#### 2.1.4 `AsyncCall.enqueue(...)`

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

### 2.2 线程池

线程池解决了两个不同的问题：

> 1. 提示性能：他们通常在执行大量异步任务时，由于减少了每个任务的调度开销，并且它们提供了一种限制和管理资源（包括线程）的方法，使得性能提升显著；
> 2. 统计信息：每个`ThreadPoolExecutor`保持一些基本的统计信息，例如完成的任务数量。

参考：[线程池之`ThreadPoolExecutor`概述](https://www.jianshu.com/p/c41e942bcd64)

#### 2.2.1 常用的线程池

常用的线程池如下：

> * `Executors.newCachedThreadPool`：无界线程池，自动线程回收，可以重复使用缓存的线程；
> * `Executors.newFixedThreadPool`：固定大小的线程池；
> * `Executors.newSingleThreadExxecutor`：单一后台线程。

`OkHttp`使用的便是第一种缓存线程池：`Executor.newCachedThreadPool`。

#### 2.2.2 核心和最大线程池数量

| 参数              | 翻译           |
| ----------------- | -------------- |
| `corePoolSize`    | 核心线程池数量 |
| `maximumPoolSize` | 最大线程池数量 |

线程执行器会根据`corePoolSize`和`maximumPoolSize`自动地调整线程池大小。

当在`execute(Runnable)`方法中提交新任务并且少于`corePoolSize`线程正在运行时，即使其他工作线程处于空闲状态，也会创建一个新线程来处理该请求。如果有多于`corePoolSize`但小于`maximumPoolSize`线程正在运行，则仅当队列已满时才会创建新线程。通过设置`corePoolSize`和`maximumPoolSize`大小相同可以创建一个固定大小的线程池。通过将`maximumPoolSize`设置为基本无上界的值，例如`Integer.MAX_VALUE`，可以允许线程池容纳任意数量的并发任务。通常，核心和最大线程池大小仅在构建时设置，但也可以使用`setCorePoolSize`和`setMaximumPoolSize`进行动态更改。

总结如下图所示：

![image](https://github.com/tianyalu/NeOkHttp/raw/master/show/thread_pool_task_process.png)

#### 2.2.3 线程工厂`ThreadFactory`

新线程使用`ThreadFactory`创建，如果未另行指定，则使用`Executors.defaultThreadFactory`来创建工厂，使其全部位于同一个`ThreadGroup`中，并且具有相同的`NORM_PRIORITY`优先级和非守护进程状态。

通过提供不同的`ThreadFactory`，可以更改线程的名称、线程组、优先级和守护进程状态等，如果`ThreadFactory`在通过从`newThread`返回`null`询问时未能创建线程，则执行程序将继续，但可能无法执行任何任务。

线程应该有`modifyThread`权限，如果工作线程或使用该池的其它线程不具备此权限，则服务可能会降级：配置更改可能无法及时生效，并且关闭池可能会保持可终止但尚未完成的状态。

#### 2.2.4 时间存活时间`keep-alive times`

如果线程池当前拥有超过`corePoolSize`的线程，那么多余的线程在空闲时间超过`keepAliveTime`时会被终止，这提供了一种在不积极使用线程池时减少资源消耗的方法。

如果池在以后变得更加活跃，则应构建新线程。也可以使用`setKeepAliveTime(long, TimeUnit)`方法进行调整。

防止空闲线程在关闭之前终止，可以使用如下方法：

```java
setKeepAlive(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
```

默认情况下，`keep-alive`策略只适用于存在且超过`corePoolSize`线程的情况。但是，只要`keepAliveTime`值不为0，方法`allowCoreThreadTimeOut(boolean)`也可用于将此超时策略应用于**核心线程**。

#### 2.2.5 `BlockingQueue`队列

`BlockingQueue`队列用于存放提交的任务，队列的实际容量与线程池大小相关联：

> 1. 如果当前线程池任务线程数量小于核心线程池数量，执行器总是优先创建一个任务线程，而不是从线程队列中取出一个空闲线程；
> 2. 如果当前线程池任务线程数量大于核心线程池数量，执行器总是优先从线程队列中取出一个空闲线程，二不是创建一个任务线程；
> 3. 如果当前线程池任务线程数量大于核心线程池数量，且队列中无空闲任务线程，将会创建一个任务线程，直到超出`maximumPoolSize`，如果超过了`maximumPoolSize`，则任务将会被拒绝，执行拒绝策略。

#### 2.2.6 拒绝任务`Rejected tasks`

拒绝任务有两种情况：

> 1. 线程池已经被关闭；
> 2. 任务队列已满且线程池已达到`maximumPoolSize`容量。

无论哪种情况都会调用`RejectedExecutionHandler`的`rejectedException`方法。预定义了四中处理策略：

> 1. `AbortPolicy`：默认策略，抛出`RejectedExecutionException`运行时异常；
> 2. `CallerRunsPolicy`：这提供了一个简单的反馈控制的机制，可以减慢提交新任务的速度；
> 3. `DiscardPolicy`：直接丢弃新提交的任务；
> 4. `DiscardOlderstPolicy`：如果执行器没有关闭，队列头的任务将会被丢弃，然后执行器重新尝试执行任务（如果失败，则重复这一过程）。

我们可以自己定义`RejectedExecutionHandler`以适应特殊的容量和队列策略场景中。

#### 2.2.7 守护线程

`Java`将线程分为`User`线程和`Daemon`线程两种，其中`Dameon thread`即守护线程。

> 1. 所谓守护线程就是运行在程序后台的线程，程序的主线程`Main`（比方`java`程序一开始启动时创建的那个线程）不会是守护线程；
> 2. `Daemon thread`在`Java`里面的定义是，如果虚拟机中只有`Daemon thread` 在运行，则虚拟机退出。 
>     通常`Daemon`线程用来为`Use`r线程提供某些服务。程序的`main()`方法线程是一个`User`进程，`User`进程创建的进程为`User`进程。当所有的`User`线程结束后，`JVM`才会结束；
> 3. 通过在一个线程对象上调用`setDaemon(true)`，可以将`user`线程创建的线程明确地设置成`Daemon`线程。通常新创建的线程会从创建它的进程哪里继承`daemon`状态，除非明确地在线程对象上调用`setDaemon`方法来改变`daemon`状态。 需要注意的是，`setDaemon()`方法必须在调用线程的`start()`方法之前调用。一旦一个线程开始执行（如，调用了`start()`方法），它的`daemon`状态不能再修改。通过方法`isDaemon()`可以知道一个线程是否`Daemon`线程。
> 4. 总之,必须等所有的`Non-daemon`线程都运行结束了，只剩下`daemon`的时候，`JVM`才会停下来，注意`Main`主程序是`Non-daemon`线程，默认产生的线程全部是`Non-daemon`线程。

```java
public class MyDemonThread {
    public static void main(String[] args) {
        Thread thread = new Thread(){
            @Override
            public void run() {
                super.run();
                while (true) {
                    try {
                        Thread.sleep(0);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        System.out.println("run ...");
                    }
                }
            }
        };
        //守护线程
        thread.setDaemon(true);
        thread.start();
    }

    // run ...
    // run ...
    // run ...
    // Class transformation time: 0.0119051s for 113 classes or 1.0535486725663717E-4s per class
    // run ...
    // run ...
    // Process finished with exit code 0
}
```

### 2.3 构建者设计模式

构建者设计模式可以用盖房子的例子形象地描述：

![image](https://github.com/tianyalu/NeOkHttp/raw/master/show/builder_design_mode.png)

代码样例如下：

```java
public class Request<T>{
    private String action;
    private int reqEvent;
    private long seqId;
    private T req;
    private transient int reqCount;
    private transient long timeout;

    public Request() {

    }

    public Request(String action, int reqEvent, long seqId, T req, int reqCount, long timeout) {
        this.action = action;
        this.reqEvent = reqEvent;
        this.seqId = seqId;
        this.req = req;
        this.reqCount = reqCount;
        this.timeout = timeout;
    }

    public static class Builder<T> {
        //action 请求类型
        private String action;
        private int reqEvent;
        private long seqId;
        //请求子类数据，按照具体业务划分
        private T req;
        //请求次数 便于重试
        private int reqCount;
        //超时时间
        private long timeout;

        public Builder<T> setAction(String action) {
            this.action = action;
            return this;
        }

        public Builder<T> setReqEvent(int reqEvent) {
            this.reqEvent = reqEvent;
            return this;
        }

        public Builder<T> setSeqId(long seqId) {
            this.seqId = seqId;
            return this;
        }

        public Builder<T> setReq(T req) {
            this.req = req;
            return this;
        }

        public Builder<T> setReqCount(int reqCount) {
            this.reqCount = reqCount;
            return this;
        }

        public Builder<T> setTimeout(long timeout) {
            this.timeout = timeout;
            return this;
        }

        public Request<T> build() {
            return new Request<T>(action, reqEvent, seqId, req, reqCount, timeout);
        }
    }

    public String getAction() {
        return action;
    }

    public int getReqEvent() {
        return reqEvent;
    }

    public long getSeqId() {
        return seqId;
    }

    public T getReq() {
        return req;
    }

    public int getReqCount() {
        return reqCount;
    }

    public long getTimeout() {
        return timeout;
    }
}
```

