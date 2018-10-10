package clojurewerkz.spyglass;

import clojure.lang.IDeref;
import clojure.lang.IBlockingDeref;
import net.spy.memcached.ops.OperationStatus;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * The same as net.spy.memcached.internal.BulkGetFuture but without the set operations
 * and implements clojure.lang.IDeref and clojure.lang.IBlockingDeref.
 */
public class BulkGetFuture implements IDeref, IBlockingDeref{
  private net.spy.memcached.internal.BulkGetFuture bgf;

  public net.spy.memcached.internal.BulkGetFuture getOriginalFuture() {
    return this.bgf;
  }

  public boolean cancel(boolean ign) {
    return bgf.cancel(ign);
  }

  public boolean isCancelled() {
    return bgf.isCancelled();
  }

  public Map get() throws InterruptedException, ExecutionException {
    return bgf.get();
  }

  public Map get(long to, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
    return bgf.get(to, unit);
  }

  public Map getSome(long to, TimeUnit unit) throws InterruptedException, ExecutionException {
    return bgf.getSome(to, unit);
  }

  public boolean isTimeout() {
    return bgf.isTimeout();
  }

  public OperationStatus getStatus() {
    return bgf.getStatus();
  }

  public boolean isDone() {
    return bgf.isDone();
  }

  public BulkGetFuture(net.spy.memcached.internal.BulkGetFuture bgf) {
    this.bgf = bgf;
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
      return this.bgf.get();
    } catch (InterruptedException e) {
      return null;
    } catch (ExecutionException e) {
      return null;
    }
  }
}
