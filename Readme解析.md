### RetroGO

基于OKHttp3,Retrofit2,RxJava2封装的网络请求。

[RetroNet][1]

okhttp+Retrofit 扩展

##### Retrofit创建

```java
 @JvmStatic
    fun <T> create(service: Class<T>): T {
        LogUtil.d(" create service 1: " + service.simpleName + "    " +
                " myPid : " + Process.myPid() + "  mDefaultConfig == null" +
                (mDefaultConfig == null).toString())
        if (mDefaultConfig == null) {
            throw java.lang.RuntimeException("mDefaultConfig == null, you must set a default config before!")
        }
        return Api.provide(mDefaultConfig).create(service)
    }

```

`RetroNet.create(Service::class.java)`这句话要从Retrofit源码里面create函数开始，它是一个动态代理，Retrofit通过代理方法将我们定义的网络接口方法进行转化为我们接口定义的网络工作对象。代理这个方法其实就是Retrofit的核心价值。

```java
public <T> T create(final Class<T> service) {
    ...
    return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class<?>[] { service },
        new InvocationHandler() {
            ...
            ServiceMethod<Object, Object> serviceMethod =
                (ServiceMethod<Object, Object>) loadServiceMethod(method);
            OkHttpCall<Object> okHttpCall = new OkHttpCall<>(serviceMethod, args);
            return serviceMethod.callAdapter.adapt(okHttpCall);
          }
        });
  }
```

代理的理解可以自行百度下，代理最好的办法就是看动态生成的代理类去理解：

拿到的代理类，大概如下：

```java
public final class Service extends Proxy implements Service{
  ...//一些Object自带方法
  private static Method m3;//接口定义的方法
  static {
    try {
      //Object自带方法的初始化
      m0,m1,m2 = ...
      //接口中定义的方法
      m3 = Class.forName("com.xx.xx$Servcie")//反射接口类
          .getMethod("gettest",//反射函数
              new Class[] { Class.forName("java.lang.String") });//反射参数
      //接口中定义的其他方法
      ...
    } 
    ...
  }
//返回接口实例对象
public Service (InvocationHandler invocationHandler){
  super(invocationHandler);
}
//
public final Call gettest(String str){
  ...
  try{//用Handler去调用
    return (Call)this.h.invoke(this, m3, new Object[]{str});
  }
}

}
```

Retrofit使用动态代理，其实是为了开发者在写代码的时候方便调用，而真正负责生产Call网络工作对象的，还是Retrofit.create函数中定义的这个invocationHandler

回头在来看Retrofit create方法做了什么：

```java
 public <T> T create(final Class<T> service) {
    validateServiceInterface(service);
    return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class<?>[] { service },
        new InvocationHandler() {
          private final Platform platform = Platform.get();
          private final Object[] emptyArgs = new Object[0];

          @Override public @Nullable Object invoke(Object proxy, Method method,
              @Nullable Object[] args) throws Throwable {
            // If the method is a method from Object then defer to normal invocation.
            if (method.getDeclaringClass() == Object.class) {
              return method.invoke(this, args);
            }
            if (platform.isDefaultMethod(method)) {
              return platform.invokeDefaultMethod(method, service, proxy, args);
            }
            return loadServiceMethod(method).invoke(args != null ? args : emptyArgs);
          }
        });
  }
```

在create方法中，首先会先检查传入的service是否满足接口的要求。validateServiceInterface(service)和create最终都会触发loadServiceMethod(method)方法

```java
ServiceMethod<?> loadServiceMethod(Method method) {
    ServiceMethod<?> result = serviceMethodCache.get(method);
    if (result != null) return result;

    synchronized (serviceMethodCache) {
      result = serviceMethodCache.get(method);
      if (result == null) {
        result = ServiceMethod.parseAnnotations(this, method);
        serviceMethodCache.put(method, result);
      }
    }
    return result;
  }
```

该方法将所有的方法缓存在serviceMethodCache集合中，检查缓存中是否有已经存在method如果存在则直接返回不存在则调用`sult = ServiceMethod.parseAnnotations(this,`并保存在缓存中。Retrofit.loadServiceMethod()将获取serviceMethod对象的实现委托给ServiceMethod.parseAnnotation实现，根据字面意思可推测，ServiceMethod通过解析注解来获取相应对象。

##### ServiceMethod

ServiceMethod可以理解为将服务器接口类的方法转化为网络请求Call的转化类，传入的参数为Retrofit和method，然后通过反射原理解析method，得到其实例。

```java
abstract class ServiceMethod<T> {
  static <T> ServiceMethod<T> parseAnnotations(Retrofit retrofit, Method method) {
    RequestFactory requestFactory = RequestFactory.parseAnnotations(retrofit, method);

    Type returnType = method.getGenericReturnType();
    if (Utils.hasUnresolvableType(returnType)) {
      throw methodError(method,
          "Method return type must not include a type variable or wildcard: %s", returnType);
    }
    if (returnType == void.class) {
      throw methodError(method, "Service methods cannot return void.");
    }

    return HttpServiceMethod.parseAnnotations(retrofit, method, requestFactory);
  }

  abstract @Nullable T invoke(Object[] args);
}
```

通过ServiceMethod方法，通过RequestFactory.parseAnnotations()方法获取requestFactory对象，这个对象就包含了发起请求的所以数据；



