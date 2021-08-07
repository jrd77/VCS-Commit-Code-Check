package com.github.jrd77.codecheck.handler;

import com.intellij.codeInsight.editorActions.wordSelection.AbstractWordSelectioner;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.JavaTokenType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiJavaToken;
import com.intellij.psi.PsiKeyword;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiUtil;
import java.util.Iterator;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class MyJavaWordSelection extends AbstractWordSelectioner {
    @Override
    public boolean canSelect(@NotNull PsiElement e) {
        if (e instanceof PsiKeyword) {
            return true;
        }
        if (e instanceof PsiJavaToken) {
            IElementType tokenType = ((PsiJavaToken) e).getTokenType();
            return tokenType == JavaTokenType.IDENTIFIER || tokenType == JavaTokenType.STRING_LITERAL;
        }
        return false;
    }

    @Override
    public List<TextRange> select(@NotNull PsiElement e, @NotNull CharSequence editorText, int cursorOffset, @NotNull Editor editor) {
        List<TextRange> ranges = super.select(e, editorText, cursorOffset, editor);
        if (PsiUtil.isJavaToken(e, JavaTokenType.STRING_LITERAL)) {
            killRangesBreakingEscapes(e, ranges, e.getTextRange());
        }
        return ranges;
    }

    private static void killRangesBreakingEscapes(PsiElement e, List<TextRange> ranges, TextRange literalRange) {
        for (Iterator<TextRange> iterator = ranges.iterator(); iterator.hasNext(); ) {
            TextRange each = iterator.next();
            if (literalRange.contains(each) &&
                    literalRange.getStartOffset() < each.getStartOffset() &&
                    e.getText().charAt(each.getStartOffset() - literalRange.getStartOffset() - 1) == '\\') {
                iterator.remove();
            }
        }
    }
}