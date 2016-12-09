package com.ads.eps.core.use;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.apache.sling.models.annotations.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ads.eps.core.utils.LinkUtil;
import com.day.cq.commons.inherit.HierarchyNodeInheritanceValueMap;
import com.day.cq.commons.inherit.InheritanceValueMap;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

/**
 *
 * @authors Nishit
 * 
 * 
 */

@Model(adaptables = Resource.class)
public class Header {

	/** The log. */
	private final Logger LOG = LoggerFactory.getLogger(getClass());

	@Inject
	private Resource resource;
	/** The resource resolver. */
	@Inject
	private ResourceResolver resourceResolver;

	public List<Map<String, String>> menuFieldsList;
	public List<Map<String, String>> additionalMenuFieldsList;
	public List<Map<String, Object>> secondaryMenuFieldsList;

	public List<Map<String, String>> getMenuFieldsList() {
		try {
			menuFieldsList = new ArrayList<Map<String, String>>();
			secondaryMenuFieldsList = new ArrayList<Map<String, Object>>();
			InheritanceValueMap ivm = new HierarchyNodeInheritanceValueMap(resource);
			String[] menuFields = ivm.getInherited("menuitems", String[].class);
			LOG.debug("Entered getMenuFieldsList() method in {}", this.getClass());
			JSONObject documentObj = null;
			Map<String, String> map = null;
			if (null != menuFields && menuFields.length > 0) {
				LOG.debug("menuFields length is {}", menuFields.length);
				int ct = 1;
				for (int i = 0; i < menuFields.length; i++) {
					documentObj = new JSONObject(menuFields[i]);
					map = new HashMap<String, String>();
					String menuLabel = documentObj.has("menulabel") ? documentObj.getString("menulabel") : "";
					String menuUrl = documentObj.has("menuurl") ? documentObj.getString("menuurl") : "";
					String path = menuUrl;
					String menuFlyOutLabel = documentObj.has("menuflyoutlabel")
							? documentObj.getString("menuflyoutlabel") : "";
					menuUrl = StringUtils.isNotBlank(menuUrl) ? LinkUtil.createValidLinkFromAuthoredProperty(menuUrl)
							: "";
					String menuIcon = documentObj.has("menuicon") ? documentObj.getString("menuicon") : "";
					String menuIsButton = documentObj.has("menuisbutton")
							&& documentObj.getString("menuisbutton").contains("true") ? "true" : "false";
					String menuFlyOutControl = "";
					if (menuIsButton.equals("true")) {
						menuUrl = "#" + menuLabel.replace(" ", "-").toLowerCase() + "-" + ct;
						String menuId = menuLabel.replace(" ", "-").toLowerCase() + "-" + ct;
						menuFlyOutControl = "flyout-" + ct;
						ct++;
						populateSecondaryMenu(path, menuFlyOutControl, menuId, menuIcon, menuLabel, menuFlyOutLabel);
					}
					if (StringUtils.isNotBlank(menuLabel) && StringUtils.isNotBlank("menuicon")
							&& StringUtils.isNotBlank(menuUrl)) {
						map.put("menulabel", menuLabel);
						map.put("menuurl", menuUrl);
						map.put("menuicon", menuIcon);
						map.put("menuisbutton", menuIsButton);
						map.put("menuflyoutcontrol", menuFlyOutControl);
						menuFieldsList.add(map);
					}
				}
			}
		} catch (JSONException e) {
			LOG.error("error in ", e.getMessage());
		}
		return menuFieldsList;
	}

	private void populateSecondaryMenu(String path, String menuFlyOutControl, String menuId, String menuIcon,
			String menuLabel, String menuFlyOutLabel) {
		PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
		Page rootPage = pageManager.getPage(path);
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String, String>> menuItemsList = new ArrayList<Map<String, String>>();

		if (rootPage != null) {
			String rootPageTitle = StringUtils.isNotBlank(menuFlyOutLabel) ? menuFlyOutLabel : menuLabel;
			if (StringUtils.isNotBlank(rootPageTitle)) {
				map.put("menuheader", rootPageTitle);
			}
			Iterator<Page> childPageIt = rootPage.listChildren();
			while (childPageIt.hasNext()) {
				Page childPage = childPageIt.next();
				ValueMap valueMap = childPage.getProperties();
				boolean hideInGrid = valueMap.containsKey("hideInNav") ? valueMap.get("hideInNav", Boolean.class)
						: false;
				if (!hideInGrid) {
					String title = valueMap.containsKey("navigationtitle")
							? valueMap.get("navigationtitle", String.class) : valueMap.get("jcr:title", String.class);
					String url = LinkUtil.createValidLinkFromAuthoredProperty(childPage.getPath());
					Map<String, String> menuMap = new HashMap<String, String>();
					menuMap.put("title", title);
					menuMap.put("url", url);
					menuItemsList.add(menuMap);
				}
			}
		}
		if (menuItemsList.size() == 0)

		{
			Map<String, String> menuMap = new HashMap<String, String>();
			menuMap.put("title", "Coming Soon..");
			menuMap.put("url", "#");
			menuItemsList.add(menuMap);
		}
		Map<String, String> menuMap = new HashMap<String, String>();
		menuMap.put("title", "See All");
		menuMap.put("url", LinkUtil.createValidLinkFromAuthoredProperty(path));
		menuItemsList.add(menuMap);
		map.put("menuitemslist", menuItemsList);
		map.put("menuicon", menuIcon);
		map.put("id", menuId);
		map.put("menuflyoutcontrol", menuFlyOutControl);
		secondaryMenuFieldsList.add(map);
	}

	public List<Map<String, String>> getAdditionalMenuFieldsList() {
		try {
			additionalMenuFieldsList = new ArrayList<Map<String, String>>();
			InheritanceValueMap ivm = new HierarchyNodeInheritanceValueMap(resource);
			String[] menuFields = ivm.getInherited("additionalmenuitems", String[].class);
			LOG.debug("Entered getAdditionalMenuFieldsList() method in {}", this.getClass());
			JSONObject documentObj = null;
			Map<String, String> map = null;
			if (null != menuFields && menuFields.length > 0) {
				LOG.debug("additionalMenuFields length is {}", menuFields.length);
				for (int i = 0; i < menuFields.length; i++) {
					documentObj = new JSONObject(menuFields[i]);
					map = new HashMap<String, String>();
					String menuLabel = documentObj.has("menulabel") ? documentObj.getString("menulabel") : "";
					String menuUrl = documentObj.has("menuurl") ? documentObj.getString("menuurl") : "";

					menuUrl = StringUtils.isNotBlank(menuUrl) ? LinkUtil.createValidLinkFromAuthoredProperty(menuUrl)
							: "";
					String menuIcon = documentObj.has("menuicon") ? documentObj.getString("menuicon") : "";

					if (StringUtils.isNotBlank(menuLabel) && StringUtils.isNotBlank("menuicon")
							&& StringUtils.isNotBlank(menuUrl)) {
						map.put("menulabel", menuLabel);
						map.put("menuurl", menuUrl);
						map.put("menuicon", menuIcon);
						additionalMenuFieldsList.add(map);
					}
				}
			}
		} catch (JSONException e) {
			LOG.error("error in ", e.getMessage());
		}
		return additionalMenuFieldsList;
	}

	public List<Map<String, Object>> getSecondaryMenuFieldsList() {
		return secondaryMenuFieldsList;
	}

}
