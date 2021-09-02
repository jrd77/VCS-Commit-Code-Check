package com.github.jrd77.codecheck.data.save;

import com.github.jrd77.codecheck.data.model.CodeMatchModel;
import com.github.jrd77.codecheck.data.model.FileMatchModel;

import java.util.List;

public interface SaveInterface {


    Boolean addCodeMatch(String str);

    Boolean addFileMatch(String str);

    Boolean removeCodeMatch(CodeMatchModel newModel);

    Boolean removeFileMatch(FileMatchModel newModel);

    Boolean setCodeMatch(List<String> strList);

    Boolean setFileMatch(List<String> strList);

    Boolean clearCodeMatch();

    Boolean clearFileMatch();

    Boolean codeMatchContain(String str);

    Boolean fileMatchContain(String str);

    List<String> codeMatchList();

    List<String> fileMatchList();

    Boolean getOpenCheck();

    void setOpenCheck(boolean b);
}
