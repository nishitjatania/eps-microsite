package com.ads.eps.core.use;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ads.eps.core.utils.LinkUtil;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

/**
 *
 * @author Nishit Jatania
 */
@Model(adaptables = Resource.class)
public class ArticleList {

	@Inject
	@Named("rootpath")
	@Default(values = "")
	private String rootPath;

	/** The definitions. */
	@Inject
	@Named("definitions")
	@Default(values = "{}")
	private String[] definitions;

	public List<Map<String, String>> articleList;

	/** The resource resolver. */
	@Inject
	private ResourceResolver resourceResolver;

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(ArticleList.class);

	/**
	 * Gets the root path.
	 *
	 * @return the root path
	 */
	public String getRootPath() {
		return rootPath;
	}

	/**
	 * Gets the definitions.
	 *
	 * @return the definitions
	 */
	public String[] getDefinitions() {
		return definitions;
	}

	public List<Map<String, String>> getArticleList() {
		LOG.debug("Entered getArticleList");
		articleList = new ArrayList<Map<String, String>>();
		List<String> pathList = new ArrayList<String>();
		PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
		LOG.debug("Root Path: {}", rootPath);
		if (StringUtils.isNotBlank(getRootPath())) {
			Page rootPage = pageManager.getPage(rootPath);
			if (rootPage != null) {
				Iterator<Page> childPageIt = rootPage.listChildren();
				while (childPageIt.hasNext()) {
					Page childPage = childPageIt.next();
					String gridResPath = childPage.getPath();
					if (!pathList.contains(gridResPath) && articleList.size() < 8) {
						pathList.add(gridResPath);
						LOG.debug("gridResPath: {}", gridResPath);
						populateArticleList(childPage);
					}
				}
			}
		}
		try {
			JSONObject documentObj;
			String[] listofObject = getDefinitions();
			if (null != listofObject && listofObject.length > 0 && !StringUtils.equals(listofObject[0], "{}")) {
				for (int i = 0; i < listofObject.length; i++) {
					documentObj = new JSONObject(listofObject[i]);
					String gridResPath = documentObj.has("linkpath") ? documentObj.getString("linkpath") : "";
					if (StringUtils.isNotBlank(gridResPath)) {
						Page childPage = pageManager.getPage(gridResPath);
						if (childPage != null) {
							if (!pathList.contains(gridResPath) && articleList.size() < 8) {
								pathList.add(gridResPath);
								populateArticleList(childPage);
							}
						}
					}
				}

			}
		} catch (JSONException e) {
			LOG.error("", e);
		}
		LOG.debug("Exiting getArticleList");
		return articleList;
	}

	private void populateArticleList(Page childPage) {
		LOG.debug("Entered populateArticleList");
		ValueMap valueMap = childPage.getProperties();
		boolean hideInGrid = valueMap.containsKey("hideingrid") ? valueMap.get("hideingrid", Boolean.class) : false;
		String title = valueMap.containsKey("articletitle") ? valueMap.get("articletitle", String.class) : "";
		LOG.debug("Article Title: {}", title);
		if (!hideInGrid && StringUtils.isNotBlank(title)) {
			String url = LinkUtil.createValidLinkFromAuthoredProperty(childPage.getPath());
			String description = valueMap.containsKey("articledescription")
					? valueMap.get("articledescription", String.class) : "";
			String bgImage = valueMap.containsKey("articlebgimage") ? valueMap.get("articlebgimage", String.class) : "";
			String bgImageMobile = valueMap.containsKey("articlebgmobileimage")
					? valueMap.get("articlebgmobileimage", String.class) : "";
			String bgImageTablet = valueMap.containsKey("articlebgtabletimage")
					? valueMap.get("articlebgtabletimage", String.class) : "";
			String bgImageXl = valueMap.containsKey("articlebgxlimage") ? valueMap.get("articlebgxlimage", String.class)
					: "";

			Map<String, String> map = new HashMap<String, String>();
			map.put("title", title);
			map.put("bgimage", bgImage);
			map.put("bgmobileimage", bgImageMobile);
			map.put("bgtabletimage", bgImageTablet);
			map.put("bgxlimage", bgImageXl);
			map.put("description", description);
			map.put("url", url);
			articleList.add(map);
		}
		LOG.debug("Exiting populateArticleList");
	}

}
