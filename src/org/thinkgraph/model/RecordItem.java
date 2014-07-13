package org.thinkgraph.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RecordItem implements Serializable {

	private static final long serialVersionUID = -1423447950753248866L;

	private long createdAt;

	private final List<String> tags = new ArrayList<String>();

	private final String path;

	private final long sizeInBytes;

	public RecordItem(long createdAt, long sizeInBytes, String path,
			String... tags) {
		this.createdAt = createdAt;
		this.tags.addAll(Arrays.asList(tags));
		this.path = path;
		this.sizeInBytes = sizeInBytes;
	}

    public  void setCreatedAt(long ts){
        createdAt=ts;
    }

	public long getSizeInBytes() {
		return sizeInBytes;
	}

	public long getCreatedAt() {
		return createdAt;
	}

	public List<String> getTags() {
		return tags;
	}

	public String getPath() {
		return path;
	}
}