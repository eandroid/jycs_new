package com.easemob.chatuidemo.domain;

import java.util.ArrayList;
import java.util.List;

public class Result<T> {
	
    private String msg;
	private List<T> results=new ArrayList<T>();
	public Result(){
		super();
	}
	public Result(String msg, List<T> results) {
		super();
		this.msg = msg;
		this.results = results;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public List<T> getResults() {
		return results;
	}
	public void setResults(List<T> results) {
		this.results = results;
	}
	
}
