package clojurewerkz.spyglass;

import clojure.lang.IDeref;
import clojure.lang.IBlockingDeref;
import net.spy.memcached.ops.OperationStatus;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * The same as net.spy.memcached.internal.GetFuture but without the set operations
 * and implements clojure.lang.IDeref and clojure.lang.IBlockingDeref.
 */
public class GetFuture implements IDeref, IBlockingDeref{
  private net.spy.memcached.internal.GetFuture gf;

  public GetFuture(net.spy.memcached.internal.GetFuture gf) {
    this.gf = gf;
  }

  public boolean cancel(boolean ign) {
    return gf.cancel(ign);
  }

  public Object get(long duration, TimeUnit units) throws InterruptedException, TimeoutException, ExecutionException {
    return gf.get(duration, units);
  }

  public boolean isDone() {
    return gf.isDone();
  }

  public boolean isCancelled() {
    return gf.isCancelled();
  }

  public OperationStatus getStatus() {
    return gf.getStatus();
  }

  public Object get() throws InterruptedException, ExecutionException {
    return gf.get();
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
      return this.gf.get();
    } catch (InterruptedException e) {
      return null;
    } catch (ExecutionException e) {
      return null;
    }
  }
}
