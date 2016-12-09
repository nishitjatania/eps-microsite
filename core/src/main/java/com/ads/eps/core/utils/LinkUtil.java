package com.ads.eps.core.utils;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

/**
 * Static utility helpers for link functions
 * 
 */

public class LinkUtil {

	private static final Logger log = LoggerFactory.getLogger(LinkUtil.class);

	/**
	 * String that determines if a link is internal or not
	 */
	public static final String DEFAULT_INTERNAL_LINK_PREFIX = "/";

	/**
	 * Do internal links require extensions
	 */
	public static final boolean EXTENSIONS_REQUIRED = true;

	public static final String DEFAULT_LINK_EXTENSION = ".html";

	private LinkUtil() {
		// prevents instantiation
	}

	/**
	 * Convenience wrapper around
	 * createExternalizedLinkFromAuthoredProperty(String, String,
	 * ResourceResolver, HttpServletRequest) passing in / as the
	 * internalLinkPrefix.
	 * 
	 * @see LinkUtil.createExternalizedLinkFromAuthoredProperty(String, String,
	 *      ResourceResolver, HttpServletRequest)
	 * @param link
	 *            <code>String</code> to be evaluated
	 * @param resourceResolver
	 *            <code>ResourceResolver</code> current request's resource
	 *            resolved
	 * @param request
	 *            <code>HttpServletRequest</code> current HTTP request -
	 *            optional but less capable resource mapping.
	 * @return String
	 */
	public static String createExternalizedLinkFromAuthoredProperty(String link, ResourceResolver resourceResolver,
			HttpServletRequest request) {
		return createExternalizedLinkFromAuthoredProperty(link, DEFAULT_INTERNAL_LINK_PREFIX, resourceResolver,
				request);
	}

	/**
	 * Performs multiple tasks against a link created by and AEM author.
	 * <ul>
	 * <li>Will map a link if required in order to ensure any JCR resource
	 * resolver mappings are applied to the link. For links being printed out in
	 * HTML this method is not required - the link checker will apply the
	 * mappings. This method is intended to used in situations where the link
	 * checker will not be run, for example when doing a 301 redirect server
	 * side, or printing out links in JS files or XML files.</li>
	 * <li>Ensures that link valid for use in href or src attributes by adding
	 * an extension and network protocol if required.</li>
	 * </ul>
	 * 
	 * @see LinkUtils.mapLink(String, String, ResourceResolver,
	 *      HttpServletRequest)
	 * @see LinkUtil.createValidLinkFromAuthoredProperty(String, String)
	 * @param link
	 *            <code>String</code> to be evaluated
	 * @param internalLinkPrefix
	 *            <code>String</code> internal link prefix used to determine if
	 *            the link is an internal link
	 * @param resourceResolver
	 *            <code>ResourceResolver</code> current request's resource
	 *            resolved
	 * @param request
	 *            <code>HttpServletRequest</code> current HTTP request -
	 *            optional but less capable resource mapping.
	 * @return String
	 */
	public static String createExternalizedLinkFromAuthoredProperty(String link, String internalLinkPrefix,
			ResourceResolver resourceResolver, HttpServletRequest request) {
		link = createValidLinkFromAuthoredProperty(link, internalLinkPrefix);
		return mapLink(link, internalLinkPrefix, resourceResolver, request);
	}

	/**
	 * Convenience wrapper around createValidLinkFromAuthoredProperty(String,
	 * String) passing in / as the internalLinkPrefix.
	 * 
	 * @see LinkUtil.createValidLinkFromAuthoredProperty(String, String)
	 * @param link
	 *            <code>String</code> link to be checked.
	 * @return String
	 */
	public static String createValidLinkFromAuthoredProperty(String link) {
		return createValidLinkFromAuthoredProperty(link, DEFAULT_INTERNAL_LINK_PREFIX);
	}

