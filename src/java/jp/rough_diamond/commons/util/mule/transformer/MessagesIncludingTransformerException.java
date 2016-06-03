/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */

package jp.rough_diamond.commons.util.mule.transformer;

import java.util.List;

import jp.rough_diamond.commons.resource.Messages;

import org.mule.api.transformer.Transformer;
import org.mule.api.transformer.TransformerException;
import org.mule.config.i18n.Message;

/**
 *
 */
@SuppressWarnings("unchecked")
public class MessagesIncludingTransformerException extends TransformerException {
	private static final long serialVersionUID = 1L;
	private final Messages msgs;
	/**
	 * @param message
	 */
	public MessagesIncludingTransformerException(Message message, Messages msgs) {
		super(message);
		this.msgs = msgs;
	}

	/**
	 * @param message
	 * @param transformer
	 */
	public MessagesIncludingTransformerException(Message message,
			Transformer transformer, Messages msgs) {
		super(message, transformer);
		this.msgs = msgs;
	}

	/**
	 * @param message
	 * @param transformers
	 */
	public MessagesIncludingTransformerException(Message message,
			List transformers, Messages msgs) {
		super(message, transformers);
		this.msgs = msgs;
	}

	/**
	 * @param transformer
	 * @param cause
	 */
	public MessagesIncludingTransformerException(Transformer transformer,
			Throwable cause, Messages msgs) {
		super(transformer, cause);
		this.msgs = msgs;
	}

	/**
	 * @param transformers
	 * @param cause
	 */
	public MessagesIncludingTransformerException(List transformers,
			Throwable cause, Messages msgs) {
		super(transformers, cause);
		this.msgs = msgs;
	}

	/**
	 * @param message
	 * @param cause
	 */
	public MessagesIncludingTransformerException(Message message,
			Throwable cause, Messages msgs) {
		super(message, cause);
		this.msgs = msgs;
	}

	/**
	 * @param message
	 * @param transformer
	 * @param cause
	 */
	public MessagesIncludingTransformerException(Message message,
			Transformer transformer, Throwable cause, Messages msgs) {
		super(message, transformer, cause);
		this.msgs = msgs;
	}

	/**
	 * @param message
	 * @param transformers
	 * @param cause
	 */
	public MessagesIncludingTransformerException(Message message,
			List transformers, Throwable cause, Messages msgs) {
		super(message, transformers, cause);
		this.msgs = msgs;
	}

	public Messages getMessages() {
		return this.msgs;
	}
}
