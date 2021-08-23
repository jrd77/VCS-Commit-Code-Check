package com.github.jrd77.codecheck.data.save;

import com.github.jrd77.codecheck.data.VcsCheckSettingsState;
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

        return state.ruleList.add(str);
    }

    @Override
    public Boolean addFileMatch(String str) {
        return state.ignoreList.add(str);
    }

    @Override
    public Boolean setCodeMatch(List<String> strList) {

        state.ruleList=new LinkedList<>();
        return state.ruleList.addAll(strList);
    }

    @Override
    public Boolean setFileMatch(List<String> strList) {
        state.ignoreList=new LinkedList<>();
        return state.ignoreList.addAll(strList);
    }

    @Override
    public Boolean clearCodeMatch() {
        state.ruleList=new LinkedList<>();
        return true;
    }

    @Override
    public Boolean clearFileMatch() {

        state.ignoreList=new LinkedList<>();
        return true;
    }

    @Override
    public Boolean codeMatchContain(String str) {
        return state.ruleList.contains(str);
    }

    @Override
    public Boolean fileMatchContain(String str) {
        return state.ignoreList.contains(str);
    }

    @Override
    public List<String> codeMatchList() {
        return state.ruleList;
    }

    @Override
    public List<String> fileMatchList() {
        return state.ignoreList;
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
