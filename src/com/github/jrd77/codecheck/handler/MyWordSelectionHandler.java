package com.github.jrd77.codecheck.handler;

import com.intellij.codeInsight.editorActions.ExtendWordSelectionHandler;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MyWordSelectionHandler implements ExtendWordSelectionHandler {
    @Override
    public boolean canSelect(@NotNull PsiElement psiElement) {
        return false;
    }

    @Nullable
    @Override
    public List<TextRange> select(@NotNull PsiElement psiElement, @NotNull CharSequence charSequence, int i, @NotNull Editor editor) {
        return null;
    }
}
