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
public class RightRail {

	/** The log. */
	private final Logger LOG = LoggerFactory.getLogger(getClass());

	@Inject
	@Named("details")
	@Default(values = "")
	private String details[];

	public List<Map<String, String>> rightRailList;

	public List<Map<String, String>> getRightRailList() {
		try {
			LOG.debug("Entered getAccordionFieldsList() method in", this.getClass());
			rightRailList = new ArrayList<Map<String, String>>();
			JSONObject documentObj = null;
			Map<String, String> map = null;
			if (null != details && details.length > 0) {
				LOG.debug("details length is {}", details.length);
				for (int i = 0; i < details.length; i++) {
					documentObj = new JSONObject(details[i]);
					map = new HashMap<String, String>();
					String detail = documentObj.has("detail") ? documentObj.getString("detail") : "";
					if (StringUtils.isNotBlank(detail)) {
						map.put("detail", detail);
						rightRailList.add(map);
					}
				}
			}
		} catch (JSONException e) {
			LOG.error("error in ", e.getMessage());
		}
		return rightRailList;
	}

}
