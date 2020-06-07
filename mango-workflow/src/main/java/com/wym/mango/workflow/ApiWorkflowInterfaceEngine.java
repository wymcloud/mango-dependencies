package com.wym.mango.workflow;

import com.wym.mango.workflow.exception.ApiBaseException;
import com.wym.mango.workflow.model.ApiContext;
import com.wym.mango.workflow.model.ApiRequest;
import com.wym.mango.workflow.model.ApiResponse;
import lombok.Data;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * pipeline 的抽象实现
 *
 * @author "wangshuai131 <wangshuai30@jd.com>" 2017-12-13
 **/
public class ApiWorkflowInterfaceEngine implements ApiWorkflowInterface {

    /**
     * 使用的 handlers
     */
    @Setter
    private List<ApiWorkflowHandler<ApiRequest, ApiResponse>> handlers;



    @Override
    public ApiResponse execute(ApiRequest request, long timestamp, Object... others) throws ApiBaseException {
        if (CollectionUtils.isEmpty(handlers)) {
            return null;
        }

        if (request == null) {
            request = new ApiEmptyRequest();
        }


        ApiContext<ApiRequest, ApiResponse> context = new ApiContext(request, null, timestamp);

        Qualifier qualifier = null;

        for (ApiWorkflowHandler<ApiRequest, ApiResponse> handler : handlers) {

            if (qualifier == null) { // 用于做日志记录。
                qualifier = handler.getClass().getAnnotation(Qualifier.class);

            }

            if (!handler.supports(context, others)) { // 如果不支持，则 continue。
                continue;
            }

            try {
                handler.handle(context, others);
            } catch (Throwable e) { // 捕获饼处理异常。
//                handleThrowable(context, e);
            } finally {
                long duration = System.currentTimeMillis() - timestamp;


            }

        }

        long duration = System.currentTimeMillis() - timestamp;

        return context.getResponse();
    }

    /**
     * 记录重复日志。
     *
     * @param timestamp
     * @param source
     * @param handler
     */
    private void deprecatedLogging(long timestamp, String source, ApiWorkflowHandler<ApiRequest, ApiResponse> handler) {
        Deprecated deprecated = handler.getClass().getAnnotation(Deprecated.class);
        if (deprecated == null) {
            return;
        }
    }


    @Override
    public void after(ApiRequest request, ApiResponse response, long timestamp, Object... others) {

    }


    /**
     * 针对各种异常的处理。
     * TODO 将异常及对应的 code 做成 mapping 形式，进行自动化。
     *
     * @param context
     * @param e
     * @throws ApiBaseException
     */
//    private void handleThrowable(ApiContext<ApiRequest, ApiResponse> context, Throwable e) throws ApiBaseException {
//        String source = context.getSource();
//        long timestamp = context.getTimestamp();
//        ApiResponse response = context.getResponse();
//        // 打印堆栈
//        String localizedMessage = e.getLocalizedMessage();
//        String logMessage = String.format("TS[%s-%s] %s：%s | %s | STACK TRACE: ",
//                source, timestamp, e.getClass().getSimpleName(), localizedMessage, JSON.toJSON(context));
//
//        if (!(e instanceof ApiBaseException)) {
//            log.error(logMessage, e);
//            HttpResponseStatus status = HttpResponseStatus.INTERNAL_SERVER_ERROR;
//
//            if (e instanceof IllegalArgumentException) { // 参数一场，标记 status
//                status = HttpResponseStatus.BAD_REQUEST;
//            } else if (e instanceof RpcException) { // RPC 调用一场，标记 status
//                status = HttpResponseStatus.BAD_GATEWAY;
//                localizedMessage = "系统异常，请重试或联系客服[JSF]";
//            } else if (e instanceof NullPointerException) { // 空指针异常，给一个友好文案。
//                localizedMessage = "系统异常，请重试或联系客服[N1]";
//            } else {
//                localizedMessage = "系统异常，请重试或联系客服[N2]";
//            }
//            throw new ApiBaseException(localizedMessage, status.code(), timestamp);
//        }
//
//        ((ApiBaseException) e).setTimestamp(timestamp);
//
//        if (e instanceof ApiContinueException) {// 捕获到 continue 异常，返回继续向下执行代码。
//            log.warn(logMessage, e);
//            return;
//        }
//
//        log.error(logMessage, e);
//
//        if (e instanceof ApiInterruptException && ((ApiInterruptException) e).getResponse() == null) { // 如果是 中断异常，且 response 没有内容，尝试将 response 放入。
//            ((ApiInterruptException) e).setResponse(response);
//        }
//
//        throw (ApiBaseException) e;
//    }


//    @Override
//    public void after(ApiRequest request, ApiResponse response, long timestamp, Object... others) {
//        if (request == null || !request.getClass().isAssignableFrom(ApiFlightSearchRequest.class)) {
//            return; // 只上报查询，后续全部放开
//        }
//
//        ApiFlightSearchRequest oreq = (ApiFlightSearchRequest) request;
//
//        BaseRequest<IceStatisticReq> br = new BaseRequest<>();
//        br.setReqType(ReqTypeEnum.ICE_STATISTIC_DATA);
//        // serviceKey: jipiao.api_UTIL_com.jd.airplane.api.jsf.ApiFlightJsfFacade_flightPrimarySearch
//        br.setReq(
//                IceStatisticReq.builder()
//                        .appName(appName)
//                        .appType(AppTypeEnum.UTIL.toString())
//                        .interfaceName(ApiFlightJsfFacade.class.getName())
//                        .methodName("flightPrimarySearch")
//                        .reqTime(Long.toString(timestamp))
//                        .reqData(JSON.toJSONString(request))
//                        .resTime(Long.toString(System.currentTimeMillis()))
//                        .resData(JSON.toJSONString(response))
//                        .uuid(oreq.getUuid())
//                        .build()
//        );
//
//        iceStatisticSendUtil.sendData(br);
//    }

    /**
     * 空实现
     */
    @Data
    class ApiEmptyRequest implements ApiRequest {
    }

    @Data
    class ApiExceptionResponse implements ApiResponse {

    }


}
