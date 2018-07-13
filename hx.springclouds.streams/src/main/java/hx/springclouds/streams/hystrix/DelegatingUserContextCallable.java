package hx.springclouds.streams.hystrix;

import hx.springclouds.streams.interceptors.UserContext;
import hx.springclouds.streams.interceptors.UserContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.util.concurrent.Callable;

public class DelegatingUserContextCallable<T> implements Callable {

    private final Callable<T> delegate;
    private UserContext originalUserContext;
    private static final Logger logger=LoggerFactory.getLogger(DelegatingUserContextCallable.class);


    public DelegatingUserContextCallable(Callable<T> delegate, UserContext userContext) {
        Assert.notNull(delegate,"delegate cannot not null");
        Assert.notNull(userContext,"userContext cannot not null");
        this.delegate =delegate;
        this.originalUserContext=userContext;
    }

    public DelegatingUserContextCallable(Callable<T> delegate) {
        this(delegate, UserContextHolder.getUserContext());
    }

    @Override
    public T call() throws Exception {
        UserContextHolder.setUserContext(originalUserContext);
        try {
            return delegate.call();
        }
        finally {
            this.originalUserContext=null;
        }

    }
}
