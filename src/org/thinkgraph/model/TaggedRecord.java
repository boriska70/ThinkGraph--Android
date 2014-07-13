package org.thinkgraph.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TaggedRecord implements Serializable {

	private static final long serialVersionUID = 6766989757647199341L;

	private final List<RecordItem> associations = new ArrayList<RecordItem>();

	private final String path;

	private final String tag;

	public TaggedRecord(String tag, String path, RecordItem... associations) {
		this.tag = tag;
		this.path = path;
		this.associations.addAll(Arrays.asList(associations));
	}

	public List<RecordItem> getAssociations() {
		return associations;
	}

	public String getPath() {
		return path;
	}

	public String getTag() {
		return tag;
	}
}