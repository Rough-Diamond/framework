/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.resource;

import java.util.List;

public class MessagesUtil {
    public static Messages translate(Messages msgs, String before, String after) {
        Messages ret = new Messages();
        for(String key : msgs.getProperties()) {
            String newKey = key.replaceFirst(before, after);
            List<Message> list = msgs.get(key);
            for(Message msg : list) {
                ret.add(newKey, new Message(msg.key, msg.values));
            }
        }
        return ret;
    }

    //Messagesを任意のプロパティに集約させる
    public static Messages concatProperty(Messages msgs, String property) {
        Messages ret = new Messages();
        for(String key : msgs.getProperties()) {
            for(Message msg : msgs.get(key)) {
                ret.add(property, msg);
            }
        }
        return ret;
    }
}
