package neatlogic.framework.matrix.dto;

public class MatrixDefaultValueFilterVo {
    private final MatrixKeywordFilterVo valueFieldFilter;
    private final MatrixKeywordFilterVo textFieldFilter;

    public MatrixDefaultValueFilterVo(MatrixKeywordFilterVo valueFieldFilter, MatrixKeywordFilterVo textFieldFilter) {
        this.valueFieldFilter = valueFieldFilter;
        this.textFieldFilter = textFieldFilter;
    }

    public MatrixKeywordFilterVo getValueFieldFilter() {
        return valueFieldFilter;
    }

    public MatrixKeywordFilterVo getTextFieldFilter() {
        return textFieldFilter;
    }
}
