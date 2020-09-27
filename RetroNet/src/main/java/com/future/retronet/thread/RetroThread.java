package com.future.retronet.thread;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * see {@link ThreadStrategy }
 *
 * @author athoucai
 * @date 2018/9/11
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RetroThread {

    ThreadStrategy subscribeThread() default ThreadStrategy.IO;

    ThreadStrategy observeThread();
}