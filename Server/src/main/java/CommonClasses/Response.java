package CommonClasses;

// Immutable Response object
public class Response<T> {

    private final T result;
    private final boolean isFailure;
    private final String errMsg;

    public Response(T result, boolean isFailure, String errMsg) {
        this.result = result;
        this.isFailure = isFailure;
        this.errMsg = errMsg;
        //TODO: LogWriter(isFailure, errMsg);
    }

    public T getResult() {
        return result;
    }

    public boolean isFailure() {
        return isFailure;
    }

    public String getErrMsg() {
        return errMsg;
    }

    //TODO:
    //    private void LogWriter (boolean isFailure, String errMsg){
    //        if(!isFailure) CommerceSystem.log.logger.info(errMsg);
    //        else if (errMsg.startsWith("CRITICAL")) CommerceSystem.logCrit.logger.severe(errMsg);
    //        else CommerceSystem.log.logger.warning(errMsg);
    //    }
}
