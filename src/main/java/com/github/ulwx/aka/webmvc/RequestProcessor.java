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
	 * @return 如果返回非null，则为跳转的视图名称，此时后续RequestProcessor#onBefore()方法将不会执行。
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
	 * @return
	 */
	default void onAfter(HttpServletRequest request, ActionMethodInfo actionMethodInfo,
								 RequestUtils context, String resultViewName) {
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
	 * @return
	 */
	default void onException(HttpServletRequest request,
							   ActionMethodInfo actionMethodInfo,
							   RequestUtils context,
							   Exception e,ProcessorStatus status) {
	}

	/**
	 * Action方法不管出错还是成功，最终会回调此方法。
	 * @param request
	 * @param actionMethodInfo
	 * @param context
	 * @param resultViewName
	 * @param e
	 * @param status 任何一种状态都可能发生
	 * @return 如果返回true，则继续下一个处理。返回false，则不会执行下一个RequestProcessor#onFinished()。
	 */
	default void onFinished(HttpServletRequest request,ActionMethodInfo actionMethodInfo,
							RequestUtils context,String resultViewName,Exception e,ProcessorStatus status
	) {

	}
}


