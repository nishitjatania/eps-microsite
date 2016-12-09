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
public class Accordion{

	/** The log. */
	private final Logger LOG = LoggerFactory.getLogger(getClass());

	@Inject
	@Named("accordionitems")
	@Default(values = "")
	private String accordionItems[];

	public List<Map<String, String>> accordionFieldsList;

	public List<Map<String, String>> getAccordionFieldsList() {
		try {
			LOG.debug("Entered getAccordionFieldsList() method in", this.getClass());
			accordionFieldsList = new ArrayList<Map<String, String>>();
			JSONObject documentObj = null;
			Map<String, String> map = null;
			if (null != accordionItems && accordionItems.length > 0) {
				LOG.debug("accordionItems length is {}", accordionItems.length);
				for (int i = 0; i < accordionItems.length; i++) {
					documentObj = new JSONObject(accordionItems[i]);
					map = new HashMap<String, String>();
					String title = documentObj.has("title") ? documentObj.getString("title") : "";
					String description = documentObj.has("description") ? documentObj.getString("description") : "";
					if (StringUtils.isNotBlank(title) && StringUtils.isNotBlank(description)) {
						map.put("title", title);
						map.put("description", description);
						accordionFieldsList.add(map);
					}
				}
			}
		} catch (JSONException e) {
			LOG.error("error in ", e.getMessage());
		}
		return accordionFieldsList;
	}

}
