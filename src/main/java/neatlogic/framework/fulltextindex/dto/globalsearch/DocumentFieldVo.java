/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
