package CommonClasses;

<<<<<<< HEAD
public class Response<T> {
=======
// Immutable Response object
public class Response<T> {

>>>>>>> guiding_baskets
    private final T result;
    private final boolean isFailure;
    private final String errMsg;

    public Response(T result, boolean isFailure, String errMsg) {
        this.result = result;
        this.isFailure = isFailure;
        this.errMsg = errMsg;
<<<<<<< HEAD
        //LogWriter(isFailure, errMsg);
=======
        //TODO: LogWriter(isFailure, errMsg);
>>>>>>> guiding_baskets
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

<<<<<<< HEAD
//    private void LogWriter (boolean isFailure, String errMsg){
//        if(!isFailure) CommerceSystem.log.logger.info(errMsg);
//        else if (errMsg.startsWith("CRITICAL")) CommerceSystem.logCrit.logger.severe(errMsg);
//        else CommerceSystem.log.logger.warning(errMsg);
//    }
=======
    //TODO:
    //    private void LogWriter (boolean isFailure, String errMsg){
    //        if(!isFailure) CommerceSystem.log.logger.info(errMsg);
    //        else if (errMsg.startsWith("CRITICAL")) CommerceSystem.logCrit.logger.severe(errMsg);
    //        else CommerceSystem.log.logger.warning(errMsg);
    //    }
>>>>>>> guiding_baskets
}
