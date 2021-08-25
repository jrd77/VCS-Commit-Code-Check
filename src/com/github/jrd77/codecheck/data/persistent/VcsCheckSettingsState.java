package com.github.jrd77.codecheck.data.persistent;
// Copyright 2000-2021 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

import com.github.jrd77.codecheck.data.model.FileMatchModel;
import com.github.jrd77.codecheck.data.model.RuleTypeEnum;
import com.github.jrd77.codecheck.util.CollUtil;
import com.github.jrd77.codecheck.util.JsonUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Supports storing the application settings in a persistent way.
 * The {@link State} and {@link Storage} annotations define the name of the data and the file name where
 * these persistent application settings are stored.
 */
@State(
        name = "com.github.jrd77.codecheck.data.persistent.VcsCheckSettingsState",
        storages = {@Storage("SdkVcsCheckPlugin.xml")}
)
public class VcsCheckSettingsState implements PersistentStateComponent<VcsCheckSettingsState> {

    static {
        VcsCheckSettingsState instance = getInstance();
        //老版本未升级数据进行数据更新
        if (instance.openCheck && !instance.oldDataUpdated) {
            updateDataVersion(instance);
        }
    }

    @Deprecated
    public List<String> ruleList = new LinkedList<>();
    ;
    @Deprecated
    public List<String> ignoreList = new LinkedList<>();
    public List<String> codeMatchList = new LinkedList<>();

    public Boolean openCheck = Boolean.FALSE;
    public List<String> fileMatchList = new LinkedList<>();
    public boolean oldDataUpdated = Boolean.FALSE;

    public static VcsCheckSettingsState getInstance() {
        return ApplicationManager.getApplication().getService(VcsCheckSettingsState.class);
    }

    @Nullable
    @Override
    public VcsCheckSettingsState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull VcsCheckSettingsState state) {


        XmlSerializerUtil.copyBean(state, this);
    }

    private static void updateDataVersion(VcsCheckSettingsState instance) {
        List<String> ruleList = instance.ruleList;
        if (CollUtil.isNotEmpty(ruleList)) {
            instance.codeMatchList.addAll(ruleList);
        }
        List<String> ignoreList = instance.ignoreList;
        if (CollUtil.isNotEmpty(ignoreList)) {
            instance.fileMatchList = ignoreList.stream().map(x -> JsonUtil.toJson(new FileMatchModel(x, RuleTypeEnum.REGEXP, null))).collect(Collectors.toList());
        }
        instance.oldDataUpdated = true;
    }

    @Override
    public String toString() {
        return "VcsCheckSettingsState{" +
                "ruleList=" + ruleList +
                ", ignoreList=" + ignoreList +
                ", openCheck=" + openCheck +
                ", codeMatchList=" + codeMatchList +
                ", fileMatchList=" + fileMatchList +
                ", oldVersion=" + oldDataUpdated +
                '}';
    }
}
