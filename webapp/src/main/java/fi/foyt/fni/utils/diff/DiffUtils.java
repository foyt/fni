package fi.foyt.fni.utils.diff;

import java.util.LinkedList;

import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch;
import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch.Diff;
import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch.Patch;

public class DiffUtils {

  public static String makePatch(String text1, String text2) {
    DiffMatchPatch diffMatchPatch = new DiffMatchPatch();
    LinkedList<Diff> diffs = diffMatchPatch.diffMain(text1, text2);
    diffMatchPatch.diffCleanupEfficiency(diffs);
    return diffMatchPatch.patchToText(diffMatchPatch.patchMake(diffs));
  }
  
  public static PatchResult applyPatch(String text, String textline) {
    DiffMatchPatch diffMatchPatch = new DiffMatchPatch();
    LinkedList<Patch> patches = new LinkedList<Patch>(diffMatchPatch.patchFromText(textline));
    Object[] patchResult = diffMatchPatch.patchApply(patches, text);
    
    String patchedText = (String) patchResult[0];
    boolean[] applied = (boolean[]) patchResult[1];
    
    return new PatchResult(patchedText, applied);
  }
  
}
