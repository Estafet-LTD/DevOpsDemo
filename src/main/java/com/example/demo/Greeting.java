package com.example.demo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Greeting {

	  private final long id;
	  private final String content;

	  public Greeting(@JsonProperty("id") long id, 
			          @JsonProperty("content") String content) {
	    this.id = id;
	    this.content = content;
	  }

	  public long getId() {
	    return id;
	  }

	  public String getContent() {
	    return content;
	  }
	  
	  @Override
	    public String toString() {
	        return "Greeting::toString() {" +
	                "content='" + content + '\'' +
	                ", id=" + id +
	                '}';
	  }
	}