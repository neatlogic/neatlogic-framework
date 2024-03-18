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

import java.util.ArrayList;
import java.util.List;

public class DocumentFieldVo {
	private Long id;
	private Long wordId;
	private String field;
	private String word;
	private String wordType;
	private String type;
	private Long documentId;
	private Integer counter;
	private List<DocumentOffsetVo> offsetList;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Long getWordId() {
		return wordId;
	}

	public void setWordId(Long wordId) {
		this.wordId = wordId;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public Long getDocumentId() {
		return documentId;
	}

	public void setDocumentId(Long documentId) {
		this.documentId = documentId;
	}

	public int getCounter() {
		if (counter == null && offsetList != null) {
			counter = offsetList.size();
		}
		return counter;
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public void addCounter(int c) {
		counter += c;
	}

	public List<DocumentOffsetVo> getOffsetList() {
		return offsetList;
	}

	public void setOffsetList(List<DocumentOffsetVo> offsetList) {
		this.offsetList = offsetList;
	}

	public void addOffset(DocumentOffsetVo documentFieldOffsetVo) {
		if (offsetList == null) {
			offsetList = new ArrayList<>();
		}
		offsetList.add(documentFieldOffsetVo);
	}

	public String getWordType() {
		return wordType;
	}

	public void setWordType(String wordType) {
		this.wordType = wordType;
	}

}