	/**
	 * Ensures that link created by a AEM author is valid for use in href or src
	 * attributes but adding and extension if required and adding network
	 * protocol if required.
	 * 
	 * @see LinkUtil.addExtensionToLink(String, String)
	 * @see LinkUtil.addProtocolToLink(String)
	 * @param link
	 *            <code>String</code> link to be checked.
	 * @param internalLinkPrefix
	 *            <code>String</code> pattern that indicates if a link is
	 *            internal or not.
	 * @return String
	 */
	public static String createValidLinkFromAuthoredProperty(String link, String internalLinkPrefix) {
		link = addExtensionToLink(link, internalLinkPrefix);
		return addProtocolToLink(link);
	}

	/**
	 * Convenience wrapper around mapLink(String, String, ResourceResolver,
	 * HttpServletRequest) passing in / as the internalLinkPrefix.
	 * 
	 * @see LinkUtils.mapLink(String, String, ResourceResolver,
	 *      HttpServletRequest)
	 * @see LinkUtils.isInternalLink(String, String)
	 * @param link
	 *            <code>String</code> to be mapped
	 * @param resourceResolver
	 *            <code>ResourceResolver</code> current request's resource
	 *            resolved
	 * @param request
	 *            <code>HttpServletRequest</code> current HTTP request -
	 *            optional but less capable resource mapping.
	 * @return String mapped link based on JCR Resource Resolver rules.
	 */
	public static String mapLink(String link, ResourceResolver resourceResolver, HttpServletRequest request) {
		return mapLink(link, DEFAULT_INTERNAL_LINK_PREFIX, resourceResolver, request);
	}

	/**
	 * Will map a link if required in order to ensure any JCR resource resolver
	 * mappings are applied to the link. For links being printed out in HTML
	 * this method is not required - the link checker will apply the mappings.
	 * This method is intended to used in situations where the link checker will
	 * not be run, for example when doing a 301 redirect server side, or
	 * printing out links in JS files or XML files. This method runs
	 * LinkUtils.isInternalLink(String) to determine if the link is eligible for
	 * mapping.
	 * 
	 * @see LinkUtils.isInternalLink(String, String)
	 * @param link
	 *            <code>String</code> to be evaluated
	 * @param internalLinkPrefix
	 *            <code>String</code> internal link prefix used to determine if
	 *            the link requires mapping.
	 * @param resourceResolver
	 *            <code>ResourceResolver</code> current request's resource
	 *            resolved
	 * @param request
	 *            <code>HttpServletRequest</code> current HTTP request -
	 *            optional but less capable resource mapping.
	 * @return String mapped link based on JCR Resource Resolver rules.
	 */
	public static String mapLink(String link, String internalLinkPrefix, ResourceResolver resourceResolver,
			HttpServletRequest request) {
		if (resourceResolver != null && request != null && isInternalLink(link, internalLinkPrefix)) {
			link = resourceResolver.map(request, link);
		} else if (resourceResolver != null && isInternalLink(link, internalLinkPrefix)) {
			link = resourceResolver.map(link);
		}

		return link;
	}

	/**
	 * Convenience wrapper around isInternalLink(String link, String
	 * internalLinkPrefix) passing in / as the internalLinkPrefix.
	 * 
	 * @see LinkUtil.isInternalLink(String, String)
	 * @param link
	 *            <code>String</code> to be evaluated
	 * @return boolean
	 */
	public static boolean isInternalLink(String link) {
		return isInternalLink(link, DEFAULT_INTERNAL_LINK_PREFIX);
	}

