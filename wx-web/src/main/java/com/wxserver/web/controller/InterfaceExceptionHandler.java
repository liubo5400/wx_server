package com.wxserver.web.controller;

import com.wxserver.web.dto.ErrorCode;
import com.wxserver.web.dto.ErrorDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;

import java.util.Map;

/**
 * 自定义ExceptionHandler，专门处理接口异常.
 *
 */
// 会被Spring-MVC自动扫描，但又不属于Controller的annotation。
@ControllerAdvice
public class InterfaceExceptionHandler {

	private static Logger logger = LoggerFactory
			.getLogger(InterfaceExceptionHandler.class);

	@ExceptionHandler(value = { NoSuchRequestHandlingMethodException.class,
			HttpRequestMethodNotSupportedException.class,
			HttpMediaTypeNotSupportedException.class,
			HttpMediaTypeNotAcceptableException.class,
			MissingServletRequestParameterException.class,
			ServletRequestBindingException.class,
			ConversionNotSupportedException.class, 
			TypeMismatchException.class,
			HttpMessageNotReadableException.class,
			HttpMessageNotWritableException.class,
			MethodArgumentNotValidException.class,
			MissingServletRequestPartException.class, 
			BindException.class,
			Exception.class
	})
	
	@ResponseBody
	public final ErrorDTO handleException(Exception ex, WebRequest request) {
		ErrorDTO erb = null;
		logger.error("系统异常", ex);
		if (ex instanceof NoSuchRequestHandlingMethodException) {
			erb = new ErrorDTO(ErrorCode.CODE_SYSTEM_ERROR,
					ErrorCode.MSG_SYSTEM_ERROR);

		} else if (ex instanceof HttpRequestMethodNotSupportedException) {
			erb = new ErrorDTO(ErrorCode.CODE_ERROR_METHOD,
					ErrorCode.MSG_ERROR_METHOD);

		} else if (ex instanceof HttpMediaTypeNotSupportedException) {
			erb = new ErrorDTO(ErrorCode.CODE_SYSTEM_ERROR,
					ErrorCode.MSG_SYSTEM_ERROR);

		} else if (ex instanceof HttpMediaTypeNotAcceptableException) {
			erb = new ErrorDTO(ErrorCode.CODE_SYSTEM_ERROR,
					ErrorCode.MSG_SYSTEM_ERROR);

		} else if (ex instanceof MissingServletRequestParameterException) {
			erb = new ErrorDTO(ErrorCode.CODE_PARAM_MISSING,
					ErrorCode.MSG_PARAM_MISSING);

		} else if (ex instanceof ServletRequestBindingException) {
			erb = new ErrorDTO(ErrorCode.CODE_PARAM_FORMAT_ERROR,
					ErrorCode.MSG_PARAM_FORMAT_ERROR);

		} else if (ex instanceof ConversionNotSupportedException) {
			erb = new ErrorDTO(ErrorCode.CODE_PARAM_FORMAT_ERROR,
					ErrorCode.MSG_PARAM_FORMAT_ERROR);

		} else if (ex instanceof TypeMismatchException) {
			erb = new ErrorDTO(ErrorCode.CODE_PARAM_FORMAT_ERROR,
					ErrorCode.MSG_PARAM_FORMAT_ERROR);

		} else if (ex instanceof HttpMessageNotReadableException) {
			erb = new ErrorDTO(ErrorCode.CODE_SYSTEM_ERROR,
					ErrorCode.MSG_SYSTEM_ERROR);

		} else if (ex instanceof HttpMessageNotWritableException) {
			erb = new ErrorDTO(ErrorCode.CODE_SYSTEM_ERROR,
					ErrorCode.MSG_SYSTEM_ERROR);

		} else if (ex instanceof MethodArgumentNotValidException) {
			erb = new ErrorDTO(ErrorCode.CODE_SYSTEM_ERROR,
					ErrorCode.MSG_SYSTEM_ERROR);

		} else if (ex instanceof MissingServletRequestPartException) {
			erb = new ErrorDTO(ErrorCode.CODE_SYSTEM_ERROR,
					ErrorCode.MSG_SYSTEM_ERROR);

		} else if (ex instanceof BindException) {
			erb = new ErrorDTO(ErrorCode.CODE_PARAM_FORMAT_ERROR,
					ErrorCode.MSG_PARAM_FORMAT_ERROR);

		} else {
			erb = new ErrorDTO(ErrorCode.CODE_SYSTEM_ERROR,
					ErrorCode.MSG_SYSTEM_ERROR);
		}
		return erb;
	}
}
