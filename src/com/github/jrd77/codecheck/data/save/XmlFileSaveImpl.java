package com.github.jrd77.codecheck.data.save;

import com.github.jrd77.codecheck.data.model.CodeMatchModel;
import com.github.jrd77.codecheck.data.model.FileMatchModel;
import com.github.jrd77.codecheck.data.persistent.VcsCheckSettingsState;
import com.github.jrd77.codecheck.util.JsonUtil;
import com.intellij.openapi.application.ApplicationManager;

import java.util.LinkedList;
import java.util.List;

/**
 *
 */
public class XmlFileSaveImpl implements SaveInterface {

    private static VcsCheckSettingsState state=null;
    private static XmlFileSaveImpl instance=null;
    static {
        state=ApplicationManager.getApplication().getService(VcsCheckSettingsState.class);
    }

    public static SaveInterface getInstance() {
        if(instance==null){
            instance=new XmlFileSaveImpl();
        }
        return instance;
    }
    private XmlFileSaveImpl() {

    }

    @Override
    public Boolean addCodeMatch(String str) {
        return state.codeMatchList.add(str);
    }

    @Override
    public Boolean addFileMatch(String str) {
        return state.fileMatchList.add(str);
    }

    @Override
    public Boolean removeCodeMatch(CodeMatchModel newModel) {

        List<CodeMatchModel> codeMatchModels = JsonUtil.fromJson(state.codeMatchList, CodeMatchModel.class);
        boolean remove = codeMatchModels.remove(newModel);
        if (remove) {
            return setCodeMatch(JsonUtil.toJsonList(codeMatchModels));
        }
        return remove;
    }

    @Override
    public Boolean removeFileMatch(FileMatchModel newModel) {
        List<FileMatchModel> fileMatchModels = JsonUtil.fromJson(state.fileMatchList, FileMatchModel.class);
        boolean remove = fileMatchModels.remove(newModel);
        if (remove) {
            return setFileMatch(JsonUtil.toJsonList(fileMatchModels));
        }
        return remove;
    }

    @Override
    public Boolean setCodeMatch(List<String> strList) {

        state.codeMatchList = new LinkedList<>();
        return state.codeMatchList.addAll(strList);
    }

    @Override
    public Boolean setFileMatch(List<String> strList) {
        state.fileMatchList = new LinkedList<>();
        return state.fileMatchList.addAll(strList);
    }

    @Override
    public Boolean clearCodeMatch() {
        state.codeMatchList = new LinkedList<>();
        return true;
    }

    @Override
    public Boolean clearFileMatch() {

        state.fileMatchList = new LinkedList<>();
        return true;
    }

    @Override
    public Boolean codeMatchContain(String str) {
        return state.codeMatchList.contains(str);
    }

    @Override
    public Boolean fileMatchContain(String str) {
        return state.fileMatchList.contains(str);
    }

    @Override
    public List<String> codeMatchList() {
        return state.codeMatchList;
    }

    @Override
    public List<String> fileMatchList() {
        return state.fileMatchList;
    }

    @Override
    public Boolean getOpenCheck() {
        return state.openCheck;
    }

    @Override
    public void setOpenCheck(boolean b) {
        state.openCheck=b;
    }


}