```java
 /**
   * Inspects the annotations on an interface method to construct a reusable service method that
   * speaks HTTP. This requires potentially-expensive reflection so it is best to build each service
   * method only once and reuse it.
   */
  static <ResponseT, ReturnT> HttpServiceMethod<ResponseT, ReturnT> parseAnnotations(
      Retrofit retrofit, Method method, RequestFactory requestFactory) {
    boolean isKotlinSuspendFunction = requestFactory.isKotlinSuspendFunction;
    boolean continuationWantsResponse = false;
    boolean continuationBodyNullable = false;

    Annotation[] annotations = method.getAnnotations();
    Type adapterType;
    if (isKotlinSuspendFunction) {
      Type[] parameterTypes = method.getGenericParameterTypes();
      Type responseType = Utils.getParameterLowerBound(0,
          (ParameterizedType) parameterTypes[parameterTypes.length - 1]);
      if (getRawType(responseType) == Response.class && responseType instanceof ParameterizedType) {
        // Unwrap the actual body type from Response<T>.
        responseType = Utils.getParameterUpperBound(0, (ParameterizedType) responseType);
        continuationWantsResponse = true;
      } else {
        // TODO figure out if type is nullable or not
        // Metadata metadata = method.getDeclaringClass().getAnnotation(Metadata.class)
        // Find the entry for method
        // Determine if return type is nullable or not
      }

      adapterType = new Utils.ParameterizedTypeImpl(null, Call.class, responseType);
      annotations = SkipCallbackExecutorImpl.ensurePresent(annotations);
    } else {
      adapterType = method.getGenericReturnType();
    }

    CallAdapter<ResponseT, ReturnT> callAdapter =
        createCallAdapter(retrofit, method, adapterType, annotations);
    Type responseType = callAdapter.responseType();
    if (responseType == okhttp3.Response.class) {
      throw methodError(method, "'"
          + getRawType(responseType).getName()
          + "' is not a valid response body type. Did you mean ResponseBody?");
    }
    if (responseType == Response.class) {
      throw methodError(method, "Response must include generic type (e.g., Response<String>)");
    }
    // TODO support Unit for Kotlin?
    if (requestFactory.httpMethod.equals("HEAD") && !Void.class.equals(responseType)) {
      throw methodError(method, "HEAD method must use Void as response type.");
    }

    Converter<ResponseBody, ResponseT> responseConverter =
        createResponseConverter(retrofit, method, responseType);

    okhttp3.Call.Factory callFactory = retrofit.callFactory;
    if (!isKotlinSuspendFunction) {
      return new CallAdapted<>(requestFactory, callFactory, responseConverter, callAdapter);
    } else if (continuationWantsResponse) {
      //noinspection unchecked Kotlin compiler guarantees ReturnT to be Object.
      return (HttpServiceMethod<ResponseT, ReturnT>) new SuspendForResponse<>(requestFactory,
          callFactory, responseConverter, (CallAdapter<ResponseT, Call<ResponseT>>) callAdapter);
    } else {
      //noinspection unchecked Kotlin compiler guarantees ReturnT to be Object.
      return (HttpServiceMethod<ResponseT, ReturnT>) new SuspendForBody<>(requestFactory,
          callFactory, responseConverter, (CallAdapter<ResponseT, Call<ResponseT>>) callAdapter,
          continuationBodyNullable);
    }
  }
```



HttpServiceMethod.parseAnnotations() 分了几个步揍，第一，获取响应类型，根据响应类型和注解创建responseConverter对象，第二根据方法返回类型和注解创建CallAdapter对象；根据callAdapter获得HttpServiceMethod实例，

在回头看

```java
loadServiceMethod(method).invoke(args != null ? args : emptyArgs)
```

最终调用发起网络的请求的方法invoke()该方法是真正发起网络请求的地方。

```java
@Override final @Nullable ReturnT invoke(Object[] args) {
    Call<ResponseT> call = new OkHttpCall<>(requestFactory, args, callFactory, responseConverter);
    return adapt(call, args);
  }
```

`invoke()`方法将调用OkhttpCall将参数方法传递给okhttp由其完成网络请求。

###### OkHttpCall

上面分析HttpServiceMethod.invoke()方法时，只是简单的说明了一个okhttpcall,实际上，okhttpcall集成自call，这里的call是Retrofit中的call，跟Okhttp中的call非常类似，主要是声明同步调用request()方法和异步调用enqueue()和其他状态判断方法。okhttpcall中持有了okhttp3.call对象，个方法的实现均是委托给okhttp3.call对象去实现的。

```java
 private okhttp3.Call createRawCall() throws IOException {
    okhttp3.Call call = callFactory.newCall(requestFactory.create(args));
    if (call == null) {
      throw new NullPointerException("Call.Factory returned null.");
    }
    return call;
  }
```

callFactory是配置Retrofit时创建的OkHttpClient对象，requestFactory.create(args)创建了一个Request，这样，就生成了一个完整的okhttp3.Call对象。

#### 总结

整个流程大致梳理结束，Retrofit主要工作内容，定义了一个包含网络请求的服务类接口，然后利用动态机制创建了这个接口的对象并返回，在接口类对象调用到它的方法时都会调用到这里InvocationHandler对象的invoke方法，该方法有三个参数，proxy是动态代理生成的对象，method是调用到的方法对象，args是调用方法的参数，通过解析这个method并执行，达到发起网络请求的目的。

其次Retrofit并没有做网络请求，实际操作网络请求的是最终持有Okttp3.Call的对象，也就是网络都是由OkHttp3完成请求。








