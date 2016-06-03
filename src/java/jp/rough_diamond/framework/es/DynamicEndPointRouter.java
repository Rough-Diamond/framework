/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */

package jp.rough_diamond.framework.es;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleMessage;
import org.mule.api.endpoint.EndpointURI;
import org.mule.api.endpoint.OutboundEndpoint;
import org.mule.api.routing.CouldNotRouteOutboundMessageException;
import org.mule.endpoint.DynamicURIOutboundEndpoint;
import org.mule.endpoint.MuleEndpointURI;
import org.mule.routing.outbound.ChainingRouter;

/**
 * 送信先EndPointの切り替えを行うルーター
 */
public class DynamicEndPointRouter extends ChainingRouter {
	private final static Log log = LogFactory.getLog(DynamicEndPointRouter.class);
	
	public final static String ENDPOINT_KEY = DynamicEndPointRouter.class.getName() + ".ENDPOINT";

	@SuppressWarnings("deprecation")
	@Override
	public OutboundEndpoint getEndpoint(int index, MuleMessage message) throws CouldNotRouteOutboundMessageException {
		String endPointUrl = (String)message.getProperty(ENDPOINT_KEY);
		if(endPointUrl == null) {
			log.debug("EndpointURLが指定されていません。configで定義されたエンドポイントを使用します");
			return super.getEndpoint(index, message);
		}
		try {
			OutboundEndpoint oep = (OutboundEndpoint)endpoints.get(index);
			EndpointURI base = oep.getEndpointURI();
			EndpointURI newUri = new MuleEndpointURI(endPointUrl, base.getEndpointName(), base.getConnectorName(), base.getTransformers(), 
					base.getResponseTransformers(), base.getParams(), new URI(endPointUrl));
			oep = new DynamicURIOutboundEndpoint(oep, newUri);
	        return oep; 
		} catch (URISyntaxException e) {
			log.warn("URIフォーマットが誤っています。:" + endPointUrl);
			throw new RuntimeException(e);
		}
	}
}
