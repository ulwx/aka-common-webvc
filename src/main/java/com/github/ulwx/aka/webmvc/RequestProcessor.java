package com.github.ulwx.aka.webmvc;

import com.ulwx.tool.RequestUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * 请求处理器
 */
public interface RequestProcessor {

	/**
	 * 处理前回调方法，在Action方法执行前调用
	 * @param request
	 * @param actionMethodInfo
	 * @param context
	 * @return  如果返回null，则继续下一个处理；
	 * 返回非空对象，不会执行下一个RequestProcessor#onBefore()，并且会拦截正常的Action方法执行，
	 * 并且使用返回的逻辑视图名称作为响应。
	 */
	default String onBefore(HttpServletRequest request,
							ActionMethodInfo actionMethodInfo,RequestUtils context) {
		return null;
	}

	/**
	 * 处理成功回调方法，此时在Action方法已经执行完毕后调用
	 * @param request
	 * @param actionMethodInfo
	 * @param context
	 * @param resultViewName
	 * @return 如果返回null，则继续下一个处理。返回非空，则不会执行下一个RequestProcessor#onBefore()，
	 * 并且使用返回的逻辑视图名称作为响应。
	 */
	default String onAfter(HttpServletRequest request, ActionMethodInfo actionMethodInfo,
								 RequestUtils context, String resultViewName) {
		return null;
	}

	/**
	 * 异常处理回调方法，此时在Action方法执行过程出现异常。
	 * @param request
	 * @param actionMethodInfo
	 * @param context
	 * @param e 只有
	 *     start(0),
	 *     onBeforeComplete(1),
	 *     actionComplete(2),
	 *     onAfterComplete(3)
	 *    这四种状态可能发生
	 * @return 如果返回null，则继续下一个处理。返回非空，则不会执行下一个RequestProcessor#onException()，
	 * 	 并且使用返回的逻辑视图名称作为响应。
	 */
	default String onException(HttpServletRequest request,
							   ActionMethodInfo actionMethodInfo,
							   RequestUtils context,
							   Exception e,ProcessorStatus status) {
		return null;
	}

	/**
	 * 完成处理回调方法，即onAfter()方法或onException()方法执行后调用此方法。
	 * @param request
	 * @param actionMethodInfo
	 * @param context
	 * @param resultViewName
	 * @param e
	 * @param status 任何一种状态都可能发生
	 * @return 如果返回true，则继续下一个处理。返回false，则不会执行下一个RequestProcessor#onFinished()。
	 */
	default boolean onFinished(HttpServletRequest request,ActionMethodInfo actionMethodInfo,
							RequestUtils context,String resultViewName,Exception e,ProcessorStatus status
	) {
		return true;
	}
}