	/**
	 * Determines if a link is an internal link based on the internal link
	 * prefix passed in. Defaults to / as the internal link prefix if one is not
	 * provided. Examples
	 * <ul>
	 * <li>link = /content/site/landing and link internalLinkPrefix = / result
	 * is true</li>
	 * <li>link = http://mysite.com/products and link internalLinkPrefix = /
	 * result is false</li>
	 * <li>link = mysite.com/products and link internalLinkPrefix = / result is
	 * false</li>
	 * <li>link = mysite.com/products and link internalLinkPrefix = / result is
	 * false</li>
	 * <li>link = /products and link internalLinkPrefix = /content result is
	 * false</li>
	 * <li>link = /products and link internalLinkPrefix = null or empty string
	 * result is true (because it uses the default of /</li>
	 * </ul>
	 * 
	 * @param link
	 *            <code>String</code> to be evaluated
	 * @param internalLinkPrefix
	 *            <code>String</code> link is evaluated against
	 * @return boolean
	 */
	public static boolean isInternalLink(String link, String internalLinkPrefix) {
		if (StringUtils.isEmpty(StringUtils.trim(internalLinkPrefix))) {
			internalLinkPrefix = DEFAULT_INTERNAL_LINK_PREFIX;
		}

		return StringUtils.isNotEmpty(link) && link.startsWith(internalLinkPrefix);
	}

	/**
	 * Convenience wrapper around addExtensionToLink(String link, String
	 * internalLinkPrefix) passing in / as the internalLinkPrefix.
	 * 
	 * @see LinkUtil.addExtensionToLink(String, String)
	 * @param link
	 *            <code>String</code> to be evaluated.
	 * @return String link provided plus extension if required.
	 */
	public static String addExtensionToLink(String link) {
		return addExtensionToLink(link, DEFAULT_INTERNAL_LINK_PREFIX);
	}

	/**
	 * Checks to see if a link requires and extension and if required adds
	 * .html. Following items are checked to determine if a link requires an
	 * extension:
	 * <ul>
	 * <li>Does the site require extensions - configuration</li>
	 * <li>Is the link and internal CQ link @see LinkUtil.isInternalLink(String,
	 * String)</li>
	 * <li>Does the link already have an extension</li>
	 * </ul>
	 * 
	 * @see LinkUtil.isInternalLink(String, String)
	 * @param link
	 *            <code>String</code> to be evaluated.
	 * @param internalLinkPrefix
	 *            <code>String</code> link is evaluated against
	 * @return String link provided plus extension if required.
	 */
	public static String addExtensionToLink(String link, String internalLinkPrefix) {
		if (linkNeedsExtension(link, internalLinkPrefix)) {
			link = link + DEFAULT_LINK_EXTENSION;
		}

		return link;
	}

	private static boolean linkNeedsExtension(String link, String internalLinkPrefix) {
		return EXTENSIONS_REQUIRED && isInternalLink(link, internalLinkPrefix)
				&& !StringUtils.containsAny(link, new char[] { '.', '?' }) && !StringUtils.equals(link, "/");
	}

	/**
	 * Checks to see if a link requires a network protocol and if it is required
	 * prepends the URL with http://.
	 * <ul>
	 * <li>/content/something - site root relative link and doesn't need
	 * protocol</li>
	 * <li>http://www.google.com - already has protocol</li>
	 * <li>www.google.com/somelink - this was entered by an author and needs a
	 * protocol to function.</li>
	 * </ul>
	 * This method does not support relative links - it assumes that a relative
	 * link is in fact link with server name that needs a protocol.
	 * 
	 * @param link
	 *            link <code>String</code> to be evaluated
	 * @return boolean
	 */
	public static String addProtocolToLink(String link) {
		String retVal = link;

		if (linkNeedsProtocol(link)) {
			retVal = "http://" + retVal;
		}

		return retVal;
	}

	public static String addProtocolToLink(String link, SlingHttpServletRequest request) {
		String retVal = link;
		String protocol = "http://";
		if (request.getProtocol().contains("https") || request.getProtocol().contains("HTTPS")
				|| request.getScheme().equalsIgnoreCase("https") || request.getServerPort() == 443) {
			protocol = "https://";
		}
		if (linkNeedsProtocol(link)) {
			retVal = protocol + retVal;
		}

		return retVal;
	}

	private static boolean linkNeedsProtocol(String link) {
		return StringUtils.isNotEmpty(link) && !link.startsWith("/") && !StringUtils.contains(link, "://");
	}
}
