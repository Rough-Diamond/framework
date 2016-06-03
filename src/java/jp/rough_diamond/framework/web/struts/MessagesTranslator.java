/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.framework.web.struts;

import java.util.List;

import jp.rough_diamond.commons.resource.Message;
import jp.rough_diamond.commons.resource.Messages;

import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

public class MessagesTranslator {
    public static ActionMessages translate(Messages msgs) {
        ActionMessages ret = new ActionMessages();
        for(String key : msgs.getProperties()) {
            List<Message> list = msgs.get(key);
            for(Message msg : list) {
                ret.add(key, new ActionMessage(msg.key, msg.values));
            }
        }
        return ret;
    }
}
