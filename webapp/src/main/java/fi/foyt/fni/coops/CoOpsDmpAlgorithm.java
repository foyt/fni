package fi.foyt.fni.coops;

import fi.foyt.coops.CoOpsAlgorithm;
import fi.foyt.coops.CoOpsConflictException;
import fi.foyt.coops.CoOpsInternalErrorException;
import fi.foyt.coops.CoOpsNotImplementedException;
import fi.foyt.coops.model.File;
import fi.foyt.fni.utils.diff.DiffUtils;
import fi.foyt.fni.utils.diff.PatchResult;

public class CoOpsDmpAlgorithm implements CoOpsAlgorithm {
  
  @Override
  public String patch(File file, String patch) throws CoOpsNotImplementedException, CoOpsConflictException, CoOpsInternalErrorException {
    if (file == null) {
      throw new CoOpsInternalErrorException("File is null");
    }
    
    String content = file.getContent();
    if (content == null) {
      content =  "";
    }
    
    PatchResult patchResult = DiffUtils.applyPatch(content, patch);
    if (!patchResult.allApplied()) {
      throw new CoOpsConflictException();
    }
    
    return patchResult.getPatchedText();
  }
  
  @Override
  public String unpatch(File file, String patch) throws CoOpsNotImplementedException, CoOpsConflictException, CoOpsInternalErrorException {
    throw new CoOpsNotImplementedException();
  }

}
