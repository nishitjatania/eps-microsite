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
public class Grid {

	/** The log. */
	private final Logger LOG = LoggerFactory.getLogger(getClass());

	@Inject
	@Named("images")
	@Default(values = "")
	private String images[];

	public List<Map<String, String>> gridFieldsList;

	public List<Map<String, String>> getGridFieldsList() {
		try {
			LOG.debug("Entered getGridFieldsList() method in {}", this.getClass());
			gridFieldsList = new ArrayList<Map<String, String>>();
			JSONObject documentObj = null;
			Map<String, String> map = null;
			if (null != images && images.length > 0) {
				LOG.debug("imagepath length is {}", images.length);
				for (int i = 0; i < images.length; i++) {
					documentObj = new JSONObject(images[i]);
					map = new HashMap<String, String>();
					String image = documentObj.has("imagepath") ? documentObj.getString("imagepath") : "";
					String title = documentObj.has("title") ? documentObj.getString("title") : "";
					if (StringUtils.isNotBlank(image)) {
						map.put("image", image);
						map.put("title", title);
						gridFieldsList.add(map);
					}
				}
			}
		} catch (JSONException e) {
			LOG.error("error in ", e.getMessage());
		}
		return gridFieldsList;
	}

}
