package fi.foyt.fni.utils.diff;

public class PatchResult {

	public PatchResult(String patchedText, boolean[] applied) {
	  this.patchedText = patchedText;
	  this.applied = applied;
  }
	
	public boolean[] getApplied() {
	  return applied;
  }
	
	public void setApplied(boolean[] applied) {
	  this.applied = applied;
  }
	
	public String getPatchedText() {
	  return patchedText;
  }
	
	public void setPatchedText(String patchedText) {
	  this.patchedText = patchedText;
  }
	
	public boolean allApplied() {
		for (boolean patchApplied : applied) {
			if (patchApplied == false)
				return false;
		}
		
		return true;
	}
	
	private String patchedText;
	private boolean[] applied;
}
