/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.patch.velocity.tools;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.rough_diamond.framework.web.struts.VelocityViewer;

import org.apache.velocity.tools.struts.TilesTool;

/**
 * Tilesツールの拡張
 */
public class TilesToolExt extends TilesTool {
	@Override
	protected String acquireString(String url) throws IOException, Exception {
		//以下既存ソースからコピー
        // Record whether our URL is absolute or relative
        if (isAbsoluteUrl(url))
        {
            // for absolute URLs, delegate to our peer
            BufferedReader r = new BufferedReader(acquireReader(url));
            StringBuffer sb = new StringBuffer();
            int i;
            // under JIT, testing seems to show this simple loop is as fast
            // as any of the alternatives
            while ((i = r.read()) != -1)
            {
                sb.append((char)i);
            }
            r.close();

            return sb.toString();
        }
        else // handle relative URLs ourselves
        {
            // retrieve an appropriate ServletContext
            // normalize the URL if we have an HttpServletRequest
            if (!url.startsWith("/"))
            {
                String sp = ((HttpServletRequest)request).getServletPath();
                url = sp.substring(0, sp.lastIndexOf('/')) + '/' + url;
            }

            // strip the session id from the url
            url = stripSession(url);

            //yamane ここから追加
            if(url.endsWith(".vm")) {
            	return parseVTL(url);
            }
            //yamane ここまで追加
            // from this context, get a dispatcher
            RequestDispatcher rd = application.getRequestDispatcher(url);
            if (rd == null)
            {
                throw new Exception("Couldn't get a RequestDispatcher for \""
                                    + url + "\"");
            }

            // include the resource, using our custom wrapper
            ImportResponseWrapper irw =
                new ImportResponseWrapper((HttpServletResponse)response);
            try
            {
                rd.include(request, irw);
            }
            catch (IOException ex)
            {
                throw new Exception("Problem importing the relative URL \""
                                    + url + "\". " + ex);
            }
            catch (RuntimeException ex)
            {
                throw new Exception("Problem importing the relative URL \""
                                    + url + "\". " + ex);
            }

            // disallow inappropriate response codes per JSTL spec
            if (irw.getStatus() < 200 || irw.getStatus() > 299)
            {
                throw new Exception("Invalid response code '" + irw.getStatus()
                                    + "' for \"" + url + "\"");
            }

            // recover the response String from our wrapper
            return irw.getString();
        }
	}

	private String parseVTL(String url) {
		String sp = ((HttpServletRequest)request).getServletPath();
        String prefix = sp.substring(0, sp.lastIndexOf('/')) + '/';
        String templateName = url.substring(prefix.length());
        String ret = VelocityViewer.getText(templateName, application, request, response); 
        return ret;
	}
}
