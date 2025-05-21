package client.ui.component.text;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.StyleConstants;

public class KoreanFilter extends DocumentFilter {
    @Override
    public void insertString(FilterBypass fb, int offset, String text, AttributeSet attr)
            throws BadLocationException {
        if (!containsKorean(text) || isComposedText(attr)) {
            super.insertString(fb, offset, text, attr);
        }
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attr)
            throws BadLocationException {
        if (!containsKorean(text) || isComposedText(attr)) {
            super.replace(fb, offset, length, text, attr);
        }
    }

    private boolean containsKorean(String text) {
        return text != null && text.matches(".*[가-힣ㄱ-ㅎㅏ-ㅣ].*");
    }

    // 조합 중 텍스트인지 확인 (한글 입력 도중 상태)
    private boolean isComposedText(AttributeSet attr) {
        return attr != null && attr.isDefined(StyleConstants.ComposedTextAttribute);
    }
}