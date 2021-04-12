### 使用说明

已经发布Jcenter仓库，

使用本库只需要添加

`implementation 'com.retro:RetroGo:1.0.1'`即可
仓库重新升级原先Jcenter不在更新
在根目录build.gradle 添加
```groovy
    repositories {
        google()
        jcenter()
        maven { url 'https://www.jitpack.io' }
    }
```
需要应用的仓库下添加
`implementation 'com.github.smallwhitexujian:RetroGo:1.0.0'`

初始化Application创建的时候初始化

```java
public class HttpHelper {
    static String mBaseUrl;
    static boolean mDebug = false;
    /**
     * 初始化方法
     * @param context 上下文
     * @param isDebug 是否是开发环境
     * @param baseUrl 域名  
     */
    public static void init(Context context, boolean isDebug, String baseUrl) {
        mBaseUrl = baseUrl;
        mDebug = isDebug;
        RetroNet.INSTANCE.init(context, DefaultHttpConfig.class, isDebug);
    }
}
```

```java
public class DefaultHttpConfig extends BaseConfig {
    @Override
    protected void client(OkHttpClient.Builder clientBuilder) {
        if (HttpHelper.mDebug) {
            clientBuilder.addNetworkInterceptor(new RetroLogInterceptor());
        }
    }

    @Override
    protected String getBaseUrl() {
        return HttpHelper.mBaseUrl;
    }
}
```

接口如下：

```java
public interface TestService {
    @GET("/iqx/master")//RxJAVA模式通过观察者方式回调结果
    Observable<RetroBean<Object>> gettest();

    @GET("/iqx/master")//使用OKhttp Call方式回调请求结果
    Call<RetroBean<Object>> gettest2();
}
```

获取结果可以如下：

```java
 findViewById<View>(R.id.btn).setOnClickListener {
           service.gettest().subscribe({ carBeans -> // 处理数据 直接获取到List<JavaBean> carBeans
                    LogUtil.e("============>$carBeans")
                    if (carBeans.code == 200){
                        LogUtil.e("============>$carBeans")
                    }
                }) { throwable -> // 处理异常
                    LogUtil.e("============>$throwable")
              } 
           }
        }

        btn2.setOnClickListener {
            var call: Call<Any>? = service.gettest2()
            call?.enqueue(object : Callback<Any> {
                override fun onFailure(call: Call<Any>, t: Throwable) {
                    LogUtil.e("============>$t")
                }

                override fun onResponse(call: Call<Any>, response: Response<Any>) {
                    var res = response.body().toString();
                    LogUtil.e("============>$res")

                }
            })
        }
```
