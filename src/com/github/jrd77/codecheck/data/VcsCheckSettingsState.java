package com.github.jrd77.codecheck.data;
// Copyright 2000-2021 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;

/**
 * Supports storing the application settings in a persistent way.
 * The {@link State} and {@link Storage} annotations define the name of the data and the file name where
 * these persistent application settings are stored.
 */
@State(
        name = "com.github.jrd77.codecheck.data.VcsCheckSettingsState",
        storages = {@Storage("SdkVcsCheckPlugin.xml")}
)
public class VcsCheckSettingsState implements PersistentStateComponent<VcsCheckSettingsState> {

    public List<String> ruleList=new LinkedList<>();

    public List<String> ignoreList=new LinkedList<>();;

    public Boolean openCheck=Boolean.FALSE;

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
}
