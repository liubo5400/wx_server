package com.wxserver.web.dto;

public class ErrorDTO {
	private int code;
	private String msg;

	public ErrorDTO() {
		this.code = ErrorCode.CODE_SYSTEM_ERROR;
		this.msg = ErrorCode.MSG_SYSTEM_ERROR;
	}
	
	public ErrorDTO(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

}
