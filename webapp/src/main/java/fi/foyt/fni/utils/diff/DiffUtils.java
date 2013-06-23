package fi.foyt.fni.utils.diff;

import java.util.LinkedList;

import name.fraser.neil.plaintext.diff_match_patch;
import name.fraser.neil.plaintext.diff_match_patch.Diff;
import name.fraser.neil.plaintext.diff_match_patch.Patch;

public class DiffUtils {

  public static String makePatch(String text1, String text2) {
    diff_match_patch diffMatchPatch = new diff_match_patch();
    LinkedList<Diff> diffs = diffMatchPatch.diff_main(text1, text2);
    diffMatchPatch.diff_cleanupEfficiency(diffs);
    return diffMatchPatch.patch_toText(diffMatchPatch.patch_make(diffs));
  }
  
  public static PatchResult applyPatch(String text, String textline) {
    diff_match_patch diffMatchPatch = new diff_match_patch();
    LinkedList<Patch> patches = new LinkedList<Patch>(diffMatchPatch.patch_fromText(textline));
    Object[] patchResult = diffMatchPatch.patch_apply(patches, text);
    
    String patchedText = (String) patchResult[0];
    boolean[] applied = (boolean[]) patchResult[1];
    
    return new PatchResult(patchedText, applied);
  }
  
}
