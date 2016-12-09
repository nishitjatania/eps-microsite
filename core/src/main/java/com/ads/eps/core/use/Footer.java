package com.ads.eps.core.use;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.inherit.HierarchyNodeInheritanceValueMap;
import com.day.cq.commons.inherit.InheritanceValueMap;

/**
 *
 * @authors Nishit
 * 
 * 
 */

@Model(adaptables = Resource.class)
public class Footer {

	/** The log. */
	private final Logger LOG = LoggerFactory.getLogger(getClass());

	@Inject
	private Resource resource;

	public List<Map<String, String>> socialFieldsList = new ArrayList<Map<String, String>>();

	public List<Map<String, String>> getSocialFieldsList() {
		try {
			InheritanceValueMap ivm = new HierarchyNodeInheritanceValueMap(resource);
			String[] socialFields = ivm.getInherited("social", String[].class);
			LOG.debug("Entered getSocialFieldsList() method in", this.getClass());
			JSONObject documentObj = null;
			Map<String, String> map = null;
			if (null != socialFields && socialFields.length > 0) {
				LOG.debug("socialFields length is {}", socialFields.length);
				for (int i = 0; i < socialFields.length; i++) {
					documentObj = new JSONObject(socialFields[i]);
					map = new HashMap<String, String>();
					String title = documentObj.has("socialtitle") ? documentObj.getString("socialtitle") : "";
					String url = documentObj.has("socialurl") ? documentObj.getString("socialurl") : "";
					String icon = documentObj.has("socialicon") ? documentObj.getString("socialicon") : "";
					if (StringUtils.isNotBlank(title) && StringUtils.isNotBlank(icon) && StringUtils.isNotBlank(url)) {
						map.put("socialtitle", title);
						map.put("socialurl", url);
						map.put("socialicon", icon);
						socialFieldsList.add(map);
					}
				}
			}
		} catch (JSONException e) {
			LOG.error("error in ", e.getMessage());
		}
		return socialFieldsList;
	}

}
