package com.wxserver.web.dto;

public class ErrorCode {

	public final static int CODE_SYSTEM_ERROR = 0;
	public final static String MSG_SYSTEM_ERROR = "系统异常";

	public final static int CODE_PARAM_MISSING = 1001;
	public final static String MSG_PARAM_MISSING = "参数丢失";

	public final static int CODE_PARAM_FORMAT_ERROR = 1002;
	public final static String MSG_PARAM_FORMAT_ERROR = "参数格式错误";

	public final static int CODE_REFERER_ERROR = 1003;
	public final static String MSG_REFERER_ERROR = "非法请求";

	public final static int CODE_RESOURCE_NOT_FOUND = 1004;
	public final static String MSG_RESOURCE_NOT_FOUND = "资源不存在";

	public final static int CODE_ERROR_METHOD = 1005;
	public final static String MSG_ERROR_METHOD = "错误的请求方式（GET/POST）";

	public final static int CODE_ERROR_FILENOTFOUND = 1006;
	public final static String MSG_ERROR_FILENOTFOUND = "未找到文件";

	public final static int CODE_ERROR_FILENAME_ERROR = 1007;
	public final static String MSG_ERROR_FILENAME_ERROR = "文件名解析异常";

	public final static int CODE_ERROR_WEB_POWER_ERROR = 1008;
	public final static String MSG_ERROR_WEB_POWER_ERROR = "接口权限不足";

	public final static int CODE_REQUEST_FREQUENTLY = 1009;
	public final static String MSG_REQUEST_FREQUENTLY = "请求过于频繁，请稍后再试";

	public final static int CODE_REQUEST_TIMEOUT = 1010;
	public final static String MSG_REQUEST_TIMEOUT = "请求超时";
}
