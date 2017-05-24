package com.github.oscerd.kafka.connect.infinispan;

import java.io.Serializable;

import org.infinispan.protostream.annotations.ProtoDoc;
import org.infinispan.protostream.annotations.ProtoField;

@ProtoDoc("@Indexed")
public class Author implements Serializable {
	
	private String name;

	@ProtoField(number = 1, required = true)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return "Author [name=" + name + "]";
	}
}
