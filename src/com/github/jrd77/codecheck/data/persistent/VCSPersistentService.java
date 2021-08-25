package com.github.jrd77.codecheck.data.persistent;

import com.github.jrd77.codecheck.data.model.CodeMatchModel;
import com.github.jrd77.codecheck.data.model.FileMatchModel;
import com.github.jrd77.codecheck.data.persistent.convert.CodeMatchModelConverter;
import com.github.jrd77.codecheck.data.persistent.convert.FileMatchModelConverter;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.intellij.util.xmlb.annotations.OptionTag;

import java.util.List;

/**
 * @author zhen.wang
 * @description TODO
 * @date 2021/8/25 10:27
 */
@State(
        name = "com.github.jrd77.codecheck.data.persistent.VCSPersistentService",
        storages = {@Storage("com.github.jrd77.codecheck.id.codecheck.xml")}
)
public class VCSPersistentService implements PersistentStateComponent<VCSPersistentService> {


    @OptionTag(converter = CodeMatchModelConverter.class)
    public List<CodeMatchModel> codeMatchList;

    @OptionTag(converter = FileMatchModelConverter.class)
    public List<FileMatchModel> fileMatchList;

    public boolean oldVersion = false;

    public boolean init = false;

    @Override
    public VCSPersistentService getState() {
        return this;
    }

    @Override
    public void loadState(VCSPersistentService state) {
        XmlSerializerUtil.copyBean(state, this);
    }

}
