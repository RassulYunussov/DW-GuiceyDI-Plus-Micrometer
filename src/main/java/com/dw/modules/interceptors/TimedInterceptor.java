package com.dw.modules.interceptors;

import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.*;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

public class TimedInterceptor implements MethodInterceptor {


    private final MeterRegistry registry;
    private final Function<MethodInvocation, Iterable<Tag>> tagsBasedOnJoinPoint;

    public TimedInterceptor(MeterRegistry registry) {
        this.registry = registry;
        this.tagsBasedOnJoinPoint =  (mi) -> Tags.of(new String[]{"class", mi.getStaticPart().getClass().getName(), "method", mi.getMethod().getName()});
    }

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        Method method = methodInvocation.getMethod();
        Timed timed = method.getAnnotation(Timed.class);

        String metricName = timed.value().isEmpty() ? "method.timed" : timed.value();
        boolean stopWhenCompleted = CompletionStage.class.isAssignableFrom(method.getReturnType());
        return !timed.longTask() ? this.processWithTimer(methodInvocation, timed, metricName, stopWhenCompleted) : this.processWithLongTaskTimer(methodInvocation, timed, metricName, stopWhenCompleted);
    }
    private Object processWithTimer(MethodInvocation methodInvocation, Timed timed, String metricName, boolean stopWhenCompleted) throws Throwable {
        Timer.Sample sample = Timer.start(this.registry);
        if (stopWhenCompleted) {
            try {
                return ((CompletionStage)methodInvocation.proceed()).whenComplete((result, throwable) -> {
                    this.record(methodInvocation, timed, metricName, sample, this.getExceptionTag((Throwable) throwable));
                });
            } catch (Exception var12) {
                this.record(methodInvocation, timed, metricName, sample, var12.getClass().getSimpleName());
                throw var12;
            }
        } else {
            String exceptionClass = "none";

            Object var7;
            try {
                var7 = methodInvocation.proceed();
            } catch (Exception var13) {
                exceptionClass = var13.getClass().getSimpleName();
                throw var13;
            } finally {
                this.record(methodInvocation, timed, metricName, sample, exceptionClass);
            }

            return var7;
        }
    }
    private Object processWithLongTaskTimer(MethodInvocation methodInvocation, Timed timed, String metricName, boolean stopWhenCompleted) throws Throwable {
        Optional<LongTaskTimer.Sample> sample = this.buildLongTaskTimer(methodInvocation, timed, metricName).map(LongTaskTimer::start);
        if (stopWhenCompleted) {
            try {
                return ((CompletionStage)methodInvocation.proceed()).whenComplete((result, throwable) -> {
                    sample.ifPresent(this::stopTimer);
                });
            } catch (Exception var10) {
                sample.ifPresent(this::stopTimer);
                throw var10;
            }
        } else {
            Object var6;
            try {
                var6 = methodInvocation.proceed();
            } finally {
                sample.ifPresent(this::stopTimer);
            }

            return var6;
        }
    }
    private void stopTimer(io.micrometer.core.instrument.LongTaskTimer.Sample sample) {
        try {
            sample.stop();
        } catch (Exception var3) {
        }

    }

    private Optional<LongTaskTimer> buildLongTaskTimer(MethodInvocation methodInvocation, Timed timed, String metricName) {
        try {
            return Optional.of(LongTaskTimer.builder(metricName).description(timed.description().isEmpty() ? null : timed.description()).tags(timed.extraTags()).tags(this.tagsBasedOnJoinPoint.apply(methodInvocation)).register(this.registry));
        } catch (Exception var5) {
            return Optional.empty();
        }
    }
    private void record(MethodInvocation methodInvocation, Timed timed, String metricName, Timer.Sample sample, String exceptionClass) {
        try {
            sample.stop(Timer.builder(metricName).description(timed.description().isEmpty() ? null : timed.description()).tags(timed.extraTags()).tags(new String[]{"exception", exceptionClass}).tags(this.tagsBasedOnJoinPoint.apply(methodInvocation)).publishPercentileHistogram(timed.histogram()).publishPercentiles(timed.percentiles().length == 0 ? null : timed.percentiles()).register(this.registry));
        } catch (Exception var7) {
        }

    }

    private String getExceptionTag(Throwable throwable) {
        if (throwable == null) {
            return "none";
        } else {
            return throwable.getCause() == null ? throwable.getClass().getSimpleName() : throwable.getCause().getClass().getSimpleName();
        }
    }
}
