package pharma.Connector;

/**
 * connectAndGetJSON to the EBI OLS serves two purposes:
 * - get the terms (children ...) >> TERMS - this is default!
 * - get the page size (to evade the paging problem) >> PAGES
 * 
 * @author asztrik
 *
 */
public enum ConnectionPurpose {
	   TERMS(1),
	   PAGES(2);
	   private int value;
	   private ConnectionPurpose(int value) {
	      this.value = value;
	   }
	   public int getValue() {
	      return value;
	   } 
}
