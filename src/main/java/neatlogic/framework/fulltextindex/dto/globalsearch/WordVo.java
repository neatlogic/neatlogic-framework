/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

package neatlogic.framework.fulltextindex.dto.globalsearch;

import neatlogic.framework.common.dto.BasePageVo;
import org.apache.commons.lang3.StringUtils;

@Deprecated
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
