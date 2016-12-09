package com.ads.eps.core.use;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;

import com.ads.eps.core.utils.LinkUtil;

@Model(adaptables = { Resource.class, SlingHttpServletRequest.class })
public class BaseComponent {
	@Inject
	@Named("link")
	@Default(values = "no link")
	private String link;

	@PostConstruct
	public void activate() {
	}

	public String getValidLink() {
		return LinkUtil.createValidLinkFromAuthoredProperty(link);
	}
}