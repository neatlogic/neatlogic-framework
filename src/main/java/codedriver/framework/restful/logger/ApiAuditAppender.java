package codedriver.framework.restful.logger;

public class ApiAuditAppender {

	public final static String DEFAULT_FILE_NAME_PATTERN = "yyyy-MM-dd HH-mm-ss";
	private String filePath;
	private long maxFileSize;
	private int maxHistory;
	private String fileNamepattern = DEFAULT_FILE_NAME_PATTERN;
	public ApiAuditAppender() {
	}
	public ApiAuditAppender(String filePath, long maxFileSize, int maxHistory) {
		this.filePath = filePath;
		this.maxFileSize = maxFileSize;
		this.maxHistory = maxHistory;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public long getMaxFileSize() {
		return maxFileSize;
	}
	public void setMaxFileSize(long maxFileSize) {
		this.maxFileSize = maxFileSize;
	}
	public int getMaxHistory() {
		return maxHistory;
	}
	public void setMaxHistory(int maxHistory) {
		this.maxHistory = maxHistory;
	}
	public String getFileNamepattern() {
		return fileNamepattern;
	}
	public void setFileNamepattern(String fileNamepattern) {
		this.fileNamepattern = fileNamepattern;
	}
	
}
