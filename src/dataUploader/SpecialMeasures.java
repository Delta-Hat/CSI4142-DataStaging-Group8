package dataUploader;

import java.io.Serializable;

public class SpecialMeasures implements Serializable {

		/**
	 * 
	 */
	private static final long serialVersionUID = -2833405207672499302L;
		private int specialMeasuresKey;
		private String title;
		private String description;
		private String keyword1;
		private String keyword2;
		private int startYear;
		private int endYear;
		private int startMonth;
		private int endMonth;
		private int startDay;
		private int endDay;
		
		public SpecialMeasures(int specialMeasuresKey, String title, String description, String keyword1, String keyword2, int startYear, int endYear, int startMonth, int endMonth, int startDay, int endDay) {
			this.setSpecialMeasuresKey(specialMeasuresKey);
			this.setTitle(title);
			this.setDescription(description);
			this.setKeyword1(keyword1);
			this.setKeyword2(keyword2);
			this.setStartYear(startYear);
			this.setEndYear(endYear);
			this.setStartMonth(startMonth);
			this.setEndMonth(endMonth);
			this.setStartDay(startDay);
			this.setEndDay(endDay);
		}

		public int getSpecialMeasuresKey() {
			return specialMeasuresKey;
		}

		public void setSpecialMeasuresKey(int specialMeasuresKey) {
			this.specialMeasuresKey = specialMeasuresKey;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public String getKeyword1() {
			return keyword1;
		}

		public void setKeyword1(String keyword1) {
			this.keyword1 = keyword1;
		}

		public String getKeyword2() {
			return keyword2;
		}

		public void setKeyword2(String keyword2) {
			this.keyword2 = keyword2;
		}

		public int getStartYear() {
			return startYear;
		}

		public void setStartYear(int startYear) {
			this.startYear = startYear;
		}

		public int getEndYear() {
			return endYear;
		}

		public void setEndYear(int endYear) {
			this.endYear = endYear;
		}

		public int getStartMonth() {
			return startMonth;
		}

		public void setStartMonth(int startMonth) {
			this.startMonth = startMonth;
		}

		public int getStartDay() {
			return startDay;
		}

		public void setStartDay(int startDay) {
			this.startDay = startDay;
		}

		public int getEndMonth() {
			return endMonth;
		}

		public void setEndMonth(int endMonth) {
			this.endMonth = endMonth;
		}

		public int getEndDay() {
			return endDay;
		}

		public void setEndDay(int endDay) {
			this.endDay = endDay;
		}
}
