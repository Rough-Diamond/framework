/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.resource;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * メッセージ集合
 */
public class Messages implements Serializable{
	private static final long serialVersionUID = 1L;
	private Map<String, List<Message>> messageMap = new LinkedHashMap<String, List<Message>>();

	public void add(Messages msgs) {
		Set<String> properties = msgs.getProperties();
		for(String property : properties) {
			List<Message> msgList = msgs.get(property);
			for(Message msg : msgList) {
				add(property, msg);
			}
		}
	}

	public void add(String property, Message msg) {
		List<Message> list = messageMap.get(property);
		if(list == null) {
			list = new ArrayList<Message>();
			messageMap.put(property, list);
		}
		list.add(msg);
	}

	public Set<String> getProperties() {
		return messageMap.keySet();
	}

	public List<Message> get(String property) {
		return messageMap.get(property);
	}

	public boolean hasError() {
		return getProperties().size() != 0;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for(String property : getProperties()) {
			for(Message msg : get(property)) {
				builder.append(property);
				builder.append(":");
				builder.append(msg);
				builder.append('\n');
			}
		}
		return builder.toString();
	}
}
