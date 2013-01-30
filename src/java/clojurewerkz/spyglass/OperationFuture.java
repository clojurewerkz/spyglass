package clojurewerkz.spyglass;

import clojure.lang.IDeref;
import clojure.lang.IBlockingDeref;
import net.spy.memcached.ops.Operation;
import net.spy.memcached.ops.OperationStatus;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * The same as net.spy.memcached.internal.OperationFuture but without the set operations
 * and implements clojure.lang.IDeref and clojure.lang.IBlockingDeref.
 */
public class OperationFuture implements IDeref, IBlockingDeref {
  private net.spy.memcached.internal.OperationFuture of;

  public OperationFuture(net.spy.memcached.internal.OperationFuture of) {
    this.of = of;
  }

  public net.spy.memcached.internal.OperationFuture getOriginalFuture() {
    return this.of;
  }

  public boolean cancel(boolean ign) {
    return of.cancel(ign);
  }

  public Object get(long duration, TimeUnit units) throws InterruptedException, TimeoutException, ExecutionException {
    return of.get(duration, units);
  }

  public Object get() throws InterruptedException, ExecutionException {
    return of.get();
  }

  public String getKey() {
    return of.getKey();
  }

  public OperationStatus getStatus() {
    return of.getStatus();
  }

  public boolean isCancelled() {
    return of.isCancelled();
  }

  public boolean isDone() {
    return of.isDone();
  }

  @Override
  public Object deref(long timeout, Object defaultValue) {
    try{
      return get(timeout, TimeUnit.MILLISECONDS);
    } catch (TimeoutException e) {
      return defaultValue;
    } catch (InterruptedException e) {
      return defaultValue;
    } catch (ExecutionException e) {
      return defaultValue;
    }
  }

  @Override
  public Object deref() {
    try {
      return this.of.get();
    } catch (InterruptedException e) {
      return null;
    } catch (ExecutionException e) {
      return null;
    }
  }
}
