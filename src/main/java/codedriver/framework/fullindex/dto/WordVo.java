package codedriver.framework.fullindex.dto;

import codedriver.framework.common.dto.BasePageVo;
import org.apache.commons.lang3.StringUtils;

public class WordVo extends BasePageVo {
	private Long id;
	private String word;
	private String type;
	private int offsetStart;
	private int offsetEnd;

	public WordVo() {

	}

	public WordVo(String _word) {
		this.word = _word;
	}

	public WordVo(String _word, String _type) {
		this.word = _word;
		this.type = _type;
	}

	public WordVo(String _word, String _type, int _offsetStart, int _offsetEnd) {
		this.word = _word;
		this.type = _type;
		this.offsetStart = _offsetStart;
		this.offsetEnd = _offsetEnd;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getOffsetStart() {
		return offsetStart;
	}

	public void setOffsetStart(int offsetStart) {
		this.offsetStart = offsetStart;
	}

	public int getOffsetEnd() {
		return offsetEnd;
	}

	public void setOffsetEnd(int offsetEnd) {
		this.offsetEnd = offsetEnd;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other)
			return true;
		if (other == null)
			return false;
		if (!(other instanceof WordVo))
			return false;

		final WordVo word = (WordVo) other;
		try {
			if (getId() != null && getId().equals(word.getId())) {
				return true;
			} else if (StringUtils.isNotBlank(getWord()) && getWord().equalsIgnoreCase(word.getWord())) {
				return true;
			}
		} catch (Exception ex) {
			return false;
		}
		return false;
	}

	@Override
	public int hashCode() {
		int result = getWord().hashCode() * 7;
		return result;
	}
}