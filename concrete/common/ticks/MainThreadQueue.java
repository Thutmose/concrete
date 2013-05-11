package concrete.common.ticks;
 
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Vector;

 
/**For queueing methods needing execution onto the main thread
 * upon the next tick as a simple concurrency prevention.*/
public class MainThreadQueue extends Ticker{
 
        private static MainThreadQueue instance= new MainThreadQueue();
 
        private static List<MethodCall> pendingExecutions= new Vector<MethodCall>();
 
        /**@param caller the object which runs this method, null if static*/
        public static void addTo(Method method, Object caller, Object[] params){
                synchronized(pendingExecutions){
                        pendingExecutions.add( new MethodCall(method, caller, params) );
                }
        }
 
        @Override
        public void onUpdate(){
                MethodCall[] executions= new MethodCall[pendingExecutions.size()];
                synchronized(pendingExecutions) {
                        pendingExecutions.toArray(executions);
                        pendingExecutions.clear();
                }
                for(MethodCall call : executions)
                        call.execute();
        }
 
 
        public static class MethodCall{
                Method method;
                Object caller;
                Object[] params;
 
                /**@param caller the object which runs this method, null if static*/
                public MethodCall(Method method, Object caller, Object[] params){
                        this.method= method;
                        this.caller= caller;
                        this.params= params;
                }
                public void execute(){
                        try {
                                method.invoke(caller, params);
                        } catch (IllegalArgumentException e) {e.printStackTrace();}
                        catch (IllegalAccessException e) {e.printStackTrace();}
                        catch (InvocationTargetException e) {e.printStackTrace();}                             
                }
        }
}